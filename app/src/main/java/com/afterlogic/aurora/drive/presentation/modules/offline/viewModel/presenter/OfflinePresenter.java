package com.afterlogic.aurora.drive.presentation.modules.offline.viewModel.presenter;

import com.afterlogic.aurora.drive.model.AuroraFile;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface OfflinePresenter {
    void checkAuth();

    void refresh();

    void downloadFile(AuroraFile file);
    void openFile(AuroraFile file);
    void sendFile(AuroraFile file);

    void cancelCurrentLoad();

    void onGoToOnline();

    void onViewStart();
    void onViewStop();
}
