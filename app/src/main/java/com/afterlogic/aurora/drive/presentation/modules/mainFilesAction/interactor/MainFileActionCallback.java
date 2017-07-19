package com.afterlogic.aurora.drive.presentation.modules.mainFilesAction.interactor;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

public interface MainFileActionCallback {

    void onRenameFileAction();

    void onDeleteFileAction();

    void onDownloadFileAction();

    void onShareFileAction();

    void onMakeOfflineFileAction(boolean offline);

    void onMakePublicLink(boolean publicLink);

    void onCopyPublicLinkFileAction();

    void onCopyFileAction();

    void onReplaceFileAction();
}
