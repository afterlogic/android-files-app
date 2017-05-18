package com.afterlogic.aurora.drive.model;

/**
 * Created by aleksandrcikin on 12.05.17.
 * mail: mail@sunnydaydev.me
 */

public class Actions {

    private boolean isList = false;

    public Actions() {
    }

    public Actions(boolean isList) {
        this.isList = isList;
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean list) {
        isList = list;
    }
}
