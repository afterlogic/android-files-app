package com.afterlogic.aurora.drive.presentation.modules.upload.interactor;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.interactor.FilesListInteractor;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface UploadFilesInteractor extends FilesListInteractor {

    Single<AuroraFile> createFolder(AuroraFile parentFolder, String name);

    Observable<Progressible<AuroraFile>> uploadFile(AuroraFile folder, Uri file);
}
