package com.afterlogic.aurora.drive.presentation.assembly.assemblies;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationModulesStore;
import com.afterlogic.aurora.drive.presentation.modules.filelist.assembly.FileListComponent;
import com.afterlogic.aurora.drive.presentation.modules.filelist.assembly.FileListModule;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.assembly.MainFilesComponent;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.assembly.MainFilesModule;
import com.afterlogic.aurora.drive.presentation.modules.login.assembly.LoginComponent;
import com.afterlogic.aurora.drive.presentation.modules.login.assembly.LoginModule;
import com.afterlogic.aurora.drive.presentation.modules.start.assembly.StartComponent;
import com.afterlogic.aurora.drive.presentation.modules.start.assembly.StartModule;

import dagger.Subcomponent;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Presentation's dagger modules component creator.
 *
 * NOTE:
 * For default component creation method in BaseWireframe all methods must be called 'plus'.
 */
@Subcomponent(modules = AssembliesAssemblyModule.class)
public interface AssembliesAssemblyComponent {

    PresentationModulesStore store();

    LoginComponent plus(LoginModule module);

    StartComponent plus(StartModule module);

    MainFilesComponent plus(MainFilesModule module);

    FileListComponent plus(FileListModule module);
}
