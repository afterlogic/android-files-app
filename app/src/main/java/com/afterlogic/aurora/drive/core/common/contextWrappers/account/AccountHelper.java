package com.afterlogic.aurora.drive.core.common.contextWrappers.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.CoreScope;
import com.afterlogic.aurora.drive.core.common.util.AccountUtil;
import com.afterlogic.aurora.drive.core.common.util.NumberUtil;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.error.AccountManagerError;

import javax.inject.Inject;

import okhttp3.HttpUrl;

/**
 * Created by sunny on 31.08.17.
 */
@CoreScope
public class AccountHelper {

    public static final String ACCOUNT_TYPE = "com.afterlogic.aurora";
    public static final String FILE_SYNC_AUTHORITY = "com.afterlogic.aurora.filesync.provider";

    static final String ACCOUNT_ID = "account_id";
    static final String APP_TOKEN = "app_token";
    static final String AUTH_TOKEN = "auth_token";
    static final String DOMAIN = "domain";
    static final String API_VERSION = "apiVersion";
    static final String HAS_SESSION = "hasSession";

    private final AccountManager accountManager;

    @Inject
    AccountHelper(Context context) {
        this.accountManager = AccountManager.get(context);
    }

    public void createAccount(String login) {

        Account account = getCurrentAccountData();

        if (account == null) {
            account = new Account(login, AccountUtil.ACCOUNT_TYPE);
            if (!accountManager.addAccountExplicitly(account, null, null)) {
                throw new AccountManagerError();
            }
        } else {
            throw new IllegalStateException("Already has account: " + account.name);
        }

    }

    public void removeCurrentAccount() {
        Account account = getCurrentAccountData();

        if (account != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(account);
            } else {
                accountManager.removeAccount(account,null, null);
            }
        }
    }

    @Nullable
    public AuroraSession getCurrentAccountSessionData() {

        Account account = getCurrentAccountData();

        if (account == null) return null;

        if (!"true".equals(accountManager.getUserData(account, HAS_SESSION))) return null;

        String domain = accountManager.getUserData(account, DOMAIN);

        long accountId = NumberUtil.parseLong(
                accountManager.getUserData(account, ACCOUNT_ID),
                0
        );

        int apiVersion = NumberUtil.parseInt(
                accountManager.getUserData(account, API_VERSION),
                Const.ApiVersion.API_NONE
        );

        return new AuroraSession(
                accountManager.getUserData(account, APP_TOKEN),
                accountManager.getUserData(account, AUTH_TOKEN),
                accountId,
                account.name,
                accountManager.getPassword(account),
                HttpUrl.parse(domain),
                apiVersion
        );
    }

    public void updateCurrentAccountSessionData(@Nullable AuroraSession session){
        Account account = getCurrentAccountData();

        if (account == null) {
            throw new IllegalStateException("Doesn't have account.");
        }

        if (session == null) {

            accountManager.setUserData(account, HAS_SESSION, "false");

            accountManager.setUserData(account, ACCOUNT_ID, null);
            accountManager.setUserData(account, DOMAIN, null);
            accountManager.setUserData(account, APP_TOKEN, null);
            accountManager.setUserData(account, AUTH_TOKEN, null);
            accountManager.setUserData(account, API_VERSION, null);
            accountManager.setPassword(account, null);

        } else {

            accountManager.setUserData(account, HAS_SESSION, "true");

            accountManager.setUserData(account, ACCOUNT_ID, String.valueOf(session.getAccountId()));
            accountManager.setUserData(account, DOMAIN, session.getDomain().toString());
            accountManager.setUserData(account, APP_TOKEN, session.getAppToken());
            accountManager.setUserData(account, AUTH_TOKEN, session.getAuthToken());
            accountManager.setUserData(account, API_VERSION, String.valueOf(session.getApiVersion()));
            accountManager.setPassword(account, session.getPassword());

        }

    }

    @Nullable
    public AuroraAccount getCurrentAccount() {
        Account account = getCurrentAccountData();
        return account == null ? null : new AuroraAccount(accountManager, account);
    }

    private Account getCurrentAccountData(){
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        return accounts.length > 0 ? accounts[0] : null;
    }
}
