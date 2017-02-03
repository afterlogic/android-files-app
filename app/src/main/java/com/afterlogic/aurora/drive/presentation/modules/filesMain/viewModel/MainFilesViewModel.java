package com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;


import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesViewModel {

    private final ObservableList<FileType> mFileTypes = new ObservableArrayList<>();

    public ObservableList<FileType> getFileTypes() {
        return mFileTypes;
    }

    public MainFilesModel getController(){
        return new Controller();
    }

    private class Controller implements MainFilesModel{

        @Override
        public void setFileTypes(List<FileType> types) {
            mFileTypes.clear();
            mFileTypes.addAll(types);
        }
    }
}
