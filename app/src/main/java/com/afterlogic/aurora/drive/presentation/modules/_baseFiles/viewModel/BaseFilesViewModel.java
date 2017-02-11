package com.afterlogic.aurora.drive.presentation.modules._baseFiles.viewModel;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.model.FileType;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface BaseFilesViewModel {

    ObservableList<FileType> getFileTypes();

    BaseFilesModel getModel();

    ObservableBoolean getRefreshing();

    ObservableField<String> getFolderTitle();

    ObservableBoolean getLocked();

    ObservableInt getCurrentPagePosition();
}
