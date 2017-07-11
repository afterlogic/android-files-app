package com.afterlogic.aurora.drive.presentation.modules.main.v2.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.net.Uri;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.interfaces.OnItemClickListener;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.BaseAuroraFileViewModel;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class MainFileViewModel extends BaseAuroraFileViewModel {

    public final ObservableField<Uri> icon = new ObservableField<>();
    public final ObservableField<Uri> statusIcon = new ObservableField<>();
    public final ObservableBoolean isFolder = new ObservableBoolean();
    public final ObservableBoolean selected = new ObservableBoolean();
    public final ObservableInt syncProgress = new ObservableInt(-1);
    public final ObservableBoolean isOffline = new ObservableBoolean();
    public final ObservableBoolean shared = new ObservableBoolean();

    private final AuroraFile file;
    private final AppResources appResources;

    private Uri thumbnail;

    MainFileViewModel(AuroraFile file,
                      OnItemClickListener<AuroraFile> onItemClickListener,
                      AppResources appResources) {
        super(file, onItemClickListener);
        this.file = file;
        this.appResources = appResources;

        SimpleOnPropertyChangedCallback.addTo(this::onOfflineStatusChanged, isOffline, syncProgress);
        isOffline.notifyChange();

        updateValues();
        setThumbnail(null);
    }

    public void onLongClick() {

    }

    void setThumbnail(Uri thumb) {
        thumbnail = thumb;

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

    private void updateValues() {
        shared.set(file.isShared());
        isFolder.set(file.isFolder());

        setThumbnail(thumbnail);
    }

    private void onOfflineStatusChanged() {
        Uri statusIcon;
        if (isOffline.get()) {
            if (syncProgress.get() != -1) {
                statusIcon = appResources.getResourceUri(R.drawable.ic_sync);
            } else {
                statusIcon = appResources.getResourceUri(R.drawable.ic_offline);
            }
        } else {
            statusIcon = null;
        }

        if (!ObjectsUtil.equals(statusIcon, this.statusIcon.get())) {
            this.statusIcon.set(statusIcon);
        }
    }
}
