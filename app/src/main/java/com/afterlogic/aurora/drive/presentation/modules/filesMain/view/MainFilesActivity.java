package com.afterlogic.aurora.drive.presentation.modules.filesMain.view;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.databinding.ActivityMainFilesBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.presenter.MainFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel.MainFilesViewModel;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesActivity extends BaseActivity implements MainFilesView, FileActionCallback {

    @Inject @ViewPresenter
    protected MainFilesPresenter mPresenter;

    @Inject
    protected MainFilesViewModel mViewModel;

    private MainFilesPagerAdapter mAdapter;

    private final OnViewModelPropertyChangedCallback mPropertyChangedCallback = new OnViewModelPropertyChangedCallback();

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.filesMain().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainFilesBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main_files);

        setSupportActionBar(binding.toolbar);

        binding.tabs.setupWithViewPager(binding.viewpager, true);

        mAdapter = new MainFilesPagerAdapter(getSupportFragmentManager());
        binding.setAdapter(mAdapter);
        binding.setViewModel(mViewModel);

        updateTitle(mViewModel.getFolderTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(mViewModel.getLocked());
        mViewModel.addOnPropertyChangedCallback(mPropertyChangedCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.removeOnPropertyChangedCallback(mPropertyChangedCallback);
    }

    @Override
    public void onOpenFolder(AuroraFile folder) {
        mViewModel.getController().setCurrentFolder(folder);
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

    @Override
    public void onBackPressed() {
        if (getCurrentFragment() == null || !getCurrentFragment().onBackPressed()){
            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Nullable
    private FileListFragment getCurrentFragment(){
        if (mAdapter.getCount() == 0) return null;

        return mAdapter.getPrimaryFragment();
    }

    private void updateTitle(String title){
        if (TextUtils.isEmpty(title)){
            setTitle(getString(R.string.app_name));
        } else {
            setTitle(title);
        }
    }

    private class OnViewModelPropertyChangedCallback extends Observable.OnPropertyChangedCallback{

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            MainFilesViewModel viewModel = (MainFilesViewModel) observable;
            switch (i){

                case BR.locked:
                    boolean locked = viewModel.getLocked();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(locked);
                    break;

                case BR.folderTitle:
                    String title = viewModel.getFolderTitle();
                    updateTitle(title);
                    break;
            }
        }
    }
}
