package com.afterlogic.aurora.drive.presentation.assembly;

import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.presentation.common.modules.assembly.MVVMComponent;
import com.annimon.stream.Stream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MVVMComponentsStoreImpl implements MVVMComponentsStore {

    private final Map<UUID, StoreItem> mModulesStore = new HashMap<>();

    @Inject
    MVVMComponentsStoreImpl() {
    }

    @Override
    public void store(UUID uuid, MVVMComponent component){
        StoreItem storeItem = getOrStoreNew(uuid);
        storeItem.setComponent(component);
    }

    @Override
    public void remove(@Nullable UUID uuid){
        if (uuid == null) return;

        StoreItem item = mModulesStore.remove(uuid);
        if (item == null) return;

        Stream.of(item.getSubModules()).forEach(this::remove);
    }

    @Override
    @Nullable
    public <T> MVVMComponent<T> getModuleComponent(UUID targetId) {
        if (targetId == null) return null;

        StoreItem item = mModulesStore.get(targetId);
        if (item == null) return null;

        return item.getComponent();
    }

    private StoreItem getOrStoreNew(UUID uuid){
        StoreItem item = mModulesStore.get(uuid);
        if (item == null){
            item = new StoreItem();
            mModulesStore.put(uuid, item);
        }
        return item;
    }

    private class StoreItem{

        private MVVMComponent mComponent;

        private Set<UUID> mSubModules = new HashSet<>();

        public void setComponent(MVVMComponent component) {
            mComponent = component;
        }

        @Nullable
        public <T> MVVMComponent<T> getComponent() {
            try {
                //noinspection unchecked
                return (MVVMComponent<T>) mComponent;
            } catch (ClassCastException e){
                return null;
            }
        }

        public Set<UUID> getSubModules() {
            return mSubModules;
        }
    }
}
