package com.afterlogic.aurora.drive.presentation.modules.upload.interactor;

import android.net.Uri;

import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.BaseFilesListInteractor;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class UploadFilesInteractorImpl extends BaseFilesListInteractor implements UploadFilesInteractor {

    private final FilesRepository mFilesRepository;

    @Inject UploadFilesInteractorImpl(ObservableScheduler scheduler, FilesRepository filesRepository) {
        super(scheduler, filesRepository);
        mFilesRepository = filesRepository;
    }


    @Override
    public Single<AuroraFile> createFolder(AuroraFile parentFolder, String name) {
        AuroraFile newFolder = AuroraFile.create(parentFolder, name, true);
        return mFilesRepository.createFolder(newFolder)
                .andThen(mFilesRepository.checkFile(newFolder))
                .compose(this::composeDefault);
    }

    @Override
    public Observable<Progressible<AuroraFile>> uploadFile(AuroraFile folder, Uri file) {
        return mFilesRepository.uploadFile(folder, file)
                .compose(this::composeDefault);
    }
}
