package com.afterlogic.aurora.drive.model;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class OfflineInfo {

    private OfflineType mOfflineType;
    private SyncState mSyncState;

    public OfflineInfo(OfflineType offlineType, SyncState syncState) {
        mOfflineType = offlineType;
        mSyncState = syncState;
    }

    public OfflineType getOfflineType() {
        return mOfflineType;
    }

    public SyncState getSyncState() {
        return mSyncState;
    }
}
