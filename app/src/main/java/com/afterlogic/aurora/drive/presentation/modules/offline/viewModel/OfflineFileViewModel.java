package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.net.Uri;
import android.view.View;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.OnActionListener;
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.AuroraFileViewModel;
import com.github.nitrico.lastadapter.StableId;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public class OfflineFileViewModel extends AuroraFileViewModel implements View.OnLongClickListener, StableId {

    public final ObservableField<Uri> icon = new ObservableField<>();
    public final ObservableField<Uri> statusIcon = new ObservableField<>();
    public final ObservableField<Uri> defaultIcon = new ObservableField<>();
    public final ObservableBoolean isFolder = new ObservableBoolean();
    public final ObservableInt syncProgress = new ObservableInt(-1);

    private final AuroraFile file;
    private final OnFileViewLongClickListener onLongClickListener;
    private final AppResources appResources;

    private final long stableId;

    OfflineFileViewModel(AuroraFile file,
                         OnActionListener<AuroraFile> onItemClickListener,
                         OnFileViewLongClickListener onLongClickListener,
                         AppResources appResources,
                         long stableId) {
        super(file, onItemClickListener);
        this.file = file;
        this.onLongClickListener = onLongClickListener;
        this.appResources = appResources;
        this.stableId = stableId;

        SimpleOnPropertyChangedCallback.addTo(syncProgress, this::onSyncProgressChanged);
        syncProgress.notifyChange();

        isFolder.set(file.isFolder());
        setThumbnail(null);

        int fileIconRes = FileUtil.getFileIconRes(file);
        defaultIcon.set(appResources.getResourceUri(fileIconRes));
    }

    public boolean onLongClick(View view) {
        onLongClickListener.onFileLongLick(view, file);
        return true;
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
