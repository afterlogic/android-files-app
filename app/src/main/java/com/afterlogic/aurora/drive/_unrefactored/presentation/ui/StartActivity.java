package com.afterlogic.aurora.drive._unrefactored.presentation.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive.model.AuroraSession;

/**
 * Created by sashka on 29.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuroraSession session = Api.getApiProvider().getSessionManager().getAuroraSession();

        boolean logged = session != null && session.isComplete();

        Intent i = logged ?
                new Intent(this, FilesListActivity.class) :
                AuroraLoginActivity.IntentCreator.makeNextActivity(
                        new Intent(this, AuroraLoginActivity.class),
                        FilesListActivity.class
                );
        startActivity(i);

        finish();
    }
}
