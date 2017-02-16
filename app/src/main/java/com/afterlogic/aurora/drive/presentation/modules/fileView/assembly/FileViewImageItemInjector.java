package com.afterlogic.aurora.drive.presentation.modules.fileView.assembly;

import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.presentation.assembly.assemblies.AssembliesAssemblyComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.BaseInjector;
import com.afterlogic.aurora.drive.presentation.common.modules.assembly.PresentationComponent;
import com.afterlogic.aurora.drive.presentation.common.modules.view.PresentationView;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewImageItemFragment;
import com.afterlogic.aurora.drive.presentation.modules.fileView.view.FileViewImageItemView;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewImageItemInjector extends BaseInjector<FileViewImageItemFragment, FileViewImageItemView, FileViewImageItemModule> {

    @Inject FileViewImageItemInjector(AssembliesAssemblyComponent component) {
        super(component);
    }

    @NonNull
    @Override
    protected FileViewImageItemModule createModule() {
        return new FileViewImageItemModule();
    }

    @Override
    protected PresentationComponent<FileViewImageItemView, FileViewImageItemFragment> assembly(FileViewImageItemFragment target, AssembliesAssemblyComponent component, FileViewImageItemModule module) {
        if (target.getActivity() instanceof PresentationView){
            PresentationView rootView = (PresentationView) target.getActivity();
            UUID uuid = rootView.getModuleUuid();
            if (uuid != null) {
                PresentationComponent rootComponent = component.store().get(uuid);
                if (rootComponent instanceof FileViewImageSubcomponentCreator){
                    FileViewImageSubcomponentCreator creator = (FileViewImageSubcomponentCreator) rootComponent;
                    return creator.plus(module);
                }
            }
        }

        return super.assembly(target, component, module);
    }
}
