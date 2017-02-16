package com.afterlogic.aurora.drive.data.modules.files.mapper.p7.file.factory;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.data.modules.files.mapper.p7.file.concrete.AuroraFileP7BlToNetMapper;
import com.afterlogic.aurora.drive.data.modules.files.mapper.p7.file.concrete.AuroraFileP7NetToBlMapper;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.data.model.project7.AuroraFileP7;

import javax.inject.Inject;

/**
 * Created by sashka on 10.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AuroraFileP7MapperFactoryImpl implements AuroraFileP7MapperFactory {

    @Inject public AuroraFileP7MapperFactoryImpl() {
    }

    @Override
    public Mapper<AuroraFile, AuroraFileP7> netToBl() {
        return new AuroraFileP7NetToBlMapper();
    }

    @Override
    public Mapper<AuroraFileP7, AuroraFile> blToNet() {
        return new AuroraFileP7BlToNetMapper();
    }
}
