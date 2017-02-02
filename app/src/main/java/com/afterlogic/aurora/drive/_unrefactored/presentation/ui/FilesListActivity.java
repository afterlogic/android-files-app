package com.afterlogic.aurora.drive._unrefactored.presentation.ui;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.DownloadType;
import com.afterlogic.aurora.drive._unrefactored.core.util.FileUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.IntentUtil;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiCallback;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiError;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.adapters.FilesAdapter;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.dialogs.FileActionsBottomSheet;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments.FilesCallback;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments.FilesListFragment;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments.FilesRootFragment;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments.OfflineFilesFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FilesListActivity extends BaseFileActionActivity implements
        FilesCallback,
        FileActionsBottomSheet.FileActionListener,
        FilesRootFragment.FilesRootFragmentCallback,
        FilesAdapter.MultichoiseListener
{

    public static final String EXTRA_FOLDER_TITLE =
            FilesListActivity.class.getName() + ".EXTRA_FOLDER_TITLE";
    public static final String EXTRA_IS_ROOT =
            FilesListActivity.class.getName() + ".EXTRA_IS_ROOT";

    private static final String KEY_MODE = FilesListActivity.class.getName() + ".KEY_MODE";
    protected static final int MODE_ONLINE = 0;
    protected static final int MODE_OFFLINE_NETWORK = 1;
    protected static final int MODE_OFFLINE_MANUAL = 2;

    //UI References
    private TabLayout mTabLayout;
    protected FilesRootFragment mFilesRootFragment;
    private MenuItem mMultichoiseActionItem;
    protected MenuItem mOfflineModeMenuItem;
    protected MenuItem mOnlineModeMenuItem;

    private String mFolderTitle;
    private boolean mIsRoot = true;
    private boolean mMultichoiseAllowed = true;

    //Triggered in MultichoiseMenu constructor
    private MultichoiseMenu mMultichoiseMenu;
    private List<AuroraFile> mSelectedFiles = new ArrayList<>();

    private int mCurrentMode = MODE_ONLINE;

    ////////////////////////////////////////////////
    // [START Override super class] // <editor-fold desc="Override super class">
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Lifecycle] // <editor-fold desc="Lifecycle">
    ////////////////////////////////////////////////

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        if (savedInstanceState != null) {
            mFolderTitle = savedInstanceState.getString(EXTRA_FOLDER_TITLE, null);
            mIsRoot = savedInstanceState.getBoolean(EXTRA_IS_ROOT, true);
            mCurrentMode = savedInstanceState.getInt(KEY_MODE, MODE_ONLINE);
        }

        if (mFilesRootFragment == null){
            Fragment fragment = getCurrentFragment();
            if (fragment instanceof FilesRootFragment){
                mFilesRootFragment = (FilesRootFragment) fragment;
            }
        }

        //TODO: Check real mode and content state

        updateActionBar(mIsRoot, mFolderTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mMultichoiseActionItem = menu.findItem(R.id.action_multichoise);
        mMultichoiseActionItem.setVisible(mMultichoiseAllowed);

        mOfflineModeMenuItem = menu.findItem(R.id.action_offline_mode);
        mOnlineModeMenuItem = menu.findItem(R.id.action_online_mode);

        updateOfflineModeMenus(mCurrentMode);

        MenuItem logout = menu.findItem(R.id.action_logout);
        AuroraSession session = Api.getCurrentSession();
        if (session != null) {
            logout.setTitle(getString(R.string.logout, session.getLogin()));
        }else{
            logout.setTitle(getString(R.string.logout, ""));
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_FOLDER_TITLE, mFolderTitle);
        outState.putBoolean(EXTRA_IS_ROOT, mIsRoot);
        outState.putInt(KEY_MODE, mCurrentMode);
    }

    ////////////////////////////////////////////////
    // [END Lifecycle] // </editor-fold>
    ////////////////////////////////////////////////

    @Override
    public int getActivityLayout() {
        return R.layout.activity_tabs;
    }

    @Nullable
    @Override
    public Fragment onCreateFragment() {
        mFilesRootFragment = getOnlineContentFragment();
        return mFilesRootFragment;
    }

    @Override
    public boolean isCatchLoginFail() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_multichoise:
                if (mFilesRootFragment != null){
                    FilesListFragment listFragment = mFilesRootFragment.getCurrentListFragment();
                    if (listFragment != null && !listFragment.isRefreshing()){
                        startSupportActionMode(new MultichoiseMenu());
                    } else {
                        Toast.makeText(this, R.string.prompt_refreshing_files_list, Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.action_offline_mode:
                mCurrentMode = MODE_OFFLINE_MANUAL;
                showOfflineState();
                break;
            case R.id.action_online_mode:
                showOnlineState();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFilesRootFragment != null){
            mFilesRootFragment.refreshAllFolders();
        }
    }

    @Override
    protected void onPreviousTaskNotRestored() {
        super.onPreviousTaskNotRestored();
        if (mFilesRootFragment != null){
            mFilesRootFragment.refreshFolderByType(null);
        }
    }

    @Override
    protected void onFileSuccessOfflineChanged(AuroraFile file, boolean offline) {
        mFilesRootFragment.notifyDataSetChanged();
    }

    @Override
    protected void onFilesSuccessDeleted(List<AuroraFile> files) {
        if (files.size() == 0) return;
        mFilesRootFragment.refreshFolderByType(files.get(0).getType());
    }

    @Override
    protected void onFileSuccessRenamed(AuroraFile file) {
        if (file == null) {
            mFilesRootFragment.notifyDataSetChanged();
        } else {
            mFilesRootFragment.refreshFolderByType(file.getType());
        }
    }

    @Override
    protected void onUploadFinished(boolean success, AuroraFile uploadFolder) {
        if (mFilesRootFragment != null && uploadFolder != null){
            mFilesRootFragment.refreshFolderByType(uploadFolder.getType());
        }
    }

    @Override
    protected void onUploadTaskCancelled() {
        if (mFilesRootFragment != null){
            mFilesRootFragment.refreshCurrentFolder();
        }
    }

    ////////////////////////////////////////////////
    // [END Override super class] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Implementation] // <editor-fold desc="Implementation">
    ////////////////////////////////////////////////

    /**
     * {@link FilesRootFragment.FilesRootFragmentCallback#getFilesTabLayout()}  implementation.
     */
    @Override
    public TabLayout getFilesTabLayout() {
        return mTabLayout;
    }

    /**
     * {@link FilesRootFragment.FilesRootFragmentCallback#requestFileUpload(AuroraFile)}  implementation.
     */
    @SuppressLint("InlinedApi")
    @Override
    public void requestFileUpload(AuroraFile folder) {
        uploadFile(folder);
    }

    /**
     * {@link FilesCallback#onOpenFolder(AuroraFile)}  implementation.
     */
    @Override
    public void onOpenFolder(AuroraFile folder) {
        mIsRoot = "".equals(folder.getFullPath());
        mFolderTitle = folder.getName();
        updateActionBar(mIsRoot, mFolderTitle);
        mFilesRootFragment.onOpenFolder(folder);
    }

    /**
     * {@link FilesCallback#showActions(AuroraFile)}  implementation.
     */
    @Override
    public void showActions(AuroraFile file) {
        FileActionsBottomSheet actions = FileActionsBottomSheet.newInstance(file);
        actions.show(getSupportFragmentManager(), "file_actions");
    }

    /**
     * {@link FilesCallback#onFileClicked(AuroraFile, List)}  implementation.
     */
    @Override
    public void onFileClicked(AuroraFile file, List<AuroraFile> all) {
        if (file.isLink()){
            //[START Open url]
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(file.getLinkUrl()));

            if (IntentUtil.isAvailable(this, intent)){
                startActivity(intent);
            } else {
                onCantOpenFile();
            }
            //[END Open url]
        }else {
            if (file.isPreviewAble()){
                startActivity(
                        FileViewActivity.IntentCreator.newInstance(all, file, this)
                );
            }else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setType(file.getContentType());

                if (IntentUtil.isAvailable(this, intent)) {
                    if (!file.isOfflineMode()) {
                        downloadFile(file, DownloadType.DOWNLOAD_OPEN);
                    } else {
                        File localFile = FileUtil.getOfflineFile(file, getApplicationContext());
                        if (localFile.exists()){
                            onFileDownloaded(file, localFile, DownloadType.DOWNLOAD_OPEN);
                        } else {
                            new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                                    .setMessage(R.string.prompt_offline_file_not_exist)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        }
                    }
                } else {
                    onCantOpenFile();
                }
            }
        }
    }

    /**
     * {@link FilesCallback#createFolder(String, String, String)}  implementation.
     */
    @Override
    public void createFolder(String path, final String type, String folderName) {

        int taskId = Api.createFolder(folderName, path, type, new ApiCallback<Void>() {
            @Override
            public void onSucces(Void result) {
                onFolderSuccessCreated(path, type, folderName);
                onTaskEnd(true);
            }

            @Override
            public void onError(ApiError error) {
                String message = error.getMessage();
                if (TextUtils.isEmpty(message)){
                    message = getString(R.string.error_folder_creation);
                }
                Toast.makeText(FilesListActivity.this, message, Toast.LENGTH_LONG).show();
                onTaskEnd(false);
            }
        });
        onTaskStart(taskId, UNKNOWN_MAX_PROGRESS);
    }

    /**
     * {@link FilesAdapter.MultichoiseListener#onMultichoiseChanged(List)}  implementation.
     */
    @Override
    public void onMultichoiseChanged(List<AuroraFile> selected) {
        if (mMultichoiseMenu != null) {
            mSelectedFiles.clear();
            mSelectedFiles.addAll(selected);
            mMultichoiseMenu.onMultichoiseChanged(selected);
        }
    }

    /**
     * {@link FileActionsBottomSheet.FileActionListener#onActionSelected(int, AuroraFile)}  implementation.
     */
    @Override
    public void onActionSelected(int action, AuroraFile file) {
        switch (action){
            case R.id.action_rename:
                showRenameDialog(file);
                break;
            case R.id.action_delete:
                deleteFiles(Collections.singletonList(file));
                break;
            case R.id.action_download:
                downloadFile(file, DownloadType.DOWNLOAD_TO_DOWNLOADS);
                break;
            case R.id.action_send:
                downloadFile(file, DownloadType.DOWNLOAD_FOR_EMAIL);
                break;
            case R.id.action_offline_on:
                makeFileOffline(file, true);
                break;
            case R.id.action_offline_off:
                makeFileOffline(file, false);
                break;
        }
    }

    ////////////////////////////////////////////////
    // [END Implementation] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Own methods] // <editor-fold desc="Own methods">
    ////////////////////////////////////////////////

    public FilesRootFragment getOnlineContentFragment(){
        return new FilesRootFragment();
    }

    public Fragment getOfflineContentFragment() {
        return OfflineFilesFragment.newInstance(mCurrentMode == MODE_OFFLINE_MANUAL);
    }

    public void showOnlineState(){
        mCurrentMode = MODE_ONLINE;
        mFilesRootFragment = getOnlineContentFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(getFragmentContentLayout(), mFilesRootFragment)
                .commit();

        updateOfflineModeMenus(mCurrentMode);
    }

    public void showOfflineState(){
        mFilesRootFragment = null;
        if (mCurrentMode == MODE_ONLINE){
            mCurrentMode = MODE_OFFLINE_NETWORK;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(getFragmentContentLayout(), getOfflineContentFragment())
                .commit();

        mTabLayout.removeAllTabs();
        mTabLayout.clearOnTabSelectedListeners();
        mTabLayout.setVisibility(View.GONE);

        setMultiChoiseAllowed(false);
        updateOfflineModeMenus(mCurrentMode);
    }

    public void onAvailableFilesTypesChecked(){
        setMultiChoiseAllowed(true);
    }

    private void setMultiChoiseAllowed(boolean allowed) {
        if (allowed == mMultichoiseAllowed) return;

        mMultichoiseAllowed = allowed;
        if (mMultichoiseActionItem != null) {
            mMultichoiseActionItem.setVisible(allowed);
        }
    }

    private void updateActionBar(boolean isRoot, String title){
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            updateHomeAsUpIndicator(isRoot, ab);
            ab.setTitle(isRoot ? getTitle() : title);
        }
    }

    protected void updateHomeAsUpIndicator(boolean isRoot, ActionBar ab){
        ab.setDisplayHomeAsUpEnabled(!isRoot);
    }

    protected void updateOfflineModeMenus(int mode){
        boolean online = mode == MODE_ONLINE;
        if (mOfflineModeMenuItem != null) {
            mOfflineModeMenuItem.setVisible(online);
        }
        if (mOnlineModeMenuItem != null) {
            mOnlineModeMenuItem.setVisible(!online);
        }
    }

    /**
     * Show alert dialog when cant open file.
     */
    private void onCantOpenFile(){
        new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setMessage(R.string.prompt_cant_open_file)
                .setPositiveButton(R.string.dialog_ok, null)
                .show();
    }

    /**
     * Logout - remove current {@link Account} and clear all user data and cached files.
     */
    private void logout(){
        AccountUtil.logout(this);

        //Start login activity
        startActivity(
                AuroraLoginActivity.IntentCreator.makeNextActivity(
                        new Intent(this, AuroraLoginActivity.class),
                        FilesListActivity.class
                )
        );
        finish();
    }

    protected void onFolderSuccessCreated(String path, String type, String name){
        if (mFilesRootFragment != null){
            mFilesRootFragment.refreshFolderByType(type);
        }
    }

    ////////////////////////////////////////////////
    // [END Own methods] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Classes] // <editor-fold desc="Classes">
    ////////////////////////////////////////////////

    private class MultichoiseMenu implements ActionMode.Callback, FilesAdapter.MultichoiseListener{

        private ActionMode mActionMode;

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_multichoise, menu);
            mFilesRootFragment.setMultichoiseMode(true);
            mSelectedFiles.clear();

            mActionMode = mode;
            mActionMode.setTitle(getString(R.string.title_action_selected, 0));
            mMultichoiseMenu = this;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (mSelectedFiles != null && mSelectedFiles.size() > 0){
                switch (item.getItemId()){
                    case R.id.action_delete:
                        List<AuroraFile> selected = new ArrayList<>();
                        selected.addAll(mSelectedFiles);
                        deleteFiles(selected);
                        mode.finish();
                        mSelectedFiles.clear();
                        return true;
                    case R.id.action_download:
                        downloadFiles(mSelectedFiles, DownloadType.DOWNLOAD_TO_DOWNLOADS);
                        mode.finish();
                        mSelectedFiles.clear();
                        return true;
                }
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mFilesRootFragment.setMultichoiseMode(false);
            mMultichoiseMenu = null;
        }

        @Override
        public void onMultichoiseChanged(List<AuroraFile> selected) {
            mActionMode.setTitle(getString(R.string.title_action_selected, selected.size()));
            boolean allowDownload = selected.size() > 0;
            for (AuroraFile item:selected){
                if (item.isFolder()){
                    allowDownload = false;
                    break;
                }
            }

            mActionMode.getMenu()
                    .findItem(R.id.action_download)
                    .setVisible(allowDownload);
            mActionMode.getMenu()
                    .findItem(R.id.action_delete)
                    .setVisible(selected.size() > 0);
        }
    }

    ////////////////////////////////////////////////
    // [END Classes] // </editor-fold>
    ////////////////////////////////////////////////
}
