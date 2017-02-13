package com.afterlogic.aurora.drive.model;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public enum SyncState {

    UNKNOWN(Constants.UNKNOWN),
    REQUIRE_SYNC(Constants.REQUIRE_SYNC),
    SYNCING(Constants.SYNCING),
    SYNCED(Constants.SYNCED);

    private final String mName;

    public static SyncState parse(String name){
        switch (name){
            case Constants.REQUIRE_SYNC: return REQUIRE_SYNC;
            case Constants.SYNCING: return SYNCING;
            case Constants.SYNCED: return SYNCED;

            default: return UNKNOWN;
        }
    }

    SyncState(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }

    private static class Constants {
        private static final String UNKNOWN = "unknown";
        private static final String REQUIRE_SYNC = "require_sync";
        private static final String SYNCING = "syncing";
        private static final String SYNCED = "synced";
    }
}
