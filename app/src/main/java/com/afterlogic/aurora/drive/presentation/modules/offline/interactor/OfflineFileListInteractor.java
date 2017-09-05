package com.afterlogic.aurora.drive.presentation.modules.offline.interactor;

import android.net.Uri;

import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.SearchableFilesListInteractor;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view.SyncListener;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;
import com.annimon.stream.Stream;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

import static com.afterlogic.aurora.drive.data.modules.files.FilesDataModule.OFFLINE_DIR;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineFileListInteractor extends SearchableFilesListInteractor {

    private final FilesRepository filesRepository;
    private final File offlineDir;
    private final Provider<SyncListener> syncListener;

    @Inject
    OfflineFileListInteractor(FilesRepository filesRepository,
                              @Named(OFFLINE_DIR) File offlineDir,
                              Provider<SyncListener> syncListener) {
        super(filesRepository);
        this.filesRepository = filesRepository;
        this.offlineDir = offlineDir;
        this.syncListener = syncListener;
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder) {
        return filesRepository.getOfflineFiles();
    }

    @Override
    public Single<List<AuroraFile>> getFiles(AuroraFile folder, String pattern) {

        String preparedPattern = pattern.trim().toLowerCase();

        return getFiles(folder)
                .map(files -> Stream.of(files)
                        .filter(file -> {
                            String preparedName = file.getName()
                                    .trim()
                                    .toLowerCase();
                            return preparedName.contains(preparedPattern);
                        })
                        .toList()
                );
    }

    public Observable<Progressible<File>> downloadForOpen(AuroraFile file) {
        return Observable.defer(() -> {
            File target = FileUtil.getFile(offlineDir, file);
            if (!target.exists()){
                return Observable.error(new FileNotFoundException());
            }
            return Observable.just(new Progressible<>(target, target.length(), target.length(), file.getName(), true));
        });
    }

    public Single<Boolean> checkIsSynced(AuroraFile file) {
        // TODO: check by repository
        return Single.fromCallable(() -> FileUtil.getFile(offlineDir, file).exists());
    }

    public Completable disableOffline(AuroraFile file) {
        return filesRepository.setOffline(file, false);
    }

    public Single<Uri> getThumbnail(AuroraFile file){
        return Single.fromCallable(() -> {
            File localFile = new File(offlineDir, file.getPathSpec());
            return Uri.fromFile(localFile);
        });
    }

    public Observable<SyncProgress> listenSyncProgress() {
        SyncListener syncListener = this.syncListener.get();
        return Observable.defer(syncListener::getProgressSource)
                .doOnSubscribe(disposable -> syncListener.onStart())
                .doFinally(syncListener::onStop);
    }
}
