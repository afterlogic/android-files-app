package com.afterlogic.aurora.drive.application.navigation;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;

/**
 * Created by aleksandrcikin on 17.07.17.
 * mail: mail@sunnydaydev.me
 */

public interface WithErrorHandling {

    Consumer<Throwable> getErrorConsumer();
}
