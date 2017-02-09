package com.afterlogic.aurora.drive.presentation.modulesBackground.fileLoad.view;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileLoadObservable {

    private final FileLoadProgressSource mProgressSource = new FileLoadProgressSource();
    private final ResultSource mResultSource = new ResultSource();

    public Observable<FileLoadProgress> progress(){
        return Observable.defer(() -> mProgressSource);
    }

    public Completable result(){
        return Completable.defer(() -> mResultSource);
    }

    private class FileLoadProgressSource implements ObservableSource<FileLoadProgress> {

        @Override
        public void subscribe(Observer<? super FileLoadProgress> observer) {

        }
    }

    private class ResultSource implements CompletableSource{

        @Override
        public void subscribe(CompletableObserver cs) {

        }
    }
}
