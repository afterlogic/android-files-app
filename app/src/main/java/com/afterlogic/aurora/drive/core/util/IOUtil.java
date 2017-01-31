package com.afterlogic.aurora.drive.core.util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by sashka on 22.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class IOUtil {

    public static void close(InputStream is){
        if (is == null) return;
        try {
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(OutputStream os){
        if (os == null) return;
        try {
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
