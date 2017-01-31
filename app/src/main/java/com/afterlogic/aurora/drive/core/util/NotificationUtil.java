package com.afterlogic.aurora.drive.core.util;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.afterlogic.aurora.drive.R;

/**
 * Created by sashka on 14.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class NotificationUtil {
    public static final int FILE_EXIST = -3;
    public static final int CANT_UPLOAD_CHANGES = -4;
    public static final int SYNC_FILE_CONFLICT = -5;

    private static final int UNIQUE_REQUEST_ID_SHIFT = -1000;
    private static int mUniquePendingRequestId = UNIQUE_REQUEST_ID_SHIFT;

    public static void notifyMessage(String title, String message, int id, Context ctx){
        NotificationCompat.Builder notify = getDefaultNotifyBuilder(ctx)
                .setTicker(title)
                .setContentTitle(title)
                .setContentText(message);

        NotificationManagerCompat manager = NotificationManagerCompat.from(ctx);
        manager.notify(id, notify.build());
    }

    public static void notifyProgress(String title, int progress, int max, int id, Context ctx){
        NotificationCompat.Builder notify = getDefaultNotifyBuilder(ctx)
                .setTicker(title)
                .setContentTitle(title);

        if (max > 0){
            notify.setProgress(max, progress, false);
        }else{
            notify.setProgress(0, 0, true);
        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(ctx);
        manager.notify(id, notify.build());
    }

    public static NotificationCompat.Builder getDefaultNotifyBuilder(Context ctx){
        return new NotificationCompat.Builder(ctx)
                .setSmallIcon(R.drawable.ic_notify)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(ctx, R.color.colorPrimary));
    }

    public static int getNextUniquePendingRequsetId(){
        if (mUniquePendingRequestId <= UNIQUE_REQUEST_ID_SHIFT){
            return mUniquePendingRequestId--;
        } else {
            mUniquePendingRequestId = UNIQUE_REQUEST_ID_SHIFT;
            return getNextUniquePendingRequsetId();
        }
    }
}
