package com.afterlogic.aurora.drive.presentation.modules.main.v2.interactor;

import android.support.v4.app.Fragment;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.SubModuleScope;
import com.afterlogic.aurora.drive.core.common.rx.PermissionEventSource;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.model.error.PermissionDeniedError;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import io.reactivex.Completable;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */
@SubModuleScope
public class MainFilesListViewInteractor {

    private static final int STORAGE_PERMISSION_REQUEST = 1;

    private static final String[] STORAGE_PERMISSIONS = new String[]{
            WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE
    };

    private final OptWeakRef<Fragment> weakView = OptWeakRef.empty();

    private final EventBus bus;

    @Inject
    public MainFilesListViewInteractor(EventBus bus) {
        this.bus = bus;
    }

    public void bindView(Fragment fragment) {
        weakView.set(fragment);
    }

    public void clearView() {
        weakView.clear();
    }

    Completable requireWritePermission() {
        return PermissionEventSource.create(bus)
                .startWith(requestPermission(STORAGE_PERMISSION_REQUEST, STORAGE_PERMISSIONS).toObservable())
                .filter(event -> event.getRequestId() == STORAGE_PERMISSION_REQUEST)
                .firstOrError()
                .flatMapCompletable(event -> {
                    if (event.isAllGranted()) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new PermissionDeniedError(STORAGE_PERMISSION_REQUEST, STORAGE_PERMISSIONS));
                    }
                });
    }

    private Completable requestPermission(int requestId, String[] permissions) {
        return Completable.fromAction(() -> {

            Fragment fragment = weakView.get();
            if (fragment == null) {
                throw new Error("View not present.");
            }

            fragment.requestPermissions(permissions, requestId);
        });
    }
}
