package com.afterlogic.aurora.drive.presentation.common.modules.presenter;

import android.content.Context;

import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.model.error.PermissionDeniedError;
import com.afterlogic.aurora.drive.presentation.common.modules.view.LoadView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.common.util.PermissionUtil;

import io.reactivex.Observable;
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

    public <T> Observable<T> progressibleLoadTask(Observable<Progressible<T>> observable){
        return observable.startWith(checkAndWaitPermissionResult(
                FILES_STORAGE_ACCESS,
                new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}
        ))//----|
                //TODO load progress title (downloadOrGetOffline/upload)
                .doOnNext(progress -> {
                    float value = progress.getMax() > 0 ?
                            (float) progress.getProgress() / progress.getMax() : -1;
                    getView().showLoadProgress(progress.getName(), value * 100);
                })
                .filter(Progressible::isDone)
                .map(Progressible::getData)
                .doOnSubscribe(disposable -> getView().showLoadProgress("", -1))
                .doFinally(() -> getView().hideProgress())
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
}
