package com.afterlogic.aurora.drive.presentation.modules.main.view;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.databinding.ActivityMainBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesActivity;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesListFragment;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.FilesListCallback;
import com.afterlogic.aurora.drive.presentation.modules.main.presenter.MainFilesPresenter;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesModel;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesViewModel;
import com.annimon.stream.Stream;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesActivity extends BaseFilesActivity<MainFilesViewModel, MainFilesPresenter> implements MainFilesView, FilesListCallback, FileListFragmentCallback {

    private ActivityMainBinding mBinding;
    private MenuItem mLogoutMenuItem;
    private ActionMode mMultiChoiseActionMode;

    private final SimpleListener mLoginListener = new SimpleListener(this::updateLogoutMenuByViewModel);
    private final SimpleListener mMultiChoseListener = new SimpleListener(this::updateMultiChoiseMode);
    private final SimpleListener mSelectedCountListener = new SimpleListener(this::updateMultiChoiseCount);
    private final SimpleListener mSelectedFolderListener = new SimpleListener(this::updateMultiChoiseAvailableActions);

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.main().inject(this);
    }

    @Override
    public BaseFilesListFragment getFilesContent(String type) {
        return new MainFileListFragment();
    }

    @Override
    public ViewDataBinding onCreateBind() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //[START Init FAB menu]
        mBinding.uploadFile.setOnClickListener(view -> fabAction(MainFileListFragment::uploadFile));
        mBinding.createFolder.setOnClickListener(view -> fabAction(MainFileListFragment::createFolder));
        mBinding.fabCollapser.setOnTouchListener((v, event) -> collapseFabAction());
        //[END Init FAB menu]
        return mBinding;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel.getLogin().addOnPropertyChangedCallback(mLoginListener);
        mViewModel.getMultichoiseMode().addOnPropertyChangedCallback(mMultiChoseListener);
        mViewModel.getSelectedCount().addOnPropertyChangedCallback(mSelectedCountListener);
        mViewModel.getSelectedHasFolder().addOnPropertyChangedCallback(mSelectedFolderListener);
        updateMultiChoiseMode();
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
        mViewModel.getLogin().removeOnPropertyChangedCallback(mLoginListener);
        mViewModel.getMultichoiseMode().removeOnPropertyChangedCallback(mMultiChoseListener);
        mViewModel.getSelectedCount().removeOnPropertyChangedCallback(mSelectedCountListener);
        mViewModel.getSelectedHasFolder().removeOnPropertyChangedCallback(mSelectedFolderListener);
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

    @Override
    public void onSelectedFilesChanged(int selected, boolean hasFolder) {
        MainFilesModel model = mViewModel.getModel();
        model.setSelectedCount(selected);
        model.setSetSelectedHasFolder(hasFolder);
    }

    private void updateLogoutMenuByViewModel(){
        if (mLogoutMenuItem == null) return;
        mLogoutMenuItem.setTitle(getString(R.string.logout, mViewModel.getLogin().get()));
    }

    private void updateMultiChoiseMode(){
        boolean active = mViewModel.getMultichoiseMode().get();
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

        int count = mViewModel.getSelectedCount().get();
        String title = getString(R.string.title_action_selected, count);
        mMultiChoiseActionMode.setTitle(title);
    }

    private void updateMultiChoiseAvailableActions(){
        if (mMultiChoiseActionMode == null) return;

        boolean hasFolder = mViewModel.getSelectedHasFolder().get();
        Menu menu = mMultiChoiseActionMode.getMenu();
        Stream.of(R.id.action_offline, R.id.action_download, R.id.action_send)
                .map(menu::findItem)
                .filter(ObjectsUtil::nonNull)
                .forEach(item -> item.setVisible(!hasFolder));
    }

    private void ifCurrentFragment(Consumer<MainFileListFragment> fragmentConsumer){
        MainFileListFragment fragment = (MainFileListFragment) getCurrentFragment();
        if (fragment != null){
            fragmentConsumer.consume(fragment);
        }
    }

    private boolean collapseFabAction(){
        if (mBinding == null || !mBinding.addMenu.isExpanded()) return false;
        mBinding.addMenu.collapse();
        return true;
    }

    private void fabAction(Consumer<MainFileListFragment> fragmentConsumer){
        collapseFabAction();
        ifCurrentFragment(fragmentConsumer);
    }

    private void startMultiChoseActionMode(){
        mMultiChoiseActionMode = startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_multichoise, menu);
                updateMultiChoiseAvailableActions();
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
}
