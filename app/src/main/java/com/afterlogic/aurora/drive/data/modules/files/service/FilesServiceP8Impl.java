package com.afterlogic.aurora.drive.data.modules.files.service;

import android.content.Context;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.interfaces.ProgressListener;
import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.ExtRequestBody;
import com.afterlogic.aurora.drive.data.common.network.ParamsBuilder;
import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.data.common.network.p8.CloudServiceP8;
import com.afterlogic.aurora.drive.data.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive.data.model.project8.FilesResponseP8;
import com.afterlogic.aurora.drive.data.model.project8.UploadResultP8;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.ReplaceFileDto;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.gson.Gson;

import java.util.HashMap;
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
public class FilesServiceP8Impl extends CloudServiceP8 implements FilesServiceP8 {

    private final Api8 mApi;
    private final Context mContext;

    @Inject FilesServiceP8Impl(Api8 api, @P8 Gson gson, Context context) {
        super(Api8.Module.FILES, gson);
        mApi = api;
        mContext = context;
    }

    @Override
    public Single<ApiResponseP8<FilesResponseP8>> getFiles(String type, String path, String pattern) {
        return Single.defer(() -> mApi.getFiles(getDefaultFields(
                Api8.Method.GET_FILES,
                new ParamsBuilder()
                        .put(Api8.Param.TYPE, type)
                        .put(Api8.Param.PATH, path)
                        .put(Api8.Param.PATTERN, pattern)
                        .create()
        )));
    }

    @Override
    public Single<ResponseBody> getFileThumbnail(String type, String path, String name, String publicHash) {
        return Single.defer(() -> mApi.getFileThumbnail(getDefaultFields(
                Api8.Method.GET_FILE_THUMBNAIL,
                new ParamsBuilder()
                        .put(Api8.Param.TYPE, type)
                        .put(Api8.Param.PATH, path)
                        .put(Api8.Param.NAME, name)
                        .put(Api8.Param.SHARED_HASH, publicHash)
                        .create()
        )));
    }

    @Override
    public Single<ResponseBody> viewFile(String type, String path, String name, String publicHash) {
        return Single.defer(() -> mApi.getFile(getDefaultFields(
                Api8.Method.VIEW_FILE,
                new ParamsBuilder()
                        .put(Api8.Param.TYPE, type)
                        .put(Api8.Param.PATH, path)
                        .put(Api8.Param.NAME, name)
                        .put(Api8.Param.SHARED_HASH, publicHash)
                        .create()
        )));
    }

    @Override
    public Single<ApiResponseP8<Boolean>> createFolder(String type, String path, String name) {
        return Single.defer(() -> {
            HashMap<String, Object> params = new ParamsBuilder()
                    .put(Api8.Param.TYPE, type)
                    .put(Api8.Param.PATH, path)
                    .put(Api8.Param.FOLDER_NAME, name)
                    .create();

            Map<String, Object> fields = getDefaultFields(
                    Api8.Method.CREATE_FOLDER,
                    params
            );
        return mApi.createFolder(fields);
        });
    }

    @Override
    public Single<ApiResponseP8<Boolean>> renameFile(String type, String path, String name, String newName, boolean isLink) {
        return Single.defer(() -> {
            HashMap<String, Object> params = new ParamsBuilder()
                    .put(Api8.Param.TYPE, type)
                    .put(Api8.Param.PATH, path)
                    .put(Api8.Param.NAME, name)
                    .put(Api8.Param.NEW_NAME, newName)
                    .put(Api8.Param.IS_LINK, isLink)
                    .create();
            Map<String, Object> fields = getDefaultFields(Api8.Method.RENAME, params);
            return mApi.rename(fields);
        });
    }

    @Override
    public Single<ApiResponseP8<Boolean>> delete(String type, List<DeleteFileInfo> files) {
        return Single.defer(() -> {
            HashMap<String, Object> params = new ParamsBuilder()
                    .put(Api8.Param.TYPE, type)
                    .put(Api8.Param.ITEMS, files)
                    .create();
            Map<String, Object> fields = getDefaultFields(Api8.Method.DELETE, params);
            return mApi.delete(fields);
        });
    }

    @Override
    public Single<ResponseBody> downloadFile(String type, String path, String name, String publicHash) {
        return Single.defer(() -> {
            HashMap<String, Object> params = new ParamsBuilder()
                    .put(Api8.Param.TYPE, type)
                    .put(Api8.Param.PATH, path)
                    .put(Api8.Param.NAME, name)
                    .put(Api8.Param.SHARED_HASH, publicHash)
                    .create();
            // TODO: Return to DOWNLOAD_FILE ?
            Map<String, Object> fields = getDefaultFields(Api8.Method.VIEW_FILE, params);
            return mApi.getFile(fields);
        });
    }

    @Override
    public Single<ApiResponseP8<UploadResultP8>> uploadFile(String type, String path, FileInfo fileInfo, @Nullable ProgressListener progressUpdater) {
        return Single.defer(() -> {
            Map<String, Object> params = new ParamsBuilder()
                    .put(Api8.Param.TYPE, type)
                    .put(Api8.Param.PATH, path)
                    .put(Api8.Param.NAME, fileInfo.getName())
                    .create();

            List<MultipartBody.Part> fields = Stream.of(getDefaultFields(Api8.Method.UPLOAD_FILE, params))
                    .map(this::stringBody)
                    .collect(Collectors.toList());

            ExtRequestBody uploadBody = new ExtRequestBody(fileInfo, mContext);
            if (progressUpdater != null){
                uploadBody.setProgressListener(progressUpdater);
            }
            MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                    Api8.ApiField.JUA_UPLOADER, fileInfo.getName(), uploadBody
            );

            return mApi.upload(fields, filePart);
        });
    }

    @Override
    public Single<ApiResponseP8<String>> createPublicLink(String type, String path, String name, long size, boolean isFolder) {
        return Single.defer(() -> {
            Map<String, Object> params = new ParamsBuilder()
                    .put(Api8.Param.TYPE, type)
                    .put(Api8.Param.PATH, path)
                    .put(Api8.Param.NAME, name)
                    .put(Api8.Param.SIZE, size)
                    .put(Api8.Param.IS_FOLDER, isFolder ? 1 : 0)
                    .create();
            Map<String, Object> fields = getDefaultFields(Api8.Method.CREATE_PUBLIC_LINK, params);
            return mApi.createPublicLink(fields);
        });
    }

    @Override
    public Single<ApiResponseP8<Boolean>> deletePublicLink(String type, String path, String name) {
        return Single.defer(() -> {
            Map<String, Object> params = new ParamsBuilder()
                    .put(Api8.Param.TYPE, type)
                    .put(Api8.Param.PATH, path)
                    .put(Api8.Param.NAME, name)
                    .create();
            Map<String, Object> fields = getDefaultFields(Api8.Method.DELETE_PUBLIC_LINK, params);
            return mApi.deletePublicLink(fields);
        });
    }

    @Override
    public Single<ApiResponseP8<Boolean>> replaceFiles(String fromType, String toType, String fromPath, String toPath, List<ReplaceFileDto> files) {
        return Single.defer(() -> {
            Map<String, Object> params = new ParamsBuilder()
                    .put(Api8.Param.FROM_PATH, fromPath)
                    .put(Api8.Param.TO_PATH, toPath)
                    .put(Api8.Param.FROM_TYPE, fromType)
                    .put(Api8.Param.TO_TYPE, toType)
                    .put(Api8.Param.FILES, files)
                    .create();
            Map<String, Object> fields = getDefaultFields(Api8.Method.MOVE, params);
            return mApi.replaceFiles(fields);
        });
    }

    @Override
    public Single<ApiResponseP8<Boolean>> copyFiles(String fromType, String toType, String fromPath, String toPath, List<ReplaceFileDto> files) {
        return Single.defer(() -> {
            Map<String, Object> params = new ParamsBuilder()
                    .put(Api8.Param.FROM_PATH, fromPath)
                    .put(Api8.Param.TO_PATH, toPath)
                    .put(Api8.Param.FROM_TYPE, fromType)
                    .put(Api8.Param.TO_TYPE, toType)
                    .put(Api8.Param.FILES, files)
                    .create();
            Map<String, Object> fields = getDefaultFields(Api8.Method.COPY, params);
            return mApi.copyFiles(fields);
        });
    }


    private MultipartBody.Part stringBody(Map.Entry<String, Object> value){
        return MultipartBody.Part.createFormData(value.getKey(), String.valueOf(value.getValue()));
    }

}
