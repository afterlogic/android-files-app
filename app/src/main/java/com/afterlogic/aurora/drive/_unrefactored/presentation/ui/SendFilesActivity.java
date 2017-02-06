package com.afterlogic.aurora.drive._unrefactored.presentation.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.FileUtil;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Task;
import com.afterlogic.aurora.drive.databinding.ActivitySendFilesBinding;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments.ConnectionFailedFragment;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments.FilesListFragment;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sashka on 25.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class SendFilesActivity extends ChooseFileActivity {

    private static final String UPLOAD_TASK_STARTED =
            SendFilesActivity.class.getName() + ".UPLOAD_TASK_STARTED";
    private boolean mIsUploadTaskStarted = false;

    private ArrayList<FileInfo> mFilesForUpload = new ArrayList<>();
    private AuroraFile mTargetFolder;

    private ActivitySendFilesBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            mIsUploadTaskStarted = savedInstanceState.getBoolean(UPLOAD_TASK_STARTED, false);
            if (!isTaskProceeding() && mIsUploadTaskStarted){
                finish();
            }
        }

        //Create folder button
        FloatingActionButton createFolder = (FloatingActionButton) findViewById(R.id.create_folder);
        if (createFolder != null) {
            createFolder.setOnClickListener(v -> {
                if (mFilesRootFragment != null) {
                    FilesListFragment current = mFilesRootFragment.getCurrentListFragment();
                    if (current != null) {
                        current.requestCreateFolder();
                    }
                }
            });
        }
    }

    @Override
    protected void onSetContentView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_send_files);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(UPLOAD_TASK_STARTED, mIsUploadTaskStarted);
    }

    @Override
    public void onTaskStart(int taskId, long maxProgress) {
        super.onTaskStart(taskId, maxProgress);
        mIsUploadTaskStarted = true;
    }

    @Override
    public void onTaskEnd(boolean success, Task task) {
        int currentType = getTaskType();
        super.onTaskEnd(success, task);
        if (success && currentType == TYPE_UPLOAD) {
            finish();
        } else {
            mIsUploadTaskStarted = false;
        }
    }

    @Override
    public void onFileClicked(AuroraFile file, List<AuroraFile> all) {
        Toast.makeText(this, R.string.toast_choose_directory_for_files_upload, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("InlinedApi")
    public void onUploadFilesClicked(View v){
        if (mFilesRootFragment != null){
            Intent intent = getIntent();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clipData = intent.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++){
                    Uri uri = clipData.getItemAt(i).getUri();
                    FileInfo file = FileUtil.fileInfo(uri, this);
                    if (file != null){
                        mFilesForUpload.add(file);
                    }
                }
            } else {
                Uri uri = intent.getData();
                if (uri != null){
                    FileInfo file = FileUtil.fileInfo(uri, this);
                    if (file != null){
                        mFilesForUpload.add(file);
                    }
                }
            }

            mTargetFolder = mFilesRootFragment.getCurrentFolder();

            if (hasReadPermission()) {
                uploadFiles(mTargetFolder, mFilesForUpload);
            } else {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERM
                );
            }
        }
    }

    @Override
    protected void onReadPermissionGranted() {
        uploadFiles(mTargetFolder, mFilesForUpload);
    }

    @Override
    protected void onFolderSuccessCreated(String path, String type, String name) {
        super.onFolderSuccessCreated(path, type, name);
        mFilesRootFragment.getCurrentListFragment().openFolderOnRefreshed(name);
    }

    @Override
    public void onAvailableFilesTypesChecked() {
        super.onAvailableFilesTypesChecked();
        mBinding.setOnlineState(true);
    }

    @Override
    public void showOfflineState() {
        super.showOfflineState();
        mBinding.setOnlineState(false);
    }

    @Override
    public Fragment getOfflineContentFragment() {
        return ConnectionFailedFragment.newInstance();
    }

}
