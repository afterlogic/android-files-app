package com.afterlogic.aurora.drive.data.common.db.dao;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.data.common.db.model.WatchingFile;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sashka on 13.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class WatchingFileDAO extends BaseDaoImpl<WatchingFile, String> {

    public WatchingFileDAO(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, WatchingFile.class);
    }

    public List<WatchingFile> getFilesList(int type){
        try {
            return this.queryForEq(WatchingFile.COLUMN_TYPE, type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<WatchingFile> getFilesList(){
        try {
            return this.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public WatchingFile getWatching(AuroraFile file){
        try {
            String spec = WatchingFile.Spec.getRemoteUniqueSpec(file);
            return queryForId(spec);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
