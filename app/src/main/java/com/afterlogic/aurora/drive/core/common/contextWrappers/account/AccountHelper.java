package com.afterlogic.aurora.drive.core.common.contextWrappers.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.CoreScope;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.AuthorizedAuroraSession;
import com.afterlogic.aurora.drive.model.error.AccountManagerError;

import javax.inject.Inject;

/**
 * Created by sunny on 31.08.17.
 */
@CoreScope
public class AccountHelper {

    public static final String ACCOUNT_TYPE = "com.afterlogic.aurora";
    public static final String FILE_SYNC_AUTHORITY = "com.afterlogic.aurora.filesync.provider";

    public static final String ACCOUNT_ID = "account_id";
    public static final String APP_TOKEN = "app_token";
    public static final String AUTH_TOKEN = "auth_token";
    public static final String DOMAIN = "domain";
    public static final String API_VERSION = "apiVersion";
    public static final String HAS_SESSION = "hasSession";
    public static final String EMAIL = "email";

    private final Context context;
    private final AccountManager accountManager;

    public static Account getCurrentAccount(Context ctx){
        AccountManager am = AccountManager.get(ctx);
        Account[] accounts = am.getAccountsByType(ACCOUNT_TYPE);
        return accounts.length > 0 ? accounts[0] : null;
    }

    @Inject
    AccountHelper(Context context) {
        this.accountManager = AccountManager.get(context);
        this.context = context;
    }

    public void createAccount(String login) {

        Account account = getCurrentAccount(context);

        if (account == null) {
            account = new Account(login, ACCOUNT_TYPE);
            if (!accountManager.addAccountExplicitly(account, null, null)) {
                throw new AccountManagerError();
            }
        } else {
            throw new IllegalStateException("Already has account: " + account.name);
        }

    }

    public void removeCurrentAccount() {
        Account account = getCurrentAccount(context);

        if (account != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                accountManager.removeAccountExplicitly(account);
            } else {
                accountManager.removeAccount(account,null, null);
            }
        }
    }

    public Account getCurrentAccount() {
        return getCurrentAccount(context);
    }

    public boolean hasCurrentAccount() {
        return getCurrentAccount() != null;
    }

    @Nullable
    public AuroraSession getCurrentAccountSession() {

        Account account = getCurrentAccount(context);

        if (account == null) return null;

        if (!"true".equals(accountManager.getUserData(account, HAS_SESSION))) return null;

        return new AuroraSession(account, accountManager);
    }

    public void updateCurrentAccountSessionData(@Nullable AuthorizedAuroraSession session){
        Account account = getCurrentAccount(context);

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
            accountManager.setUserData(account, EMAIL, null);

        } else {

            if (!account.name.equals(session.getUser())) {
                throw new IllegalStateException("Session must have same user.");
            }

            accountManager.setUserData(account, HAS_SESSION, "true");

            accountManager.setUserData(account, ACCOUNT_ID, String.valueOf(session.getAccountId()));
            accountManager.setUserData(account, DOMAIN, session.getDomain().toString());
            accountManager.setUserData(account, APP_TOKEN, session.getAppToken());
            accountManager.setUserData(account, AUTH_TOKEN, session.getAuthToken());
            accountManager.setUserData(account, API_VERSION, String.valueOf(session.getApiVersion()));
            accountManager.setUserData(account, EMAIL, session.getEmail());
            accountManager.setPassword(account, session.getPassword());

        }

    }

}
