package com.afterlogic.aurora.drive.presentation.modules.fileView.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewActivity;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewPresentationView;

import dagger.Subcomponent;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = FileViewModule.class)
public interface FileViewComponent extends PresentationComponent<FileViewPresentationView, FileViewActivity>, FileViewImageSubcomponentCreator {
    FileViewImageItemComponent plus(FileViewImageItemModule module);
}
