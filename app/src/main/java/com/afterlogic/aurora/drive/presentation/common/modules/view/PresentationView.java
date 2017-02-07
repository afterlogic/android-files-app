package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.support.annotation.StringRes;

import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.Repeat;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.annotations.RepeatPolicy;

import java.util.UUID;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Base view input.
 */
public interface PresentationView extends ViewContext{
    int TYPE_MESSAGE_MAJOR = 1;
    int TYPE_MESSAGE_MINOR = 0;

    /**
     * Show content loading indicator.
     */
    @Repeat(RepeatPolicy.EACH_UNHANDLED)
    void showLoadingProgress(String message, int type);

    /**
     * Hide content loading indicator.
     */
    @Repeat(RepeatPolicy.EACH_UNHANDLED)
    void hideLoadingProgress(int type);

    @Repeat(RepeatPolicy.EACH_UNHANDLED)
    void showMessage(String message, int type);

    @Repeat(RepeatPolicy.EACH_UNHANDLED)
    void showMessage(@StringRes int messageId, int type);

    boolean isActive();


    UUID getModuleUuid();

    void setModuleUuid(UUID uuid);

    @Repeat(RepeatPolicy.EACH_UNHANDLED)
    void requestPermissions(String[] permissions, int id);
}
