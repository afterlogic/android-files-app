package com.afterlogic.aurora.drive.presentation.modules.offline.view;

import com.github.nitrico.lastadapter.StableId;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

class OfflineEmptyListItem implements StableId {

    @Override
    public long getStableId() {
        return Long.MIN_VALUE + 1;
    }
}
