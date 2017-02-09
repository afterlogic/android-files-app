package com.afterlogic.aurora.drive.presentation.modules.filesMain.interactor;

import com.afterlogic.aurora.drive.presentation.common.modules.interactor.Interactor;
import com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel.FileType;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by sashka on 06.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MainFilesInteractor extends Interactor {
    Single<List<FileType>> getAvailableFileTypes();
}
