package com.afterlogic.aurora.drive.data.modules.files.service;

import com.afterlogic.aurora.drive.data.modules.files.model.db.OfflineFileInfoEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by sashka on 14.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FilesLocalService {
    Completable addOffline(OfflineFileInfoEntity entity);

    Completable removeOffline(String pathSpec);

    Single<List<OfflineFileInfoEntity>> getOffline();

    Maybe<OfflineFileInfoEntity> get(String pathSpec);
}
