package com.afterlogic.aurora.drive.data.modules.project7.modules.files.mapper.uploadResult.concrete;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.model.UploadResult;
import com.afterlogic.aurora.drive.model.project7.UploadResultP7;

/**
 * Created by sashka on 15.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class UploadResultP7toBlMapper implements Mapper<UploadResult, UploadResultP7> {
    @Override
    public UploadResult map(UploadResultP7 source) {
        return new UploadResult();
    }
}
