package com.afterlogic.aurora.drive.presentation.modules.upload.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesFragment;
import com.afterlogic.aurora.drive.presentation.modules.upload.view.UploadFilesView;

import dagger.Subcomponent;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = UploadFilesModule.class)
public interface UploadFilesComponent extends PresentationComponent<UploadFilesView, UploadFilesFragment> {
}
