package com.afterlogic.aurora.drive.data.common.db;

import com.afterlogic.aurora.drive.data.modules.files.model.db.OfflineFileInfoEntityDao;

/**
 * Created by sashka on 15.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface DataBaseProvider {
    OfflineFileInfoEntityDao offlineFileInfo();
    void reset();
}
