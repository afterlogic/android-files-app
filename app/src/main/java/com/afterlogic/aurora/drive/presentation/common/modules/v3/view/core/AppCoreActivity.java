package com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core;

import android.annotation.SuppressLint;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.afterlogic.aurora.drive.application.navigation.AppNavigator;

import javax.inject.Inject;

import ru.terrakok.cicerone.NavigatorHolder;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

@SuppressLint("Registered")
public class AppCoreActivity extends AppCompatActivity {

    @Inject
    PermissionsInteractor permissionsInteractor;

    @Inject
    ActivityResultInteractor activityResultInteractor;

    @Inject
    NavigatorHolder navigatorHolder;

    private int fragmentContainerId = View.NO_ID;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsInteractor != null) {
            permissionsInteractor.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityResultInteractor != null) {
            activityResultInteractor.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (navigatorHolder != null) {
            navigatorHolder.setNavigator(new AppNavigator(this, fragmentContainerId));
        }
    }

    @Override
    protected void onPause() {
        if (navigatorHolder != null) {
            navigatorHolder.removeNavigator();
        }
        super.onPause();
    }

    protected void setFragmentContainerId(int fragmentContainerId) {
        this.fragmentContainerId = fragmentContainerId;
    }
}
