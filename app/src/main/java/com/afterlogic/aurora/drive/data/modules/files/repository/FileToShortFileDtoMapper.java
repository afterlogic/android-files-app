package com.afterlogic.aurora.drive.data.modules.files.repository;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.ShortFileDto;
import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

class FileToShortFileDtoMapper implements Mapper<ShortFileDto, AuroraFile> {

    @Override
    public ShortFileDto map(AuroraFile source) {
        return new ShortFileDto(source.getName(), source.isFolder(), source.getPath(), source.getType());
    }

}
