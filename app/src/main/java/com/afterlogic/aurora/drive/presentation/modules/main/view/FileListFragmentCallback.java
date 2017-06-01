package com.afterlogic.aurora.drive.presentation.modules.main.view;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileListFragmentCallback {
    void onSelectedFilesChanged(int selected, boolean hasFolder);
    void onActionsEnabledChanged(boolean enabled);
}
