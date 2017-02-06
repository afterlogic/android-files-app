package com.afterlogic.aurora.drive.presentation.modules.login.view;

import android.content.Context;
import android.content.Intent;

import com.afterlogic.aurora.drive.presentation.modules.filesMain.view.MainFilesActivity;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class LoginIntent {

    public static final String EXTRA_NEXT_ACTIVITY =
            LoginActivity.class.getName() + ".EXTRA_NEXT_ACTIVITY";
    public static final String EXTRA_FINISH_ON_RESULT =
            LoginActivity.class.getName() + "EXTRA_FINISH_ON_RESULT";

    public static Intent intent(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        return makeNextActivity(intent, MainFilesActivity.class);
    }

    public static Intent makeNextActivity(Intent i, Class activityClass){
        i.putExtra(EXTRA_NEXT_ACTIVITY, activityClass);
        return i;
    }

    public static Intent loginAndReturn(Intent i){
        i.putExtra(EXTRA_FINISH_ON_RESULT, true);
        return i;
    }
}
