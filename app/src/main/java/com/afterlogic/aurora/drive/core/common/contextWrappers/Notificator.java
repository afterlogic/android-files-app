package com.afterlogic.aurora.drive.core.common.contextWrappers;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.AppScope;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 04.08.17.
 * mail: mail@sunnydaydev.me
 */

@AppScope
public class Notificator {

    private static final String CHANEL_MAIN = "main_chanel";

    private static final int PENDING_DOWNLOADS = 1;

    private static final int ID_DOWNLOADS = 1;

    private final Context appContext;

    @Inject
    public Notificator(Context appContext) {
        this.appContext = appContext;
    }

    public void makeDownloadsNotification(String title, String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(appContext, CHANEL_MAIN);

        Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                appContext, PENDING_DOWNLOADS, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = builder.setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_notify)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat nm = NotificationManagerCompat.from(appContext);
        nm.notify(UUID.randomUUID().toString(), ID_DOWNLOADS, notification);
    }
}
