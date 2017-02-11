package com.afterlogic.aurora.drive.presentation.modules.filesMain.interactor;

import com.afterlogic.aurora.drive.presentation.common.modules.interactor.Interactor;
import com.afterlogic.aurora.drive.model.FileType;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFilesInteractor extends Interactor {

    Single<String> getUserLogin();

    Single<List<FileType>> getAvailableFileTypes();

    Completable logout();
}
