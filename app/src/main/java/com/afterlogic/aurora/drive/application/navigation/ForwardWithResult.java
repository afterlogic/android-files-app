package com.afterlogic.aurora.drive.application.navigation;

import ru.terrakok.cicerone.commands.Command;

/**
 * Created by aleksandrcikin on 14.07.17.
 * mail: mail@sunnydaydev.me
 */
class ForwardWithResult implements Command {

    private String screenName;
    private int requestId;
    private Object transitionData;

    public ForwardWithResult(String screenName, int requestId, Object transitionData) {
        this.screenName = screenName;
        this.requestId = requestId;
        this.transitionData = transitionData;
    }

    public String getScreenKey() {
        return screenName;
    }

    public int getRequestCode() {
        return requestId;
    }

    public Object getTransitionData() {
        return transitionData;
    }
}
