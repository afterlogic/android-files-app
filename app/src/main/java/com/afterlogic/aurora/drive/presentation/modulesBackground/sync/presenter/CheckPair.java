package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.presenter;

import androidx.annotation.NonNull;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by sashka on 14.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

class CheckPair {

    private final AuroraFile mLocal;
    private final AuroraFile mRemote;

    @NonNull
    private SyncType mSyncType = SyncType.NO_NEED;

    public CheckPair(AuroraFile local, AuroraFile remote) {
        mLocal = local;
        mRemote = remote;
    }

    public AuroraFile getLocal() {
        return mLocal;
    }

    public AuroraFile getRemote() {
        return mRemote;
    }

    public void setSyncType(@NonNull SyncType syncType) {
        mSyncType = syncType;
    }

    @NonNull
    public SyncType getSyncType() {
        return mSyncType;
    }
}
