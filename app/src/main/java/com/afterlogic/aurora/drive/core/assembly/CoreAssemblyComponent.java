package com.afterlogic.aurora.drive.core.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.CoreScope;
import com.afterlogic.aurora.drive.data.assembly.DataAssemblyComponent;
import com.afterlogic.aurora.drive.data.assembly.DataModule;

import dagger.Subcomponent;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * See {@link CoreAssemblyModule}.
 */
@CoreScope
@Subcomponent(modules = {CoreAssemblyModule.class})
public interface CoreAssemblyComponent {
    DataAssemblyComponent plus(DataModule module);
}
