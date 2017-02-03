package com.afterlogic.aurora.drive.data.modules.files.p7.mapper.file.concrete;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive._unrefactored.model.project7.AuroraFileP7;

/**
 * Created by sashka on 10.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AuroraFileP7BlToNetMapper implements Mapper<AuroraFileP7, AuroraFile> {
    @Override
    public AuroraFileP7 map(AuroraFile source) {
        return new AuroraFileP7(
                source.getName(),
                source.getPath(),
                source.getFullPath(),
                source.isFolder(),
                source.isLink(),
                source.getLinkUrl(),
                source.getLinkType(),
                source.getThumbnailLink(),
                source.hasThumbnail(),
                source.getContentType(),
                source.getHash(),
                source.getType(),
                source.getSize(),
                source.getLastModified() / 1000
        );
    }
}
