package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import com.github.nitrico.lastadapter.StableId;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineHeader implements StableId {

    private boolean manual;

    public OfflineHeader(boolean manual) {
        this.manual = manual;
    }

    @Override
    public long getStableId() {
        return Long.MIN_VALUE;
    }

    public boolean getIsManual() {
        return manual;
    }
}
