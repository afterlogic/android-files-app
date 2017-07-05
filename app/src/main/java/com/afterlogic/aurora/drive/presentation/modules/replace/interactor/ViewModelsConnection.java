package com.afterlogic.aurora.drive.presentation.modules.replace.interactor;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;
import com.afterlogic.aurora.drive.presentation.modules.replace.viewModel.FolderStackSize;

import javax.inject.Inject;

import io.reactivex.subjects.PublishSubject;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

@ModuleScope
class ViewModelsConnection {

    public PublishSubject<FolderStackSize> folderStackSize = PublishSubject.create();
    public PublishSubject<String> popRequest = PublishSubject.create();
    public PublishSubject<String> createFolderRequest = PublishSubject.create();

    @Inject
    ViewModelsConnection() {
    }
}
