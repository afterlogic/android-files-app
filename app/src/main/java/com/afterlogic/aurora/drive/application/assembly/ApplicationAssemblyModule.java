package com.afterlogic.aurora.drive.application.assembly;

import android.content.Context;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.AppScope;

import dagger.Module;
import dagger.Provides;
import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Root Dagger component.
 * Provide application context, configurators.
 */
@Module
public class ApplicationAssemblyModule {

    private App mAppContext;

    public ApplicationAssemblyModule(App appContext) {
        mAppContext = appContext;
    }

    @Provides
    Context provideApplicationContext(){
        return mAppContext;
    }

    @Provides
    App application(){
        return mAppContext;
    }

    @Provides @AppScope
    Cicerone<AppRouter> cicerone() {
        return Cicerone.create(new AppRouter());
    }

    @Provides @AppScope
    NavigatorHolder navigatorHolder(Cicerone<AppRouter> cicerone) {
        return cicerone.getNavigatorHolder();
    }

    @Provides @AppScope
    Router router(Cicerone<AppRouter> cicerone) {
        return cicerone.getRouter();
    }
}
