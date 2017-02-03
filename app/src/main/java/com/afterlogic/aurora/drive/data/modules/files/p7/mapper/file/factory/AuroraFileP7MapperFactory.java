package com.afterlogic.aurora.drive.data.modules.files.p7.mapper.file.factory;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive._unrefactored.model.project7.AuroraFileP7;

/**
 * Created by sashka on 10.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface AuroraFileP7MapperFactory {

    Mapper<AuroraFile, AuroraFileP7> netToBl();

    Mapper<AuroraFileP7, AuroraFile> blToNet();
}
