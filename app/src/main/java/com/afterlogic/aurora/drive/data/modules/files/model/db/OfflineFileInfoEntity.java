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

    @Generated(hash = 716220914)
    public OfflineFileInfoEntity(String pathSpec, String offlineType) {
        this.pathSpec = pathSpec;
        this.offlineType = offlineType;
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
}
