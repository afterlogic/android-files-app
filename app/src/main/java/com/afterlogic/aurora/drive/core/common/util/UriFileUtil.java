package com.afterlogic.aurora.drive.core.common.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by sashka on 15.09.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class UriFileUtil {

    public static String getFileName(Uri uri, Context context){
        switch (uri.getScheme()){
            case "file": return new File(uri.getPath()).getName();
            case "content":
                Cursor cursor = context.getContentResolver().query(
                        uri,
                        new String[]{OpenableColumns.DISPLAY_NAME},
                        null, null, null);
                String name = null;
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                    cursor.close();
                }
                return name;
            default:
                return null;
        }
    }

    public static String getFileExtension(Uri uri, Context context){
        switch (uri.getScheme()){
            case "content":
                MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                String type = context.getContentResolver().getType(uri);
                return mimeTypeMap.getExtensionFromMimeType(type);
            default:
                return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        }
    }
}
