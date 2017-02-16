package com.afterlogic.aurora.drive.presentation.modules.fileView.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseFragment;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.fileView.presenter.FileVIewImageItemPresenter;
import com.afterlogic.aurora.drive.presentation.modules.fileView.viewModel.FileViewImageItemViewModel;

import javax.inject.Inject;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewImageItemFragment extends BaseFragment implements FileViewImageItemView {

    @Inject
    protected FileViewImageItemViewModel mViewModel;

    @Inject @ViewPresenter
    protected FileVIewImageItemPresenter mPresenter;

    public static FileViewImageItemFragment newInstance(AuroraFile file) {

        Bundle args = new Bundle();
        args.putParcelable("file", file);
        FileViewImageItemFragment fragment = new FileViewImageItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.fileViewImageItem().inject(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            mViewModel.viewCreatedWith(getArguments().getParcelable("file"));
        }
    }
}
