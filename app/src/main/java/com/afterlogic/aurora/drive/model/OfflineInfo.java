package com.afterlogic.aurora.drive.model;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class OfflineInfo {

    private OfflineType mOfflineType;

    public OfflineInfo(OfflineType offlineType) {
        mOfflineType = offlineType;
    }

    public OfflineType getOfflineType() {
        return mOfflineType;
    }
}
