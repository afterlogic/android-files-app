package com.afterlogic.aurora.drive.presentation.common.modules.model.presenter;

import android.content.Context;
import android.os.PowerManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.Holder;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.PermissionDeniedError;
import com.afterlogic.aurora.drive.presentation.common.modules.view.LoadView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.common.util.PermissionUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.Disposable;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.afterlogic.aurora.drive.model.events.PermissionGrantEvent.FILES_STORAGE_ACCESS;

/**
 * Created by sashka on 18.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class BaseLoadPresenter<V extends LoadView> extends BasePresenter<V> implements LoadPresenter{

    private final Context mAppContext;
    private Disposable mCurrentFileTask = null;
    private static long sWakeLockId = 0;

    public BaseLoadPresenter(ViewState<V> viewState, Context appContext) {
        super(viewState);
        mAppContext = appContext;
    }

    @Override
    public void onCancelCurrentTask() {
        if (mCurrentFileTask != null){
            mCurrentFileTask.dispose();
        }
    }

    public <T> Observable<T> trackCurrentTask(Observable<T> observable){
        return observable.doOnSubscribe(disposable -> mCurrentFileTask = disposable)
                .doFinally(() -> mCurrentFileTask = null);
    }

    public <T> ObservableTransformer<Progressible<T>, T> progressibleLoadTask(boolean download){
        return observable -> observable.startWith(checkAndWaitPermissionResult(
                FILES_STORAGE_ACCESS,
                new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}
        ))//----|
                .compose(notifyPercentProgress(download))
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .compose(this::wakeLock)
                .compose(this::trackCurrentTask);
    }

    public  <T> Observable<T> checkAndWaitPermissionResult(int requestId, String... perms){
        return Observable.defer(() -> {
            if (!PermissionUtil.isAllGranted(mAppContext, perms)){
                return Observable.<T>error(new PermissionDeniedError(requestId, perms));
            } else {
                return Observable.<T>empty();
            }
        })//----|
                .doOnError(this::onErrorObtained)
                .retryWhen(attempts -> observePermissions(requestId, true));

    }

    public  <T> ObservableTransformer<Progressible<T>, Progressible<T>> notifyPercentProgress(boolean download){
        String progressTitle = download ? mAppContext.getString(R.string.dialog_files_title_dowloading) :
                mAppContext.getString(R.string.dialog_files_title_uploading);
        return observable -> observable
                .doOnNext(progress -> {
                    float value = progress.getMax() > 0 && progress.getProgress() >= 0?
                            (float) progress.getProgress() / progress.getMax() : -1;
                    getView().showLoadProgress(
                            progress.getName(),
                            progressTitle,
                            value * 100
                    );
                })
                .doFinally(() -> getView().hideProgress());
    }

    public  <T> Observable<T> wakeLock(Observable<T> observable){
        Holder<PowerManager.WakeLock> wakeLockHolder = new Holder<>();
        return observable
                .doOnSubscribe(disposable -> {
                    PowerManager pm = (PowerManager) mAppContext.getSystemService(Context.POWER_SERVICE);
                    PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "loadTask:" + sWakeLockId++);
                    wakeLock.acquire();
                    wakeLockHolder.set(wakeLock);
                })
                .doFinally(() -> wakeLockHolder.get().release());
    }
}
