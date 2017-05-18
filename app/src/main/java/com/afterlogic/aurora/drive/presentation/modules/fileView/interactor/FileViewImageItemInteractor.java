package com.afterlogic.aurora.drive.presentation.modules.fileView.interactor;

import android.net.Uri;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.common.modules.model.interactor.Interactor;
import io.reactivex.Single;

/**
 * Created by sashka on 16.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public interface FileViewImageItemInteractor extends Interactor {

    Single<Uri> viewFile(AuroraFile file);
}
