package com.afterlogic.aurora.drive.presentation.modules.fileView.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.OldBaseInjector;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewActivity;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewPresentationView;

import javax.inject.Inject;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewInjector extends OldBaseInjector<FileViewActivity, FileViewPresentationView, FileViewModule> {

    @Inject FileViewInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected FileViewModule createModule() {
        return new FileViewModule();
    }
}
