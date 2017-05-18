package com.afterlogic.aurora.drive.data.modules.files.mapper.general;

import com.afterlogic.aurora.drive.data.common.mapper.BiMapper;
import com.afterlogic.aurora.drive.data.modules.files.model.db.OfflineFileInfoEntity;
import com.afterlogic.aurora.drive.model.AuroraFile;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by sashka on 14.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileMapperFactoryImpl implements FilesMapperFactory{
    @Inject
    FileMapperFactoryImpl() {
    }

    @Override
    public BiMapper<AuroraFile, OfflineFileInfoEntity, File> offlineToBl() {
        return new OfflineToBlMapper();
    }
}
