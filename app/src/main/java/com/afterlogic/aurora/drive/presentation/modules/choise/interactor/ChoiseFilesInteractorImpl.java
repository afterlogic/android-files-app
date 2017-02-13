package com.afterlogic.aurora.drive.presentation.modules.choise.interactor;

import android.content.Context;

import com.afterlogic.aurora.drive._unrefactored.core.util.DownloadType;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.BaseFilesListInteractor;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Observable;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoiseFilesInteractorImpl extends BaseFilesListInteractor implements ChoiseFilesInteractor {

    private final FilesRepository mFilesRepository;
    private final Context mAppContext;

    @Inject
    ChoiseFilesInteractorImpl(ObservableScheduler scheduler,
                              FilesRepository filesRepository,
                              Context appContext) {
        super(scheduler, filesRepository);
        mFilesRepository = filesRepository;
        mAppContext = appContext;
    }


    @Override
    public Observable<Progressible<File>> download(AuroraFile file) {
        return mFilesRepository.download(file, FileUtil.getTargetFileByType(file, DownloadType.DOWNLOAD_OPEN, mAppContext))
                .compose(this::composeDefault);
    }
}
