package com.afterlogic.aurora.drive.presentation.modulesBackground.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.contextWrappers.account.AccountHelper;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;

/**
 * Created by sashka on 05.04.16.
 * mail: sunnyday.development@gmail.com
 */
class Authenticator extends AbstractAccountAuthenticator {

    private Context context;

    // Simple constructor
    public Authenticator(Context context) {
        super(context);
        this.context = context;
    }

    // Editing properties is not supported
    @Override
    public Bundle editProperties(
            AccountAuthenticatorResponse r, String s) {
        throw new UnsupportedOperationException();
    }

    // Don't add additional accounts
    @Override
    public Bundle addAccount(
            AccountAuthenticatorResponse r,
            String s,
            String s2,
            String[] strings,
            Bundle bundle) throws NetworkErrorException {

        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(AccountHelper.ACCOUNT_TYPE);

        Bundle result = new Bundle();

        if (accounts.length > 0) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> Toast.makeText(context,
                    R.string.error_add_more_than_one_account,
                    Toast.LENGTH_LONG).show());
            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        }else{
            Intent intent = LoginActivity.intent(false);

            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, r);

            result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
            result.putParcelable(AccountManager.KEY_INTENT, intent);
        }
        return result;
    }

    // Ignore attempts to confirm credentials
    @Override
    public Bundle confirmCredentials(
            AccountAuthenticatorResponse r,
            Account account,
            Bundle bundle) throws NetworkErrorException {
        return null;
    }

    // Getting an authentication token is not supported
    @Override
    public Bundle getAuthToken(
            AccountAuthenticatorResponse r,
            Account account,
            String s,
            Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    // Getting a label for the auth token is not supported
    @Override
    public String getAuthTokenLabel(String s) {
        throw new UnsupportedOperationException();
    }

    // Updating user credentials is not supported
    @Override
    public Bundle updateCredentials(
            AccountAuthenticatorResponse r,
            Account account,
            String s, Bundle bundle) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    // Checking features for the account is not supported
    @Override
    public Bundle hasFeatures(
            AccountAuthenticatorResponse r,
            Account account, String[] strings) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

}