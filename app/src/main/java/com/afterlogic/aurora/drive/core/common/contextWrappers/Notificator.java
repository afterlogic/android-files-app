package com.afterlogic.aurora.drive.core.common.contextWrappers;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.annotation.scopes.AppScope;
import com.afterlogic.aurora.drive.core.consts.NotificationConst;
import com.afterlogic.aurora.drive.presentation.modules.login.view.LoginActivity;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;

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

    private static final int PENDING_RELOGIN = 2;

    private static final int ID_DOWNLOADS = 200001;
    private static final int ID_SYNC_PROGRESS = 200002;
    private static final int ID_AUTH_REQUIRED = 200003;

    private final Context appContext;

    private final NotificationManagerCompat notificationManagerCompat;
    private final NotificationManager notificationManager;

    @Inject
    public Notificator(Context appContext) {
        this.appContext = appContext;
        notificationManager = ((NotificationManager) appContext
                .getSystemService(Context.NOTIFICATION_SERVICE));
        notificationManagerCompat = NotificationManagerCompat.from(appContext);

        initChannels();
    }

    public void notifyDownloadedToDownloads(String title, String message) {

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

        notificationManagerCompat.notify(UUID.randomUUID().toString(), ID_DOWNLOADS, notification);
    }

    public void notifyMessage(@Nullable String tag, String title, String message) {

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(appContext, CHANEL_MAIN);

        Notification notification = notifyBuilder.setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notify)
                .setTicker(message)
                .setContentText(message)
                .setContentTitle(title == null ? appContext.getString(R.string.app_name) : title)
                .build();

        notificationManagerCompat.notify(tag, NotificationConst.SERVICE_MAJOR_MESSAGE, notification);
    }

    public void notifySyncProgress(SyncProgress progress) {

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(appContext, CHANEL_MAIN)
                .setContentTitle(appContext.getString(R.string.prompt_syncing))
                .setProgress(100, progress.getProgress(), progress.getProgress() == -1)
                .setContentText(progress.getFileName())
                .setSmallIcon(R.drawable.ic_notify)
                .setDefaults(0)
                .setOngoing(true);

        notificationManagerCompat.notify(ID_SYNC_PROGRESS, notifyBuilder.build());
    }

    public void notifyAuthRequired() {

        PendingIntent contentIntent = PendingIntent.getActivity(
                appContext, PENDING_RELOGIN, LoginActivity.intent(true), 0
        );

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(appContext, CHANEL_MAIN)
                .setContentTitle(appContext.getString(R.string.app_name))
                .setContentText(appContext.getString(R.string.prompt_relogin_notification))
                .setSmallIcon(R.drawable.ic_notify)
                .setAutoCancel(true)
                .setContentIntent(contentIntent)
                .setOngoing(true);

        notificationManagerCompat.notify(ID_AUTH_REQUIRED, notifyBuilder.build());

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void initChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationChannel main = new NotificationChannel(
                CHANEL_MAIN, "Main", NotificationManager.IMPORTANCE_DEFAULT
        );
        //noinspection ConstantConditions
        notificationManager.createNotificationChannel(main);
    }

    public void cancelSyncProgressNotification() {
        notificationManagerCompat.cancel(ID_SYNC_PROGRESS);
    }
}
