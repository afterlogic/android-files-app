package com.afterlogic.aurora.drive.data.common.network.p8;

import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.FormatHeader;
import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.ApiRequest;
import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.ToString;
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
import com.afterlogic.aurora.drive.data.model.project8.GetUserParametersDto;
import com.afterlogic.aurora.drive.data.model.project8.LoginParametersDto;
import com.afterlogic.aurora.drive.data.common.network.p8.requestparams.DeletePublicLinkParameters;
import com.afterlogic.aurora.drive.data.model.project8.UploadResultP8;
import com.afterlogic.aurora.drive.data.model.project8.UserP8;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.P8StorageDto;
import com.afterlogic.aurora.drive.model.AuthToken;

import java.util.List;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
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
        String CONTENT_TYPE_X_FORM_URL_ENCODED =
                "Content-Type: application/x-www-form-urlencoded";
    }

    interface Module {

        String CORE = "Core";
        String FILES = "Files";
        String EXTERNAL_CLIENTS_LOGIN_FORM_WEBCLIENT = "ExternalClientsLoginFormWebclient";

    }

    interface Method {

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
        String GET_STORAGES = "GetStorages";
        String COPY = "Copy";
        String MOVE = "Move";
        String IS_AVAILABLE = "IsAvailable";
        String GET_AUTHENTICATED_ACCOUNT = "GetAuthenticatedAccount";

    }

    interface Param {
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
    @Headers(ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED)
    @ApiRequest(module = Module.CORE, method = Method.PING)
    Single<ApiResponseP8<String>> ping(@Url String url, @Body EmptyParameters parameters);

    @POST
    @Headers(ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED)
    @ApiRequest(module = Module.CORE, method = Method.LOGIN)
    Single<ApiResponseP8<AuthToken>> login(
            @Url String url,
            @Body LoginParametersDto params
    );

    @POST
    @Headers(ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED)
    @ApiRequest(module = Module.CORE, method = Method.GET_AUTHENTICATED_ACCOUNT)
    Single<ApiResponseP8<UserP8>> getUser(
            @Url String host,
            @Header(ApiHeader.NAME_AUTHORISATION) @FormatHeader("Bearer %s") String token,
            @Body GetUserParametersDto params
    );

    @POST
    @Headers(ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED)
    @ApiRequest(module = Module.EXTERNAL_CLIENTS_LOGIN_FORM_WEBCLIENT, method = Method.IS_AVAILABLE)
    Single<ApiResponseP8<Boolean>> checkExternalClientLoginFormAvailable(
            @Url String host, @Body EmptyParameters params);

    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.GET_FILES)
    Single<ApiResponseP8<FilesResponseP8>> getFiles(@Body GetFilesParameters params);


    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.GET_FILE_THUMBNAIL)
    Single<ResponseBody> getFileThumbnail(@Body GetFileThumbnailParameters params);

    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.VIEW_FILE)
    @Streaming
    Single<ResponseBody> viewFile(@Body ViewFileParameters params);

    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.CREATE_FOLDER)
    Single<ApiResponseP8<Boolean>> createFolder(@Body CreateFolderParameters params);

    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.RENAME)
    Single<ApiResponseP8<Boolean>> rename(@Body RenameFileParameters params);

    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.DELETE)
    Single<ApiResponseP8<Boolean>> delete(@Body DeleteFilesParameters params);

    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.CREATE_PUBLIC_LINK)
    Single<ApiResponseP8<String>> createPublicLink(@Body CreatePublicLinkParameters link);

    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.DELETE_PUBLIC_LINK)
    Single<ApiResponseP8<Boolean>> deletePublicLink(@Body DeletePublicLinkParameters link);

    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.GET_STORAGES)
    Single<ApiResponseP8<List<P8StorageDto>>> getAvailableStorages(@Body EmptyParameters request);

    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.MOVE)
    Single<ApiResponseP8<Boolean>> moveFiles(@Body MoveFilesParameters files);

    @POST(API)
    @Headers({
            ApiHeader.AUTHORISATION,
            ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED
    })
    @ApiRequest(module = Module.FILES, method = Method.COPY)
    Single<ApiResponseP8<Boolean>> copyFiles(@Body MoveFilesParameters files);

    @POST(API)
    @Headers(ApiHeader.AUTHORISATION)
    @Multipart
    Single<ApiResponseP8<UploadResultP8>> upload(
            @Part(ApiField.MODULE) @ToString String module, // Module.FILES
            @Part(ApiField.METHOD) @ToString String method, // Method.UPLOAD_FILE
            @Part(ApiField.PARAMS) UploadFileParameters params,
            @Part MultipartBody.Part file
    );

}
