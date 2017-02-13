package com.afterlogic.aurora.drive.presentation.modules.choise.viewModel;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel.BaseFilesBiModel;

import javax.inject.Inject;

/**
 * Created by sashka on 10.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */
@ModuleScope
public class ChoiseBiModel extends BaseFilesBiModel implements ChoiseModel, ChoiseViewModel {

    @Inject
    ChoiseBiModel() {
        super();
    }
}
