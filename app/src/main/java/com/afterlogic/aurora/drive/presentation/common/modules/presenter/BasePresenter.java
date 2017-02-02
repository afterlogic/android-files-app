package com.afterlogic.aurora.drive.presentation.common.modules.presenter;

import android.os.Bundle;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.exceptions.CompositeException;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Base presenter implementation.
 */
public abstract class BasePresenter<V extends PresentationView> implements Presenter {

    //Presenter view input
    private final V mView;
    private UUID mUUID;

    //Presenter enabled state
    private boolean mIsActive;

    private boolean mStarted;

    private List<Stoppable> mStoppableList = new ArrayList<>();

    private Observable<Boolean> mLifeCycle = Observable.create(new ObservableOnSubscribe<Boolean>() {
        @Override
        public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
            e.onNext(mIsActive);
            registerStoppable(new Stoppable() {
                @Override
                public void onStart() {
                    e.onNext(true);
                }

                @Override
                public void onStop() {
                    e.onNext(false);
                }
            });
        }
    });

    public BasePresenter(ViewState<V> viewState) {
        mView = viewState.getViewProxy();
        registerStoppable(viewState);
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
        //no-op
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
        if (mIsActive){
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

        if (BuildConfig.DEBUG) {
            mView.showMessage(error.getMessage(), PresentationView.TYPE_MESSAGE_MINOR);
        }
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
        return source.takeUntil(getLifeCycle().filter(live -> !live));
    }

    protected Observable<Boolean> startWithView(){
        return mLifeCycle.filter(live -> live);
    }

    protected Observable<Boolean> getLifeCycle(){
        return mLifeCycle;
    }
}