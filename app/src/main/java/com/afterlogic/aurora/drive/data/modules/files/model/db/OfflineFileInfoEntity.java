package com.afterlogic.aurora.drive.data.modules.files.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Entity
public class OfflineFileInfoEntity {

    @Id
    private String pathSpec;

    private String offlineType;

    private long lastUse;

    @Generated(hash = 1610096244)
    public OfflineFileInfoEntity(String pathSpec, String offlineType,
            long lastUse) {
        this.pathSpec = pathSpec;
        this.offlineType = offlineType;
        this.lastUse = lastUse;
    }

    @Generated(hash = 1353197095)
    public OfflineFileInfoEntity() {
    }

    public String getPathSpec() {
        return this.pathSpec;
    }

    public void setPathSpec(String pathSpec) {
        this.pathSpec = pathSpec;
    }

    public String getOfflineType() {
        return this.offlineType;
    }

    public void setOfflineType(String offlineType) {
        this.offlineType = offlineType;
    }

    public long getLastUse() {
        return this.lastUse;
    }

    public void setLastUse(long lastUse) {
        this.lastUse = lastUse;
    }
}
