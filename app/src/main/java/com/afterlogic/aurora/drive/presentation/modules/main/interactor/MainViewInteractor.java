package com.afterlogic.aurora.drive.presentation.modules.main.interactor;

import android.net.Uri;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.BaseViewInteractor;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core.ActivityResultInteractor;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core.CurrentActivityTracker;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */
@ModuleScope
public class MainViewInteractor extends BaseViewInteractor {

    private static final int UPLOAD_ACTIVITY_REQUEST = 1;

    private final ActivityResultInteractor activityResultInteractor;
    private final AppRouter appRouter;

    @Inject
    MainViewInteractor(CurrentActivityTracker tracker,
                       ActivityResultInteractor activityResultInteractor,
                       AppRouter appRouter) {
        super(tracker);
        this.activityResultInteractor = activityResultInteractor;
        this.appRouter = appRouter;
    }

    Single<Uri> getFileForUpload() {
        return this.activityResultInteractor.waitResult(UPLOAD_ACTIVITY_REQUEST)
                .doOnSubscribe(disposable -> appRouter.navigateToWithResult(AppRouter.EXTERNAL_CHOOSE_FILE_FOR_UPLOAD, UPLOAD_ACTIVITY_REQUEST))
                .compose(this::checkSuccess)
                .map(event -> event.getResult().getData());
    }

    Maybe<String> getNewNameForFile(AuroraFile file) {
        return getInputDialog(
                R.string.prompt_input_new_file_name,
                inputView -> {
                    final String ext = FileUtil.getFileExtension(file.getName());

                    if (ext != null && !file.isFolder() && !file.isLink()) {

                        //Set disallow only for 'normal' file
                        inputView.setOnSelectionChangeListener((start, end) -> {
                            int lenght = inputView.getText().length();
                            int max = lenght - ext.length() - 1;
                            boolean fixed = false;
                            if (start > max){
                                start = max;
                                fixed = true;
                            }
                            if (end > max){
                                end = max;
                                fixed = true;
                            }
                            if (fixed){
                                inputView.setSelection(start, end);
                            }
                        });

                    }

                    inputView.setText(file.getName());
                    inputView.setSelection(file.getName().length());
                },
                null
        );
    }


    Maybe<String> getNewFolderName() {
        return getInputDialog(R.string.prompt_create_folder);
    }
}
