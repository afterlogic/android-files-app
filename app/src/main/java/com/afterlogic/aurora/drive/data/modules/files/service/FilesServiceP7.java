package com.afterlogic.aurora.drive.data.modules.files.service;

import com.afterlogic.aurora.drive.core.common.interfaces.ProgressListener;
import com.afterlogic.aurora.drive.data.model.AuroraFilesResponse;
import com.afterlogic.aurora.drive.data.model.project7.ApiResponseP7;
import com.afterlogic.aurora.drive.data.model.project7.AuroraFileP7;
import com.afterlogic.aurora.drive.data.model.project7.UploadResultP7;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.ReplaceFileDto;
import com.afterlogic.aurora.drive.model.FileInfo;

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

    Single<ApiResponseP7<UploadResultP7>> upload(AuroraFileP7 file, FileInfo source, ProgressListener progress);

    Single<ApiResponseP7<String>> createPublicLink(String type, String path, String name, long size, boolean isFolder);

    Single<ApiResponseP7<Boolean>> deletePublicLink(String type, String path, String name);

    Single<ApiResponseP7<Boolean>> replaceFiles(String fromType, String toType, String fromPath, String toPath, List<ReplaceFileDto> files);

    Single<ApiResponseP7<Boolean>> copyFiles(String fromType, String toType, String fromPath, String toPath, List<ReplaceFileDto> files);
}
