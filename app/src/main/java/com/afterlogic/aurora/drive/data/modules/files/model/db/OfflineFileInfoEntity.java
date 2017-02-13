package com.afterlogic.aurora.drive.data.modules.files.model.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by sashka on 02.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@Entity
public class OfflineFileInfoEntity {

    @Id(autoincrement = true)
    private Long id;

    @Unique
    private String path;

    private String type;

    private String offlineType;

    @Generated(hash = 614231716)
    public OfflineFileInfoEntity(Long id, String path, String type,
            String offlineType) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.offlineType = offlineType;
    }

    @Generated(hash = 1353197095)
    public OfflineFileInfoEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOfflineType() {
        return this.offlineType;
    }

    public void setOfflineType(String offlineType) {
        this.offlineType = offlineType;
    }
}
