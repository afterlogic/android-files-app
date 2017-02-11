package com.afterlogic.aurora.drive.presentation.modules.upload.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesListFragment;
import com.afterlogic.aurora.drive.presentation.modules.upload.presenter.UploadPresenter;
import com.afterlogic.aurora.drive.presentation.modules.upload.viewModel.UploadViewModel;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadActivity extends BaseFilesActivity<UploadViewModel, UploadPresenter> implements UploadView{

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.upload().inject(this);
    }

    @Override
    public BaseFilesListFragment getFilesContent(String type) {
        return new UploadFilesFragment();
    }

    @Override
    public ViewDataBinding onCreateBind() {
        return DataBindingUtil.setContentView(this, R.layout.activity_upload);
    }

}
