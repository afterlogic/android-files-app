package com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.interactor;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.interactor.Interactor;

import java.io.File;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileObserverInteractor extends Interactor {

    Observable<File> observeOfflineFilesChanges();

    Maybe<AuroraFile> getOfflineFile(File file);

    Completable delete(File target);

    Completable requestSync();
}
