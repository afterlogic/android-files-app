package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

/**
 * Created by sunny on 16.09.17.
 * mail: mail@sunnydaydev.me
 */

class CheckedHost {

    public String host;
    public boolean externalLoginAllowed;

    CheckedHost(String host, boolean externalLoginAllowed) {
        this.host = host;
        this.externalLoginAllowed = externalLoginAllowed;
    }

}
