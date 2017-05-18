package com.afterlogic.aurora.drive.presentation.common.modules.model.presenter;

import android.os.Bundle;

import com.afterlogic.aurora.drive.presentation.common.interfaces.Stoppable;

import java.util.UUID;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface Presenter extends Stoppable {

    /**
     * Restore presenter state.
     */
    void onRestoreInstanceState(Bundle savedInstanceState);

    /**
     * Save presenter state.
     */
    void onSaveInstanceState(Bundle outState);

    UUID getUuid();

    void setUuid(UUID uuid);
}
