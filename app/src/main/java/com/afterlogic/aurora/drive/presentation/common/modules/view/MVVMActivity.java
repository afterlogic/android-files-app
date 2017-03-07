package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;


/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class MVVMActivity extends AppCompatActivity implements MVVMView{

    private boolean mIsActive;

    public abstract void assembly(InjectorsComponent injectors);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App app = (App) getApplication();
        assembly(app.getInjectors());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mIsActive = true;
    }

    @Override
    protected void onStop() {
        mIsActive = false;
        super.onStop();
    }

    @Override
    public boolean isActive() {
        return mIsActive;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
