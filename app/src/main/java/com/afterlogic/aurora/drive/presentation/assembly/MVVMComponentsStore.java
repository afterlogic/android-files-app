package com.afterlogic.aurora.drive.presentation.assembly;

import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMComponent;

import java.util.UUID;

/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface MVVMComponentsStore {
    void store(UUID uuid, MVVMComponent component);

    void remove(@Nullable UUID uuid);

    @Nullable
    <T> MVVMComponent<T> getModuleComponent(UUID targetId);
}
