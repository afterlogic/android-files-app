package com.afterlogic.aurora.drive.core.util;

/**
 * Created by sashka on 08.04.16.
 * mail: sunnyday.development@gmail.com
 */
public enum  DownloadType{
    DOWNLOAD_OPEN(0),
    DOWNLOAD_TO_DOWNLOADS(1),
    DOWNLOAD_FOR_EMAIL(2),
    DOWNLOAD_RESULT(3),
    DOWNLOAD_FOR_OFFLINE(4);

    private int mIntValue;

    DownloadType(int i){
        mIntValue = i;
    }

    public int toInt(){
        return mIntValue;
    }

    public static DownloadType fromInt(int i){
        for (DownloadType type:values()){
            if (i == type.mIntValue){
                return type;
            }
        }
        return null;
    }
}