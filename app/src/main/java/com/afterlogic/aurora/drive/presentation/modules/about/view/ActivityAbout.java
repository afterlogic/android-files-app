package com.afterlogic.aurora.drive.presentation.modules.about.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVVMActivity;

/**
 * Created by sashka on 07.03.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ActivityAbout extends MVVMActivity {

    public static Intent intent(Context context){
        return new Intent(context, ActivityAbout.class);
    }

    @Override
    public void assembly(InjectorsComponent injectors) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_about);
        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
}
