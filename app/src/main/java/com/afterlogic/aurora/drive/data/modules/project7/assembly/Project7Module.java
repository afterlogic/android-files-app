package com.afterlogic.aurora.drive.data.modules.project7.assembly;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.core.annotations.qualifers.Project7;
import com.afterlogic.aurora.drive.data.common.DynamicDomainProvider;
import com.afterlogic.aurora.drive.data.common.DynamicEndPointInterceptor;
import com.afterlogic.aurora.drive.data.common.repository.ApiCheckRepository;
import com.afterlogic.aurora.drive.data.common.repository.FilesRepository;
import com.afterlogic.aurora.drive.data.common.repository.UserRepository;
import com.afterlogic.aurora.drive.data.modules.project7.common.Api7;
import com.afterlogic.aurora.drive.data.modules.project7.common.ApiResponseConverter7;
import com.afterlogic.aurora.drive.data.modules.project7.common.DownloadInterceptor;
import com.afterlogic.aurora.drive.data.modules.project7.common.UploadInterceptor;
import com.afterlogic.aurora.drive.data.modules.project7.modules.checker.repository.ApiCheckRepository7Impl;
import com.afterlogic.aurora.drive.data.modules.project7.modules.checker.service.ApiCheckerServiceP7;
import com.afterlogic.aurora.drive.data.modules.project7.modules.checker.service.ApiCheckerServiceP7Impl;
import com.afterlogic.aurora.drive.data.modules.project7.modules.files.repository.FilesRepository7Impl;
import com.afterlogic.aurora.drive.data.modules.project7.modules.files.service.FilesServiceP7;
import com.afterlogic.aurora.drive.data.modules.project7.modules.files.service.FilesServiceP7Impl;
import com.afterlogic.aurora.drive.data.modules.project7.modules.user.repository.UserRepositoryP7Impl;
import com.afterlogic.aurora.drive.data.modules.project7.modules.user.service.AuthServiceP7;
import com.afterlogic.aurora.drive.data.modules.project7.modules.user.service.AuthServiceP7Impl;
import com.afterlogic.aurora.drive.model.project7.ApiResponseP7;
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
 * Created by sashka on 17.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Module()
public class Project7Module {

    @Provides @Project7
    OkHttpClient provideClient(DynamicDomainProvider domainProvider){
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        //Dynamic notifyEnd point interceptor
        clientBuilder.addInterceptor(new DynamicEndPointInterceptor(
                DynamicEndPointInterceptor.DYNAMIC_BASE_URL,
                domainProvider
        ));

        clientBuilder.addInterceptor(new DownloadInterceptor());
        clientBuilder.addInterceptor(new UploadInterceptor());

        //Add logging for debug
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(interceptor);
        }
        return clientBuilder.build();
    }

    @Provides @Project7
    Gson provideGson(){
        Gson gson = new Gson();
        return new GsonBuilder()
                .registerTypeAdapter(ApiResponseP7.class, new ApiResponseConverter7(gson))
                .create();
    }

    @Provides @Project7
    Retrofit provideRetrofit(@Project7 Gson gson, @Project7 OkHttpClient client){
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .baseUrl(DynamicEndPointInterceptor.DYNAMIC_BASE_URL)
                .build();
    }

    @Provides
    Api7 provideApi(@Project7 Retrofit retrofit){
        return retrofit.create(Api7.class);
    }

    @Provides
    AuthServiceP7 provideAuthService(AuthServiceP7Impl authServiceP7){
        return authServiceP7;
    }

    @Provides
    FilesServiceP7 provideFileService(FilesServiceP7Impl serivce){
        return serivce;
    }

    @Provides @Project7
    UserRepository provideUserRepository(UserRepositoryP7Impl userRepositoryP7){
        return userRepositoryP7;
    }

    @Provides @Project7
    ApiCheckRepository provideApiCheckRepository(ApiCheckRepository7Impl repository7){
        return repository7;
    }

    @Provides @Project7
    FilesRepository provideFilesRepository(FilesRepository7Impl repository7){
        return repository7;
    }

    @Provides
    ApiCheckerServiceP7 provideApiCheckerService(ApiCheckerServiceP7Impl serviceP7){
        return serviceP7;
    }
}
