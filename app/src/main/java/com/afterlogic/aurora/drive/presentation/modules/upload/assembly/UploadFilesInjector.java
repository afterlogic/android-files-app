package com.afterlogic.aurora.drive.presentation.modules.upload.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.OldBaseInjector;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesFragment;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesView;

import javax.inject.Inject;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadFilesInjector extends OldBaseInjector<UploadFilesFragment, UploadFilesView, UploadFilesModule> implements Injector<UploadFilesFragment> {

    @Inject UploadFilesInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected UploadFilesModule createModule() {
        return new UploadFilesModule();
    }
}
