package com.afterlogic.aurora.drive._unrefactored.data.common.api;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.ApiCompatibilityUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.api.ApiResponseDeserializer;
import com.afterlogic.aurora.drive._unrefactored.data.common.ApiProvider;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;
import com.afterlogic.aurora.drive.presentation.modulesBackground.session.SessionTrackUtil;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.data.common.network.DynamicEndPointInterceptor;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive.model.error.ApiError;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.annimon.stream.Stream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
@SuppressWarnings("WeakerAccess")
public class AuroraApi {

    public static final String ACTION_AUTH_FAILED = "com.afterlogic.aurora.ACTION_AUTH_FAILED";


    private static TaskStateHandler sTaskStateHandler;
    private static Context sContext;

    private static ApiProvider sApiProvider;

    public static ApiProvider getApiProvider(){
        return sApiProvider;
    }

    public static void init(Context ctx, ApiProvider provider){
        sTaskStateHandler = new TaskStateHandler(ctx);
        sContext = ctx;
        sApiProvider = provider;
    }

    public static AuroraSession getCurrentSession() {
        return sApiProvider.getSessionManager().getSession();
    }

    public static void setCurrentSession(AuroraSession session) {
        sApiProvider.getSessionManager().setSession(session);
    }

    public static TaskStateHandler getTaskStateHandler(){
        return sTaskStateHandler;
    }

    private static ApiTaskErrorHandler sErrorHandler = new ApiTaskErrorHandler() {
        @Override
        public void onTaskError(ApiTask.TaskException ex) {
            if (ex.getCode() == ApiTask.TaskException.API_NOT_EXIST
                    && getCurrentSession() == null){

                LocalBroadcastManager.getInstance(sContext)
                        .sendBroadcast(new Intent(ACTION_AUTH_FAILED));
            }else {
                throw ex;
            }
        }
    };

    public static Context getContext() {
        return sContext;
    }

    public static ApiTaskErrorHandler getErrorHandler() {
        return sErrorHandler;
    }

    /**
     * Get Api interface.
     *
     * @param type - api interface class.
     * @param gson - extended gson converter, if not exist default will be used.
     * @return - required Api interface.
     */
    public static <T> T getApi(Class<T> type, @Nullable Gson gson){
        return getApi(type, gson, null);
    }

    /**
     * Get Api interface.
     *
     * @param type - api interface class.
     * @param gson - extended gson converter, if not exist default will be used.
     * @return - required Api interface.
     */
    public static <T> T getApi(Class<T> type, @Nullable Gson gson, Interceptor httpInterceptor){
        //[START Create client]
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        //Add logging for debug
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(s -> MyLog.d("OkHttp", s));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(interceptor);

        //Dynamic notifyEnd point interceptor
        clientBuilder.addInterceptor(new DynamicEndPointInterceptor(
                DynamicEndPointInterceptor.DYNAMIC_BASE_URL,
                () -> AuroraApi.getCurrentSession().getDomain()
        ));

        //Add additional interceptor if it exist
        if (httpInterceptor != null) {
            clientBuilder.addInterceptor(httpInterceptor);
        }
        OkHttpClient client = clientBuilder.build();
        //[END Create client]

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(DynamicEndPointInterceptor.DYNAMIC_BASE_URL)
                .client(client);
        //Add gson converter
        if (gson == null){
            builder.addConverterFactory(GsonConverterFactory.create());
        }else{
            builder.addConverterFactory(GsonConverterFactory.create(gson));
        }
        return builder.build().create(type);
    }

    /**
     * Get result from sync request call.
     * @param call - target call.
     * @param <R> - type of api response.
     * @return - result if it exist.
     * @throws IOException -
     */
    public static <R> R callSync(Call<ApiResponseP7<R>> call) throws IOException {
        ApiResponseP7<R> data = callSyncResponse(call);
        if (data != null && data.getError() == null){
            return data.getResult();
        }
        return null;
    }

    public static <R> R callSyncResponse(Call<R> call) throws IOException {
        Response<R> response = call.execute();
        if (response != null && response.isSuccessful()){
            return response.body();
        }
        return null;
    }

    /**
     * Get base system info.
     *
     * @param callback - result callback.
     */
    public static void getSystemAppData(final ApiCallback<SystemAppData> callback){

        SuccessCallback<SystemAppData> success =
                new SuccessCallback<SystemAppData>(callback) {
            @Override
            public void onSucces(ApiResponseP7<SystemAppData> result) {
                SystemAppData data = result.getResult();
                getCurrentSession().setAppToken(data.getToken());

                SessionTrackUtil.fireSessionChanged(getCurrentSession(), sContext);

                callback.onSucces(data);
            }
        };

        AuthApi api = getApi(AuthApi.class, getApiResponseGson(null));
        new ApiTask.Builder<AuthApi, ApiResponseP7<SystemAppData>>(api)
                .setApiTaskErrorHandler(sErrorHandler)
                .setApiTaskExecuter(new SessionExecuter<>(callback, false, sContext))
                .setCallback(new CallbackWrapper<AuthApi, ApiResponseP7<SystemAppData>>(success))
                .setTaskHandler(sTaskStateHandler)
                .build(new ApiTask.CallCreatorImpl<AuthApi, ApiResponseP7<SystemAppData>>() {
                    @Override
                    public Call<ApiResponseP7<SystemAppData>> createCall(AuthApi apiInterface) {
                        Single<SystemAppData> observable = getApiProvider().getUserRepository()
                                .getSystemAppData();
                        return fromObservable(observable);
                    }
                }).execute();
    }

    /**
     * Login user in Aurora.
     *
     * @param email - user email.
     * @param pass - user pass.
     * @param callback - result callback.
     */
    public static void login(final String email, final String pass,
                             final ApiCallback<AuthToken> callback){

        SuccessCallback<AuthToken> success = new SuccessCallback<AuthToken>(callback) {
            @Override
            public void onSucces(ApiResponseP7<AuthToken> result) {
                AuthToken token = result.getResult();
                getCurrentSession().setAuthToken(token.token);
                getCurrentSession().setAccountId(result.getAccountId());

                SessionTrackUtil.fireSessionChanged(getCurrentSession(), sContext);

                if (callback != null) {
                    callback.onSucces(token);
                }
            }
        };

        AuthApi api = getApi(AuthApi.class, getApiResponseGson(null));
        new ApiTask.Builder<AuthApi, ApiResponseP7<AuthToken>>(api)
                .setApiTaskErrorHandler(sErrorHandler)
                .setApiTaskExecuter(new SessionExecuter<>(callback, false, sContext))
                .setCallback(new CallbackWrapper<AuthApi, ApiResponseP7<AuthToken>>(success))
                .setTaskHandler(sTaskStateHandler)
                .build(new ApiTask.CallCreatorImpl<AuthApi, ApiResponseP7<AuthToken>>() {
                    @Override
                    public Call<ApiResponseP7<AuthToken>> createCall(AuthApi apiInterface) {
                        Single<AuthToken> observable = getApiProvider().getUserRepository()
                                .login(email, pass)
                                .andThen(Single.fromCallable(() ->
                                        new AuthToken(getApiProvider().getSessionManager().getSession().getAuthToken())
                                ));
                        return fromObservable(observable);
                    }
                }).execute();
    }

    /**
     * Default handling call failure.
     * @param call - failed call.
     * @param t - exception
     * @param mApiCallback - result callback
     */
    public static <R> void onCallFailure(Call<R> call, Throwable t, ApiCallback mApiCallback) {
        if (call.isCanceled()) {
            mApiCallback.onError(new ApiResponseError(ApiError.TASK_CANCELLED, "Request was cancelled."));
        } else {
            MyLog.majorException("API", t);
            if (t instanceof UnknownHostException) {
                mApiCallback.onError(
                        new ApiResponseError(ApiError.UNKNOWN_HOST, null));
            } else {
                mApiCallback.onError(getDefaultConnectionError());
            }
        }
    }

    /**
     * Create default connection error.
     */
    public static ApiResponseError getDefaultConnectionError(){
        return new ApiResponseError(
                ApiError.CONNECTION_ERROR,
                sContext.getString(R.string.error_default_api_error)
        );
    }

    /**
     * Get default {@link Gson} with {@link ApiResponseP7} deserializer.
     * @return - Gson with requested deserializer.
     */
    public static Gson getApiResponseGson(@Nullable Gson gson){
        return new GsonBuilder()
                .registerTypeAdapter(ApiResponseP7.class, new ApiResponseDeserializer(gson))
                .create();
    }

    public static <T> Call<ApiResponseP7<T>> fromObservable(Single<T> observable){
        return ApiCompatibilityUtil.apiResponseCall(observable, getApiProvider().getSessionManager());
    }

    /**
     * Aurora files api interface.
     */
    public interface AuthApi {

        @FormUrlEncoded
        @POST("?/Ajax/")
        Call<ApiResponseP7<SystemAppData>> getSystemAppData(@FieldMap Map<String, Object> fields);

        @FormUrlEncoded
        @POST("?/Ajax/")
        Call<ApiResponseP7<AuthToken>> login(@FieldMap Map<String, Object> fields);
    }

    ////////////////////////////////////////////////
    // [START Classes] // <editor-fold desc="Classes">
    ////////////////////////////////////////////////

    protected static class SessionExecuter<T> implements ApiTaskExecuter{

        private ApiCallback<T> mApiCallback;
        private Context mContext;
        private boolean mCheckSessionCompletition;

        public SessionExecuter(ApiCallback<T> apiCallback, boolean checkSessionCompletition, Context context) {
            mApiCallback = apiCallback;
            mCheckSessionCompletition = checkSessionCompletition;
            mContext = context;
        }

        @Override
        public void onTaskExecute(ApiTaskInterface task) {
            AuroraSession session = getCurrentSession();
            if (session == null){
                AccountManager am = AccountManager.get(mContext);
                Account account = AccountUtil.getCurrentAccount(mContext);
                if (account != null){
                    session = AccountUtil.fromAccount(account, am);
                    setCurrentSession(session);
                } else {
                    //Send local message
                    LocalBroadcastManager.getInstance(mContext)
                            .sendBroadcast(new Intent(ACTION_AUTH_FAILED));
                    task.cancel();
                }
            }

            if (session == null){
                mApiCallback.onError(
                        new ApiResponseError(ApiError.SESSION_NOT_EXIST, "Auth data not exist!"));
                MyLog.e(this, "Auth data not exist!");
                task.cancel();
            } else if (mCheckSessionCompletition && !session.isComplete()){
                mApiCallback.onError(
                        new ApiResponseError(ApiError.SESSION_NOT_COMPLETE, "Auth data not complete!"));
                MyLog.e(this, "Auth data not complete!");
                task.cancel();
            } else {
                task.executeTask();
            }
        }
    }

    /**
     * Base retrofit {@link Callback} handler ({@link ApiCallback} wrapper).
     * Check response on error and handle response result.
     */
    protected static class CallbackWrapper<T, R extends ApiResponseP7> implements Callback<R> {

        private static List<Runnable> mUnAuthorized = new ArrayList<>();

        private ApiCallback<R> mApiCallback;
        private ApiTask.CallCreatorImpl<T, R> mReCallCreator;
        private T mApi;
        private boolean mHandleUnauthorizedError = true;

        public CallbackWrapper(ApiCallback<R> apiCallback) {
            mApiCallback = apiCallback;
        }

        public CallbackWrapper(ApiCallback<R> apiCallback, ApiTask.CallCreatorImpl<T, R> reCallCreator, T api) {
            mApiCallback = apiCallback;
            mReCallCreator = reCallCreator;
            mApi = api;
        }

        @Override
        public void onResponse(final Call<R> call, Response<R> response) {
            handleResponse(response, new ResponseInterface<R>() {
                @Override
                public void onError(ApiError error) {
                    if (mHandleUnauthorizedError && mReCallCreator != null){
                        switch (error.getErrorCode()) {
                            case ApiResponseError.INVALID_TOKEN:
                                updateToken();
                                break;
                            case ApiResponseError.AUTH_FAILED:
                                relogin();
                                break;
                            default:
                                mApiCallback.onError(error);
                        }
                    }else{
                        if (error.getErrorCode() == ApiResponseError.AUTH_FAILED ||
                                error.getErrorCode() == ApiResponseError.INVALID_TOKEN){

                            LocalBroadcastManager.getInstance(sContext)
                                    .sendBroadcast(new Intent(ACTION_AUTH_FAILED));
                        }
                        mApiCallback.onError(error);
                    }
                }

                @Override
                public void onSuccess(R result) {
                    mApiCallback.onSucces(result);
                }
            });
        }

        private <V extends ApiResponseP7> void handleResponse(Response<V> response, ResponseInterface<V> responseInterface){
            ApiError error;
            V result = null;

            if (response.isSuccessful()) {
                //Handle response
                result = response.body();
                error = result.getError();
            }else{
                //Check on network error
                if (response.errorBody() != null) {
                    try {
                        String errorString = response.errorBody().string();
                        error = new ApiResponseError(response.code(), errorString);
                    } catch (IOException e) {
                        e.printStackTrace();
                        error = getDefaultConnectionError();
                    }
                }else{
                    error = new ApiResponseError(response.code(), response.message());
                }
            }

            if (error == null){
                responseInterface.onSuccess(result);
            }else{
                responseInterface.onError(error);
            }
        }

        @Override
        public void onFailure(Call<R> call, Throwable t) {
            onCallFailure(call, t, mApiCallback);
        }

        private void relogin(){
            if (!addUnAuthorized()) return;

            AuroraSession session = getCurrentSession();
            login(session.getLogin(), session.getPassword(), new ApiCallback<AuthToken>() {
                @Override
                public void onSucces(AuthToken result) {
                    Stream.of(mUnAuthorized).forEach(Runnable::run);
                    mUnAuthorized.clear();
                }

                @Override
                public void onError(ApiError error) {
                    mApiCallback.onError(error);
                }
            });
        }

        private void updateToken(){
            if (!addUnAuthorized()) return;

            getSystemAppData(new ApiCallback<SystemAppData>() {
                @Override
                public void onSucces(SystemAppData result) {
                    relogin();
                }

                @Override
                public void onError(ApiError error) {
                    mApiCallback.onError(error);
                }
            });
        }

        private boolean addUnAuthorized(){
            final ApiTask.CallUpdater<R> callUpdater = mReCallCreator.startUpdateCall();

            if (callUpdater == null) return false;

            Runnable unauthorizedRunnable = () -> {
                mHandleUnauthorizedError = false;
                callUpdater.updateAndReCall(mReCallCreator.createCall(mApi));
            };
            mUnAuthorized.add(unauthorizedRunnable);

            return mUnAuthorized.size() == 1;
        }

        private interface ResponseInterface<T>{
            void onError(ApiError error);
            void onSuccess(T result);
        }
    }

    protected static abstract class SuccessCallback<T> implements ApiCallback<ApiResponseP7<T>>{
        private ApiCallback mApiCallback;

        public SuccessCallback(ApiCallback apiCallback) {
            mApiCallback = apiCallback;
        }

        @Override
        public void onSucces(ApiResponseP7<T> result) {

        }

        @Override
        public void onError(ApiError error) {
            mApiCallback.onError(error);
        }
    }


    ////////////////////////////////////////////////
    // [END Classes] // </editor-fold>
    ////////////////////////////////////////////////

}
