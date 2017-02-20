package com.afterlogic.aurora.drive.presentation.modules.choise.view;

import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesListFragment;
import com.afterlogic.aurora.drive.presentation.modules.choise.model.presenter.ChoiseFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseFilesViewModel;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoiseFilesFragment extends BaseFilesListFragment<ChoiseFilesViewModel, ChoiseFilesPresenter> implements ChoiseFilesView {

    @Override
    protected void assembly(InjectorsComponent modulesFactory) {
        modulesFactory.choiseFileList().inject(this);
    }
}
