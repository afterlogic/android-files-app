package com.afterlogic.aurora.drive._unrefactored.core.util;

import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sashka on 25.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiCompatibilityUtil {

    public static <T> Call<ApiResponseP7<T>> apiResponseCall(Single<T> observable, SessionManager sessionManager){
        Single<ApiResponseP7<T>> apiResponseP7Observable = observable
                .map(result -> parseResponse(result, sessionManager))
                .onErrorResumeNext(error -> parseError(error, sessionManager));
        return new ObservableCall<>(apiResponseP7Observable);
    }

    public static <T> Call<T> transparentCall(Single<T> observable){
        return new ObservableCall<>(observable);
    }

    private static <T> ApiResponseP7<T> parseResponse(T result, SessionManager sessionManager){
        return new ApiResponseP7<>(
                sessionManager.getSession().getAccountId(),
                result,
                null
        );
    }

    private static <T> Single<ApiResponseP7<T>> parseError(Throwable error, SessionManager sessionManager){
        if (error instanceof ApiResponseError){
            ApiResponseP7<T> responseP7 = new ApiResponseP7<>(
                    sessionManager.getSession().getAccountId(),
                    null,
                    new com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiResponseError(
                            ((ApiResponseError) error).getErrorCode(),
                            error.getMessage()
                    )
            );
            return Single.just(responseP7);
        } else {
            return Single.error(error);
        }
    }

    private static class ObservableCall<T> implements Call<T>{

        private Single<T> mObservable;

        private T mResultHolder;
        private Throwable mErrorHolder;
        private Disposable mDisposable;

        ObservableCall(Single<T> observable) {
            mObservable = observable
                    .doOnSuccess(result -> {
                        MyLog.d(this, "onNext()");
                        mResultHolder = result;
                    })
                    .doOnError(error -> {
                        MyLog.d(this, "onError()");
                        MyLog.majorException(this, error);
                        mErrorHolder = error;
                    });
        }

        @Override
        public Response<T> execute() throws IOException {
            MyLog.d(this, "execute()");

            subscribe(mObservable);

            return getResponse(null);
        }

        @Override
        public void enqueue(Callback<T> callback) {
            MyLog.d(this, "enque()");

            Single<T> observable = mObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> getResponse(callback));

            subscribe(observable);
        }

        @Override
        public boolean isExecuted() {
            return mDisposable != null;
        }

        @Override
        public void cancel() {
            mDisposable.dispose();
        }

        @Override
        public boolean isCanceled() {
            return mDisposable.isDisposed();
        }

        @SuppressWarnings("CloneDoesntCallSuperClone")
        @Override
        public Call<T> clone() {
            throw new RuntimeException("Not support clone");
        }

        @Override
        public Request request() {
            throw new RuntimeException("Not support request");
        }

        private void subscribe(Single<T> observable){
            mDisposable = observable.subscribe(
                    result -> {},
                    error -> {}
            );
        }

        private Response<T> getResponse(@Nullable Callback<T> callback) {
            Response<T> response;

            if (mErrorHolder == null) {
                response = Response.success(mResultHolder);
            } else {
                if (mErrorHolder instanceof HttpException) {
                    MyLog.d(this, "Get response from http exception.");
                    //noinspection unchecked
                    response = (Response<T>) ((HttpException) mErrorHolder).response();
                } else {
                    MyLog.d(this, "Create null response.");
                    response = null;
                }
            }

            if (callback != null) {
                if (mErrorHolder == null) {
                    MyLog.d(this, "Call success callback: " + this);
                    callback.onResponse(this, response);
                } else {
                    MyLog.d(this, "Call fail callback: " + this);
                    callback.onFailure(this, mErrorHolder);
                }
            }

            MyLog.d(this, "Return observable response: " + this);
            return response;
        }
    }
}
