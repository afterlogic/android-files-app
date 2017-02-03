package com.afterlogic.aurora.drive.presentation.modules.filesMain.view;

import android.content.Context;
import android.content.Intent;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesIntent {
    public static Intent intent(Context context){
        return new Intent(context, MainFilesActivity.class);
    }
}
