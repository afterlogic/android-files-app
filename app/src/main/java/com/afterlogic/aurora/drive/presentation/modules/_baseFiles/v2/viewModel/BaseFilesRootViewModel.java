package com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.core.common.rx.Subscriber;
import com.afterlogic.aurora.drive.model.FileType;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.LifecycleViewModel;
import com.afterlogic.aurora.drive.presentation.common.modules.v3.viewModel.UiObservableField;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.v2.interactor.BaseFilesRootInteractor;

/**
 * Created by aleksandrcikin on 11.07.17.
 * mail: mail@sunnydaydev.me
 */

public class BaseFilesRootViewModel extends LifecycleViewModel {

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> subtitle = new UiObservableField<>();
    public final ObservableBoolean fileTypesLocked = new ObservableBoolean(false);
    public final ObservableList<FileType> fileTypes = new ObservableArrayList<>();

    private final BaseFilesRootInteractor interactor;
    private final Subscriber subscriber;

    public BaseFilesRootViewModel(BaseFilesRootInteractor interactor, Subscriber subscriber) {
        this.interactor = interactor;
        this.subscriber = subscriber;
    }
}
