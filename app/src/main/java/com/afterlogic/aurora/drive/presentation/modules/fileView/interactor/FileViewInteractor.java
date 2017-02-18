package com.afterlogic.aurora.drive.presentation.modules.fileView.interactor;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.FilesListInteractor;

import java.io.File;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewInteractor extends FilesListInteractor {

    Observable<Progressible<File>> downloadForOpen(AuroraFile file);

    Observable<Progressible<File>> downloadToDownloads(AuroraFile file);

    Single<AuroraFile> rename(AuroraFile file, String newName);

    Completable deleteFile(AuroraFile file);

    Completable setOffline(AuroraFile file, boolean offline);

    Single<Boolean> getOfflineStatus(AuroraFile file);
}
