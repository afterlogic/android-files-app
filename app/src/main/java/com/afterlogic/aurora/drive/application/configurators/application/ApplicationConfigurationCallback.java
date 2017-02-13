package com.afterlogic.aurora.drive.application.configurators.application;

import com.afterlogic.aurora.drive.presentation.assembly.modules.ModulesFactoryComponent;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Callback for {@link ApplicationConfigurator} result.
 */
public interface ApplicationConfigurationCallback {
    void onWireframeFactoryConfigured(ModulesFactoryComponent component);
}
