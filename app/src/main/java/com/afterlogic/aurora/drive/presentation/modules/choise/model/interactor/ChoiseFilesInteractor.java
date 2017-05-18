package com.afterlogic.aurora.drive.presentation.modules.choise.model.interactor;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.afterlogic.aurora.drive.presentation.modules._baseFiles.model.interactor.FilesListInteractor;

import java.io.File;

import io.reactivex.Observable;

/**
 * Created by sashka on 11.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface ChoiseFilesInteractor extends FilesListInteractor {

    Observable<Progressible<File>> download(AuroraFile file);
}
