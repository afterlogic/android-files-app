package com.afterlogic.aurora.drive.data.modules.files.repository;

import android.net.Uri;

import com.afterlogic.aurora.drive.data.model.UploadResult;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.Progressible;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileSubRepository {
    Single<List<String>> getAvailableFileTypes();

    Single<List<AuroraFile>> getFiles(AuroraFile folder);

    Completable rename(AuroraFile file, String newName);

    Observable<Progressible<UploadResult>> uploadFileToServer(AuroraFile folder, FileInfo fileInfo);

    Completable delete(String type, List<AuroraFile> files);

    Single<Uri> getFileThumbnail(AuroraFile file);

    Single<Uri> viewFile(AuroraFile file);

    Completable createFolder(AuroraFile file);

    Completable checkFileExisting(AuroraFile file);

    Single<AuroraFile> checkFile(AuroraFile file);

    Single<ResponseBody> downloadFileBody(AuroraFile file);
}
