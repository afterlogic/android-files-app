package com.afterlogic.aurora.drive.model;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public enum OfflineType {

    NOT_OFFLINE(Constants.NOT_OFFLINE), CACHE(Constants.CACHE), OFFLINE(Constants.OFFLINE);

    private final String mName;

    public static OfflineType parse(String name){
        switch (name){
            case Constants.CACHE: return CACHE;
            case Constants.OFFLINE: return OFFLINE;
            case Constants.NOT_OFFLINE: return NOT_OFFLINE;

            default: throw new IllegalArgumentException("Illeagal offline type name.");
        }
    }

    OfflineType(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }

    private static class Constants {
        private static final String CACHE = "cache";
        private static final String OFFLINE = "offline";
        private static final String NOT_OFFLINE = "not_offline";
    }
}
