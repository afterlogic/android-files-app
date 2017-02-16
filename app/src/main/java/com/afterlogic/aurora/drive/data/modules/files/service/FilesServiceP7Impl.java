package com.afterlogic.aurora.drive.data.modules.files.service;

import android.content.Context;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.interfaces.ProgressListener;
import com.afterlogic.aurora.drive.data.common.annotations.P7;
import com.afterlogic.aurora.drive.data.common.network.ExtRequestBody;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.common.network.p7.Api7;
import com.afterlogic.aurora.drive.data.common.network.p7.AuthorizedServiceP7;
import com.afterlogic.aurora.drive.data.model.AuroraFilesResponse;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.data.model.project7.ApiResponseP7;
import com.afterlogic.aurora.drive.data.model.project7.AuroraFileP7;
import com.afterlogic.aurora.drive.data.model.project7.UploadResultP7;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FilesServiceP7Impl extends AuthorizedServiceP7 implements FilesServiceP7 {

    private final Api7 mApi;
    private final Gson mGson;
    private final Context mContext;

    @SuppressWarnings("WeakerAccess")
    @Inject public FilesServiceP7Impl(SessionManager sessionManager, Api7 api, @P7 Gson gson, Context context) {
        super(sessionManager);
        mApi = api;
        mGson = gson;
        mContext = context;
    }

    @Override
    public Single<ApiResponseP7<AuroraFilesResponse>> getFiles(String path, String type, String filterPatter) {
        Map<String, Object> fields = getDefaultParams(Api7.Actions.FILES)
                .put(Api7.Fields.TYPE, type)
                .put(Api7.Fields.PATH, path)
                .put(Api7.Fields.PATTERN, filterPatter)
                .create();
        return mApi.getFiles(fields);
    }

    @Override
    public Single<ApiResponseP7<AuroraFileP7>> checkFile(String type, String folder, String name) {
        Map<String, Object> fields = getDefaultParams(Api7.Actions.FILE_INFO)
                .put(Api7.Fields.TYPE, type)
                .put(Api7.Fields.PATH, folder)
                .put(Api7.Fields.NAME, name)
                .create();
        return mApi.checkFile(fields);
    }

    @Override
    public Single<ApiResponseP7<Boolean>> createFolder(String type, String path, String name) {
        Map<String, Object> fields = getDefaultParams(Api7.Actions.FILES_FOLDER_CREATE)
                .put(Api7.Fields.FOLDER_NAME, name)
                .put(Api7.Fields.PATH, path)
                .put(Api7.Fields.TYPE, type)
                .create();
        return mApi.createFolder(fields);
    }

    @Override
    public Single<ApiResponseP7<Boolean>> renameFile(String type, String path, String name, String newName, boolean isLink){
        Map<String, Object> fields = getDefaultParams(Api7.Actions.FILES_RENAME)
                .put(Api7.Fields.NAME, name)
                .put(Api7.Fields.NEW_NAME, newName)
                .put(Api7.Fields.IS_LINK, isLink ? 1 : 0)
                .put(Api7.Fields.PATH, path)
                .put(Api7.Fields.TYPE, type)
                .create();
        return mApi.renameFile(fields);
    }

    @Override
    public Single<ApiResponseP7<Boolean>> deleteFiles(String type, List<AuroraFileP7> files){
        Map<String, Object> fields = getDefaultParams(Api7.Actions.FILES_DELETE)
                .put(Api7.Fields.ITEMS, mGson.toJson(files))
                .put(Api7.Fields.TYPE, type)
                .put(Api7.Fields.PATH, "")
                .create();
        return mApi.deleteFiles(fields);
    }

    @Override
    public Single<ResponseBody> download(AuroraFileP7 file) {
        AuroraSession session = getSessionManager().getSession();
        return mApi.downloadFile(
                session.getAccountId(),
                file.getHash(),
                session.getAuthToken()
        );
    }

    @Override
    public Single<ApiResponseP7<UploadResultP7>> upload(AuroraFileP7 file, FileInfo source, @Nullable ProgressListener progressListener) {

        String authToken = getSessionManager().getSession().getAuthToken();

        ExtRequestBody uploadBody = new ExtRequestBody(source, mContext);
        if (progressListener != null){
            uploadBody.setProgressListener(progressListener);
        }

        return mApi.upload(authToken, file.getType(), file.getFullPath(), source.getName(), uploadBody);
    }
}
