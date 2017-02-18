package com.afterlogic.aurora.drive.presentation.modules.fileView.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.BaseInjector;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewActivity;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewPresentationView;

import javax.inject.Inject;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewInjector extends BaseInjector<FileViewActivity, FileViewPresentationView, FileViewModule> {

    @Inject FileViewInjector(AssembliesAssemblyComponent component) {
        super(component);
    }

    @NonNull
    @Override
    protected FileViewModule createModule() {
        return new FileViewModule();
    }
}
