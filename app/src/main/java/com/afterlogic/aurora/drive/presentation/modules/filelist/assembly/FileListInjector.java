package com.afterlogic.aurora.drive.presentation.modules.filelist.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.BaseInjector;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListView;

import javax.inject.Inject;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileListInjector extends BaseInjector<FileListFragment, FileListView, FileListModule> implements Injector<FileListFragment> {

    @Inject FileListInjector(AssembliesAssemblyComponent component) {
        super(component);
    }

    @NonNull
    @Override
    protected FileListModule createModule() {
        return new FileListModule();
    }
}
