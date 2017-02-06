package com.afterlogic.aurora.drive._unrefactored.presentation.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.DownloadType;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments.FileViewCallback;
import com.afterlogic.aurora.drive._unrefactored.presentation.ui.fragments.FileViewFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sashka on 30.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileViewActivity extends BaseFileActionActivity implements
        FileViewCallback
{

    private static final String EXTRA_FILES =
            FileViewActivity.class.getName() + ".EXTRA_FILES";
    private static final String EXTRA_CURRENT_POSITION =
            FileViewActivity.class.getName() + ".EXTRA_CURRENT_POSITION";
    private static final String EXTRA_FULLSCREEN =
            FileViewActivity.class.getName() + ".EXTRA_FULLSCREEN";

    private boolean mIsFullScreen = false;
    private FileViewFragment mFileViewFragment;
    private String mTitle;

    ////////////////////////////////////////////////
    // [START Override superclass] // <editor-fold desc="Override superclass">
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Lifecycle] // <editor-fold desc="Lifecycle">
    ////////////////////////////////////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mFileViewFragment == null) {
            mFileViewFragment = (FileViewFragment) getSupportFragmentManager()
                    .findFragmentById(getFragmentContentLayout());
        }

        if (savedInstanceState != null) {
            mIsFullScreen = savedInstanceState.getBoolean(EXTRA_FULLSCREEN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActionBar ab = getSupportActionBar();
        if (ab != null && mIsFullScreen) {
            ab.hide();
            mFileViewFragment.setSwipeAble(false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_FULLSCREEN, mIsFullScreen);
    }

    ////////////////////////////////////////////////
    // [END Lifecycle] // </editor-fold>
    ////////////////////////////////////////////////

    @Nullable
    @Override
    public Fragment onCreateFragment() {
        List<AuroraFile> files = getIntent().getParcelableArrayListExtra(EXTRA_FILES);
        int current = getIntent().getIntExtra(EXTRA_CURRENT_POSITION, 0);
        mFileViewFragment = FileViewFragment.newInstance(files, files.get(current));
        return mFileViewFragment;
    }

    @Override
    public void initActionBar(ActionBar ab) {
        super.initActionBar(ab);
        ab.setDisplayHomeAsUpEnabled(true);
        if (mTitle != null){
            ab.setTitle(mTitle);
        }
    }

    ////////////////////////////////////////////////
    // [END Override superclass] // </editor-fold>
    ////////////////////////////////////////////////

    ////////////////////////////////////////////////
    // [START Implementation] // <editor-fold desc="Implementation">
    ////////////////////////////////////////////////

    /**
     * {@link FileViewCallback#updateTitle(String)}  implementation.
     */
    @Override
    public void updateTitle(String title) {
        mTitle = title;
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
    }

    /**
     * {@link FileViewCallback#requireFullscreen(boolean)}  implementation.
     */
    @Override
    public boolean requireFullscreen(boolean fullscreen) {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            if (fullscreen) {
                ab.hide();
            } else {
                ab.show();
            }
            mIsFullScreen = fullscreen;
            mFileViewFragment.setSwipeAble(!fullscreen);
            getWindow().clearFlags(
                    fullscreen ?
                            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN :
                            WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
            getWindow().addFlags(
                    !fullscreen ?
                            WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN :
                            WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
            return true;
        }
        return false;
    }

    /**
     * {@link FileViewCallback#downloadFile(AuroraFile)}  implementation.
     */
    @Override
    public void downloadFile(final AuroraFile file) {
        downloadFile(file, DownloadType.DOWNLOAD_TO_DOWNLOADS);
    }

    @Override
    public void renameFile(AuroraFile file) {
        super.showRenameDialog(file);
    }

    @Override
    public void deleteFile(AuroraFile file) {
        deleteFiles(Collections.singletonList(file));
    }

    @Override
    public void makeFileOffline(AuroraFile file, boolean offline) {
        super.makeFileOffline(file, offline);
    }

    @Override
    public void sendFile(AuroraFile fileP7) {
        downloadFile(fileP7, DownloadType.DOWNLOAD_FOR_EMAIL);
    }

    ////////////////////////////////////////////////
    // [END Implementation] // </editor-fold>
    ////////////////////////////////////////////////

    @Override
    protected void onFileDownloaded(final AuroraFile file, final File result, DownloadType downloadType) {
        if (!isActive()) return;
        switch (downloadType){
            case DOWNLOAD_TO_DOWNLOADS:
                new AlertDialog.Builder(FileViewActivity.this, R.style.AppTheme_Dialog)
                        .setMessage(R.string.prompt_dialog_file_succes_download_open)
                        .setPositiveButton(
                                R.string.dialog_button_yes,
                                (dialog, which) -> super.onFileDownloaded(file, result, DownloadType.DOWNLOAD_OPEN)
                        )
                        .setNegativeButton(R.string.dialog_button_no, null)
                        .show();
                break;

            default:
                super.onFileDownloaded(file, result, downloadType);
        }
    }

    @Override
    protected void onFileSuccessRenamed(AuroraFile file) {
        notifyDataChanged();
    }

    @Override
    protected void onFilesSuccessDeleted(List<AuroraFile> files) {
        if (mFileViewFragment != null){
            mFileViewFragment.onFilesDeleted(files);
        }
    }

    @Override
    protected void onFileSuccessOfflineChanged(AuroraFile file, boolean offline) {
        notifyDataChanged();
    }

    @Override
    protected void onUploadFinished(boolean success, AuroraFile uploadFolder) {
        //no-op
    }

    @Override
    protected void onUploadTaskCancelled() {
        //no-op
    }

    private void notifyDataChanged(){
        if (mFileViewFragment == null) return;
        mFileViewFragment.notifyDataSetChanged();
    }

    public static class IntentCreator{
        public static Intent newInstance(List<AuroraFile> files, AuroraFile current, Context ctx){
            int currentPosition = 0;
            for (int i = 0; i < files.size(); i++){
                if (files.get(i) == current){
                    currentPosition = i;
                    break;
                }
            }

            ArrayList<AuroraFile> extra = new ArrayList<>();
            extra.addAll(files);

            Intent i = new Intent(ctx, FileViewActivity.class);
            i.putParcelableArrayListExtra(EXTRA_FILES, extra);
            i.putExtra(EXTRA_CURRENT_POSITION, currentPosition);
            return i;
        }
    }
}
