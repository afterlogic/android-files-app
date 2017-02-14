package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.interactor;

import android.net.Uri;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.data.modules.files.FilesDataModule;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.modules.interactor.BaseInteractor;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by sashka on 14.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class SyncInteractorImpl extends BaseInteractor implements SyncInteractor {

    private final FilesRepository mFilesRepository;
    private final File mOfflineFolder;

    @Inject SyncInteractorImpl(ObservableScheduler scheduler,
                               FilesRepository filesRepository,
                               @Named(FilesDataModule.OFFLINE_DIR) File offlineFolder) {
        super(scheduler);
        mFilesRepository = filesRepository;
        mOfflineFolder = offlineFolder;
    }

    @Override
    public Single<List<AuroraFile>> getOfflineFiles() {
        return mFilesRepository.getOfflineFiles()
                .compose(this::composeImmediate);
    }

    @Override
    public Single<AuroraFile> check(AuroraFile file) {
        return mFilesRepository.checkFile(file);
    }

    @Override
    public Observable<Progressible<AuroraFile>> upload(AuroraFile file) {
        return Observable.defer(() -> {
            File realFile = FileUtil.getFile(mOfflineFolder, file);
            return mFilesRepository.rewriteFile(file, Uri.fromFile(realFile))
                    .doOnNext(progress -> {
                        if (progress.isDone()){
                            if (!realFile.setLastModified(progress.getData().getLastModified())){
                                MyLog.majorException(new IOException("Can't set last modified"));
                            }
                        }
                    });
        });
    }

    @Override
    public Observable<Progressible<AuroraFile>> download(AuroraFile file) {
        return Observable.defer(() -> {
            File realFile = FileUtil.getFile(mOfflineFolder, file);
            return mFilesRepository.download(file, realFile)
                    .map(progress -> progress.map(file));
        });
    }
}
