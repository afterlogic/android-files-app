package com.afterlogic.aurora.drive.core.common.util;

import java.io.Closeable;

/**
 * Created by sashka on 15.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class IOUtil {

    public static void closeQuietly(Closeable closeable){
        if (closeable == null) return;
        try{
            closeable.close();
        } catch (Exception e){
            //no-op
        }
    }
}
