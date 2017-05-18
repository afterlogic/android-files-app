package com.afterlogic.aurora.drive.data.modules.files.mapper.general;

import com.afterlogic.aurora.drive.data.common.mapper.BiMapper;
import com.afterlogic.aurora.drive.data.modules.files.model.db.OfflineFileInfoEntity;
import com.afterlogic.aurora.drive.model.AuroraFile;

import java.io.File;

/**
 * Created by sashka on 14.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FilesMapperFactory {

    BiMapper<AuroraFile, OfflineFileInfoEntity, File> offlineToBl();
}
