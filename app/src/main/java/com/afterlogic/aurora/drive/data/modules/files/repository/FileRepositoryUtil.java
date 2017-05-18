package com.afterlogic.aurora.drive.data.modules.files.repository;

import android.os.Handler;
import android.os.Looper;

import com.afterlogic.aurora.drive.model.AuroraFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sashka on 13.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

class FileRepositoryUtil {

    static final Map<String, List<AuroraFile>> CHECKED_TYPES = new HashMap<>();

    static void startClearCheckedCountDown(){
        Handler deleteHandler = new Handler(Looper.getMainLooper());
        deleteHandler.postDelayed(
                CHECKED_TYPES::clear,
                500
        );
    }
}
