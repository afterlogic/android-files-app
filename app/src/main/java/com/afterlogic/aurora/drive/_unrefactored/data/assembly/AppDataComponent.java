package com.afterlogic.aurora.drive._unrefactored.data.assembly;

import com.afterlogic.aurora.drive._unrefactored.core.annotations.scoupes.AppScoupe;
import com.afterlogic.aurora.drive._unrefactored.data.common.ApiProvider;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project7.assembly.P7DataMappersModule;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project7.assembly.Project7Module;
import com.afterlogic.aurora.drive._unrefactored.data.modules.project8.assembly.Project8Module;

import dagger.Component;

/**
 * Created by sashka on 11.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
@AppScoupe
@Component(modules = {
        DataModule.class,
        Project7Module.class,
        P7DataMappersModule.class,
        Project8Module.class
})
public interface AppDataComponent {
    void inject(ApiProvider provider);
}
