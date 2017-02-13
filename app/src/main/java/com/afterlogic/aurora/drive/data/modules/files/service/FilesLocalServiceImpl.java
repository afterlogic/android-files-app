package com.afterlogic.aurora.drive.data.modules.files.service;

import com.afterlogic.aurora.drive.data.common.db.DataBaseProvider;
import com.afterlogic.aurora.drive.data.common.service.LocalService;
import com.afterlogic.aurora.drive.data.modules.files.model.db.OfflineFileInfoEntity;
import com.afterlogic.aurora.drive.data.modules.files.model.db.OfflineFileInfoEntityDao;
import com.afterlogic.aurora.drive.data.modules.files.model.db.OfflineFileInfoEntityDao.Properties;
import com.afterlogic.aurora.drive.model.OfflineType;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FilesLocalServiceImpl extends LocalService{

    private final OfflineFileInfoEntityDao mOfflineFileInfoDao;

    @Inject FilesLocalServiceImpl(DataBaseProvider db) {
        mOfflineFileInfoDao = db.offlineFileInfo();
    }

    public Completable addOffline(OfflineFileInfoEntity entity){
        return defer(() -> mOfflineFileInfoDao.insertOrReplaceInTx(entity));
    }

    public Completable removeOffline(String pathSpec){
        return defer(() -> mOfflineFileInfoDao.deleteByKey(pathSpec));
    }

    public Single<List<OfflineFileInfoEntity>> getOffline(){
        return resultOrEmptyList(() -> mOfflineFileInfoDao.queryBuilder()
                .where(Properties.OfflineType.eq(OfflineType.OFFLINE.toString()))
                .list()
        );
    }

    public Maybe<OfflineFileInfoEntity> get(String pathSpec){
        return resultOrEmpty(() -> mOfflineFileInfoDao.load(pathSpec));
    }
}
