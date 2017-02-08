package com.afterlogic.aurora.drive._unrefactored.data.common.api;

import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive._unrefactored.core.util.ApiCompatibilityUtil;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class Api extends AuroraApi{


    private static <T> ApiTask<AuroraFiles, ApiResponseP7<T>> getDefaultApiTask(
            ApiCallback<ApiResponseP7<T>> callback,
            ApiTask.CallCreatorImpl<AuroraFiles, ApiResponseP7<T>> creator){
        return getDefaultApiTask(callback, true, true, creator);
    }

    private static <T> ApiTask<AuroraFiles, ApiResponseP7<T>> getDefaultApiTask(
            ApiCallback<ApiResponseP7<T>> callback, boolean checkSessionCompletition,
            boolean handleUnauthorizedError, ApiTask.CallCreatorImpl<AuroraFiles, ApiResponseP7<T>> creator){

        CallbackWrapper<AuroraFiles, ApiResponseP7<T>> wrapper;
        if (!handleUnauthorizedError) {
            wrapper = new CallbackWrapper<>(callback);
        } else {
            wrapper = new CallbackWrapper<>(callback, creator, null);
        }

        return new ApiTask.Builder<AuroraFiles, ApiResponseP7<T>>(null)
                .setCallback(wrapper)
                .setTaskHandler(getTaskStateHandler())
                .setApiTaskExecuter(new SessionExecuter<>(callback, checkSessionCompletition, getContext()))
                .setApiTaskErrorHandler(getErrorHandler())
                .build(creator);
    }

    /**
     * Get files list in dir.
     *
     * @param folder - requested folder.
     * @param pattern - files name pattern.
     * @param callback - result callback.
     */
    @SuppressWarnings("UnusedParameters")
    public static int getFiles(final AuroraFile folder, final String pattern,
                               final ApiCallback<List<AuroraFile>> callback) {

        ApiCallback<ApiResponseP7<List<AuroraFile>>> success = new SuccessCallback<List<AuroraFile>>(callback) {
            @Override
            public void onSucces(ApiResponseP7<List<AuroraFile>> result) {
                callback.onSucces(result.getResult());
            }
        };

        return getDefaultApiTask(success, new ApiTask.CallCreatorImpl<AuroraFiles, ApiResponseP7<List<AuroraFile>>>() {
            @Override
            public Call<ApiResponseP7<List<AuroraFile>>> createCall(AuroraFiles apiInterface) {
                Single<List<AuroraFile>> observable = getApiProvider().getFilesRepository()
                        .getFiles(folder)
                        .map(items -> Stream.of(items).collect(Collectors.toList()));

                return fromObservable(observable);
            }
        }).execute();
    }

    /**
     * Get latest info of requested file.
     * @param file - not full-filled {@link AuroraFile}.
     *             It can be created by {@link AuroraFile#parse(String, String, boolean)}.
     * @param callback - result callback.
     * @return current {@link Task} id.
     */
    public static int checkFile(final AuroraFile file, final ApiCallback<AuroraFile> callback){
        SuccessCallback<AuroraFile> success = new SuccessCallback<AuroraFile>(callback) {
            @Override
            public void onSucces(ApiResponseP7<AuroraFile> result) {
                callback.onSucces(result.getResult());
            }
        };

        return getDefaultApiTask(success, new ApiTask.CallCreatorImpl<AuroraFiles, ApiResponseP7<AuroraFile>>() {
            @Override
            public Call<ApiResponseP7<AuroraFile>> createCall(AuroraFiles apiInterface) {
                return checkFile(file);
            }
        }).execute();
    }

    public static Call<ApiResponseP7<AuroraFile>> checkFile(AuroraFile file){
        Single<AuroraFile> observable = getApiProvider().getFilesRepository()
                .checkFile(file);
        return fromObservable(observable);
    }

    public static Call<ResponseBody> downloadFile(final AuroraFile file){
        Single<ResponseBody> observable = getApiProvider()
                .getFilesRepository().downloadFileBody(file);
        return ApiCompatibilityUtil.transparentCall(observable);
    }

    /**
     * Create folder
     * @param name - new folder name.
     * @param path - folder path.
     * @param callback - result callback.
     */
    public static int createFolder(final String name, final String path, final String type,
                                   final ApiCallback<Void> callback){

        SuccessCallback<Boolean> success = new SuccessCallback<Boolean>(callback) {
            @Override
            public void onSucces(ApiResponseP7<Boolean> result) {
                callback.onSucces(null);
            }
        };

        return getDefaultApiTask(
                success,
                new ApiTask.CallCreatorImpl<AuroraFiles, ApiResponseP7<Boolean>>() {
                    @Override
                    public Call<ApiResponseP7<Boolean>> createCall(AuroraFiles apiInterface) {
                        Single<Boolean> observable = getApiProvider().getFilesRepository()
                                .createFolder(AuroraFile.create(path, name, type, false));
                        return fromObservable(observable);
                    }
                }
        ).execute();
    }

    /**
     * Rename file or folder.
     * @param file - current file for renaming.
     * @param newName - new file name.
     * @param callback - result callback.
     * @return - task id
     */
    public static int renameFile(final AuroraFile file, final String newName,
                                 final ApiCallback<Void> callback){

        SuccessCallback<Boolean> onSuccess = new SuccessCallback<Boolean>(callback) {
            @Override
            public void onSucces(ApiResponseP7<Boolean> result) {
                callback.onSucces(null);
            }
        };

        return getDefaultApiTask(
                onSuccess,
                new ApiTask.CallCreatorImpl<AuroraFiles, ApiResponseP7<Boolean>>() {
                    @Override
                    public Call<ApiResponseP7<Boolean>> createCall(AuroraFiles apiInterface) {
                        return fromObservable(null);
                    }
                }
        ).execute();
    }

    /**
     * Delete files from storage.
     *
     * @param type - storage type.
     * @param files - list of {@link AuroraFile}s.
     * @param callback - result callback.
     */
    @SuppressWarnings("UnusedParameters")
    public static int deleteFiles(final String type, final List<AuroraFile> files,
                                  final ApiCallback<Void> callback){

        SuccessCallback<Boolean> onSuccess = new SuccessCallback<Boolean>(callback) {
            @Override
            public void onSucces(ApiResponseP7<Boolean> result) {
                callback.onSucces(null);
            }
        };

        return getDefaultApiTask(
                onSuccess,
                new ApiTask.CallCreatorImpl<AuroraFiles, ApiResponseP7<Boolean>>() {
                    @Override
                    public Call<ApiResponseP7<Boolean>> createCall(AuroraFiles apiInterface) {
                        List<AuroraFile> targetFiles = Stream.of(files)
                                .collect(Collectors.toList());
                        return fromObservable(null);
                    }
                }
        ).execute();
    }

    public static Call<ApiResponseP7<UploadResult>> uploadFile(AuroraFile folder, FileInfo file, @Nullable ApiTask.ProgressUpdater updater){
        //Create request body from URI
        if (file == null){
            return null;
        }

        Single<UploadResult> observable = getApiProvider().getFilesRepository()
                .uploadFile(folder, file, updater);

        return fromObservable(observable);
    }

    /**
     * Aurora files api interface.
     */
    interface AuroraFiles {
    }
}
