package com.afterlogic.aurora.drive.presentation.modulesBackground.fileListener.interactor;

import android.os.FileObserver;

import com.afterlogic.aurora.drive.core.common.DeepFolderObserver;
import com.annimon.stream.Stream;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

@SuppressWarnings("WeakerAccess")
class ChangedFilesObserver implements ObservableSource<File>{

    private static final int EVENTS = FileObserver.CLOSE_WRITE | FileObserver.ATTRIB;

    private DeepFolderObserver mDeepFolderObserver;

    private final Set<Observer<? super File>> mObservers = new HashSet<>();

    public static Observable<File> observeFolder(File folder){
        return Observable.defer(() -> new ChangedFilesObserver(folder));
    }

    public ChangedFilesObserver(File folder) {
        mDeepFolderObserver = new DeepFolderObserver(folder, EVENTS, this::onFileEvent);
    }

    @Override
    public void subscribe(Observer<? super File> observer) {
        mObservers.add(observer);
        observer.onSubscribe(disposable(observer));
        updateObserverRunning();
    }

    private void onFileEvent(int event, String path){
        synchronized (mObservers){
            Stream.of(mObservers)
                    .forEach(observer -> observer.onNext(new File(path)));
        }
    }

    private Disposable disposable(Observer<? super File> observer){
        return new Disposable() {
            @Override
            public void dispose() {
                mObservers.remove(observer);
                updateObserverRunning();
            }

            @Override
            public boolean isDisposed() {
                return mObservers.contains(observer);
            }
        };
    }

    private void updateObserverRunning(){
        if (mObservers.size() > 0 && !mDeepFolderObserver.isObserving()){
            mDeepFolderObserver.startObserve();
            return;
        }

        if (mObservers.size() == 0 && mDeepFolderObserver.isObserving()){
            mDeepFolderObserver.stopObserve();
        }
    }
}
