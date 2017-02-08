package com.afterlogic.aurora.drive.presentation.modules.filelist.interactor;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.DownloadType;
import com.afterlogic.aurora.drive.core.common.rx.ObservableScheduler;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.modules.interactor.BaseInteractor;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileListInteractorImpl extends BaseInteractor implements FileListInteractor {

    private final FilesRepository mFilesRepository;
    private final Context mAppContext;

    @Inject FileListInteractorImpl(ObservableScheduler scheduler,
                                   FilesRepository filesRepository, Context appContext) {
        super(scheduler);
        mFilesRepository = filesRepository;
        mAppContext = appContext;
    }

    @Override
    public Single<List<AuroraFile>> getFilesList(AuroraFile folder) {
        return mFilesRepository.getFiles(folder)
                .compose(this::composeDefault);
    }

    @Override
    public Single<Uri> getThumbnail(AuroraFile file) {
        return mFilesRepository.getFileThumbnail(file)
                .compose(this::composeDefault);
    }

    @Override
    public Observable<Progressible<File>> downloadFileForOpen(AuroraFile file) {
        return mFilesRepository.download(file, FileUtil.getTargetFileByType(file, DownloadType.DOWNLOAD_OPEN, mAppContext))
                .compose(this::composeDefault);
    }

    @Override
    public Observable<Progressible<File>> downloadFile(AuroraFile file) {
        return mFilesRepository.download(file, FileUtil.getTargetFileByType(file, DownloadType.DOWNLOAD_TO_DOWNLOADS, mAppContext))
                .doOnNext(progress -> {
                    if (progress.isDone()){
                        File target = progress.getData();
                        DownloadManager dm = (DownloadManager) mAppContext.getSystemService(DOWNLOAD_SERVICE);
                        dm.addCompletedDownload(
                                target.getName(),
                                mAppContext.getString(R.string.prompt_downloaded_file_description),
                                true,
                                FileUtil.getFileMimeType(target),
                                target.getAbsolutePath(),
                                target.length(),
                                false
                        );
                    }
                })
                .compose(this::composeDefault);
    }
}
