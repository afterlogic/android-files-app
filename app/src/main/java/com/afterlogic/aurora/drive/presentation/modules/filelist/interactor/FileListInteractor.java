package com.afterlogic.aurora.drive.presentation.modules.filelist.interactor;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.modules.interactor.Interactor;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by sashka on 07.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileListInteractor extends Interactor {

    Single<List<AuroraFile>> getFilesList(AuroraFile folder);

    Single<Uri> getThumbnail(AuroraFile file);

    Observable<Progressible<File>> downloadFileForOpen(AuroraFile file);

    Observable<Progressible<File>> downloadFile(AuroraFile file);
}
