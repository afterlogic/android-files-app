package com.afterlogic.aurora.drive.data.modules.files.p7.repository;

import android.net.Uri;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.data.common.annotations.RepositoryCache;
import com.afterlogic.aurora.drive.data.common.cache.SharedObservableStore;
import com.afterlogic.aurora.drive.data.common.network.DynamicDomainProvider;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiTask;
import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.common.repository.Repository;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive.data.common.network.p7.Api7;
import com.afterlogic.aurora.drive.data.modules.files.p7.mapper.file.factory.AuroraFileP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.p7.mapper.uploadResult.factory.UploadResultP7MapperFactory;
import com.afterlogic.aurora.drive.data.modules.files.p7.service.FilesServiceP7;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive._unrefactored.model.project7.AuroraFileP7;
import com.afterlogic.aurora.drive._unrefactored.model.project7.UploadResultP7;
import com.annimon.stream.Stream;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import io.reactivex.Single;
import okhttp3.ResponseBody;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FilesRepository7Impl extends Repository implements FilesRepository {

    private static final String FILES_P_7 = "filesP7";

    private final Mapper<AuroraFile, AuroraFileP7> mFileNetToBlMapper;
    private final Mapper<AuroraFileP7, AuroraFile> mFileBlToNetMapper;
    private final Mapper<UploadResult, UploadResultP7> mUploadResultToBlMapper;

    private final FilesServiceP7 mCloudService;
    private final DynamicDomainProvider mDynamicDomainProvider;
    private final SessionManager mSessionManager;

    @SuppressWarnings("WeakerAccess")
    @Inject public FilesRepository7Impl(@RepositoryCache SharedObservableStore cache,
                                        AuroraFileP7MapperFactory mapperFactory,
                                        FilesServiceP7 cloudService,
                                        DynamicDomainProvider dynamicDomainProvider,
                                        SessionManager sessionManager,
                                        UploadResultP7MapperFactory uploadResultP7MapperFactory) {
        super(cache, FILES_P_7);
        mFileNetToBlMapper = mapperFactory.netToBl();
        mFileBlToNetMapper = mapperFactory.blToNet();
        mCloudService = cloudService;
        mDynamicDomainProvider = dynamicDomainProvider;
        mSessionManager = sessionManager;
        mUploadResultToBlMapper = uploadResultP7MapperFactory.p7toBl();
    }

    @Override
    public Single<Collection<AuroraFile>> getFiles(AuroraFile folder) {
        return withNetMapper(
                mCloudService.getFiles(folder.getFullPath(), folder.getType(), null)
                        .map(response -> response),
                files -> MapperUtil.list(mFileNetToBlMapper)
                        .map(files.getFiles())
        );
    }

    @Override
    public Single<Uri> getFileThumbnail(AuroraFile file) {
        return Single.defer(() -> {
            AuroraSession session = mSessionManager.getAuroraSession();
            String url = getCompleteUrl(
                    String.format(Locale.US, Api7.Links.THUMBNAIL_URL, session.getAccountId(), file.getHash(), session.getAuthToken())
            );
            return Single.just(Uri.parse(url));
        });
    }

    @Override
    public Single<Uri> viewFile(AuroraFile file) {
        return Single.defer(() -> {
            AuroraSession session = mSessionManager.getAuroraSession();
            String url = getCompleteUrl(
                    String.format(Locale.US, Api7.Links.FILE_DOWNLOAD_LINK, session.getAccountId(), file.getHash(), session.getAuthToken())
            );
            return Single.just(Uri.parse(url));
        });
    }

    @Override
    public Single<Boolean> createFolder(AuroraFile file) {
        return withNetMapper(mCloudService.createFolder(file.getType(), file.getPath(), file.getName()));
    }

    @Override
    public Single<Boolean> rename(AuroraFile file, String newName) {
        return withNetMapper(mCloudService.renameFile(
                file.getType(),
                file.getPath(),
                file.getName(),
                newName,
                file.isLink()
        ));
    }

    @Override
    public Single<AuroraFile> checkFile(AuroraFile file) {
        return withNetMapper(
                mCloudService.checkFile(file.getType(), file.getPath(), file.getName()).map(response -> response),
                mFileNetToBlMapper
        );
    }

    @Override
    public Single<Boolean> delete(List<AuroraFile> files) {
        return Single.defer(() -> {
            String type = files.get(0).getType();
            checkFilesType(type, files);

            List<AuroraFileP7> mapped = MapperUtil.list(mFileBlToNetMapper).map(files);
            return withNetMapper(mCloudService.deleteFiles(type, mapped));
        });
    }

    @Override
    public Single<ResponseBody> downloadFileBody(AuroraFile file) {
        return mCloudService.download(mFileBlToNetMapper.map(file));
    }

    @Override
    public Single<UploadResult> uploadFile(AuroraFile folder, FileInfo file, @Nullable ApiTask.ProgressUpdater progressUpdater) {
        return withNetMapper(
                mCloudService.upload(mFileBlToNetMapper.map(folder), file, progressUpdater)
                        .map(response -> response),
                mUploadResultToBlMapper
        );
    }

    private String getCompleteUrl(String url){
        return mDynamicDomainProvider.getDomain().toString() + url;
    }

    private void checkFilesType(String type, List<AuroraFile> files) throws IllegalArgumentException{
        boolean allInType = Stream.of(files)
                .allMatch(file -> type.equals(file.getType()));
        if (!allInType){
            throw new IllegalArgumentException("All files must be in one type.");
        }
    }
}
