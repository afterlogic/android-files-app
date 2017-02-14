package com.afterlogic.aurora.drive._unrefactored.presentation.ui;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.DialogUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.DownloadType;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.IntentUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.WatchingFileManager;
import com.afterlogic.aurora.drive._unrefactored.core.util.task.TaskProgressNotifier;
import com.afterlogic.aurora.drive._unrefactored.core.util.task.TaskRegisterConnection;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiCallback;
import com.afterlogic.aurora.drive.model.error.ApiError;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Task;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive._unrefactored.presentation.receivers.SyncResolveReceiver;
import com.afterlogic.aurora.drive._unrefactored.presentation.services.FileLoadService;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncService;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.common.base.BaseFileDownloadActivity;
import com.afterlogic.aurora.drive.presentation.common.components.view.SelectionEditText;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
abstract class BaseFileActionActivity extends BaseFileDownloadActivity{

    private static final int FILE_SELECT_CODE = 102;
    public static final int REQUEST_READ_PERM = 103;

    protected static final int TYPE_UPLOAD = 0;
    protected static final int TYPE_DOWNLOAD = 1;
    protected static final int TYPE_OTHER = 2;

    public static final String EXTRA_TASK_TYPE =
            BaseFileActionActivity.class.getName() + ".EXTRA_TASK_TYPE";
    public static final String EXTRA_UPLOAD_FOLDER =
            BaseFileActionActivity.class.getName() + ".EXTRA_UPLOAD_FOLDER";


    private int mTaskType = -1;

    private AuroraFile mUploadFolder;
    ////////////////////////////////////////////////
    // [START Override super class] // <editor-fold desc="Override super class">
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Lifecycle] // <editor-fold desc="Lifecycle">
    ////////////////////////////////////////////////


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_TASK_TYPE, mTaskType);
        outState.putParcelable(EXTRA_UPLOAD_FOLDER, mUploadFolder);
    }

    ////////////////////////////////////////////////
    // [END Lifecycle] // </editor-fold>
    ////////////////////////////////////////////////

    @Override
    public boolean isCatchLoginFail() {
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_READ_PERM:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    onReadPermissionGranted();
                } else {
                    Toast.makeText(this, R.string.toast_cant_upload_permission_not_granted,
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case FILE_SELECT_CODE:
                if(resultCode == RESULT_OK){
                    if (data.getData() != null) {
                        onUploadFileChosen(data.getData());
                    } else {
                        Toast.makeText(this,
                                R.string.error_cant_read_file, Toast.LENGTH_LONG).show();
                    }
                }else{
                    mUploadFolder = null;
                }
                break;
        }
    }

    @Override
    public void onRestoreTask(Bundle savedInstanceState) {
        super.onRestoreTask(savedInstanceState);
        mTaskType = savedInstanceState.getInt(EXTRA_TASK_TYPE, -1);
    }

    @Override
    public ProgressDialog onCreateProgressDialog() {
        ProgressDialog progressDialog =  super.onCreateProgressDialog();
        if (mTaskType == TYPE_DOWNLOAD || mTaskType == TYPE_UPLOAD) {
            progressDialog.setTitle(mTaskType == TYPE_UPLOAD ?
                    getString(R.string.dialog_files_title_uploading) :
                    getString(R.string.dialog_files_title_dowloading));
            progressDialog.setButton(
                    DialogInterface.BUTTON_NEGATIVE,
                    getString(R.string.dialog_cancel),
                    (dialog, which) -> {
                        Task task = Api.getTaskStateHandler()
                                .getTask(getTaskId());
                        if (task != null) {
                            task.cancel();
                        }
                        if (mTaskType == TYPE_UPLOAD) {
                            onUploadTaskCancelled();
                        }
                    }
            );
        }
        return progressDialog;
    }

    @Override
    protected void onProgressDialogUpdate(Task task, ProgressDialog progressDialog) {
        super.onProgressDialogUpdate(task, progressDialog);
        if (mTaskType == TYPE_DOWNLOAD || mTaskType == TYPE_UPLOAD) {
            if (task.getLabel() != null){
                progressDialog.setMessage(task.getLabel());
            } else {
                progressDialog.setMessage("");
            }
        }
    }

    @Override
    public TaskProgressNotifier onCreateNotifyProgressUpdater(Task task) {
        switch (mTaskType){
            case TYPE_UPLOAD:
                TaskProgressNotifier.Title title = new
                        TaskProgressNotifier.TaskLabelFormatTitle(
                        getString(R.string.prompt_notify_upload_file),
                        task
                );
                return new TaskProgressNotifier(title, task, getApplicationContext());

            case TYPE_DOWNLOAD:
                title = new TaskProgressNotifier.TaskLabelFormatTitle(
                        getString(R.string.prompt_notify_download_file),
                        task
                );
                return new TaskProgressNotifier(title, task, getApplicationContext()){
                    @Override
                    public void onEnd(Task task, Task.EndStatus status) {
                        endListenProgress(true);
                    }
                };
            default:
                return null;
        }
    }

    @Override
    public void downloadFiles(List<AuroraFile> files, DownloadType downloadType) {
        mTaskType = TYPE_DOWNLOAD;
        super.downloadFiles(files, downloadType);
    }

    @Override
    public void onTaskEnd(boolean success, Task task) {
        super.onTaskEnd(success, task);
        switch (mTaskType){
            case TYPE_UPLOAD:
                onUploadFinished(success, mUploadFolder);
                mUploadFolder = null;
                break;
        }
        mTaskType = -1;
    }

    @Override
    protected void onFileDownloaded(AuroraFile file, File result, DownloadType type){
        if (!isActive()) return;

        Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", result);

        switch (type){
            case DOWNLOAD_OPEN:
                //Open file in suitable application
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, file.getContentType());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (IntentUtil.isAvailable(getApplicationContext(), intent)) {
                    startActivity(intent);
                }else{
                    new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                            .setMessage(R.string.prompt_cant_open_file)
                            .setPositiveButton(R.string.dialog_ok, null)
                            .show();
                }
                break;

            case DOWNLOAD_FOR_EMAIL:
                //Start 'send' intent, for attaching to email
                intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setType(file.getContentType());
                intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(intent, getString(R.string.prompt_send_by_email_chooser)));
                break;
        }
    }

    private void getFileIntent(File file, String action){}

    ////////////////////////////////////////////////
    // [END Override super class] // </editor-fold>
    ////////////////////////////////////////////////


    ////////////////////////////////////////////////
    // [START Own methods] // <editor-fold desc="Own methods">
    ////////////////////////////////////////////////

    public int getTaskType() {
        return mTaskType;
    }

    /**
     * Rename file to new inputted name.
     */
    protected void showRenameDialog(final AuroraFile file){
        //[START Prepare input view (disallow change file extension)]
        @SuppressLint("InflateParams")
        View inputView = LayoutInflater.from(this)
                .inflate(R.layout.item_layout_dialog_input, null);
        final String ext = FileUtil.getFileExtension(file.getName());
        if (ext != null && !file.isFolder() && !file.isLink()) {
            //Set disallow only for 'normal' file
            final SelectionEditText input = (SelectionEditText) inputView.findViewById(R.id.input);
            input.setOnSelectionChangeListener((start, end) -> {
                int lenght = input.getText().length();
                int max = lenght - ext.length() - 1;
                boolean fixed = false;
                if (start > max){
                    start = max;
                    fixed = true;
                }
                if (end > max){
                    end = max;
                    fixed = true;
                }
                if (fixed){
                    input.setSelection(start, end);
                }
            });
        }
        //[END Prepare input view (disallow change file extension)]

        //Show dialog
        DialogUtil.showInputDialog(
                inputView,
                getString(R.string.prompt_input_new_file_name),
                file.getName(),
                this,
                (dialogInterface, input) -> {

                    String newName = input.getText().toString();
                    if (TextUtils.isEmpty(newName)){
                        input.setError(getString(R.string.error_field_required));
                        input.requestFocus();
                        return;
                    }

                    final String trimmed = newName.trim();
                    //Check new name if it is same as old closeQuietly dialog without any action
                    if (newName.equals(file.getName()) || trimmed.equals(file.getName())){
                        dialogInterface.dismiss();
                        return;
                    }

                    AuroraFile newFile = file.clone();
                    newFile.setName(trimmed);

                    checkRenameFile(file, newFile);

                    dialogInterface.dismiss();
                });
    }

    /**
     * Check is file with new name not exist.
     * @param oldFile - old {@link AuroraFile}.
     * @param newFile - same {@link AuroraFile} with new name.
     */
    private void checkRenameFile(final AuroraFile oldFile, final AuroraFile newFile){
        int taskId = Api.checkFile(newFile, new ApiCallback<AuroraFile>() {
            @Override
            public void onSucces(AuroraFile result) {
                new AlertDialog.Builder(BaseFileActionActivity.this, R.style.AppTheme_Dialog)
                        .setMessage(R.string.error_renamed_file_exist)
                        .setPositiveButton(R.string.dialog_ok, null)
                        .show();
            }

            @Override
            public void onError(ApiError error) {
                if (error.getErrorCode() == ApiResponseError.FILE_NOT_EXIST){
                    //File not exist so can rename
                    rename(oldFile, newFile);
                } else {
                    Toast.makeText(BaseFileActionActivity.this,
                            R.string.error_default_api_error, Toast.LENGTH_LONG).show();
                }
            }
        });

        mTaskType = TYPE_OTHER;
        onTaskStart(taskId, UNKNOWN_MAX_PROGRESS);
    }

    /**
     * Start next rename task.
     * @param oldFile - old {@link AuroraFile}.
     * @param newFile - same {@link AuroraFile} with new name.
     */
    private void rename(final AuroraFile oldFile, final AuroraFile newFile){
        int taskId = Api.renameFile(oldFile, newFile.getName(), new ApiCallback<Void>() {
            @Override
            public void onSucces(Void result) {
                //Check renamed file (for getting last modified)
                int taskId = Api.checkFile(newFile, new ApiCallback<AuroraFile>() {
                    @Override
                    public void onSucces(AuroraFile result) {
                        if (result != null) {
                            renameLocalFile(oldFile, result);
                            oldFile.setName(result.getName());
                        }
                        onFileSuccessRenamed(oldFile);
                        onTaskEnd(true);
                    }

                    @Override
                    public void onError(ApiError error) {

                    }
                });
                onTaskStart(taskId, UNKNOWN_MAX_PROGRESS);
            }

            @Override
            public void onError(ApiError error) {
                Toast.makeText(BaseFileActionActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                onTaskEnd(false);
            }
        });
        onTaskStart(taskId, UNKNOWN_MAX_PROGRESS);
    }

    protected abstract void onFileSuccessRenamed(AuroraFile file);


    protected abstract void onFilesSuccessDeleted(List<AuroraFile> files);

    protected abstract void onFileSuccessOfflineChanged(AuroraFile file, boolean offline);

    protected abstract void onUploadFinished(boolean success, AuroraFile uploadFolder);

    protected abstract void onUploadTaskCancelled();

    /**
     * Rename local equivalent for remote file.
     * @param oldFile - old {@link AuroraFile}.
     * @param newFile - result {@link AuroraFile} with new name.
     */
    public void renameLocalFile(AuroraFile oldFile, AuroraFile newFile){
        Context ctx = BaseFileActionActivity.this;
        //Get cached or offline with old name
        DBHelper db = new DBHelper(ctx);
        WatchingFileDAO dao = db.getWatchingFileDAO();
        WatchingFile watchingFile = dao.getWatching(oldFile);

        //Update offline/cached local file
        if (watchingFile != null) {
            try {
                File local = new File(watchingFile.getLocalFilePath());
                if (local.exists()) {
                    File localTarget = null;
                            //watchingFile.getType() == WatchingFile.TYPE_OFFLINE ?
                            //        FileUtil.getOfflineFile(newFile, ctx) :
                            //        FileUtil.getCacheFile(newFile, ctx);
                    //local target must be not exist
                    boolean success = !localTarget.exists() || localTarget.delete();

                    if (success) {
                        boolean synced = watchingFile.getSyncStatus()
                                == WatchingFile.SYNCED;

                        WatchingFile newWatching = new WatchingFile(
                                newFile, localTarget, watchingFile.getType(), synced
                        );

                        if (synced){
                            //Rename and update last modify for local target or request sync
                            if (!local.renameTo(localTarget) ||
                                    !localTarget.setLastModified(newFile.getLastModified())){
                                //If not renamed or last modified stamp not updated
                                //check file type
                                if (newWatching.getType() == WatchingFile.TYPE_OFFLINE) {
                                    //If offline set sync state to need sync
                                    newWatching.setSyncStatus(WatchingFile.SYNC_NEED);
                                    //SyncService.requestSync(
                                    //        newWatching.getRemoteUniqueSpec(),
                                    //        AccountUtil.getCurrentAccount(ctx)
                                    //);
                                } else {
                                    //if cached remove new local file if it exist
                                    if (localTarget.exists()){
                                        //noinspection ResultOfMethodCallIgnored
                                        localTarget.delete();
                                    }
                                    newWatching = null;
                                }
                            }
                        } else {
                            //if previous state was not synced so no need add new file to db for cached files
                            if (watchingFile.getType() == WatchingFile.TYPE_CACHE){
                                newWatching = null;
                            }
                        }
                        //Add new file to db if ot not null
                        if (newWatching != null) {
                            dao.createOrUpdate(newWatching);
                        }

                    } else {
                        //TODO handle not removed local file
                        Log.e(BaseFileActionActivity.class.getSimpleName(),
                                "Can't rename local file. Target file exist and not deleted.");
                    }
                }
                dao.delete(watchingFile);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        db.close();
    }

    /**
     * Delete file from file storage after confirm.
     * All files must be one type.
     */
    protected void deleteFiles(final List<AuroraFile> files){
        if (files == null || files.size() == 0) return;

        String message;
        if (files.size() > 1){
            StringBuilder fileList = new StringBuilder();
            for (AuroraFile file: files){
                if (fileList.length() > 0){
                    fileList.append(',').append('\n');
                }
                fileList.append(file.getName());
            }
            message = getString(R.string.prompt_delete_file_confirm_multiple, fileList.toString());
        } else {
            message = getString(R.string.prompt_delete_file_confirm, files.get(0).getName());
        }
        new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setMessage(message)
                .setPositiveButton(getString(R.string.dialog_button_yes), (dialog, which) -> {

                    String type = files.get(0).getType();

                    int taskId = Api.deleteFiles(type, files, new ApiCallback<Void>() {
                        @Override
                        public void onSucces(Void result) {
                            deleteLocal(files);
                            onFilesSuccessDeleted(files);

                            onTaskEnd(true);
                        }

                        @Override
                        public void onError(ApiError error) {
                            onTaskEnd(false);
                            Toast.makeText(BaseFileActionActivity.this,
                                    error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                    mTaskType = TYPE_OTHER;
                    onTaskStart(taskId, UNKNOWN_MAX_PROGRESS);
                })
                .setNegativeButton(getString(R.string.dialog_button_no), null)
                .show();
    }

    /**
     * Delete local files equivalent.
     * @param deleted - deleted remote files.
     */
    private void deleteLocal(List<AuroraFile> deleted){
        Context ctx = BaseFileActionActivity.this;
        DBHelper db = new DBHelper(ctx);
        WatchingFileDAO dao = db.getWatchingFileDAO();

        for (AuroraFile file:deleted) {
            //Get cached or offline with old name
            WatchingFile watchingFile = dao.getWatching(file);

            if (watchingFile != null) {
                if (watchingFile.getType() == WatchingFile.TYPE_OFFLINE) {
                    //Show save to downloads dialog
                    if (isActive()) {
                        showSaveDeletedOfflineDialog(watchingFile);
                    } else {
                    /*Cause save local file to {@link android.os.Environment#DIRECTORY_DOWNLOADS}
                    for deleted remote is same problem as
                    {@link SyncService.FileSyncAdapter#showRemoteDeletedNotify(WatchingFile, Context)} */
                        SyncService.FileSyncAdapter
                                .showRemoteDeletedNotify(watchingFile, this);
                    }
                } else {
                    try {
                        dao.delete(watchingFile);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        db.close();
    }

    /**
     * Show dialog for saving local file to {@link android.os.Environment#DIRECTORY_DOWNLOADS} folder.
     * @param file - db info about deleted file.
     */
    private void showSaveDeletedOfflineDialog(final WatchingFile file){
        new AlertDialog.Builder(this, R.style.AppTheme_Dialog)
                .setMessage(getString(R.string.alert_message_remote_deleted_save_local))
                .setPositiveButton(R.string.dialog_button_yes, (dialog, which) -> {
                    sendSaveDeletedBroadcast(file, true);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.dialog_button_no, (dialog, which) -> sendSaveDeletedBroadcast(file, false))
                .setCancelable(false)
                .show();
    }

    /**
     * Cause save local file to {@link android.os.Environment#DIRECTORY_DOWNLOADS}
     * for deleted remote is same problem as
     * {@link SyncService.FileSyncAdapter#showRemoteDeletedNotify(WatchingFile, Context)}
     * we will send same broadcast message.
     *
     * @param file - db info about deleted file.
     * @param saveToDownloads - if false file will be deleted else saved in
     *                         {@link android.os.Environment#DIRECTORY_DOWNLOADS}
     *                         and in both will removed from db.
     */
    private void sendSaveDeletedBroadcast(WatchingFile file, boolean saveToDownloads){
        Intent saveIntent = new Intent(
                SyncResolveReceiver.ACTION_RESOLVE_REMOTE_NOT_EXIST);
        saveIntent.putExtra(SyncResolveReceiver.KEY_SAVE_LOCAL, saveToDownloads);
        saveIntent.putExtra(
                SyncService.KEY_TARGET,
                file.getRemoteUniqueSpec()
        );
        saveIntent.setPackage(BuildConfig.APPLICATION_ID);
        sendBroadcast(saveIntent);
    }

    /**
     * Handle upload file choosed.
     * @param data - upload file {@link Uri} data.
     */
    private void onUploadFileChosen(Uri data){
        //Get current files list
        FileInfo fileInfo = FileUtil.fileInfo(data, getApplicationContext());
        if (fileInfo != null) {
            uploadFile(fileInfo, mUploadFolder);
        } else {
            //If file info not readable show error
            Toast.makeText(BaseFileActionActivity.this,
                    R.string.error_uploading_failed, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handle succes granted read permissions.
     */
    protected void onReadPermissionGranted(){
        if (mUploadFolder != null) {
            uploadFile(mUploadFolder);
        }
    }

    /**
     * Start upload file service.
     * @param targetFolder - target {@link AuroraFile} folder.
     * @param files - list of {@link FileInfo} for local files.
     */
    protected void uploadFiles(AuroraFile targetFolder, List<FileInfo> files){
        mTaskType = TYPE_UPLOAD;

        Task task = Api.getTaskStateHandler().startNewTask();
        task.addTaskListener(this);
        addNotifyProgressUpdaterToTask(task);

        Intent intent = new Intent(this, FileLoadService.class);
        intent = FileLoadService.makeUpload(task.getId(), targetFolder, files, false, intent);

        startService(intent);
        bindService(intent, new TaskRegisterConnection(task, this), 0);
    }

    /**
     * Upload file.
     */
    protected void uploadFile(final FileInfo fileInfo, final AuroraFile folder){
        uploadFiles(folder, Collections.singletonList(fileInfo));
    }

    protected void uploadFile(AuroraFile folder){
        mUploadFolder = folder;

        if (hasReadPermission()) {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(intent, FILE_SELECT_CODE);
        }else{
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERM
            );
        }
    }

    /**
     * Check is app have {@link Manifest.permission#READ_EXTERNAL_STORAGE} permission.
     * @return - true if have.
     */
    public boolean hasReadPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
            return true;
        }else {
            int status = ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return status == PackageManager.PERMISSION_GRANTED;
        }
    }

    protected void makeFileOffline(AuroraFile file, boolean offline){

        AccountManager ac = AccountManager.get(this);
        Account[] accounts = ac.getAccountsByType(AccountUtil.ACCOUNT_TYPE);

        if (accounts.length == 0) return;

        if (offline){
            File local = null;//FileUtil.getOfflineFile(file, this);
            WatchingFileManager.from(this)
                    .addWatching(file, local, WatchingFile.TYPE_OFFLINE, false);
            //SyncService.requestSync(
            //        WatchingFile.Spec.getRemoteUniqueSpec(file), accounts[0]);
        } else {
            DBHelper db = new DBHelper(this);
            WatchingFileDAO dao = db.getWatchingFileDAO();
            WatchingFile watching = dao.getWatching(file);

            boolean activeSync = ContentResolver.isSyncActive(
                    accounts[0], AccountUtil.FILE_SYNC_AUTHORITY);
            if (activeSync){
                ContentResolver.cancelSync(accounts[0], AccountUtil.FILE_SYNC_AUTHORITY);
            }

            try {
                File local = new File(watching.getLocalFilePath());
                if (local.exists()){
                    if (!local.delete()){
                        MyLog.majorException(this, new IOException("Can't delete file: " + local.getPath()));
                    }
                }
                dao.delete(watching);
            } catch (SQLException e) {
                MyLog.majorException(this, e);
            }

            if (activeSync){
                //SyncService.requestSync(null, accounts[0]);
            }
            db.close();
        }

        onFileSuccessOfflineChanged(file, offline);
    }

    ////////////////////////////////////////////////
    // [END Own methods] // </editor-fold>
    ////////////////////////////////////////////////
}
