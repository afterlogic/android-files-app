package com.afterlogic.aurora.drive.data.modules.files.mapper.p7.file.concrete;

import com.afterlogic.aurora.drive.data.common.mapper.Mapper;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.data.model.project7.AuroraFileP7;

/**
 * Created by sashka on 10.11.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class AuroraFileP7NetToBlMapper implements Mapper<AuroraFile, AuroraFileP7> {
    @Override
    public AuroraFile map(AuroraFileP7 source) {
        return new AuroraFile(
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
                source.getLastModified() * 1000,
                false // TODO parse shared
        );
    }
}
