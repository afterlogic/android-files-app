package com.afterlogic.aurora.drive.presentation.modules.filelist.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListView;

import dagger.Subcomponent;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = FileListModule.class)
public interface FileListComponent extends PresentationComponent<FileListView, FileListFragment> {
}
