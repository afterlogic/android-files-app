package com.afterlogic.aurora.drive.core.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.CoreScope;
import com.afterlogic.aurora.drive.core.common.resourceProvders.AppStringProvider;
import com.afterlogic.aurora.drive.core.common.resourceProvders.AppStringProviderImpl;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;

import dagger.Module;
import dagger.Provides;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Core layer module.
 */
@Module
public class CoreAssemblyModule {

    @Provides @CoreScope
    ObservableScheduler provideObservableScheduler(){
        return new ObservableScheduler(Schedulers.io(), AndroidSchedulers.mainThread());
    }

    @Provides @CoreScope
    AppStringProvider provideAppStringProvider(AppStringProviderImpl stringProvider){
        return stringProvider;
    }
}
