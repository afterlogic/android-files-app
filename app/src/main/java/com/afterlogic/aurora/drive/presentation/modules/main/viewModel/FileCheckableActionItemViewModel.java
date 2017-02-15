package com.afterlogic.aurora.drive.presentation.modules.main.viewModel;

import android.widget.CompoundButton;

import com.afterlogic.aurora.drive.model.FileAction;

/**
 * Created by sashka on 15.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileCheckableActionItemViewModel extends FileActionItemViewModel implements CompoundButton.OnCheckedChangeListener{

    private final boolean mChecked;

    public FileCheckableActionItemViewModel(FileAction fileAction, boolean checked, FileAction.OnActionListener listener) {
        super(fileAction, listener);
        mChecked = checked;
    }

    public boolean getChecked(){
        return mChecked;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        getListener().onFileAction(getFileAction());
    }
}
