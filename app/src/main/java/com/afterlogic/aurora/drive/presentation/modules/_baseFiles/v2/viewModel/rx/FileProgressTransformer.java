package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel.rx;

import android.databinding.ObservableField;

import com.afterlogic.aurora.drive.core.common.rx.OptionalDisposable;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.dialog.ProgressViewModel;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 * Created by aleksandrcikin on 27.07.17.
 * mail: mail@sunnydaydev.me
 */

public class FileProgressTransformer<T extends Progressible> implements ObservableTransformer<T, T> {

    private final String title;
    private final ObservableField<ProgressViewModel> progress;

    public FileProgressTransformer(String title, ObservableField<ProgressViewModel> progress) {
        this.title = title;
        this.progress = progress;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        OptionalDisposable disposable = new OptionalDisposable();
        return upstream
                .doOnNext(progressible -> {

                    float max = progressible.getMax();
                    float progress = progressible.getProgress();

                    float progressRatio;

                    if (max > 0 && progress > 0) {
                        progressRatio = progress / max;
                    } else {
                        progressRatio = -1;
                    }

                    if (progressRatio == -1) {
                        this.progress.set(ProgressViewModel.Factory.indeterminateProgress(
                                title,
                                progressible.getName(),
                                disposable::disposeAndClear
                        ));
                    } else {
                        this.progress.set(ProgressViewModel.Factory.progress(
                                title,
                                progressible.getName(),
                                (int) (100 * progressRatio),
                                100,
                                disposable::disposeAndClear
                        ));
                    }
                })
                .doFinally(() -> progress.set(null))
                .compose(disposable::track);
    }
}
