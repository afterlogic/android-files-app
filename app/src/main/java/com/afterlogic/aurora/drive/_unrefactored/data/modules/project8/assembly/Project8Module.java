package com.afterlogic.aurora.drive._unrefactored.data.modules.project8.assembly;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive._unrefactored.core.annotations.qualifers.Project8;
import com.afterlogic.aurora.drive._unrefactored.data.common.DynamicDomainProvider;
import com.afterlogic.aurora.drive._unrefactored.data.common.DynamicEndPointInterceptor;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.ApiCheckRepository;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.FilesRepository;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.UserRepository;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.common.Api8;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.common.UploadInterceptor;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.common.converter.ApiResponseConverter8;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.checker.repository.ApiCheckRepository8Impl;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.checker.service.ApiCheckerServiceP8;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.checker.service.ApiCheckerServiceP8Impl;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.file.repository.FilesRepositoryP8Impl;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.file.service.FilesServiceP8;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.file.service.FilesServiceP8Impl;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.user.repository.UserRepositoryP8Impl;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.user.service.AuthServiceP8;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.modules.user.service.AuthServiceP8Impl;
import com.afterlogic.aurora.drive._unrefactored.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module
public class Project8Module{

    @Provides @Project8
    OkHttpClient provideClient(DynamicDomainProvider domainProvider){
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        //Dynamic notifyEnd point interceptor
        clientBuilder.addInterceptor(new DynamicEndPointInterceptor(
                DynamicEndPointInterceptor.DYNAMIC_BASE_URL,
                domainProvider
        ));

        //Upload files interceptor
        UploadInterceptor uploadInterceptor = new UploadInterceptor();
        clientBuilder.addInterceptor(uploadInterceptor);

        //Add logging for debug
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(interceptor);
        }
        return clientBuilder.build();
    }

    @Provides @Project8
    Gson provideGson(){
        Gson gson = new Gson();
        return new GsonBuilder()
                .registerTypeAdapter(ApiResponseP8.class, new ApiResponseConverter8(gson))
                .create();
    }

    @Provides @Project8
    Retrofit provideRetrofit(@Project8 OkHttpClient client, @Project8 Gson gson){
        MyLog.d(this, "Provide retrofit.");
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .baseUrl(DynamicEndPointInterceptor.DYNAMIC_BASE_URL)
                .build();
    }

    @Provides
    Api8 provideApi(@Project8 Retrofit retrofit){
        MyLog.d(this, "Provide Api8. Retrofit: " + retrofit);
        return retrofit.create(Api8.class);
    }

    @Provides
    AuthServiceP8 provideAuthService(AuthServiceP8Impl authServiceP8){
        MyLog.d(this, "Provide auth service: " + authServiceP8);
        return authServiceP8;
    }

    @Provides @Project8
    UserRepository provideUserRepository(UserRepositoryP8Impl userRepositoryP8){
        MyLog.d(this, "Provide user repository: " + userRepositoryP8);
        return userRepositoryP8;
    }

    @Provides @Project8
    ApiCheckRepository provideCheckRepository(ApiCheckRepository8Impl repository8){
        return repository8;
    }

    @Provides
    FilesServiceP8 provideFilesService(FilesServiceP8Impl serviceP8){
        return serviceP8;
    }

    @Provides @Project8
    FilesRepository provideFileRepository(FilesRepositoryP8Impl repositoryP8){
        return repositoryP8;
    }

    @Provides
    ApiCheckerServiceP8 provideCheckerService(ApiCheckerServiceP8Impl serviceP8){
        return serviceP8;
    }
}
