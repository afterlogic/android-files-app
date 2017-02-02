package com.afterlogic.aurora.drive.core.common.util;

import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.interfaces.Creator;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;

/**
 * Created by sashka on 06.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class Safe {

    private static final String TAG = Safe.class.getSimpleName();

    public static <T> T get(Creator<T> creator, T def){
        return get(creator, def, null);
    }

    public static <T> T get(Creator<T> creator, T def, @Nullable Consumer<Throwable> onError){
        try {
            return creator.create();
        } catch (Throwable e){
            if (onError != null){
                onError.consume(e);
            } else {
                MyLog.e(TAG, e.getMessage());
            }
            return def;
        }
    }

    public static void doAction(Runnable action){
        doAction(action, null);
    }

    public static void doAction(Runnable action, @Nullable Consumer<Throwable> onError){
        try{
            action.run();
        } catch (Throwable e){
            if (onError != null){
                onError.consume(e);
            } else {
                MyLog.e(TAG, e.getMessage());
            }
        }
    }
}
