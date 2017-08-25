package com.afterlogic.aurora.drive.data.common.network.p8;

import com.afterlogic.aurora.drive.data.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive.data.model.project8.FilesResponseP8;
import com.afterlogic.aurora.drive.data.model.project8.UploadResultP8;
import com.afterlogic.aurora.drive.model.AuthToken;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
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
        String JUA_UPLOADER = "jua-uploader";
    }

    interface Header {
        String AUTH_TOKEN = "{AuthToken}";
        String NAME_AUTHORISATION = "Authorization";
        String VALUE_AUTHORISATION = "Bearer " + AUTH_TOKEN;
        String AUTHORISATION = NAME_AUTHORISATION + ": " + VALUE_AUTHORISATION;
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
        String CREATE_PUBLIC_LINK = "CreatePublicLink";
        String DELETE_PUBLIC_LINK = "DeletePublicLink";
        String COPY = "Copy";
        String MOVE = "Move";
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
        String IS_FOLDER = "IsFolder";
        String FROM_TYPE = "FromType";
        String TO_TYPE = "ToType";
        String FROM_PATH = "FromPath";
        String TO_PATH = "ToPath";
        String FILES = "Files";
        String SIZE = "Size";
    }

    @POST()
    @FormUrlEncoded
    Single<ApiResponseP8<String>> ping(@Url String url, @FieldMap Map<String, Object> fields);

    @POST()
    @FormUrlEncoded
    Single<ApiResponseP8<AuthToken>> login(@Url String url, @FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    Single<ApiResponseP8<AuthToken>> login(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(Header.AUTHORISATION)
    Single<ApiResponseP8<FilesResponseP8>> getFiles(@FieldMap Map<String, Object> fields);


    @POST(API)
    @FormUrlEncoded
    @Headers(Header.AUTHORISATION)
    Single<ResponseBody> getFileThumbnail(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(Header.AUTHORISATION)
    Single<ResponseBody> getFile(@FieldMap Map<String, Object> fields);


    @POST(API)
    @FormUrlEncoded
    @Headers(Header.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> createFolder(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(Header.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> rename(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(Header.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> delete(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(Header.AUTHORISATION)
    Single<ApiResponseP8<String>> createPublicLink(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(Header.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> deletePublicLink(@FieldMap Map<String, Object> fields);


    @POST(API)
    @Multipart
    @Headers(Header.AUTHORISATION)
    Single<ApiResponseP8<UploadResultP8>> upload(
            @Part List<MultipartBody.Part> fields,
            @Part MultipartBody.Part file
    );

    @POST(API)
    @FormUrlEncoded
    @Headers(Header.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> replaceFiles(@FieldMap Map<String, Object> fields);


    @POST(API)
    @FormUrlEncoded
    @Headers(Header.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> copyFiles(@FieldMap Map<String, Object> fields);
}
