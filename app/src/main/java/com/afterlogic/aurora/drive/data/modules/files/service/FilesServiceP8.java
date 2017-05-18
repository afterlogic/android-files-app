package com.afterlogic.aurora.drive.data.modules.files.service;

import com.afterlogic.aurora.drive.data.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive.data.model.project8.FilesResponseP8;
import com.afterlogic.aurora.drive.data.model.project8.UploadResultP8;
import com.afterlogic.aurora.drive.core.common.interfaces.ProgressListener;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.afterlogic.aurora.drive.model.FileInfo;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FilesServiceP8 {

    Single<ApiResponseP8<FilesResponseP8>> getFiles(String type, String path, String pattern);

    Single<ResponseBody> getFileThumbnail(String type, String path, String name, String publicHash);

    Single<ResponseBody> viewFile(String type, String path, String name, String publicHash);

    Single<ApiResponseP8<Boolean>> createFolder(String type, String path, String name);

    Single<ApiResponseP8<Boolean>> renameFile(String type, String path, String name, String newName, boolean isLink);

    Single<ApiResponseP8<Boolean>> delete(String type, List<DeleteFileInfo> files);

    Single<ResponseBody> downloadFile(String type, String path, String name, String publicHash);

    Single<ApiResponseP8<UploadResultP8>> uploadFile(String type, String path, FileInfo fileInfo, ProgressListener progressUpdater);
}
