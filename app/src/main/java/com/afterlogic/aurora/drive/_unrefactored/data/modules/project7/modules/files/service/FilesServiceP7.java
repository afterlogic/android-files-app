package com.afterlogic.aurora.drive._unrefactored.data.modules.project7.modules.files.service;

import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiTask;
import com.afterlogic.aurora.drive._unrefactored.model.AuroraFilesResponse;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;
import com.afterlogic.aurora.drive._unrefactored.model.project7.AuroraFileP7;
import com.afterlogic.aurora.drive._unrefactored.model.project7.UploadResultP7;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FilesServiceP7 {

    Single<ApiResponseP7<AuroraFilesResponse>> getFiles(String path, String type, String filterPatter);

    Single<ApiResponseP7<AuroraFileP7>> checkFile(String type, String folder, String name);

    Single<ApiResponseP7<Boolean>> createFolder(String type, String path, String name);

    Single<ApiResponseP7<Boolean>> renameFile(String type, String path, String name, String newName, boolean isLink);

    Single<ApiResponseP7<Boolean>> deleteFiles(String type, List<AuroraFileP7> files);

    Single<ResponseBody> download(AuroraFileP7 file);

    Single<ApiResponseP7<UploadResultP7>> upload(AuroraFileP7 file, FileInfo source, @Nullable ApiTask.ProgressUpdater progressUpdater);
}
