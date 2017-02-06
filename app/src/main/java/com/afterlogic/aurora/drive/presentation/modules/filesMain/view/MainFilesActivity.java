package com.afterlogic.aurora.drive.presentation.modules.filesMain.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments.FilesCallback;
import com.afterlogic.aurora.drive.databinding.ActivityMainFilesBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.presenter.MainFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel.MainFilesViewModel;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesActivity extends BaseActivity implements MainFilesView, FilesCallback {

    @Inject @ViewPresenter
    protected MainFilesPresenter mPresenter;

    @Inject
    protected MainFilesViewModel mViewModel;

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.filesMain().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainFilesBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main_files);
        binding.tabs.setupWithViewPager(binding.viewpager, true);

        binding.setFragmentManager(getSupportFragmentManager());
        binding.setViewModel(mViewModel);
    }

    @Override
    public void onOpenFolder(AuroraFile folder) {

    }

    @Override
    public void showActions(AuroraFile file) {

    }

    @Override
    public void onFileClicked(AuroraFile file, List<AuroraFile> allFiles) {

    }

    @Override
    public void createFolder(String path, String type, String folderName) {

    }
}
