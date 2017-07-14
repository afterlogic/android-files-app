package com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.di.ForViewInteractor;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.interactor.BaseViewInteractor;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Maybe;

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

    @Inject
    public MainFilesListViewInteractor(@ForViewInteractor EventBus bus) {
        super(bus);
    }

    Completable requireWritePermission() {
        return requestPermission(STORAGE_PERMISSION_REQUEST, STORAGE_PERMISSIONS);
    }


    Maybe<String> getNewFolderName() {
        return getInputDialog(R.string.prompt_create_folder);
    }
}
