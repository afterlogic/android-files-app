package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.interactor;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.Interactor;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by sashka on 14.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface SyncInteractor extends Interactor {

    Single<List<AuroraFile>> getOfflineFiles();

    Single<AuroraFile> check(AuroraFile file);

    Observable<Progressible<AuroraFile>> upload(AuroraFile file);

    Observable<Progressible<AuroraFile>> download(AuroraFile file);

    Completable removeOffline(AuroraFile file);
}
