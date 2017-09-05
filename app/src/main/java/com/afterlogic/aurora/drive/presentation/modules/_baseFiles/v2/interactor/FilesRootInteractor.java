package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.InteractorUtil;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class FilesRootInteractor {

    private final FilesRepository filesRepository;

    private final Mapper<FileType, String> typesMapper;

    @Inject
    protected FilesRootInteractor(FilesRepository filesRepository, AppResources appResources) {
        this.filesRepository = filesRepository;
        typesMapper = new TypesMapper(appResources);
    }

    public Single<List<FileType>> getAvailableFileTypes() {
        return filesRepository.getAvailableFileTypes()
                .map(availableTypes -> MapperUtil.listOrEmpty(availableTypes, typesMapper))
                .compose(upstream -> InteractorUtil.retryIfNotAuthError(3, upstream));
    }
}
