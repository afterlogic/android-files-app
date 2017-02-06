package com.afterlogic.aurora.drive.data.common.db;

import android.content.Context;

import com.afterlogic.aurora.drive.data.modules.files.model.db.DaoMaster;
import com.afterlogic.aurora.drive.data.modules.files.model.db.DaoSession;
import com.afterlogic.aurora.drive.data.modules.files.model.db.OfflineFileInfoEntityDao;

import javax.inject.Inject;

/**
 * Created by sashka on 15.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class DataBaseProviderImpl implements DataBaseProvider {

    private DaoSession mDaoSession;

    @Inject DataBaseProviderImpl(Context context) {
        DbOpenHelper openHelper = new DbOpenHelper(context, "cache.bd");
        DaoMaster master = new DaoMaster(openHelper.getWritableDb());

        mDaoSession = master.newSession();
        mDaoSession.clear();
    }

    @Override
    public OfflineFileInfoEntityDao offlineFileInfo() {
        return mDaoSession.getOfflineFileInfoEntityDao();
    }
}
