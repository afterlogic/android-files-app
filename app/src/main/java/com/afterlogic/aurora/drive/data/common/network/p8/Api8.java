package com.afterlogic.aurora.drive.data.common.network.p8;

import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.FormatHeader;
import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.JsonField;
import com.afterlogic.aurora.drive.data.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive.data.model.project8.FilesResponseP8;
import com.afterlogic.aurora.drive.data.model.project8.GetUserParametersDto;
import com.afterlogic.aurora.drive.data.model.project8.LoginParametersDto;
import com.afterlogic.aurora.drive.data.model.project8.UploadResultP8;
import com.afterlogic.aurora.drive.data.model.project8.UserP8;
import com.afterlogic.aurora.drive.model.AuthToken;

import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface Api8 {

    String API = "?/Api/";

    String DEFAULT_FIELD_CRUNCH = "X-Api8-";

    interface ApiField {

        String MODULE = "Module";
        String METHOD = "Method";
        String PARAMS = "Parameters";
        String JUA_UPLOADER = "jua-uploader";
    }

    interface ApiHeader {
        String AUTH_TOKEN = "{AuthToken}";
        String NAME_AUTHORISATION = "Authorization";
        String VALUE_AUTHORISATION = "Bearer " + AUTH_TOKEN;
        String AUTHORISATION = NAME_AUTHORISATION + ": " + VALUE_AUTHORISATION;
    }

    interface Module {

        String HEADER = DEFAULT_FIELD_CRUNCH + "Module: ";

        String CORE = "Core";
        String FILES = "Files";
        String STANDARD_AUTH = "StandardAuth";
        String EXTERNAL_CLIENTS_LOGIN_FORM_WEBCLIENT = "ExternalClientsLoginFormWebclient";

        String HEADER_CORE = HEADER + "Core";
        String HEADER_EXTERNAL_CLIENTS_LOGIN_FORM_WEBCLIENT = HEADER
                + EXTERNAL_CLIENTS_LOGIN_FORM_WEBCLIENT;
    }

    interface Method {

        String HEADER = DEFAULT_FIELD_CRUNCH + "Method: ";

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
        String GET_USER = "GetUser";
        String IS_AVAILABLE = "IsAvailable";

        String HEADER_PING= HEADER + PING;
        String HEADER_LOGIN= HEADER + LOGIN;
        String HEADER_GET_USER = HEADER + GET_USER;
        String HEADER_IS_AVAILABLE = HEADER + IS_AVAILABLE;
    }

    interface Param {

        String HEADER = DEFAULT_FIELD_CRUNCH + "Parameters: ";

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

    static String completeUrl(String host) {
        return host + API;
    }

    @POST
    @Headers({
            Module.HEADER_CORE,
            Method.HEADER_PING
    })
    Single<ApiResponseP8<String>> ping(@Url String url);

    @POST
    @FormUrlEncoded
    @Headers({
            Module.HEADER_CORE,
            Method.HEADER_LOGIN
    })
    Single<ApiResponseP8<AuthToken>> login(
            @Url String url,
            @Field(ApiField.PARAMS) @JsonField LoginParametersDto params
    );

    @POST
    @FormUrlEncoded
    @Headers({
            Module.HEADER_CORE,
            Method.HEADER_GET_USER
    })
    Single<ApiResponseP8<UserP8>> getUser(
            @Url String host,
            @Header(ApiHeader.NAME_AUTHORISATION) @FormatHeader("Bearer %s") String token,
            @Field("Parameters") @JsonField GetUserParametersDto params
    );

    @POST
    @Headers({
            Module.HEADER_EXTERNAL_CLIENTS_LOGIN_FORM_WEBCLIENT,
            Method.HEADER_IS_AVAILABLE
    })
    Single<ApiResponseP8<Boolean>> checkExternalClientLoginFormAvailable(@Url String host);

    @POST(API)
    @FormUrlEncoded
    @Headers(ApiHeader.AUTHORISATION)
    Single<ApiResponseP8<FilesResponseP8>> getFiles(@FieldMap Map<String, Object> fields);


    @POST(API)
    @FormUrlEncoded
    @Headers(ApiHeader.AUTHORISATION)
    Single<ResponseBody> getFileThumbnail(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(ApiHeader.AUTHORISATION)
    @Streaming
    Single<ResponseBody> getFile(@FieldMap Map<String, Object> fields);


    @POST(API)
    @FormUrlEncoded
    @Headers(ApiHeader.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> createFolder(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(ApiHeader.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> rename(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(ApiHeader.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> delete(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(ApiHeader.AUTHORISATION)
    Single<ApiResponseP8<String>> createPublicLink(@FieldMap Map<String, Object> fields);

    @POST(API)
    @FormUrlEncoded
    @Headers(ApiHeader.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> deletePublicLink(@FieldMap Map<String, Object> fields);


    @POST(API)
    @Multipart
    @Headers(ApiHeader.AUTHORISATION)
    Single<ApiResponseP8<UploadResultP8>> upload(
            @Part List<MultipartBody.Part> fields,
            @Part MultipartBody.Part file
    );

    @POST(API)
    @FormUrlEncoded
    @Headers(ApiHeader.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> replaceFiles(@FieldMap Map<String, Object> fields);


    @POST(API)
    @FormUrlEncoded
    @Headers(ApiHeader.AUTHORISATION)
    Single<ApiResponseP8<Boolean>> copyFiles(@FieldMap Map<String, Object> fields);
}
