package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.view.View;

import com.afterlogic.aurora.drive.model.FileAction;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileActionItemViewModel implements View.OnClickListener{

    private final FileAction mFileAction;
    private FileAction.OnActionListener mListener;

    public FileActionItemViewModel(FileAction fileAction, FileAction.OnActionListener listener) {
        mFileAction = fileAction;
        mListener = listener;
    }

    public int getText(){
        return mFileAction.getText();
    }

    public int getIcon(){
        return mFileAction.getIcon();
    }

    @Override
    public void onClick(View view) {
        mListener.onFileAction(mFileAction);
    }

    protected FileAction.OnActionListener getListener(){
        return mListener;
    }

    protected FileAction getFileAction() {
        return mFileAction;
    }
}
