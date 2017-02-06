package com.afterlogic.aurora.drive.presentation.modules.files;

import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive._unrefactored.presentation.ui.FilesListActivity;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileListIntent {
    public static Intent intent(Context context){
        return new Intent(context, FilesListActivity.class);
    }
}
