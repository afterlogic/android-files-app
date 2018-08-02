package com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.viewModel

import androidx.databinding.ObservableField
import android.text.TextUtils
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer

import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable
import com.afterlogic.aurora.drive.core.common.rx.Subscriber
import com.afterlogic.aurora.drive.core.common.util.ObjectsUtil
import com.afterlogic.aurora.drive.model.AuroraFile
import com.afterlogic.aurora.drive.presentation.common.binding.utils.SimpleOnPropertyChangedCallback
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.interactor.SearchableFilesListInteractor
import com.afterlogic.aurora.drive.presentation.modules.baseFiles.v2.view.FileListArgs
import java.util.concurrent.TimeUnit

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by aleksandrcikin on 12.07.17.
 * mail: mail@sunnydaydev.me
 */

abstract class SearchableFileListViewModel<
        FileListVM : SearchableFileListViewModel<FileListVM, FileVM, Args>,
        FileVM : AuroraFileViewModel,
        Args : FileListArgs>
protected constructor(
        private val interactor: SearchableFilesListInteractor,
        private val subscriber: Subscriber,
        viewModelsConnection: ViewModelsConnection<FileListVM>
) : FileListViewModel<FileListVM, FileVM, Args>(interactor, subscriber, viewModelsConnection) {

    private val searchPattern = ObservableField("")
    private val setSearchQueryDisposable = OptionalDisposable()

    init {

        SimpleOnPropertyChangedCallback.addTo(searchPattern) { this.onSearchPatternChanged() }
    }

    fun onSearchQuery(query: String?) {

        val checkedQuery = query ?: ""

        if (!ObjectsUtil.equals(searchPattern.get(), checkedQuery)) {
            searchPattern.set(checkedQuery)
        }
    }

    override fun getFilesSource(folder: AuroraFile): Single<List<AuroraFile>> {
        return interactor.getFiles(folder, searchPattern.get())
    }

    private fun onSearchPatternChanged() {

        val delay = if (TextUtils.isEmpty(searchPattern.get())) 0 else 500

        Single.timer(delay.toLong(), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(setSearchQueryDisposable::disposeAndTrack)
                .subscribe(subscriber.subscribe { _ -> reloadCurrentFolder() })

    }

}
