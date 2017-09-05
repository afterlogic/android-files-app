package com.afterlogic.aurora.drive.presentation.common.components.rx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by sashka on 18.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class NetworkStateHelper {

    private final Context context;

    @Inject
    NetworkStateHelper(Context context) {
        this.context = context;
    }

    public Observable<Boolean> listenNetworkState(){

        List<Runnable> finalizers = new ArrayList<>();

        return Observable.<Boolean>create(emitter -> {

            emitter.onNext(isNetworkAvailable());

            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        emitter.onNext(isNetworkAvailable());
                    } catch (Exception ex){
                        emitter.onError(ex);
                    }
                }
            };

            MyLog.d(this, "Register receiver.");
            context.registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            finalizers.add(() -> context.unregisterReceiver(broadcastReceiver));

        })//--->
                .doFinally(() -> Stream.of(finalizers).forEach(Runnable::run));

    }

    private boolean isNetworkAvailable() throws Exception {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null){
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
}
