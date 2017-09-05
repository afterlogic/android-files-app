package com.afterlogic.aurora.drive.presentation.modules.login.viewModel;

/**
 * Created by sunny on 31.08.17.
 */

class LastLoggedHost {

    public boolean relogin;

    public String host;

    public LastLoggedHost(boolean relogin, String host) {
        this.relogin = relogin;
        this.host = host;
    }
}
