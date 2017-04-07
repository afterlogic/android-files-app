package com.afterlogic.aurora.drive.presentation.modules.offline.model.interactor;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface OfflineInteractor {
    Single<List<AuroraFile>> getOfflineFiles();
    Observable<Progressible<File>> downloadForOpen(AuroraFile file);
    Observable<Progressible<File>> downloadToDownloads(AuroraFile file);
    Single<Uri> getThumbnail(AuroraFile file);

    Single<Boolean> getAuthStatus();

    Observable<Boolean> listenNetworkState();
}
