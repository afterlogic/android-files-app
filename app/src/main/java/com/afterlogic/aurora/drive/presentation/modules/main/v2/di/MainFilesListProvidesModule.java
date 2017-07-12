package com.afterlogic.aurora.drive.presentation.modules.main.v2.di;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;

import org.greenrobot.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

@Module
public class MainFilesListProvidesModule {

    @Provides
    @SubModuleScope
    EventBus eventBus() {
        return new EventBus();
    }
}
