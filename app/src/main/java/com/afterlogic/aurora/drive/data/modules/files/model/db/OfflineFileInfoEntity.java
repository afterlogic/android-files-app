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

    @Id(autoincrement = true)
    private Long mId;

    @Generated(hash = 1723627213)
    public OfflineFileInfoEntity(Long mId) {
        this.mId = mId;
    }

    @Generated(hash = 1353197095)
    public OfflineFileInfoEntity() {
    }

    public Long getMId() {
        return this.mId;
    }

    public void setMId(Long mId) {
        this.mId = mId;
    }
}
