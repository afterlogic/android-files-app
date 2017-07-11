package com.afterlogic.aurora.drive.presentation.modules.main.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ViewDataBinding;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.databinding.ActivityMainBinding;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.UnbindableObservable;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesListFragment;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.BaseFilesMVVMActivity;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.view.FilesListCallback;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesViewModel;
import com.annimon.stream.Stream;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesActivity extends BaseFilesMVVMActivity<MainFilesViewModel> implements FilesListCallback, FileListFragmentCallback, MainFilesView {

    private ActivityMainBinding mBinding;
    private MenuItem mLogoutMenuItem;
    private MenuItem mMultiChoseMenuItem;
    private ActionMode mMultiChoiseActionMode;
    private SearchView mSearchView;

    private boolean mMultichoseVisible = true;

    public static Intent intent(Context context){
        return new Intent(context, MainFilesActivity.class);
    }

    @Override
    public void assembly(InjectorsComponent modulesFactory) {
        modulesFactory.main().inject(this);
    }

    @Override
    public BaseFilesListFragment getFilesContent(String type) {
        return new MainFileListFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
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
    protected void onBindCreatedBindings(MainFilesViewModel vm, UnbindableObservable.Bag bag) {
        super.onBindCreatedBindings(vm, bag);
        bind(vm.getLogin(), this::updateLogoutMenuByViewModel, bag);
        bind(vm.getSelectedCount(), this::updateMultiChoiseCount, bag);
        bind(vm.getSelectedHasFolder(), this::updateMultiChoiseAvailableActions, bag);
        bind(vm.getMultichoiseMode(), this::updateMultiChoiseMode, bag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mLogoutMenuItem = menu.findItem(R.id.action_logout);
        mMultiChoseMenuItem = menu.findItem(R.id.action_multichoise);
        mMultiChoseMenuItem.setVisible(mMultichoseVisible);
        updateLogoutMenuByViewModel(getViewModel().getLogin());

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchMenuItem.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                getViewModel().onLogout();
                return true;
            case R.id.action_multichoise:
                getViewModel().setMultichoiseMode(true);
                return true;
            case R.id.action_offline_mode:
                getViewModel().onOfflineModeSelected();
                return true;
            case R.id.action_about:
                getViewModel().onAbout();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOpenFolder(AuroraFile folder) {
        getViewModel().onCurrentFolderChanged(folder);
    }

    @Override
    public void onBackPressed() {
        if (mSearchView != null && !mSearchView.isIconified()) {
            mSearchView.setIconified(true);
            return;
        }
        if (collapseFabAction()) return;
        if (getCurrentFragment() != null && getCurrentFragment().onBackPressed()) return;
        if (getViewModel().onBackPressed()) return;

        super.onBackPressed();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    @Override
    public void onSelectedFilesChanged(int selected, boolean hasFolder) {
        getViewModel().setSelectedCount(selected);
        getViewModel().setSetSelectedHasFolder(hasFolder);
    }

    @Override
    public void onActionsEnabledChanged(boolean enabled) {
        if (mMultiChoseMenuItem != null) {
            mMultiChoseMenuItem.setVisible(enabled);
        }
        mMultichoseVisible = enabled;
    }

    private void updateLogoutMenuByViewModel(ObservableField<String> login){
        if (mLogoutMenuItem == null) return;
        mLogoutMenuItem.setTitle(getString(R.string.logout, login.get()));
    }

    private void updateMultiChoiseMode(ObservableBoolean field){
        boolean active = field.get();
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

    private void updateMultiChoiseCount(ObservableInt field){
        if (mMultiChoiseActionMode == null) return;

        int count = field.get();
        String title = getString(R.string.title_action_selected, count);
        mMultiChoiseActionMode.setTitle(title);
    }

    private void updateMultiChoiseAvailableActions(ObservableBoolean field){
        if (mMultiChoiseActionMode == null) return;

        boolean hasFolder = field.get();
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
                updateMultiChoiseAvailableActions(getViewModel().getSelectedHasFolder());
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                ifCurrentFragment(fragment -> fragment.onOptionsItemSelected(item));
                getViewModel().setMultichoiseMode(false);
                mode.finish();
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mMultiChoiseActionMode = null;
                getViewModel().setMultichoiseMode(false);
            }
        });
        updateMultiChoiseCount(getViewModel().getSelectedCount());
    }
}
