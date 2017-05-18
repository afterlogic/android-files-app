package com.afterlogic.aurora.drive.presentation.modules.upload.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.OldBaseInjector;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadActivity;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadView;

import javax.inject.Inject;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadInjector extends OldBaseInjector<UploadActivity, UploadView, UploadModule> implements Injector<UploadActivity> {

    @Inject UploadInjector(ModulesComponentCreator component) {
        super(component);
    }

    @NonNull
    @Override
    protected UploadModule createModule() {
        return new UploadModule();
    }
}
