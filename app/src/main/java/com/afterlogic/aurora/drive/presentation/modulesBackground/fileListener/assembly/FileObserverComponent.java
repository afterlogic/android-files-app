package com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.assembly;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverService;
import com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.view.FileObserverView;

import dagger.Subcomponent;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
@Subcomponent(modules = FileObserverModule.class)
public interface FileObserverComponent extends PresentationComponent<FileObserverView, FileObserverService> {
}
