package com.afterlogic.aurora.drive.presentation.modules.main.v2.di;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.di.ForViewInteractor;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module
public class MainProvidesModule {

    @Provides
    @ModuleScope
    @ForViewInteractor
    EventBus eventBus() {
        return new EventBus();
    }
}
