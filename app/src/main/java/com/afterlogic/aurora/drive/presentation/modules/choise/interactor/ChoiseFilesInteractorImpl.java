package com.afterlogic.aurora.drive.presentation.modules.choise.interactor;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.BaseFilesListInteractor;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoiseFilesInteractorImpl extends BaseFilesListInteractor implements ChoiseFilesInteractor {

    private final FilesRepository mFilesRepository;
    private final File mCacheDir;

    @Inject
    ChoiseFilesInteractorImpl(ObservableScheduler scheduler,
                              FilesRepository filesRepository,
                              @Named(FilesDataModule.CACHE_DIR) File cacheDir) {
        super(scheduler, filesRepository);
        mFilesRepository = filesRepository;
        mCacheDir = cacheDir;
    }


    @Override
    public Observable<Progressible<File>> download(AuroraFile file) {
        return mFilesRepository.downloadOrGetOffline(file, FileUtil.getFile(mCacheDir, file))
                .compose(this::composeDefault);
    }
}
