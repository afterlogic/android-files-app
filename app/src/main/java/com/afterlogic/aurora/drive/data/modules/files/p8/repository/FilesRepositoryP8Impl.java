package com.afterlogic.aurora.drive.data.modules.files.p8.repository;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.afterlogic.aurora.drive._unrefactored.core.annotations.qualifers.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive._unrefactored.core.util.FileUtil;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiResponseError;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiTask;
import com.afterlogic.aurora.drive._unrefactored.data.common.error.ObservableApiError;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.BaseRepository;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.FilesRepository;
import com.afterlogic.aurora.drive.data.modules.files.p8.service.FilesServiceP8;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.DeleteFileInfo;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive._unrefactored.model.project8.ApiResponseP8;
import com.afterlogic.aurora.drive._unrefactored.model.project8.AuroraFileP8;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FilesRepositoryP8Impl extends BaseRepository implements FilesRepository {

    private static final String FILES_P_8 = "filesP8";

    private final FilesServiceP8 mFilesService;
    private final File mFilesCacheDir;
    private final File mThumbCacheDir;

    private final Mapper<AuroraFile, AuroraFileP8> mFileMapper = source -> new AuroraFile(
            source.getName(),
            source.getPath(),
            source.getFullPath(),
            source.isFolder(),
            source.isLink(),
            source.getLinkUrl(),
            0, //TODO convert link Type
            source.getThumbnailLink(),
            source.isThumb(),
            source.getContentType(),
            source.getHash(),
            source.getType(),
            source.getSize(),
            source.getLastModified() * 1000
    );

    private final Mapper<DeleteFileInfo, AuroraFile> mDeleteFileMapper = source -> new DeleteFileInfo(
            source.getPath(),
            source.getName()
    );

    @SuppressWarnings("WeakerAccess")
    @Inject public FilesRepositoryP8Impl(@RepositoryCache SharedObservableStore cache, FilesServiceP8 filesService, Context context) {
        super(cache, FILES_P_8);
        mFilesService = filesService;
        mFilesCacheDir = FileUtil.getCacheFileDir(context);
        mThumbCacheDir = new File(context.getExternalCacheDir(), "thumb");
    }

    @Override
    public Single<Collection<AuroraFile>> getFiles(AuroraFile folder) {
        return withNetMapper(
                mFilesService.getFiles(folder.getType(), folder.getFullPath(), "")
                        .map(response -> response),
                result -> MapperUtil.list(mFileMapper).map(result.getFiles())
        );
    }

    @Override
    public Single<Uri> getFileThumbnail(AuroraFile file) {
        Single<ResponseBody> request = withNetMapper(
                mFilesService.getFileThumbnail(
                        file.getType(),
                        file.getPath(),
                        file.getName(),
                        file.getHash()
                ).map(response -> response),
                result -> ResponseBody.create(MediaType.parse("image/*"), Base64.decode(result, 0))
        );
        return getFile( file, mThumbCacheDir, request );
    }

    @Override
    public Single<Uri> viewFile(AuroraFile file) {
        Single<ResponseBody> request = mFilesService.viewFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                file.getHash()
        );
        return getFile(file, mFilesCacheDir, request);
    }

    @Override
    public Single<Boolean> createFolder(AuroraFile file) {
        Single<ApiResponseP8<Boolean>> netRequest = mFilesService.createFolder(
                file.getType(),
                file.getPath(),
                file.getName()
        );
        return withNetMapper(netRequest);
    }

    @Override
    public Single<Boolean> rename(AuroraFile file, String newName) {
        Single<ApiResponseP8<Boolean>> netRequest = mFilesService.renameFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                newName,
                file.isLink()
        );
        return withNetMapper(netRequest);
    }

    @Override
    public Single<AuroraFile> checkFile(AuroraFile file) {
        return getFiles(file.getParentFolder())
                .flatMap(files -> {
                    try {
                        AuroraFile requestedFile = Stream.of(files)
                                .filter(remoteFile -> remoteFile.getFullPath().equals(file.getFullPath()))
                                .findFirst()
                                .orElseThrow(() -> new ObservableApiError("File not exist", ApiResponseError.FILE_NOT_EXIST));
                        return Single.just(requestedFile);
                    } catch (ObservableApiError e){
                        return Single.error(e);
                    }
                });
    }

    @Override
    public Single<Boolean> delete(List<AuroraFile> files) {
        return Single.defer(() -> {
            String type = files.get(0).getType();

            checkFilesType(type, files);

            List<DeleteFileInfo> deleteInfo = Stream.of(files)
                    .map(mDeleteFileMapper::map)
                    .collect(Collectors.toList());

            Single<ApiResponseP8<Boolean>> netRequest = mFilesService.delete(type, deleteInfo);
            return withNetMapper(netRequest);
        });
    }

    @Override
    public Single<ResponseBody> downloadFileBody(AuroraFile file) {
        return mFilesService.downloadFile(file.getType(), file.getPath(), file.getName(), file.getHash());
    }

    @Override
    public Single<UploadResult> uploadFile(AuroraFile folder, FileInfo file, @Nullable ApiTask.ProgressUpdater progressUpdater) {
        return withNetMapper(
                mFilesService.uploadFile(folder.getType(), folder.getFullPath(), file, progressUpdater)
                        .map(response -> response),
                result -> new UploadResult()
        );
    }

    private Single<Uri> getFile(AuroraFile file, File cacheDir, Single<ResponseBody> cloud){
        return Single.defer(() -> {
            File cache = getCacheFile(cacheDir, file);
            if (cache.exists()){
                return Single.just(Uri.fromFile(cache));
            } else {
                return cloud.map(result -> saveIntoCache(result, file, cacheDir));
            }
        });
    }

    private Uri saveIntoCache(ResponseBody body, AuroraFile file, File cacheDir) throws IOException{
        File target = getCacheFile(cacheDir, file);
        //noinspection ResultOfMethodCallIgnored
        target.delete();

        File dir = target.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Make dirs failed.");
        }

        FileOutputStream fos = new FileOutputStream(target);
        InputStream is = body.byteStream();

        int readed;
        byte[] buffer = new byte[2048];
        while ((readed = is.read(buffer)) != -1){
            fos.write(buffer, 0, readed);
        }

        fos.close();
        is.close();

        return Uri.fromFile(target);
    }

    private File getCacheFile(File cacheDir, AuroraFile file){
        return new File(cacheDir, file.getFullPath());
    }

    private void checkFilesType(String type, List<AuroraFile> files) throws IllegalArgumentException{
        boolean allInType = Stream.of(files)
                .allMatch(file -> type.equals(file.getType()));
        if (!allInType){
            throw new IllegalArgumentException("All files must be in one type.");
        }
    }
}
