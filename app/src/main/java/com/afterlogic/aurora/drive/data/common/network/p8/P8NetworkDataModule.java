package com.afterlogic.aurora.drive.data.common.network.p8;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.DataScope;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.DynamicDomainProvider;
import com.afterlogic.aurora.drive.data.common.network.DynamicEndPointInterceptor;
import com.afterlogic.aurora.drive.data.common.network.p8.converter.ApiResponseConverter8;
import com.afterlogic.aurora.drive.data.common.network.util.IgnoreDeserealizationExcludeStrategy;
import com.afterlogic.aurora.drive.data.common.network.util.IgnoreSerealizationExcludeStrategy;
import com.afterlogic.aurora.drive.data.model.project8.ApiResponseP8;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class P8NetworkDataModule {

    @Provides
    @P8
    OkHttpClient provideClient(DynamicDomainProvider domainProvider,
                               AuthHeaderInterceptor authInterceptor,
                               P8ModuleHeadersInterceptor moduleHeadersInterceptor){
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        //Dynamic notifyEnd point interceptor
        clientBuilder.addInterceptor(new DynamicEndPointInterceptor(
                DynamicEndPointInterceptor.DYNAMIC_BASE_URL,
                domainProvider
        ));

        clientBuilder.addInterceptor(authInterceptor);

        clientBuilder.addInterceptor(moduleHeadersInterceptor);

        //Add logging for debug
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(
                    BuildConfig.DEBUG_HTTP
                            ? HttpLoggingInterceptor.Level.BODY
                            : HttpLoggingInterceptor.Level.HEADERS
            );
            clientBuilder.addInterceptor(interceptor);
        }

        return clientBuilder
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build();
    }

    @Provides @P8 @DataScope
    Gson provideGson(){
        Gson gson = new GsonBuilder()
                .addDeserializationExclusionStrategy(new IgnoreDeserealizationExcludeStrategy())
                .addSerializationExclusionStrategy(new IgnoreSerealizationExcludeStrategy())
                .create();

        return new GsonBuilder()
                .registerTypeAdapter(ApiResponseP8.class, new ApiResponseConverter8(gson))
                .create();
    }

    @Provides @P8
    Retrofit provideRetrofit(@P8 OkHttpClient client, @P8 Gson gson){
        MyLog.d(this, "Provide retrofit.");

        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(P8RequestFactory.create(gson))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .baseUrl(DynamicEndPointInterceptor.DYNAMIC_BASE_URL)
                .build();
    }

    @Provides
    Api8 provideApi(@P8 Retrofit retrofit){
        MyLog.d(this, "Provide Api8. Retrofit: " + retrofit);
        return retrofit.create(Api8.class);
    }

}
