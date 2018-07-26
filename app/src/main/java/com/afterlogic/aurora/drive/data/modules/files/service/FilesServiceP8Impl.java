package com.afterlogic.aurora.drive.data.modules.files.service;

import android.content.Context;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.interfaces.ProgressListener;
import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.ExtRequestBody;
import com.afterlogic.aurora.drive.data.common.network.ParamsBuilder;
import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.data.common.network.p8.CloudServiceP8;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.CreateFolderParameters;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.CreatePublicLinkParameters;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.DeleteFilesParameters;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.EmptyParameters;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.GetFileThumbnailParameters;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.GetFilesParameters;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.MoveFilesParameters;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.RenameFileParameters;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.UploadFileParameters;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.ViewFileParameters;
import com.afterlogic.aurora.drive.data.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive.data.model.project8.FilesResponseP8;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.DeletePublicLinkParameters;
import com.afterlogic.aurora.drive.data.model.project8.UploadResultP8;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.P8StorageDto;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.ShortFileDto;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FilesServiceP8Impl implements FilesServiceP8 {

    private final Api8 mApi;
    private final Context mContext;

    @Inject FilesServiceP8Impl(Api8 api, Context context) {
        mApi = api;
        mContext = context;
    }

    @Override
    public Single<ApiResponseP8<FilesResponseP8>> getFiles(String type, String path,
                                                           String pattern) {
        GetFilesParameters params = new GetFilesParameters(type, path, pattern);
        return mApi.getFiles(params);
    }

    @Override
    public Single<ResponseBody> getFileThumbnail(String type, String path,
                                                 String name, String publicHash) {
        GetFileThumbnailParameters params = new GetFileThumbnailParameters(
                type, path, name, publicHash);
        return mApi.getFileThumbnail(params);
    }

    @Override
    public Single<ResponseBody> viewFile(String type, String path, String name, String publicHash) {
        ViewFileParameters params = new ViewFileParameters(type, path, name, publicHash);
        return mApi.viewFile(params);
    }

    @Override
    public Single<ApiResponseP8<Boolean>> createFolder(String type, String path, String name) {
        CreateFolderParameters params = new CreateFolderParameters(type, path, name);
        return mApi.createFolder(params);
    }

    @Override
    public Single<ApiResponseP8<Boolean>> renameFile(String type, String path, String name,
                                                     String newName, boolean isLink) {
        RenameFileParameters params = new RenameFileParameters(type, path, name, newName, isLink);
        return mApi.rename(params);
    }

    @Override
    public Single<ApiResponseP8<Boolean>> delete(String type, List<DeleteFileInfo> files) {
        DeleteFilesParameters params = new DeleteFilesParameters(type, files);
        return mApi.delete(params);
    }

    @Override
    public Single<ResponseBody> downloadFile(String type, String path, String name,
                                             String publicHash) {
        ViewFileParameters params = new ViewFileParameters(type, path, name, publicHash);
        return mApi.viewFile(params);
    }

    @Override
    public Single<ApiResponseP8<String>> createPublicLink(
            String type, String path, String name, long size, boolean isFolder) {

        CreatePublicLinkParameters link =
                new CreatePublicLinkParameters(type, path, name, size, isFolder);
        return mApi.createPublicLink(link);

    }

    @Override
    public Single<ApiResponseP8<Boolean>> deletePublicLink(String type, String path, String name) {
        DeletePublicLinkParameters link = new DeletePublicLinkParameters(type, path, name);
        return mApi.deletePublicLink(link);
    }

    @Override
    public Single<ApiResponseP8<Boolean>> replaceFiles(
            String fromType, String toType, String toPath, List<ShortFileDto> files) {

        MoveFilesParameters request = new MoveFilesParameters(fromType, toPath, toType, files);
        return mApi.moveFiles(request);

    }

    @Override
    public Single<ApiResponseP8<Boolean>> copyFiles(
            String fromType, String toType, String toPath, List<ShortFileDto> files) {

        MoveFilesParameters request = new MoveFilesParameters(fromType, toPath, toType, files);
        return mApi.copyFiles(request);

    }

    @Override
    public Single<ApiResponseP8<List<P8StorageDto>>> getAvailableStorages() {
        return mApi.getAvailableStorages(new EmptyParameters());
    }

    @Override
    public Single<ApiResponseP8<UploadResultP8>> uploadFile(
            String type, String path, FileInfo fileInfo,
            @Nullable ProgressListener progressUpdater) {

        return Single.defer(() -> {

            ExtRequestBody uploadBody = new ExtRequestBody(fileInfo, mContext);
            if (progressUpdater != null){
                uploadBody.setProgressListener(progressUpdater);
            }

            MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                    Api8.ApiField.JUA_UPLOADER, fileInfo.getName(), uploadBody
            );

            UploadFileParameters params = new UploadFileParameters(type, path, fileInfo.getName());
            return mApi.upload(params, filePart);

        });

    }

}
