package com.afterlogic.aurora.drive.presentation.modules.filelist.viewModel;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.support.annotation.IntRange;
import android.view.View;

import com.afterlogic.aurora.drive.BR;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;
import com.afterlogic.aurora.drive.data.modules.appResources.AppResources;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.util.FileUtil;
import com.afterlogic.aurora.drive.presentation.modules.filelist.presenter.FileListPresenter;

import java.util.List;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class FileViewModel extends BaseObservable implements View.OnClickListener, View.OnLongClickListener{

    private final OptWeakRef<FileListPresenter> mPresenter;
    private final AppResources mAppResources;
    private final List<AuroraFile> mSelected;

    private final FileModel mModel = new FileModelImpl();

    private AuroraFile mAuroraFile;
    private Uri mThumbNail = null;

    public FileViewModel(AuroraFile auroraFile,
                         List<AuroraFile> multiChoise,
                         OptWeakRef<FileListPresenter> presenter,
                         AppResources appResources) {
        mAuroraFile = auroraFile;
        mPresenter = presenter;
        mAppResources = appResources;
        mSelected = multiChoise;
    }

    @Bindable
    public String getFileName(){
        return mAuroraFile.getName();
    }

    @Bindable
    public Uri getFileIcon(){
        if (mAuroraFile.isFolder()) {
            return mAppResources.getResourceUri(R.drawable.ic_folder);
        } else {
            if (mThumbNail != null) {
                return mThumbNail;
            } else {
                int fileIconRes = FileUtil.getFileIconRes(mAuroraFile);
                return mAppResources.getResourceUri(fileIconRes);
            }
        }
    }

    @Bindable
    public boolean getSelected(){
        return mSelected.contains(mAuroraFile);
    }

    public Uri getStatusIcon(){
        if (mAuroraFile.isFolder()) {
            return mAppResources.getResourceUri(R.drawable.ic_next);
        } else {
            //TODO check status
            return null;
        }
    }

    public FileModel getModel(){
        return mModel;
    }

    @IntRange(from = -1, to = 1000)
    public int getProgress(){
        return -1;
    }

    @Override
    public void onClick(View view) {
        mPresenter.ifPresent(presenter -> presenter.onFileClick(mAuroraFile));
    }

    @Override
    public boolean onLongClick(View view) {
        mPresenter.ifPresent(presenter -> presenter.onFileLongClick(mAuroraFile));
        return mPresenter.isPresent();
    }

    private class FileModelImpl implements FileModel{

        @Override
        public void setThumbNail(Uri uri) {
            mThumbNail = uri;
            notifyPropertyChanged(BR.fileIcon);
        }

        @Override
        public AuroraFile getFile() {
            return mAuroraFile;
        }

    }
}
