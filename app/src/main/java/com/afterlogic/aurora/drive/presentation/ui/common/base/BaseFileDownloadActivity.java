package com.afterlogic.aurora.drive.presentation.ui.common.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.afterlogic.aurora.drive.data.common.api.Task;
import com.afterlogic.aurora.drive.data.common.api.Api;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.services.FileLoadService;
import com.afterlogic.aurora.drive.core.util.DownloadType;
import com.afterlogic.aurora.drive.core.util.task.TaskRegisterConnection;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by sashka on 25.04.16.
 * mail: sunnyday.development@gmail.com
 */
public abstract class BaseFileDownloadActivity extends BaseTaskActivity{

    public static final int REQUEST_WRITE_PERM = 101;
    private static final String REMOTE_TARGET = BaseFileDownloadActivity.class.getName() +
            ".REMOTE_TARGET";
    private static final String DOWLOAD_TYPE = BaseFileDownloadActivity.class.getName() +
            ".DOWLOAD_TYPE";

    private AuroraFile mRemoteTarget;
    private DownloadType mDowloadType = null;

    ////////////////////////////////////////////////
    // [START Override superclass] // <editor-fold desc="Override superclass">
    ////////////////////////////////////////////////


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            mRemoteTarget = savedInstanceState.getParcelable(REMOTE_TARGET);
            mDowloadType = DownloadType.fromInt(
                    savedInstanceState.getInt(DOWLOAD_TYPE, -1)
            );
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(REMOTE_TARGET, mRemoteTarget);
        outState.putInt(DOWLOAD_TYPE, mDowloadType == null ? -1 : mDowloadType.toInt());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERM:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        mRemoteTarget != null && mDowloadType != null) {
                    downloadFile(mRemoteTarget, mDowloadType);
                }
                mRemoteTarget = null;
                break;
        }
    }

    @Override
    public void onTaskEnd(boolean success, Task task) {
        super.onTaskEnd(success, task);
        if (success){
            if (mRemoteTarget != null && mDowloadType != null) {
                File result;
                if (task != null) {
                    result = (File) task.getArgs().getSerializable(Task.TASK_RESULT);
                    onFileDownloaded(mRemoteTarget, result, mDowloadType);
                }
            }
        }
        mRemoteTarget = null;
        mDowloadType = null;
    }

    ////////////////////////////////////////////////
    // [END Override superclass] // </editor-fold>
    ////////////////////////////////////////////////

    /**
     * Check is app have {@link Manifest.permission#WRITE_EXTERNAL_STORAGE} permission.
     * @return - true if have.
     */
    private boolean hasWritePermission(){
        int status = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return status == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Download file with type of downloading.
     * @param file - source Aurora file.
     * @param downloadType - type of downloading,
     *                     it will be triggered in {@link #onFileDownloaded(AuroraFile, File, DownloadType)}
     */
    public void downloadFile(AuroraFile file, DownloadType downloadType) {
        mRemoteTarget = file;

        downloadFiles(Collections.singletonList(file), downloadType);
    }

    public void downloadFiles(List<AuroraFile> files, DownloadType downloadType){

        //Check write permissions
        if (!hasWritePermission()) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_PERM
            );
            return;
        }

        mDowloadType = downloadType;

        Task task = Api.getTaskStateHandler().startNewTask();
        task.addTaskListener(this);
        addNotifyProgressUpdaterToTask(task);

        Intent i = new Intent(this, FileLoadService.class);
        i = FileLoadService.makeDownload(task.getId(), files, downloadType, i);

        startService(i);
        bindService(i, new TaskRegisterConnection(task, this), 0);
    }

    /**
     * Handle downloaded file.
     * @param file - source Aurora file.
     * @param result - result downloaded file
     * @param type - download type
     */
    protected abstract void onFileDownloaded(AuroraFile file, @Nullable File result, DownloadType type);

}
