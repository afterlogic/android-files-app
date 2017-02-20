package com.afterlogic.aurora.drive.presentation.modules.fileView.interactor;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.BaseInteractor;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewImageItemInteractorImpl extends BaseInteractor implements FileViewImageItemInteractor {

    private final FilesRepository mFilesRepository;
    private final File mCacheDir;

    @Inject FileViewImageItemInteractorImpl(ObservableScheduler scheduler,
                                            FilesRepository filesRepository,
                                            @Named(FilesDataModule.CACHE_DIR) File cacheDir) {
        super(scheduler);
        mFilesRepository = filesRepository;
        mCacheDir = cacheDir;
    }

    @Override
    public Observable<Progressible<File>> donwloadToCache(AuroraFile file) {
        File cacheTarget = new File(mCacheDir, file.getPathSpec());
        return mFilesRepository.downloadOrGetOffline(file, cacheTarget)
                .compose(this::composeDefault);
    }
}
