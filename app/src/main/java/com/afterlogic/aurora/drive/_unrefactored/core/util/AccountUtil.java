package com.afterlogic.aurora.drive._unrefactored.core.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive._unrefactored.data.common.api.AuroraApi;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive._unrefactored.presentation.receivers.AccountLoginStateReceiver;

import okhttp3.HttpUrl;

import static android.content.Context.ACCOUNT_SERVICE;

/**
 * Created by sashka on 05.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class AccountUtil {

    public static final String ACCOUNT_TYPE = "com.afterlogic.aurora";
    public static final String FILE_SYNC_AUTHORITY = "com.afterlogic.aurora.filesync.provider";

    private static final String ACCOUNT_ID = "account_id";
    private static final String APP_TOKEN = "app_token";
    private static final String AUTH_TOKEN = "auth_token";
    private static final String DOMAIN = "domain";
    private static final String API_VERSION = "apiVersion";
    private static final String CREDENTIALS_VERSION = "credentials_version";

    private static final int ACTUAL_CREDENTIAL_VERSION = 2;

    public static AuroraSession fromAccount(Account account, AccountManager am){

        //Check current credential version
        int credentialVersion = NumberUtil.parseInt(
                am.getUserData(account, CREDENTIALS_VERSION),
                0
        );
        if (ACTUAL_CREDENTIAL_VERSION != credentialVersion) {
            onCredentialsUpdate(credentialVersion, ACTUAL_CREDENTIAL_VERSION, account, am);
            am.setUserData(account, CREDENTIALS_VERSION, String.valueOf(ACTUAL_CREDENTIAL_VERSION));
        }

        String domain = am.getUserData(account, DOMAIN);

        long accountId = NumberUtil.parseLong(
                am.getUserData(account, ACCOUNT_ID),
                0
        );

        int apiVersion = NumberUtil.parseInt(
                am.getUserData(account, API_VERSION),
                Const.ApiVersion.API_NONE
        );

        return new AuroraSession(
                am.getUserData(account, APP_TOKEN),
                am.getUserData(account, AUTH_TOKEN),
                accountId,
                account.name,
                am.getPassword(account),
                HttpUrl.parse(domain),
                apiVersion
        );
    }

    /**
     * Handle update credentials.
     */
    private static void onCredentialsUpdate(int from, int to, Account account, AccountManager am){
        if (from < 2){
            int apiVersion = NumberUtil.parseInt(
                    am.getUserData(account, API_VERSION),
                    Const.ApiVersion.API_NONE
            );
            if (apiVersion == Const.ApiVersion.API_NONE){
                am.setUserData(account, API_VERSION, String.valueOf(Const.ApiVersion.API_P7));
            }
        }
    }

    public static void updateAccountCredentials(Account account, AuroraSession session, AccountManager am){
        if (!account.name.equals(session.getLogin())) return;

        am.setUserData(account, ACCOUNT_ID, String.valueOf(session.getAccountId()));
        am.setUserData(account, DOMAIN, session.getDomain().toString());
        am.setUserData(account, APP_TOKEN, session.getAppToken());
        am.setUserData(account, AUTH_TOKEN, session.getAuthToken());
        am.setUserData(account, API_VERSION, String.valueOf(session.getApiVersion()));
        am.setPassword(account, session.getPassword());
    }

    @Nullable
    public static Account getAccount(String name, AccountManager am){
        for (Account account:am.getAccountsByType(AccountUtil.ACCOUNT_TYPE)){
            if (account.name.equals(name)){
                return account;
            }
        }
        return null;
    }

    public static Account getCurrentAccount(Context ctx){
        AccountManager am = AccountManager.get(ctx);
        Account[] accounts = am.getAccountsByType(ACCOUNT_TYPE);
        return accounts.length > 0 ? accounts[0] : null;
    }

    //TODO move to user repository
    public static void logout(Context ctx){
        //[START Clear account and session data]
        AuroraSession current = AuroraApi.getCurrentSession();
        AuroraApi.setCurrentSession(null);

        AccountManager am = (AccountManager) ctx.getSystemService(ACCOUNT_SERVICE);
        Account account = AccountUtil.getAccount(current.getLogin(), am);
        if (account != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                //noinspection deprecation
                am.removeAccount(account, null, null);
            } else {
                am.removeAccountExplicitly(account);
            }
            ctx.sendBroadcast(new Intent(AccountLoginStateReceiver.ACTION_AURORA_LOGOUT));
        }
        //[END Clear account and session data]
    }
}
