package com.afterlogic.aurora.drive.data.modules.project8.modules.file.service;

import android.content.Context;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.core.annotations.qualifers.Project8;
import com.afterlogic.aurora.drive.data.common.ExtRequestBody;
import com.afterlogic.aurora.drive.data.common.ParamsBuilder;
import com.afterlogic.aurora.drive.data.common.SessionManager;
import com.afterlogic.aurora.drive.data.common.api.ApiTask;
import com.afterlogic.aurora.drive.data.modules.project8.common.Api8;
import com.afterlogic.aurora.drive.data.modules.project8.common.AuthorizedServiceP8;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive.model.project8.FilesResponseP8;
import com.afterlogic.aurora.drive.model.project8.UploadResultP8;
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
public class FilesServiceP8Impl extends AuthorizedServiceP8 implements FilesServiceP8 {

    private final Api8 mApi;
    private final Context mContext;

    @Inject FilesServiceP8Impl(Api8 api, @Project8 Gson gson, Context context, SessionManager sessionManager) {
        super(Api8.Module.FILES, sessionManager, gson);
        mApi = api;
        mContext = context;
    }

    @Override
    public Single<ApiResponseP8<FilesResponseP8>> getFiles(String type, String path, String pattern) {
        return mApi.getFiles(getDefaultFields(
                Api8.Method.GET_FILES,
                new ParamsBuilder()
                        .put(Api8.Param.TYPE, type)
                        .put(Api8.Param.PATH, path)
                        .put(Api8.Param.PATTERN, pattern)
                        .create()
        ));
    }

    @Override
    public Single<ApiResponseP8<String>> getFileThumbnail(String type, String path, String name, String publicHash) {
        return mApi.getFileThumbnail(getDefaultFields(
                Api8.Method.GET_FILE_THUMBNAIL,
                new ParamsBuilder()
                        .put(Api8.Param.TYPE, type)
                        .put(Api8.Param.PATH, path)
                        .put(Api8.Param.NAME, name)
                        .put(Api8.Param.SHARED_HASH, publicHash)
                        .create()
        ));
    }

    @Override
    public Single<ResponseBody> viewFile(String type, String path, String name, String publicHash) {
        return mApi.getFile(getDefaultFields(
                Api8.Method.VIEW_FILE,
                new ParamsBuilder()
                        .put(Api8.Param.TYPE, type)
                        .put(Api8.Param.PATH, path)
                        .put(Api8.Param.NAME, name)
                        .put(Api8.Param.SHARED_HASH, publicHash)
                        .create()
        ));
    }

    @Override
    public Single<ApiResponseP8<Boolean>> createFolder(String type, String path, String name) {
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
    }

    @Override
    public Single<ApiResponseP8<Boolean>> renameFile(String type, String path, String name, String newName, boolean isLink) {
        HashMap<String, Object> params = new ParamsBuilder()
                .put(Api8.Param.TYPE, type)
                .put(Api8.Param.PATH, path)
                .put(Api8.Param.NAME, name)
                .put(Api8.Param.NEW_NAME, newName)
                .put(Api8.Param.IS_LINK, isLink)
                .create();
        Map<String, Object> fields = getDefaultFields(Api8.Method.RENAME, params);
        return mApi.rename(fields);
    }

    @Override
    public Single<ApiResponseP8<Boolean>> delete(String type, List<DeleteFileInfo> files) {
        HashMap<String, Object> params = new ParamsBuilder()
                .put(Api8.Param.TYPE, type)
                .put(Api8.Param.ITEMS, files)
                .create();
        Map<String, Object> fields = getDefaultFields(Api8.Method.DELETE, params);
        return mApi.delete(fields);
    }

    @Override
    public Single<ResponseBody> downloadFile(String type, String path, String name, String publicHash) {
        HashMap<String, Object> params = new ParamsBuilder()
                .put(Api8.Param.TYPE, type)
                .put(Api8.Param.PATH, path)
                .put(Api8.Param.NAME, name)
                .put(Api8.Param.SHARED_HASH, publicHash)
                .create();
        Map<String, Object> fields = getDefaultFields(Api8.Method.DOWNLOAD_FILE, params);
        return mApi.getFile(fields);
    }

    @Override
    public Single<ApiResponseP8<UploadResultP8>> uploadFile(String type, String path, FileInfo fileInfo, @Nullable ApiTask.ProgressUpdater progressUpdater) {
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
            uploadBody.setProgressUpdater(progressUpdater);
        }
        MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                Api8.Field.JUA_UPLOADER, fileInfo.getName(), uploadBody
        );

        return mApi.upload(fields, filePart);
    }

    private MultipartBody.Part stringBody(Map.Entry<String, Object> value){
        return MultipartBody.Part.createFormData(value.getKey(), String.valueOf(value.getValue()));
    }

}
