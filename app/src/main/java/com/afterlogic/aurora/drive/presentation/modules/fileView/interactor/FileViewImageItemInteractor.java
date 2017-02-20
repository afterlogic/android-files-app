package com.afterlogic.aurora.drive.presentation.modules.fileView.interactor;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.Interactor;

import java.io.File;

import io.reactivex.Observable;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewImageItemInteractor extends Interactor {

    Observable<Progressible<File>> donwloadToCache(AuroraFile file);
}
