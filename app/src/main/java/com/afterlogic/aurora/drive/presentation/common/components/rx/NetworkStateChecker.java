package com.afterlogic.aurora.drive.presentation.common.components.rx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.Holder;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by sashka on 18.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class NetworkStateChecker {

    private final Context mContext;

    @Inject public NetworkStateChecker(Context context) {
        mContext = context;
    }

    public Observable<Boolean> listenNetworkState(){
        return Observable.concat(
                checkCurrentNetworkState(),
                startListenNetworkState()
        );
    }

    private Observable<Boolean> checkCurrentNetworkState(){
        return Observable.defer(() -> Observable.just(isNetworkAvailable()));
    }

    private Observable<Boolean> startListenNetworkState(){
        Holder<BroadcastReceiver> receiver = new Holder<>();
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        try {
                            e.onNext(isNetworkAvailable());
                        } catch (Exception ex){
                            e.onError(ex);
                        }
                    }
                };
                MyLog.d(this, "Register receiver.");
                mContext.registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                receiver.set(broadcastReceiver);
            }
        })//----|
                .doOnDispose(() -> {
                    if (receiver.get() != null){
                        MyLog.d(this, "Unregister receiver.");
                        mContext.unregisterReceiver(receiver.get());
                    }
                });
    }

    private boolean isNetworkAvailable() throws Exception{
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null){
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
