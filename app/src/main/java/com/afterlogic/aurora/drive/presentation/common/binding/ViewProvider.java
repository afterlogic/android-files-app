package com.afterlogic.aurora.drive.presentation.common.binding;

import android.view.View;

/**
 * Created by sashka on 19.01.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface ViewProvider<T, V extends View> {
    T provideFor(V view);
}
