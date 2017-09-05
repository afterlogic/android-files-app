package com.afterlogic.aurora.drive.application.navigation;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;

import ru.terrakok.cicerone.commands.Forward;

/**
 * Created by aleksandrcikin on 17.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ForwardWithErrorHandling extends Forward implements WithErrorHandling{

    private Consumer<Throwable> onError;


    public ForwardWithErrorHandling(String screenName, Consumer<Throwable> onError, Object transitionData) {
        super(screenName, transitionData);
        this.onError = onError;
    }

    @Override
    public Consumer<Throwable> getErrorConsumer() {
        return onError;
    }
}
