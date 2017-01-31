package com.afterlogic.aurora.drive.data.common.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.afterlogic.aurora.drive.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive.data.common.db.model.WatchingFile;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by sashka on 13.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class DBHelper extends OrmLiteSqliteOpenHelper{
    public static final String TAG = DBHelper.class.getSimpleName();

    public static final String DB_NAME = "files.db";
    public static final int DB_VERSION = 8;


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try
        {
            TableUtils.createTable(connectionSource, WatchingFile.class);
        }
        catch (SQLException e){
            Log.e(TAG, "error creating DB " + DB_NAME);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        //TODO update db
        try {
            TableUtils.dropTable(connectionSource, WatchingFile.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get watching file dao.
     * @return - result {@link WatchingFileDAO} or null if exception.
     */
    public WatchingFileDAO getWatchingFileDAO(){
        try {
            return new WatchingFileDAO(getConnectionSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
