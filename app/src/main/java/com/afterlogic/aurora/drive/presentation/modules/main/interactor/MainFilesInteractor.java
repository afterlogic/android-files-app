package com.afterlogic.aurora.drive.presentation.modules.main.interactor;

import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.FilesInteractor;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFilesInteractor extends FilesInteractor {

    Single<String> getUserLogin();

    Completable logout();
}
