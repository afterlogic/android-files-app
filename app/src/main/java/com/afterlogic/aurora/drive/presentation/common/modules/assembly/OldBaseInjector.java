package com.afterlogic.aurora.drive.presentation.common.modules.assembly;

import androidx.annotation.NonNull;

import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.presentation.assembly.Injector;
import com.afterlogic.aurora.drive.presentation.assembly.assemblies.ModulesComponentCreator;
import com.afterlogic.aurora.drive.presentation.assembly.presentation.DoNotStore;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;

import java.util.UUID;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Base wireframe implementation for view's module.
 */
@Deprecated
public abstract class OldBaseInjector<
        Target extends View,
        View extends PresentationView,
        Module extends PresentationModule<View>> implements Injector<Target> {

    private final ModulesComponentCreator mAssembliesComponent;

    public OldBaseInjector(ModulesComponentCreator component) {
        mAssembliesComponent = component;
    }

    /**
     * Assembly. Create component from module.
     */
    @SuppressWarnings("unchecked")
    protected PresentationComponent<View, Target> assembly(Target target, ModulesComponentCreator assemblies, Module module){
        try {
            return (PresentationComponent<View, Target>) assemblies.getClass()
                    .getMethod("plus", module.getClass())
                    .invoke(assemblies, module);
        } catch (Exception e) {
            MyLog.majorException(this, e);
            throw new IllegalArgumentException("Can't find or invoke method plus(" + module.getClass().getSimpleName() + ") in AssembliesAssemblyComponent.");
        }
    }

    /**
     * Create view's module.
     */
    @NonNull
    protected abstract Module createModule();

    /**
     * Set wireframe target.
     */
    @Override
    public void inject(Target target){
        PresentationComponent<View, Target> component = null;

        PresentationModulesStore store = mAssembliesComponent.store();
        UUID uuid = target.getModuleUuid();

        if (uuid != null){
            component = store.get(uuid);
        }

        if (component == null){
            component = assembly(target, mAssembliesComponent, createModule());

            Class componentClass = component.getClass();
            if (!componentClass.isAnnotationPresent(DoNotStore.class)) {
                target.setModuleUuid(store.put(component));
            }
        }

        component.inject(target);
        component.module().setView(target);
    }
}
