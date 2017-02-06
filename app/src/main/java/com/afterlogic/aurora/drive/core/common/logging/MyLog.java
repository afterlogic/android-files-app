package com.afterlogic.aurora.drive.core.common.logging;

import android.text.TextUtils;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

/**
 * Created by sashka on 31.08.16.<p/>
 * mail: sunnyday.development@gmail.com
 *
 * Own logger implementation for configuring log in future.
 */
public class MyLog {

    private static final Logger DEFAULT_LOGGER = new DefLogger();

    private static Logger sLogger = DEFAULT_LOGGER;

    public static void setLogger(Logger logger){
        sLogger = logger;
    }

    public static void d(String message){
        d(null, message);
    }

    public static void d(Object tag, String message){
        sLogger.logDebug(getTag(tag), message);
    }

    public static void w(String message){
        w(null, message);
    }

    public static void w(Object tag, String message){
        sLogger.logWarn(getTag(tag), message);
    }

    public static void i(String message){
        i(null, message);
    }

    public static void i(Object tag, String message){
        sLogger.logInfo(getTag(tag), message);
    }

    public static void e(Throwable throwable){
        e(null, throwable);
    }

    public static void e(Object tag, Throwable e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        e(tag, sw.toString());
    }

    public static void e(String message){
        e(null, message);
    }

    public static void e(Object tag, String message){
        sLogger.logError(getTag(tag), message);
    }

    public static void majorException(Throwable throwable){
        majorException(null, throwable);
    }

    public static void majorException(Object tag, Throwable e){
        e(tag, e);
        sLogger.logMajorException(getTag(tag), e);
    }

    public static void majorException(String message){
        majorException(null, message);
    }

    public static void majorException(Object tag, String errorMessage){
        Error e = new Error(errorMessage);
        StackTraceElement[] stackTrace = e.getStackTrace();
        int index = 0;
        while (stackTrace[index].getClassName().equals(MyLog.class.getName())){
            index++;
        }
        e.setStackTrace(Arrays.copyOfRange(stackTrace, index, stackTrace.length - 1));
        majorException(tag, e);
    }

    private static String getTag(Object tag){
        String tagString = null;
        if (tag != null) {
            if (tag instanceof String || tag instanceof CharSequence || tag instanceof Number) {
                tagString = String.valueOf(tag);
            } else {
                tagString = tag.getClass().getSimpleName();
            }
        }

        if (TextUtils.isEmpty(tagString)) {
            tagString = getTagFromStackTrace();
        }

        return tagString;
    }

    private static String getTagFromStackTrace(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        boolean myLogSkipped = false;
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            String name = stackTraceElement.getClassName();
            if (!MyLog.class.getName().equals(name)) {
                if (!myLogSkipped) continue;

                int index = name.lastIndexOf('.');
                return name.substring(index + 1);
            } else {
                myLogSkipped = true;
            }
        }
        return "???";
    }

    @SuppressWarnings("WeakerAccess")
    public interface Logger{
        void logInfo(String tag, String message);
        void logDebug(String tag, String message);
        void logWarn(String tag, String message);
        void logError(String tag, String message);
        void logMajorException(String tag, Throwable error);
    }

    private static class DefLogger implements Logger{

        @Override
        public void logInfo(String tag, String message) {
            Log.i(tag, message);
        }

        @Override
        public void logDebug(String tag, String message) {
            Log.d(tag, message);
        }

        @Override
        public void logWarn(String tag, String message) {
            Log.w(tag, message);
        }

        @Override
        public void logError(String tag, String message) {
            Log.e(tag, message);
        }

        @Override
        public void logMajorException(String tag, Throwable error) {
            Log.e(tag, "", error);
        }
    }
}
