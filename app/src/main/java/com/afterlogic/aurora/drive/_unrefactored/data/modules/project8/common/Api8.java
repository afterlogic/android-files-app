package com.afterlogic.aurora.drive._unrefactored.data.modules.project8.common;

import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive._unrefactored.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive._unrefactored.model.project8.FilesResponseP8;
import com.afterlogic.aurora.drive._unrefactored.model.project8.UploadResultP8;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface Api8 {

    String API = "?/Api/";

    interface Field{

        String MODULE = "Module";
        String METHOD = "Method";
        String PARAMS = "Parameters";
        String AUTH_TOKEN = "AuthToken";
        String JUA_UPLOADER = "jua-uploader";
    }

    interface Module{

        String CORE = "Core";
        String FILES = "Files";
    }

    interface Method{

        String PING = "Ping";
        String LOGIN = "Login";
        String GET_FILES = "GetFiles";
        String GET_FILE_THUMBNAIL = "GetFileThumbnail";
        String VIEW_FILE = "ViewFile";
        String CREATE_FOLDER = "CreateFolder";
        String RENAME = "Rename";
        String DELETE = "Delete";
        String DOWNLOAD_FILE = "DownloadFile";
        String UPLOAD_FILE = "UploadFile";
    }

    interface Param{

        String LOGIN = "Login";
        String PASSWORD = "Password";
        String SIGN_ME = "SignMe";
        String TYPE = "Type";
        String PATTERN = "Pattern";
        String PATH = "Path";
        String NAME = "Name";
        String SHARED_HASH = "SharedHash";
        String FOLDER_NAME = "FolderName";
        String NEW_NAME = "NewName";
        String IS_LINK = "IsLink";
        String ITEMS = "Items";
    }

    @POST()
    @FormUrlEncoded
    Single<ApiResponseP8<String>> ping(@Url String url, @FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    Single<ApiResponseP8<AuthToken>> login(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    Single<ApiResponseP8<FilesResponseP8>> getFiles(@FieldMap Map<String, Object> fields);


    @POST(API)
    @FormUrlEncoded
    Single<ApiResponseP8<String>> getFileThumbnail(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    Single<ResponseBody> getFile(@FieldMap Map<String, Object> fields);


    @POST(API)
    @FormUrlEncoded
    Single<ApiResponseP8<Boolean>> createFolder(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    Single<ApiResponseP8<Boolean>> rename(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    Single<ApiResponseP8<Boolean>> delete(@FieldMap Map<String, Object> fields);


    @POST(API)
    @Multipart
    Single<ApiResponseP8<UploadResultP8>> upload(
            @Part List<MultipartBody.Part> fields,
            @Part MultipartBody.Part file
    );

}
