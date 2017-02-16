package com.afterlogic.aurora.drive.presentation.modules.fileView.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewImageItemFragment;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewImageItemView;

import dagger.Subcomponent;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@SubModuleScope
@Subcomponent(modules = FileViewImageItemModule.class)
public interface FileViewImageItemComponent extends PresentationComponent<FileViewImageItemView, FileViewImageItemFragment>{
}
