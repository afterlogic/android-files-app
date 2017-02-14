package com.afterlogic.aurora.drive.data.modules.files.mapper.general;

import android.net.Uri;

import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.data.common.mapper.BiMapper;
import com.afterlogic.aurora.drive.data.modules.files.model.db.OfflineFileInfoEntity;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.OfflineInfo;
import com.afterlogic.aurora.drive.model.OfflineType;
import com.afterlogic.aurora.drive.model.SyncState;

import java.io.File;

/**
 * Created by sashka on 14.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

class OfflineToBlMapper implements BiMapper<AuroraFile, OfflineFileInfoEntity, File> {
    @Override
    public AuroraFile map(OfflineFileInfoEntity offlineInfo, File offlineDir) {
        if (offlineInfo == null || offlineDir == null) return null;

        String[] pathSpec = offlineInfo.getPathSpec().split("/", 2);
        String type = pathSpec[0];
        String path = "/" + pathSpec[1];
        AuroraFile auroraFile = AuroraFile.parse(path, type, false);

        File localFile  = new File(offlineDir, offlineInfo.getPathSpec());
        if (localFile.exists()){
            auroraFile.setLastModified(localFile.lastModified());
            String mime = FileUtil.getFileMimeType(localFile);
            auroraFile.setContentType(mime);
            if (mime.startsWith("image")){
                auroraFile.setHasThumb(true);
                auroraFile.setThumbnailLink(Uri.fromFile(localFile).toString());
            }
        } else {
            auroraFile.setLastModified(-1);
            auroraFile.setHasThumb(false);
            auroraFile.setThumbnailLink(null);
            auroraFile.setContentType("*/*");
        }

        auroraFile.setOfflineInfo(new OfflineInfo(
                OfflineType.OFFLINE,
                SyncState.UNKNOWN
        ));
        return auroraFile;
    }
}
