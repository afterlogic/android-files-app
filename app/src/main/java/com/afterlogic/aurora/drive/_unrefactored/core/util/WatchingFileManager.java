package com.afterlogic.aurora.drive._unrefactored.core.util;

import android.content.Context;

import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive._unrefactored.presentation.services.FileObserverService;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.io.File;
import java.sql.SQLException;

/**
 * Created by sashka on 14.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class WatchingFileManager {

    private Context mContext;

    public WatchingFileManager(Context context) {
        mContext = context;
    }

    public static WatchingFileManager from(Context ctx){
        return new WatchingFileManager(ctx);
    }

    /**
     * Add file for watching to db
     * and refresh {@link android.os.FileObserver}s at {@link FileObserverService}
     *
     * @param remote - remote {@link AuroraFile}
     * @param local - local copy of file.
     */
    public void addWatching(AuroraFile remote, File local, int type, boolean synced){
        DBHelper dbHelper = new DBHelper(mContext);
        WatchingFileDAO dao = dbHelper.getWatchingFileDAO();
        try {
            WatchingFile watchingFile = new WatchingFile(remote, local, type, synced);
            dao.createOrUpdate(watchingFile);

            //SyncService.FileSyncAdapter.sendSyncStateChangedBroadcast(watchingFile, 0, 0, mContext);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbHelper.close();
    }

    /**
     * Remove file from watching and notify {@link FileObserverService}
     * @param local - local target file for removing from watching files.
     */
    public void removeFileFromWatching(File local) {
        removeFileFromWatching(WatchingFile.COLUMN_LOCAL_FILE, local.getAbsolutePath());
    }

    /**
     * Remove {@link WatchingFile} from watching and notify {@link FileObserverService}.
     * @param file - target file for removing
     */
    public void removeFileFromWatching(WatchingFile file) {
        removeFileFromWatching(WatchingFile.COLUMN_REMOTE_FILE_SPEC, file.getRemoteUniqueSpec());
    }

    public void removeFromWatching(AuroraFile file){
        removeFileFromWatching(WatchingFile.COLUMN_REMOTE_FILE_SPEC,
                WatchingFile.Spec.getRemoteUniqueSpec(file));
    }

    /**
     * Remove file from watching db and notify {@link FileObserverService}
     * @param id - file id (Local file path)
     */
    private void removeFileFromWatching(String field, String id) {
        DBHelper db = new DBHelper(mContext);
        WatchingFileDAO dao = db.getWatchingFileDAO();
        try {
            DeleteBuilder<WatchingFile, String> deleteBuilder = dao.deleteBuilder();
            deleteBuilder.where()
                    .eq(field, id);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isOffline(AuroraFile file){
        WatchingFile watchingFile = getWatching(file);
        return watchingFile != null && watchingFile.getType() == WatchingFile.TYPE_OFFLINE;
    }

    public WatchingFile getWatching(AuroraFile file){
        DBHelper db = new DBHelper(mContext);
        WatchingFileDAO dao = db.getWatchingFileDAO();
        WatchingFile watchingFile = dao.getWatching(file);
        db.close();
        return watchingFile;
    }
}
