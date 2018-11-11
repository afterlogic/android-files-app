package com.afterlogic.aurora.drive.presentation.modules.upload.viewModel;

import androidx.databinding.ObservableField;
import android.net.Uri;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.OnActionListener;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.AuroraFileViewModel;

/**
 * Created by aleksandrcikin on 21.07.17.
 * mail: mail@sunnydaydev.me
 */

public class UploadFileViewModel extends AuroraFileViewModel {

    private final AppResources appResources;
    private final AuroraFile file;
    private ObservableField<Uri> icon = new ObservableField<>();

    UploadFileViewModel(AuroraFile file,
                        OnActionListener<AuroraFile> onClick,
                        AppResources appResources) {
        super(file, onClick);
        this.file = file;
        this.appResources = appResources;
        setThumbnail(null);
    }

    public boolean getIsFolder() {
        return file.isFolder();
    }

    public ObservableField<Uri> getIcon() {
        return icon;
    }

    void setThumbnail(Uri thumb) {

        if (file.isFolder()) {
            thumb = appResources.getResourceUri(R.drawable.ic_folder);
        } else {
            if (thumb == null) {
                int fileIconRes = FileUtil.getFileIconRes(file);
                thumb = appResources.getResourceUri(fileIconRes);
            }
        }

        if (!ObjectsUtil.equals(thumb, icon.get())) {
            icon.set(thumb);
        }

    }

}
