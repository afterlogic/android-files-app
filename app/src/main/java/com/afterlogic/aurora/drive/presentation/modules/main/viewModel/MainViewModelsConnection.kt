package com.afterlogic.aurora.drive.presentation.modules.main.viewModel

import androidx.core.util.Pair

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel.ViewModelsConnection

import javax.inject.Inject

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * Created by aleksandrcikin on 13.07.17.
 * mail: mail@sunnydaydev.me
 */
@ModuleScope
internal class MainViewModelsConnection @Inject
constructor() : ViewModelsConnection<MainFilesListViewModel>() {

    private val multiChoiceMode: Subject<Boolean> = BehaviorSubject.createDefault(false)

    private val multiChoice: Subject<List<MultiChoiceFile>> =
            BehaviorSubject.createDefault(emptyList())

    private val multiChoiceActionPublisher: Subject<Pair<String, MultiChoiceAction>> =
            PublishSubject.create()

    private val mainActionPublisher: Subject<Pair<String, MainAction>> = PublishSubject.create()

    private val folderChangedPublisher: Subject<AuroraFile> = PublishSubject.create()

    fun setMultiChoiceMode(multiChoiceMode: Boolean) = this.multiChoiceMode.onNext(multiChoiceMode)

    fun setMultiChoice(multiChoice: List<MultiChoiceFile>) = this.multiChoice.onNext(multiChoice)

    fun sendMultiChoiceAction(targetType: String, action: MultiChoiceAction) =
            multiChoiceActionPublisher.onNext(Pair(targetType, action))

    fun sendMainAction(targetFileType: String, action: MainAction) =
            mainActionPublisher.onNext(Pair(targetFileType, action))

    fun getMultiChoiceMode(): Observable<Boolean> = multiChoiceMode.hide()

    fun getMultiChoice(): Observable<List<MultiChoiceFile>> = multiChoice.hide()

    fun listenMultiChoiceAction(fileType: String): Observable<MultiChoiceAction> =
            multiChoiceActionPublisher
                    .filter { action -> action.first == fileType }
                    .map { action -> action.second }

    fun listenMainAction(fileType: String): Observable<MainAction> = mainActionPublisher
            .filter { action -> action.first == fileType }
            .map { action -> action.second }

    fun onFolderContentChanged(folder: AuroraFile) = folderChangedPublisher.onNext(folder)

    fun lisetenFolderChanges(type: String, fullPath: String): Observable<AuroraFile> =
            folderChangedPublisher.hide()
                    .filter { it.type == type && it.fullPath == fullPath }

}
