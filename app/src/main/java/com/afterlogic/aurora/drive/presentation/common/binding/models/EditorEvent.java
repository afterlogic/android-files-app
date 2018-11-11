package com.afterlogic.aurora.drive.presentation.common.binding.models;

import android.view.KeyEvent;

/**
 * Created by sunny on 08.09.17.
 * mail: mail@sunnydaydev.me
 */

public class EditorEvent {

    private final int actionId;
    private final KeyEvent keyEvent;

    public EditorEvent(int actionId, KeyEvent keyEvent) {
        this.actionId = actionId;
        this.keyEvent = keyEvent;
    }

    public int getActionId() {
        return actionId;
    }

    public KeyEvent getKeyEvent() {
        return keyEvent;
    }
}
