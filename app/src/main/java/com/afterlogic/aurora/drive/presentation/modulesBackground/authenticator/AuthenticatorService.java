package com.afterlogic.aurora.drive.presentation.modulesBackground.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

/**
 * Created by sashka on 05.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class AuthenticatorService extends Service{

    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuthenticator = new Authenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
