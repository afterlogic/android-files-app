package com.afterlogic.aurora.drive.model;

import android.accounts.Account;
import android.accounts.AccountManager;

import com.afterlogic.aurora.drive.core.common.contextWrappers.account.AccountHelper;

import okhttp3.HttpUrl;

/**
 * Aurora session.
 * Handle account id, app token, auth token.
 */

public class AuroraSession {

    private final Account account;
    private final AccountManager accountManager;

    public AuroraSession(Account account, AccountManager accountManager) {
        this.accountManager = accountManager;
        this.account = account;
    }

    public String getUser() {
        return account.name;
    }

    public String getAppToken() {
        return accountManager.getUserData(account, AccountHelper.APP_TOKEN);
    }

    public String getAuthToken() {
        return accountManager.getUserData(account, AccountHelper.AUTH_TOKEN);
    }

    public long getAccountId() {
        String accountId = accountManager.getUserData(account, AccountHelper.ACCOUNT_ID);
        return Long.parseLong(accountId);
    }

    public String getEmail() {
        return accountManager.getUserData(account, AccountHelper.EMAIL);
    }

    public String getPassword() {
        return accountManager.getPassword(account);
    }

    public HttpUrl getDomain() {
        return HttpUrl.parse(accountManager.getUserData(account, AccountHelper.DOMAIN));
    }

    public int getApiVersion() {
        String apiVersion = accountManager.getUserData(account, AccountHelper.API_VERSION);
        return Integer.parseInt(apiVersion);
    }

}
