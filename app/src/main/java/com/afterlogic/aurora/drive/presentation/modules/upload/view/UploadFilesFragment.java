package com.afterlogic.aurora.drive.presentation.modules.upload.view;

import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesListFragment;
import com.afterlogic.aurora.drive.presentation.modules.upload.presenter.UploadFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadFilesViewModel;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadFilesFragment extends BaseFilesListFragment<UploadFilesViewModel, UploadFilesPresenter> implements UploadFilesView{

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.uploadFileList().inject(this);
    }
}
