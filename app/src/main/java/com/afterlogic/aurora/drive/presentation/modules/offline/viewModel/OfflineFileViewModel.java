package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

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
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.AuroraFileViewModel;
import com.github.nitrico.lastadapter.StableId;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineFileViewModel extends AuroraFileViewModel implements StableId {

    public final ObservableField<Uri> icon = new ObservableField<>();
    public final ObservableField<Uri> statusIcon = new ObservableField<>();
    public final ObservableBoolean isFolder = new ObservableBoolean();
    public final ObservableInt syncProgress = new ObservableInt(-1);

    private final AuroraFile file;
    private final AppResources appResources;

    private final long stableId;

    OfflineFileViewModel(AuroraFile file,
                         OnItemClickListener<AuroraFile> onItemClickListener,
                         AppResources appResources,
                         long stableId) {
        super(file, onItemClickListener);
        this.file = file;
        this.appResources = appResources;
        this.stableId = stableId;

        SimpleOnPropertyChangedCallback.addTo(syncProgress, this::onSyncProgressChanged);
        syncProgress.notifyChange();

        isFolder.set(file.isFolder());
        setThumbnail(null);
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

    private void onSyncProgressChanged() {
        Uri statusIcon;
        if (syncProgress.get() != -1) {
            statusIcon = appResources.getResourceUri(R.drawable.ic_sync);
        } else {
            statusIcon = null;
        }

        if (!ObjectsUtil.equals(statusIcon, this.statusIcon.get())) {
            this.statusIcon.set(statusIcon);
        }
    }

    @Override
    public long getStableId() {
        return stableId;
    }
}
