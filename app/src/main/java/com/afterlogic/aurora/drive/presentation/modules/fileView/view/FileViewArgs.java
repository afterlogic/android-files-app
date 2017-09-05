package com.afterlogic.aurora.drive.presentation.modules.fileView.view;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.annimon.stream.Stream;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import java.util.List;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

@Parcel(Parcel.Serialization.BEAN)
public class FileViewArgs {

    private AuroraFile file;
    private List<AuroraFile> folderContent;

    @ParcelConstructor
    public FileViewArgs(AuroraFile file, List<AuroraFile> folderContent) {
        this.file = file;
        this.folderContent = Stream.of(folderContent)
                .filter(AuroraFile::isPreviewAble)
                .toList();
    }

    public AuroraFile getFile() {
        return file;
    }

    public List<AuroraFile> getFolderContent() {
        return folderContent;
    }
}
