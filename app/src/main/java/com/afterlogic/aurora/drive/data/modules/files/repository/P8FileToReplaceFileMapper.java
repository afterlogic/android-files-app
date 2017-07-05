package com.afterlogic.aurora.drive.data.modules.files.repository;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.modules.files.model.dto.ReplaceFileDto;
import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by aleksandrcikin on 05.07.17.
 * mail: mail@sunnydaydev.me
 */

class P8FileToReplaceFileMapper implements Mapper<ReplaceFileDto, AuroraFile> {
    @Override
    public ReplaceFileDto map(AuroraFile source) {
        return new ReplaceFileDto(source.getName(), source.isFolder());
    }
}
