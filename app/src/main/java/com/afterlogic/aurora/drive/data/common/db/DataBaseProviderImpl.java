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

    private final Context mAppContext;

    private DaoSession mDaoSession;
    private DbOpenHelper mOpenHelper;
    private boolean mInstantiated = false;

    @Inject DataBaseProviderImpl(Context context) {
        mAppContext = context;
    }

    @Override
    public OfflineFileInfoEntityDao offlineFileInfo() {
        return getSession().getOfflineFileInfoEntityDao();
    }

    @Override
    public synchronized void reset() {
        if (!mInstantiated) return;

        mDaoSession.clear();
        mOpenHelper.close();
        mDaoSession = null;
        mOpenHelper = null;
        mInstantiated = false;
    }

    private synchronized DaoSession getSession(){
        if (mInstantiated) return mDaoSession;

        mOpenHelper = new DbOpenHelper(mAppContext, "cache.bd");
        DaoMaster master = new DaoMaster(mOpenHelper.getWritableDb());

        mDaoSession = master.newSession();
        mInstantiated = true;
        return mDaoSession;
    }
}
