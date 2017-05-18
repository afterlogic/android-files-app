package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.presenter;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.List;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface OfflineModelOutput {

    void notifyRefreshing(boolean refreshing);

    void setFiles(List<AuroraFile> files);

    void notifyLoadProgress(AuroraFile file, int max, int progress);

    void notifyLoadFinished();

    void setThumb(AuroraFile file, Uri thumb);

    void onCantOpenFile(AuroraFile file);

    void onErrorObtained(Throwable error);

    void onFileLoadError(AuroraFile file);

    void handleNetworkState(boolean enabled);
}
