package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.rx;

import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.ProgressViewModel;
import com.afterlogic.aurora.drive.presentation.modules.main.viewModel.MainFilesListViewModel;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

/**
 * Created by aleksandrcikin on 27.07.17.
 * mail: mail@sunnydaydev.me
 */
public class IndeterminateProgressTransformer<T> implements SingleTransformer<T, T>, CompletableTransformer {

    private MainFilesListViewModel mainFilesListViewModel;
    private final String title;
    private final String message;

    public IndeterminateProgressTransformer(MainFilesListViewModel mainFilesListViewModel, String title, String message) {
        this.mainFilesListViewModel = mainFilesListViewModel;
        this.title = title;
        this.message = message;
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        return upstream
                .doOnSubscribe(disposable -> mainFilesListViewModel.progress.set(createProgress()))
                .doFinally(() -> mainFilesListViewModel.progress.set(null));
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream
                .doOnSubscribe(disposable -> mainFilesListViewModel.progress.set(createProgress()))
                .doFinally(() -> mainFilesListViewModel.progress.set(null));
    }

    private ProgressViewModel createProgress() {
        return ProgressViewModel.Factory.indeterminateCircle(
                title,
                message
        );
    }
}
