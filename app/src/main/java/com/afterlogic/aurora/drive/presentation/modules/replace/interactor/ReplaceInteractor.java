package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.common.mapper.MapperUtil;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.FileType;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceInteractor {

    private final FilesRepository filesRepository;

    private final Mapper<FileType, String> typesMapper;

    @Inject
    ReplaceInteractor(FilesRepository filesRepository, AppResources appResources) {
        this.filesRepository = filesRepository;
        typesMapper = new TypesMapper(appResources);
    }

    public Single<List<FileType>> getAvailableFileTypes() {
        return filesRepository.getAvailableFileTypes()
                .map(availableTypes -> MapperUtil.listOrEmpty(availableTypes, typesMapper));
    }
}
