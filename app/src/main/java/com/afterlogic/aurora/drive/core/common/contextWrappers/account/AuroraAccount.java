package com.afterlogic.aurora.drive.core.common.contextWrappers.account;

import android.accounts.Account;
import android.accounts.AccountManager;

/**
 * Created by sunny on 02.09.17.
 * mail: mail@sunnydaydev.me
 */

public class AuroraAccount {

    private final AccountManager accountManager;
    private final Account account;

    AuroraAccount(AccountManager accountManager, Account account) {
        this.accountManager = accountManager;
        this.account = account;
    }

    public String getName() {
        return account.name;
    }

    public String getHost() {
        return accountManager.getUserData(account, AccountHelper.DOMAIN);
    }

}
