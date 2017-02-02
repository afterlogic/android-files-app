package com.afterlogic.aurora.drive.data.common.db;

import android.content.Context;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.data.modules.files.model.db.DaoMaster;
import com.afterlogic.aurora.drive.data.modules.files.model.db.DaoSession;

import org.greenrobot.greendao.database.Database;


/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
class DbOpenHelper extends DaoMaster.OpenHelper {

    @SuppressWarnings("WeakerAccess")
    public DbOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        MyLog.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion);

        if (!manualUpdate(db, oldVersion, newVersion)){
            MyLog.majorException(this, "Update db by dropping all tables.");
            DaoMaster.dropAllTables(db, true);
            onCreate(db);
        }
    }

    @SuppressWarnings("UnusedAssignment")
    private boolean manualUpdate(Database db, int currentVersion, int newVersion){
        DaoMaster master = new DaoMaster(db);
        DaoSession session = master.newSession();

        session.clear();

        return currentVersion == newVersion;
    }
}
