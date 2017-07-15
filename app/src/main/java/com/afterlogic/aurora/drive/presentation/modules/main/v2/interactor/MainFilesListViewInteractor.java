package com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor;

import android.net.Uri;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.navigation.AppRouter;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.di.ForViewInteractor;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.BaseViewInteractor;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */
@SubModuleScope
public class MainFilesListViewInteractor extends BaseViewInteractor{

    private static final int STORAGE_PERMISSION_REQUEST = 1;

    private static final String[] STORAGE_PERMISSIONS = new String[]{
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE
    };

    private static final int UPLOAD_ACTIVITY_REQUEST = 1;

    private final AppRouter appRouter;

    @Inject
    MainFilesListViewInteractor(@ForViewInteractor EventBus bus, AppRouter appRouter) {
        super(bus);
        this.appRouter = appRouter;
    }

    Completable requireWritePermission() {
        return requireWritePermission(STORAGE_PERMISSION_REQUEST, STORAGE_PERMISSIONS);
    }

    Single<Uri> getFileForUpload() {
        return requireActivityResult(UPLOAD_ACTIVITY_REQUEST)
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
