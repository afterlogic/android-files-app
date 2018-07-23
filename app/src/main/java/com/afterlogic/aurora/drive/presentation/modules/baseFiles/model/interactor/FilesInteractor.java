package com.afterlogic.aurora.drive.presentation.modules.baseFiles.model.interactor;

import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.Interactor;
import com.afterlogic.aurora.drive.model.Storage;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FilesInteractor extends Interactor {

    Single<List<Storage>> getAvailableFileTypes();

    Single<Boolean> getAuthStatus();
}
