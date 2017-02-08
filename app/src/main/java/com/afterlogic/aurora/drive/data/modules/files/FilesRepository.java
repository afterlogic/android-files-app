package com.afterlogic.aurora.drive.data.modules.files;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiTask;
import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface FilesRepository {

    Single<List<String>> getAvailableFileTypes();

    Single<List<AuroraFile>> getFiles(AuroraFile folder);

    Single<Uri> getFileThumbnail(AuroraFile file);

    Single<Uri> viewFile(AuroraFile file);

    Single<Boolean> createFolder(AuroraFile file);

    Single<AuroraFile> rename(AuroraFile file, String newName);

    Single<AuroraFile> checkFile(AuroraFile file);

    Completable delete(AuroraFile files);
    Completable delete(List<AuroraFile> files);

    Single<ResponseBody> downloadFileBody(AuroraFile file);

    Observable<Progressible<File>> download(AuroraFile file, File target);

    Single<UploadResult> uploadFile(AuroraFile folder, FileInfo file, @Nullable ApiTask.ProgressUpdater progressListener);
}
