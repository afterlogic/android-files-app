package com.afterlogic.aurora.drive.data.modules.files;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiTask;
import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;

import java.util.List;

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

    Single<Boolean> rename(AuroraFile file, String newName);

    Single<AuroraFile> checkFile(AuroraFile file);

    Single<Boolean> delete(List<AuroraFile> files);

    Single<ResponseBody> downloadFileBody(AuroraFile file);

    Single<UploadResult> uploadFile(AuroraFile folder, FileInfo file, @Nullable ApiTask.ProgressUpdater progressListener);
}
