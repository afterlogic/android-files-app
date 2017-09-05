package com.afterlogic.aurora.drive.model.error;

import com.afterlogic.aurora.drive.core.common.rx.SilentError;
import com.afterlogic.aurora.drive.core.common.rx.UnloggableError;

/**
 * Created by sunny on 01.09.17.
 */

public class AccountChangingNotConfirmed extends RuntimeException implements UnloggableError, SilentError {

    public AccountChangingNotConfirmed() {
        super("Account changing not confirmed.");
    }
}
