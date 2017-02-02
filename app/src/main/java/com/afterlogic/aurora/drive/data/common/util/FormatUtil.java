package com.afterlogic.aurora.drive.data.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sashka on 26.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class FormatUtil {

    private static final String TAG = FormatUtil.class.getSimpleName();

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());

    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static String formatDate(Date date){
        return DATE_FORMAT.format(date);
    }

    public static String formatTime(Date date){
        return TIME_FORMAT.format(date);
    }

}
