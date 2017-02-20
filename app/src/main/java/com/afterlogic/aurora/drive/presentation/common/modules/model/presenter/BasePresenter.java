package com.afterlogic.aurora.drive.presentation.common.modules.model.presenter;

import android.os.Bundle;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.SimpleObservableSource;
import com.afterlogic.aurora.drive.model.error.ActivityResultError;
import com.afterlogic.aurora.drive.model.error.BaseError;
import com.afterlogic.aurora.drive.model.error.PermissionDeniedError;
import com.afterlogic.aurora.drive.model.events.ActivityResultEvent;
import com.afterlogic.aurora.drive.model.events.PermissionGrantEvent;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.common.util.ActivityResultListener;
import com.afterlogic.aurora.drive.presentation.common.util.PermisionResultListener;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.exceptions.CompositeException;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Base presenter implementation.
 */
public abstract class BasePresenter<V extends PresentationView> implements Presenter {

    private final PermissionEventObservableSource mPermissionSource = new PermissionEventObservableSource();
    private final ActivityResultObservableSource mActivityResultSource = new ActivityResultObservableSource();

    //Presenter view input
    private final V mView;
    private UUID mUUID;

    //Presenter enabled state
    private boolean mIsActive;

    private boolean mStarted;

    private List<Stoppable> mStoppableList = new ArrayList<>();

    private SimpleObservableSource<ViewEvent> mViewEventSource = new SimpleObservableSource<>();

    public BasePresenter(ViewState<V> viewState) {
        if (viewState != null) {
            mView = viewState.getViewProxy();
            registerStoppable(viewState);
        } else {
            mView = null;
        }
    }

    /**
     * Restore presenter start from saved instance.
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        MyLog.d(toString(), "restoreInstance");
    }

    /**
     * Start presenter actions.
     */
    @Override
    public final void onStart() {
        MyLog.d(toString(), "onStart");
        mIsActive = true;
        if (!mStarted) {
            mStarted = true;
            MyLog.d(toString(), "onPresenterStart");
            onPresenterStart();
        }

        onViewStart();

        Stream.of(mStoppableList).forEach(Stoppable::onStart);
    }

    protected void onViewStart(){
        MyLog.d(toString(), "onViewStart");
    }

    protected void onViewStop(){
        MyLog.d(toString(), "onViewStop");
    }

    /**
     * Stop presenter actions.
     */
    @Override
    public final void onStop() {
        MyLog.d(toString(), "onStop");
        Stream.of(mStoppableList).forEach(Stoppable::onStop);

        onViewStop();

        mIsActive = false;
    }

    protected void onPresenterStart(){
        registerStoppable(new PermisionResultListener(this::onPermissionEvent));
        registerStoppable(new ActivityResultListener(this::onActivityResult));
        registerStoppable(new Stoppable() {
            @Override
            public void onStart() {
                mViewEventSource.onNext(ViewEvent.START);
            }

            @Override
            public void onStop() {
                mViewEventSource.onNext(ViewEvent.STOP);
            }
        });
    }

    /**
     * Save presenter state.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.d(toString(), "onSaveInstanceState");
    }

    @Override
    public UUID getUuid() {
        return mUUID;
    }

    @Override
    public void setUuid(UUID uuid) {
        mUUID = uuid;
    }

    protected V getView() {
        return mView;
    }

    protected void onActivityResult(ActivityResultEvent event){
        mActivityResultSource.onActivityResult(event);
    }

    protected Observable<ActivityResultEvent> observeActivityResult(){
        return Observable.defer(() -> mActivityResultSource);
    }

    protected Observable<ActivityResultEvent> observeActivityResult(int requestId, boolean checkResult){
        return observeActivityResult()
                .filter(result -> result.getRequestId() == requestId)
                .flatMap(result -> {
                    if (!checkResult || result.isSuccess()){
                        return Observable.just(result);
                    } else {
                        return Observable.error(new ActivityResultError(requestId));
                    }
                });
    }

    protected void onPermissionEvent(PermissionGrantEvent event){
        mPermissionSource.onPermissionEvent(event);
    }

    public Observable<PermissionGrantEvent> observePermissions(){
        return Observable.defer(() -> mPermissionSource);
    }

    public Observable<PermissionGrantEvent> observePermissions(int requestId, boolean checkGrant){
        return observePermissions()
                .filter(grantEvent -> grantEvent.getRequestId() == requestId)
                .flatMap(permissions -> {
                    if (!checkGrant || permissions.isAllGranted()){
                        return Observable.just(permissions);
                    } else {
                        PermissionDeniedError error = new PermissionDeniedError(
                                permissions.getRequestId(),
                                permissions.getPermissions()
                        );
                        error.setHandled(true);
                        return Observable.error(error);
                    }
                });
    }

    /**
     * Run action only if view output is presented and presenter state is active.
     */
    protected void ifViewActive(Consumer<V> consumer){
        ifViewActive(
                consumer,
                () -> MyLog.e(this, "Presenter is not active. Ignoring.")
        );
    }

    /**
     * Run action only if view output is presented and presenter state is active.
     */
    protected void ifViewActive(Consumer<V> consumer, Runnable viewNotExistConsumer){
        if (mIsActive && mView != null){
            consumer.consume(mView);
        } else {
            viewNotExistConsumer.run();
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void onErrorObtained(Throwable error){

        if (error instanceof CompositeException){
            CompositeException compositeException = (CompositeException) error;
            Stream.of(compositeException.getExceptions()).forEach(this::onErrorObtained);
            return;
        }

        if (error instanceof BaseError){
            switch (((BaseError) error).getErrorCode()){
                case PermissionDeniedError.CODE:
                    PermissionDeniedError permissionError = (PermissionDeniedError) error;
                    if (!permissionError.isHandled()) {
                        onUnhandledPermissionError(permissionError);
                    }
                    break;
            }
        }

        MyLog.e(error.getMessage());
        if (BuildConfig.DEBUG) {
            mView.showMessage(error.getMessage(), PresentationView.TYPE_MESSAGE_MINOR);
        }
    }

    protected void onUnhandledPermissionError(PermissionDeniedError error){
        getView().requestPermissions(error.getPermissions(), error.getRequestCode());
    }

    protected void registerStoppable(Stoppable stoppable){
        if (stoppable != null && !mStoppableList.contains(stoppable)){
            mStoppableList.add(stoppable);
        }
    }

    @SuppressWarnings("unused")
    protected void unregisterStoppable(Stoppable stoppable){
        if (stoppable != null){
            mStoppableList.remove(stoppable);
        }
    }

    @Override
    public String toString() {
        String fullName = super.toString();
        int lastDotIndex = fullName.lastIndexOf('.');
        return fullName.substring(lastDotIndex == -1 ? 0 : lastDotIndex);
    }

    public <T> Observable<T> untilStopView(Observable<T> source){
        return source.takeUntil(getLifeCycle().filter(event -> event == ViewEvent.STOP));
    }

    protected Observable<ViewEvent> getLifeCycle(){
        return Observable.defer(() -> mViewEventSource);
    }
}
