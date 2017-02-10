package com.afterlogic.aurora.drive.presentation.modules.filesMain.viewModel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.annimon.stream.Stream;

import java.util.List;

/**
 * Created by sashka on 03.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class MainFilesViewModel extends BaseObservable{

    private String mCurrentFileType = null;
    private String mFolderTitle;
    private boolean mRefreshing = true;
    private String mLogin;

    private final ObservableList<FileType> mFileTypes = new ObservableArrayList<>();

    public ObservableList<FileType> getFileTypes() {
        return mFileTypes;
    }

    public MainFilesModel getController(){
        return new Controller();
    }

    @Bindable
    public boolean getRefreshing() {
        return mRefreshing;
    }

    @Bindable
    public String getFolderTitle() {
        return mFolderTitle;
    }

    @Bindable
    public boolean getLocked(){
        return getCurrentPagePosition() != -1;
    }

    @Bindable
    public int getCurrentPagePosition(){
        return Stream.of(mFileTypes)
                .filter(type -> type.getFilesType().equals(mCurrentFileType))
                .map(mFileTypes::indexOf)
                .findFirst()
                .orElse(-1);
    }

    @Bindable
    public String getLogin(){
        return mLogin;
    }

    private class Controller implements MainFilesModel{

        @Override
        public void setFileTypes(List<FileType> types) {
            mFileTypes.clear();
            mFileTypes.addAll(types);
            mRefreshing = false;
            notifyPropertyChanged(BR.refreshing);
        }

        @Override
        public void setCurrentFolder(AuroraFile folder) {
            if ("".equals(folder.getFullPath())){
                mCurrentFileType = null;
                mFolderTitle = null;
            } else {
                mCurrentFileType = folder.getType();
                mFolderTitle = folder.getName();
            }
            notifyPropertyChanged(BR.folderTitle);
            notifyPropertyChanged(BR.currentPagePosition);
            notifyPropertyChanged(BR.locked);
        }

        @Override
        public void setLogin(String login) {
            mLogin = login;
            notifyPropertyChanged(BR.login);
        }
    }
}
