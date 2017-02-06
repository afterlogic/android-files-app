package com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableList;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesViewModel {

    private final ObservableList<FileType> mFileTypes = new ObservableArrayList<>();

    private final ObservableBoolean mRefreshing = new ObservableBoolean(true);

    public ObservableList<FileType> getFileTypes() {
        return mFileTypes;
    }

    public MainFilesModel getController(){
        return new Controller();
    }

    public ObservableBoolean getRefreshing() {
        return mRefreshing;
    }

    private class Controller implements MainFilesModel{

        @Override
        public void setFileTypes(List<FileType> types) {
            mFileTypes.clear();
            mFileTypes.addAll(types);
            mRefreshing.set(false);
        }
    }
}
