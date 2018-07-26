package com.afterlogic.aurora.drive.presentation.common.modules.view;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.presentation.assembly.MVVMComponentsStore;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 30.04.17.
 */

public class ModuleStoreController implements ModuleStoreIdProvider{

    private static final String MODULE_STORE_UUID = "module.store.uuid";
    private UUID mUuid;

    private final MVVMComponentsStore mModulesStore;

    @Inject
    public ModuleStoreController(MVVMComponentsStore mModulesStore) {
        this.mModulesStore = mModulesStore;
    }

    public void onViewCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mUuid = UUID.randomUUID();
        } else {
            String uuidString = savedInstanceState.getString(MODULE_STORE_UUID, null);
            if (uuidString != null) {
                mUuid = UUID.fromString(uuidString);
            } else {
                mUuid = UUID.randomUUID();
            }
        }
    }

    public void onSaveInstance(@NonNull Bundle outState) {
        if (mUuid != null) {
            outState.putString(MODULE_STORE_UUID, mUuid.toString());
        }
    }

    public void onFinallyDestroy() {
        mModulesStore.remove(mUuid);
    }

    @Override
    public UUID getStoreUuid() {
        return mUuid;
    }
}
