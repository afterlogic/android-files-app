package com.afterlogic.aurora.drive.presentation.modules.choise.presenter;

import android.content.Context;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.view.viewState.ViewState;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.presenter.BaseFilesListPresenter;
import com.afterlogic.aurora.drive.presentation.modules.choise.interactor.ChoiseFilesInteractor;
import com.afterlogic.aurora.drive.presentation.modules.choise.router.ChoiseFilesRouter;
import com.afterlogic.aurora.drive.presentation.modules.choise.view.ChoiseFilesView;
import com.afterlogic.aurora.drive.presentation.modules.choise.viewModel.ChoiseFilesModel;

import javax.inject.Inject;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class ChoiseFilesPresenterImpl extends BaseFilesListPresenter<ChoiseFilesView> implements ChoiseFilesPresenter {

    private final ChoiseFilesInteractor mInteractor;
    private final ChoiseFilesRouter mRouter;

    @Inject
    ChoiseFilesPresenterImpl(ViewState<ChoiseFilesView> viewState,
                             ChoiseFilesInteractor interactor,
                             ChoiseFilesModel model,
                             Context appContext,
                             ChoiseFilesRouter router) {
        super(viewState, interactor, model, appContext);
        mInteractor = interactor;
        mRouter = router;
    }

    @Override
    public void onFileClick(AuroraFile file) {
        if (file.isFolder()){
            super.onFileClick(file);
        } else {
            mInteractor.download(file)
                    .compose(this::progressibleLoadTask)
                    .subscribe(
                            mRouter::closeWithResult,
                            this::onErrorObtained
                    );
        }
    }
}
