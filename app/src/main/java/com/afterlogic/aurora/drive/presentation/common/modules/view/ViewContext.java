package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.content.Context;

/**
 * Created by sashka on 09.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface ViewContext {
    /**
     * Provide base view's context.
     */
    <T extends Context> T getViewContext();
}
