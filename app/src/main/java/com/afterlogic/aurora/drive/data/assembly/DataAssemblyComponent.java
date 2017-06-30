package com.afterlogic.aurora.drive.data.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.DataScope;
import com.afterlogic.aurora.drive.data.common.db.DataBaseModule;
import com.afterlogic.aurora.drive.data.common.db.DataBaseProvider;
import com.afterlogic.aurora.drive.data.modules.prefs.AppPrefs;
import com.afterlogic.aurora.drive.presentation.assembly.presentation.PresentationAssemblyComponent;

import dagger.Subcomponent;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * See {@link DataModule}.
 */
@DataScope
@Subcomponent(modules = {
        DataBaseModule.class,
        DataModule.class
})
public interface DataAssemblyComponent {

    PresentationAssemblyComponent presentationAssembly();

    AppPrefs prefs();
    DataBaseProvider dataBase();
}
