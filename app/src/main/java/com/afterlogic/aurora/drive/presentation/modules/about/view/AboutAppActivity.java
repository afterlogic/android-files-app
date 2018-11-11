package com.afterlogic.aurora.drive.presentation.modules.about.view;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVVMActivity;

/**
 * Created by sashka on 07.03.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class AboutAppActivity extends MVVMActivity {

    public static Intent intent(Context context){
        return new Intent(context, AboutAppActivity.class);
    }

    @Override
    public void assembly(InjectorsComponent injectors) {

    }

    @NonNull
    @Override
    protected ViewDataBinding onCreateBinding(@Nullable Bundle savedInstanceState) {
        return DataBindingUtil.setContentView(this, R.layout.activity_about);
    }

    @Override
    protected void onBindingCreated(@Nullable Bundle savedInstanceState) {
        super.onBindingCreated(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        if (ab != null){
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }
}
