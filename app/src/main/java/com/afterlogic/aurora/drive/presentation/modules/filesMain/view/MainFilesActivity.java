package com.afterlogic.aurora.drive.presentation.modules.filesMain.view;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.databinding.ActivityMainFilesBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.wireframes.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseActivity;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListFragment;
import com.afterlogic.aurora.drive.presentation.modules.filelist.view.FileListFragmentCallback;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.presenter.MainFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel.MainFilesViewModel;

import javax.inject.Inject;

import static android.R.attr.fragment;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesActivity extends BaseActivity implements MainFilesView, MainFilesCallback, FileListFragmentCallback {

    @Inject @ViewPresenter
    protected MainFilesPresenter mPresenter;

    @Inject
    protected MainFilesViewModel mViewModel;

    private MainFilesPagerAdapter mAdapter;

    private final OnViewModelPropertyChangedCallback mPropertyChangedCallback = new OnViewModelPropertyChangedCallback();

    private ActivityMainFilesBinding mBinding;
    private MenuItem mLogoutMenuItem;

    private ActionMode mMultiChoiseActionMode;

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.filesMain().inject(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_files);
        setSupportActionBar(mBinding.toolbar);

        mBinding.tabs.setupWithViewPager(mBinding.viewpager, true);

        mAdapter = new MainFilesPagerAdapter(getSupportFragmentManager());
        mBinding.setAdapter(mAdapter);
        mBinding.setViewModel(mViewModel);

        //[START Init FAB menu]
        mBinding.uploadFile.setOnClickListener(view -> fabAction(FileListFragment::uploadFile));
        mBinding.createFolder.setOnClickListener(view -> fabAction(FileListFragment::createFolder));
        mBinding.fabCollapser.setOnTouchListener((v, event) -> collapseFabAction());
        //[END Init FAB menu]

        getSupportActionBar().setDisplayHomeAsUpEnabled(mViewModel.getLocked());

        updateTitleByViewModel();
        updateHomeButtonByViewModel();
        updateMultiChoiseMode();
        mViewModel.addOnPropertyChangedCallback(mPropertyChangedCallback);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mLogoutMenuItem = menu.findItem(R.id.action_logout);
        updateLogoutMenuByViewModel();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                mPresenter.onLogout();
                return true;
            case R.id.action_multichoise:
                mViewModel.getModel().setMultiChoiseMode(true);
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.removeOnPropertyChangedCallback(mPropertyChangedCallback);
    }

    @Override
    public void onOpenFolder(AuroraFile folder) {
        mViewModel.getModel().setCurrentFolder(folder);
    }

    @Override
    public void onBackPressed() {
        if (collapseFabAction()) return;
        if (getCurrentFragment() != null && getCurrentFragment().onBackPressed()) return;
        if (mPresenter.onBackPressed()) return;

        super.onBackPressed();
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

    private void updateTitleByViewModel(){
        String title = mViewModel.getFolderTitle();
        if (title == null){
            setTitle(getString(R.string.app_name));
        } else {
            setTitle(title);
        }
    }

    private void updateLogoutMenuByViewModel(){
        if (mLogoutMenuItem == null) return;
        mLogoutMenuItem.setTitle(getString(R.string.logout, mViewModel.getLogin()));
    }

    private void updateHomeButtonByViewModel(){
        ActionBar ab = getSupportActionBar();

        if (ab == null) return;

        ab.setDisplayHomeAsUpEnabled(mViewModel.getLocked());
    }

    private void updateMultiChoiseMode(){
        boolean active = mViewModel.getMultichoiseMode();
        if (active){
            if (mMultiChoiseActionMode == null){
                startMultiChoseActionMode();
            }
        } else {
            if (mMultiChoiseActionMode != null){
                mMultiChoiseActionMode.finish();
            }
        }

        ifCurrentFragment(fragment -> fragment.setMultiChoiseMode(active));
    }

    private void updateMultiChoiseCount(){
        if (mMultiChoiseActionMode == null) return;

        mMultiChoiseActionMode.setTitle(getString(R.string.title_action_selected, mViewModel.getSelectedCount()));
    }

    private void ifCurrentFragment(Consumer<FileListFragment> fragmentConsumer){
        FileListFragment fragment = getCurrentFragment();
        if (fragment != null){
            fragmentConsumer.consume(fragment);
        }
    }

    private boolean collapseFabAction(){
        if (mBinding == null || !mBinding.addMenu.isExpanded()) return false;
        mBinding.addMenu.collapse();
        return true;
    }

    private void fabAction(Consumer<FileListFragment> fragmentConsumer){
        collapseFabAction();
        ifCurrentFragment(fragmentConsumer);
    }

    private void startMultiChoseActionMode(){
        mMultiChoiseActionMode = startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_multichoise, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                ifCurrentFragment(fragment -> fragment.onOptionsItemSelected(item));
                mPresenter.onMultiChoiseAction();
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mMultiChoiseActionMode = null;
                mViewModel.getModel().setMultiChoiseMode(false);
            }
        });
        updateMultiChoiseCount();
    }

    @Override
    public void onSelectedCountChanged(int selected) {
        mViewModel.getModel().setSelectedCount(selected);
    }

    private class OnViewModelPropertyChangedCallback extends Observable.OnPropertyChangedCallback{

        @SuppressWarnings("ConstantConditions")
        @Override
        public void onPropertyChanged(Observable observable, int i) {
            switch (i){
                case BR.locked: updateHomeButtonByViewModel(); break;
                case BR.folderTitle: updateTitleByViewModel(); break;
                case BR.login: updateLogoutMenuByViewModel(); break;
                case BR.multichoiseMode: updateMultiChoiseMode(); break;
                case BR.selectedCount: updateMultiChoiseCount(); break;
            }
        }
    }
}
