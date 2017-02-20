package com.afterlogic.aurora.drive.presentation.common.modules.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.common.modules.view.StoreableMVVMView;
import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;

import java.util.UUID;

/**
 * Created by sashka on 17.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public abstract class StoredInjector<T extends StoreableMVVMView> extends BaseInjector<T> {

    public StoredInjector(ModulesComponentCreator componentCreator) {
        super(componentCreator);
    }

    @Override
    protected void inject(T target, ModulesComponentCreator creator) {
        MVVMComponent<T> component = null;
        UUID uuid = target.getStoreUuid();

        if (uuid != null){
            component = creator.mvvmStore().getModuleComponent(uuid);
        }

        if (component == null) {
            component = createComponent(target, creator);

            if (uuid != null) {
                creator.mvvmStore().store(uuid, component);
            }
        }

        onComponentReady(target, component);
    }

    @NonNull
    protected abstract MVVMComponent<T> createComponent(T target, ModulesComponentCreator creator);

    protected void onComponentReady(T target, MVVMComponent<T> component){
        component.module().setView(target);
        component.inject(target);
    }
}
