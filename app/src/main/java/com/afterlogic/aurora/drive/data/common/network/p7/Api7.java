package com.afterlogic.aurora.drive.data.common.network.p7;

import com.afterlogic.aurora.drive.data.model.AuroraFilesResponse;
import com.afterlogic.aurora.drive.model.AuthToken;
import com.afterlogic.aurora.drive.model.SystemAppData;
import com.afterlogic.aurora.drive.data.model.project7.ApiResponseP7;
import com.afterlogic.aurora.drive.data.model.project7.AuroraFileP7;
import com.afterlogic.aurora.drive.data.model.project7.UploadResultP7;

import java.util.Map;

import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by sashka on 10.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface Api7 {

    String AJAX = "?/Ajax/";
    
    interface Actions{
        String SYSTEM_GET_APP_DATA = "SystemGetAppData";
        String SYSTEM_LOGIN = "SystemLogin";
        String FILES = "Files";
        String FILES_RENAME = "FilesRename";
        String FILES_FOLDER_CREATE = "FilesFolderCreate";
        String FILES_DELETE = "FilesDelete";
        String FILE_INFO = "FileInfo";
        String FILES_CREATE_PUBLIC_LINK = "FilesCreatePublicLink";
        String FILES_PUBLIC_LINK_DELETE = "FilesPublicLinkDelete";
    }

    interface Fields{
        String ACTION = "Action";
        String PATH = "Path";
        String TYPE = "Type";
        String PATTERN = "Pattern";
        String FOLDER_NAME = "FolderName";
        String NAME = "Name";
        String NEW_NAME = "NewName";
        String IS_LINK = "IsLink";
        String ITEMS = "Items";
        String EMAIL = "Email";
        String INC_PASSWORD = "IncPassword";
        String ACCOUNT_ID = "AccountID";
        String TOKEN = "Token";
        String AUTH_TOKEN = "AuthToken";
        String IS_FOLDER = "IsFolder";
    }

    interface Links{
        String FILE_DOWNLOAD_LINK = "?/Raw/FilesDownload/%d/%s/0/hash/%s"; //accountId/fileHash/authToken
        String UPLOAD_FILE_URL = "Upload/File/%s%s/%s"; //type(Personal|Corporate)/Path/FileName
        String THUMBNAIL_URL = "?/Raw/FilesThumbnail/%d/%s/0/hash/%s"; //accountId/fileHash/authToken
    }

    @FormUrlEncoded()
    @POST()
    Single<ApiResponseP7<SystemAppData>> getSystemAppData(@Url String url, @FieldMap Map<String, Object> fields);

    @FormUrlEncoded()
    @POST(AJAX)
    Single<ApiResponseP7<SystemAppData>> getSystemAppData(@FieldMap Map<String, Object> fields);

    @FormUrlEncoded
    @POST(AJAX)
    Single<ApiResponseP7<AuthToken>> login(@FieldMap Map<String, Object> fields);

    @FormUrlEncoded
    @POST(AJAX)
    Single<ApiResponseP7<AuroraFilesResponse>> getFiles(@FieldMap Map<String, Object> fields);

    @FormUrlEncoded
    @POST(AJAX)
    Single<ApiResponseP7<AuroraFileP7>> checkFile(@FieldMap Map<String, Object> fields);

    @FormUrlEncoded
    @POST(AJAX)
    Single<ApiResponseP7<Boolean>> createFolder(@FieldMap Map<String, Object> fields);

    @FormUrlEncoded
    @POST(AJAX)
    Single<ApiResponseP7<Boolean>> renameFile(@FieldMap Map<String, Object> fields);

    @FormUrlEncoded
    @POST(AJAX)
    Single<ApiResponseP7<Boolean>> deleteFiles(@FieldMap Map<String, Object> fields);

    @FormUrlEncoded
    @POST(AJAX)
    Single<ApiResponseP7<String>> createPublicLink(@FieldMap Map<String, Object> fields);

    @FormUrlEncoded
    @POST(AJAX)
    Single<ApiResponseP7<Boolean>> deletePublicLink(@FieldMap Map<String, Object> fields);

    @GET(DownloadInterceptor.INTERCEPT_DOWNLOAD)
    Single<ResponseBody> downloadFile(@Query(DownloadInterceptor.QUERY_ACCOUNT_ID) long accountId,
                                          @Query(DownloadInterceptor.QUERY_HASH) String sharedHash,
                                          @Query(DownloadInterceptor.QUERY_AUTH_TOKEN) String authToken);

    @PUT(UploadInterceptor.INTERCEPT_UPLOAD)
    Single<ApiResponseP7<UploadResultP7>> upload(@Header("Auth-Token") String authToken,
                                                     @Query(UploadInterceptor.QUERY_TYPE) String type,
                                                     @Query(UploadInterceptor.QUERY_PATH) String path,
                                                     @Query(UploadInterceptor.QUERY_FILENAME) String fileName,
                                                     @Body RequestBody fileBody);
}
