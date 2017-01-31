package com.afterlogic.aurora.drive.data.modules.project7.modules.files.mapper.uploadResult.factory;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.model.UploadResult;
import com.afterlogic.aurora.drive.model.project7.UploadResultP7;

/**
 * Created by sashka on 15.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface UploadResultP7MapperFactory {
    Mapper<UploadResult, UploadResultP7> p7toBl();
}
