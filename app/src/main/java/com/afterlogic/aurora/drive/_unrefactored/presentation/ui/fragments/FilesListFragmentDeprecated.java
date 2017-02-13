package com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments;

import android.accounts.Account;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.DialogUtil;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.api.ValidApiCallback;
import com.afterlogic.aurora.drive._unrefactored.core.util.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive._unrefactored.core.util.interfaces.OnItemLongClickListener;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiCallback;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive._unrefactored.presentation.services.SyncService;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.FilesListActivity;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.adapters.FilesAdapter;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.error.ApiError;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnBackPressedListener;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseFragment;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesCallback;
import com.annimon.stream.Stream;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FilesListFragmentDeprecated extends BaseFragment implements
        ApiCallback<List<AuroraFile>>,
        OnItemClickListener<AuroraFile>,
        OnItemLongClickListener<AuroraFile>,
        OnBackPressedListener,
        FilesAdapter.MultichoiseListener
{

    private static final String TAG = FilesListFragmentDeprecated.class.getSimpleName();
    public static final String ARGS_TYPE = TAG + ".ARGS_TYPE";
    public static final String EXTRA_FOLDERS = "EXTRA_FOLDERS";
    public static final String EXTRA_REFRESHING = "EXTRA_REFRESHING";
    public static final String EXTRA_CONTENT = "EXTRA_CONTENT";
    public static final String FIRST_CREATE = "first_create";

    //UI References
    private SwipeRefreshLayout mRefreshView;

    private List<AuroraFile> mFiles = new ArrayList<>();
    private FilesAdapter mFilesAdapter;
    private DBHelper mDB;
    private boolean mIsRefreshing;
    private boolean mNeedRefresh = true;

    private int mCurrentRefreshTaskID = 0;

    private String mType;
    private ArrayList<AuroraFile> mFolders;

    private MainFilesCallback mCallback;

    private final List<Runnable> mOnRefreshActions = new ArrayList<>();

    public static FilesListFragmentDeprecated newInstance(String type) {

        Bundle args = new Bundle();
        args.putString(ARGS_TYPE, type);
        FilesListFragmentDeprecated fragment = new FilesListFragmentDeprecated();
        fragment.setArguments(args);
        return fragment;
    }

    public static FilesListFragmentDeprecated newInstance(@NonNull String type,
                                                          @Nullable List<AuroraFile> folders,
                                                          @Nullable List<AuroraFile> content){

        ArrayList<AuroraFile> foldersArrayList = new ArrayList<>();
        if (folders != null) {
            foldersArrayList.addAll(folders);
        }
        ArrayList<AuroraFile> contentArrayList = new ArrayList<>();
        if (content != null) {
            contentArrayList.addAll(content);
        }
        Collections.sort(contentArrayList, FileUtil.AURORA_FILE_COMPARATOR);

        Bundle args = new Bundle();
        args.putString(ARGS_TYPE, type);
        args.putParcelableArrayList(EXTRA_FOLDERS, foldersArrayList);
        args.putParcelableArrayList(EXTRA_CONTENT, contentArrayList);
        args.putBoolean(EXTRA_REFRESHING, false);

        FilesListFragmentDeprecated fragment = new FilesListFragmentDeprecated();
        fragment.setArguments(args);
        return fragment;
    }

    ////////////////////////////////////////////////
    // [START Override superclass] // <editor-fold desc="Override superclass">
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START LifeCycle] // <editor-fold desc="LifeCycle">
    ////////////////////////////////////////////////


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MainFilesCallback) context;
    }

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Init adapter and WatchingFile DB
        mDB = new DBHelper(getContext());
        mFilesAdapter = new FilesAdapter(mFiles, mDB, this, this, this);

        Bundle args = getArguments();
        if (args != null) {
            mType = getArguments().getString(ARGS_TYPE);
        }

        boolean isFirstCreate = savedInstanceState == null
                || savedInstanceState.getBoolean(FIRST_CREATE, true);

        fillFromBundle(isFirstCreate ? args : savedInstanceState);

        if (mFolders == null) {
            mFolders = new ArrayList<>();
        }

        if (mNeedRefresh) {
            refreshCurrentFolder(savedInstanceState == null);
        } else {
            mFilesAdapter.setEmptyText(getString(R.string.prompt_folder_is_empty));
            mFilesAdapter.setShowEmptyView(true);
        }
    }

    private void fillFromBundle(Bundle args){
        mFolders = args.getParcelableArrayList(EXTRA_FOLDERS);
        List<AuroraFile> currentContent = args.getParcelableArrayList(EXTRA_CONTENT);
        mNeedRefresh = args.getBoolean(EXTRA_REFRESHING, true);

        if (mNeedRefresh || mFolders == null || currentContent == null) return;

        mFiles.addAll(currentContent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_files_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView list = (RecyclerView) view.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setItemAnimator(null);
        list.setAdapter(mFilesAdapter);
        mFilesAdapter.startListenSync(getContext());

        mRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mRefreshView.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        mRefreshView.setOnRefreshListener(() -> refreshCurrentFolder(false));
        setRefreshingState(mIsRefreshing);
    }

    @Override
    public void onDestroyView() {
        mFilesAdapter.stopListenSync(getContext());
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FIRST_CREATE, false);
        outState.putParcelableArrayList(EXTRA_FOLDERS, mFolders);
        outState.putBoolean(EXTRA_REFRESHING, mNeedRefresh);

        ArrayList<AuroraFile> currentContent = new ArrayList<>();
        currentContent.addAll(mFiles);
        outState.putParcelableArrayList(EXTRA_CONTENT, currentContent);
    }

    @Override
    public void onDestroy() {
        mDB.close();
        mOnRefreshActions.clear();
        super.onDestroy();
    }

    ////////////////////////////////////////////////
    // [END LifeCycle] // </editor-fold>
    ////////////////////////////////////////////////

    @Override
    public boolean onBackPressed() {
        if (mFolders.size() > 0) {
            popFolder();
            return true;
        } else {
            return false;
        }
    }

    ////////////////////////////////////////////////
    // [END Override superclass] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Implementation] // <editor-fold desc="Implementation">
    ////////////////////////////////////////////////

    /**
     * {@link ApiCallback#onSucces(Object)}  implementation.
     */
    @Override
    public void onSucces(List<AuroraFile> result) {
        mFiles.clear();
        mFiles.addAll(result);
        Collections.sort(mFiles, FileUtil.AURORA_FILE_COMPARATOR);
        mFilesAdapter.notifyDataSetChanged();

        mNeedRefresh = false;
        setRefreshingState(false);

        checkFilesOnOfflineSync(mFiles);
    }

    /**
     * {@link ApiCallback#onError(ApiError)}  implementation.
     */
    @Override
    public void onError(ApiError error) {
        if (error.getErrorCode() == ApiError.CONNECTION_ERROR || error.getErrorCode() == ApiError.UNKNOWN_HOST){
            if (getActivity() != null){
                FilesListActivity activity = (FilesListActivity) getActivity();
                activity.showOfflineState();
                return;
            }
        }
        mNeedRefresh = true;
        mFilesAdapter.setEmptyText(getString(R.string.prompt_error_occurred));
        setRefreshingState(false);
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * {@link OnItemClickListener#onItemClick(Object)}  implementation.
     */
    @Override
    public void onItemClick(AuroraFile item) {
        if (!isAdded()) return;

        if (mIsRefreshing){
            Toast.makeText(getContext(), R.string.prompt_refreshing_files_list, Toast.LENGTH_SHORT).show();

            return;
        }

        if (item.isFolder()){
            openFolder(item);
        }else{
            //mCallback.onFileClicked(item, mFiles);
        }
    }

    /**
     * {@link OnItemLongClickListener#onItemLongClick(Object)}  implementation.
     */
    @Override
    public boolean onItemLongClick(AuroraFile item) {
        mNeedRefresh = true;
        //mCallback.showActions(item);
        return false;
    }

    /**
     * {@link FilesAdapter.MultichoiseListener#onMultichoiseChanged(List)}  implementation.
     */
    @Override
    public void onMultichoiseChanged(List<AuroraFile> selected) {
        if (getActivity() instanceof FilesAdapter.MultichoiseListener){
            ((FilesAdapter.MultichoiseListener) getActivity()).onMultichoiseChanged(selected);
        }
    }

    ////////////////////////////////////////////////
    // [END Implementation] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Own methods] // <editor-fold desc="Own methods">
    ////////////////////////////////////////////////

    public void setMultichoiseMode(boolean multichoiseMode){
        mFilesAdapter.setMultichoiseMode(multichoiseMode);
    }

    /**
     * Check gotten files on sync if it is available in offline mode.
     * @param files - all gotten files.
     */
    private void checkFilesOnOfflineSync(List<AuroraFile> files){
        if (!mDB.isOpen()) return;
        WatchingFileDAO dao = mDB.getWatchingFileDAO();
        try {
            for (AuroraFile remote : files) {
                String spec = WatchingFile.Spec.getRemoteUniqueSpec(remote);

                //get offline file for target remote file
                WatchingFile watchingFile = dao.queryBuilder()
                        .where()
                        .eq(WatchingFile.COLUMN_REMOTE_FILE_SPEC, spec)
                        .and()
                        .eq(WatchingFile.COLUMN_TYPE, WatchingFile.TYPE_OFFLINE)
                        .queryForFirst();

                //If remote file is offline check last modify time
                if (watchingFile != null){
                    File local = new File(watchingFile.getLocalFilePath());

                    //Start sync if need it
                    if (!local.exists() || watchingFile.isNeedSync(remote, local)){
                        Account account = AccountUtil.getCurrentAccount(getContext());
                        SyncService.FileSyncAdapter.requestSync(
                                watchingFile.getRemoteUniqueSpec(), account);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigate to dipper folder.
     * @param folder - target folder.
     */
    private void openFolder(AuroraFile folder){
        mFolders.add(0, folder);
        refreshCurrentFolder(true);
    }

    /**
     * Navigate up in folder.
     */
    private void popFolder(){
        mFolders.remove(0);
        refreshCurrentFolder(true);
    }

    /**
     * Reload current folder.
     */
    public void refreshCurrentFolder(){
        refreshCurrentFolder(false);
    }

    /**
     * Reload current folder.
     */
    private void refreshCurrentFolder(boolean resetContent){
        mCurrentRefreshTaskID++;

        mNeedRefresh = true;

        if (getContext() != null) {
            setRefreshingState(true);

            mFilesAdapter.setEmptyText(getString(R.string.prompt_folder_is_empty));

            AuroraFile folder = getCurrentFolder();

            if (resetContent) {
                mCallback.onOpenFolder(folder);
                mFiles.clear();
                mFilesAdapter.notifyDataSetChanged();
            }

            //Start api request but handle only response on last request
            final int currentTaskId = mCurrentRefreshTaskID;
            Api.getFiles(folder, null, new ValidApiCallback<>(this,() -> currentTaskId == mCurrentRefreshTaskID));
        }
    }

    /**
     * Set uis state to refreshing (show loading indicator) or not.
     */
    private void setRefreshingState(boolean isRefresh){
        mIsRefreshing = isRefresh;
        if (mRefreshView != null){
            mRefreshView.post(() -> mRefreshView.setRefreshing(mIsRefreshing));
        }
        mFilesAdapter.setShowEmptyView(!isRefresh);
        if (!isRefresh){
            synchronized (mOnRefreshActions) {
                Stream.of(mOnRefreshActions).forEach(Runnable::run);
                mOnRefreshActions.clear();
            }
        }
    }

    /**
     * Show create folder dialog and start create request on "Ok" click.
     */
    public void requestCreateFolder(){
        DialogUtil.showInputDialog(getString(R.string.prompt_input_folder_name),
                getContext(), (dialogInterface, input) -> {
                    String path = mFolders.size() == 0 ? "" : mFolders.get(0).getFullPath();

                    String folderName = input.getText().toString();
                    if (TextUtils.isEmpty(folderName)){
                        input.setError(getString(R.string.error_field_required));
                        input.requestFocus();
                        return;
                    }

                    mNeedRefresh = true;

                    //mCallback.createFolder(path, mType, folderName);

                    //Dismiss once everything is OK.
                    dialogInterface.dismiss();
                });
    }

    /**
     * Handle file deleting.
     * @param files - deleted file.
     */
    public void onFilesDeleted(List<AuroraFile> files){
        boolean deleted = false;

        for (AuroraFile file:files) {
            for (AuroraFile item:mFiles){
                if (file.getFullPath().equals(item.getFullPath())){
                    deleted = true;
                    mFiles.remove(item);
                    break;
                }
            }
        }

        if (deleted) {
            mFilesAdapter.notifyDataSetChanged();
        }
    }

    public AuroraFile getCurrentFolder(){
        return mFolders.size() == 0 ? AuroraFile.parse("", mType, true) : mFolders.get(0);
    }

    public void notifyDataSetChanged(){
        mFilesAdapter.notifyDataSetChanged();
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    public void openFolderOnRefreshed(String name){
        Runnable openFolder = () -> {
            AuroraFile folder = Stream.of(mFiles)
                    .filter(file -> file.isFolder() && name.equals(file.getName()))
                    .findFirst()
                    .orElse(null);

            if (folder != null){
                openFolder(folder);
            }
        };
        if (mIsRefreshing){
            mOnRefreshActions.add(openFolder);
        } else {
            openFolder.run();
        }
    }

    ////////////////////////////////////////////////
    // [END Own methods] // </editor-fold>
    ////////////////////////////////////////////////
}
