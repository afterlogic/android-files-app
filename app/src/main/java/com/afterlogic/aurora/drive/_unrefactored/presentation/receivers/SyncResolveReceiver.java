package com.afterlogic.aurora.drive._unrefactored.presentation.receivers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive._unrefactored.presentation.services.SyncService;
import com.afterlogic.aurora.drive._unrefactored.core.util.FileUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.NotificationUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;

import java.io.File;
import java.sql.SQLException;

/**
 * Created by sashka on 20.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class SyncResolveReceiver extends BroadcastReceiver {

    public static final String ACTION_RESOLVE_SYNC_CONFLICT =
            "com.afterlogic.aurora.drive.ACTION_RESOLVE_SYNC_CONFLICT";
    public static final String ACTION_RESOLVE_REMOTE_NOT_EXIST =
            "com.afterlogic.aurora.drive.ACTION_RESOLVE_REMOTE_NOT_EXIST";

    public static final String KEY_SAVE_LOCAL = SyncResolveReceiver.class.getName() +
            ".KEY_SAVE_LOCAL";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case ACTION_RESOLVE_SYNC_CONFLICT:
                receiveConflict(context, intent);
                break;
            case ACTION_RESOLVE_REMOTE_NOT_EXIST:
                receiveRemoteNotExist(context, intent);
                break;
        }
    }

    /**
     * Handle broadcast from conflict files notification action.
     * @param context - application context.
     * @param intent - result intent from notification.
     */
    private void receiveConflict(Context context, Intent intent){
        String spec = intent.getStringExtra(SyncService.FileSyncAdapter.KEY_CHANGED_REMOTE_FILE_SPEC);
        int type = intent.getIntExtra(SyncService.FileSyncAdapter.KEY_RESOLVE_CONFLICT, 0);

        //Start sync
        AccountManager ac = AccountManager.get(context);
        Account[] accounts = ac.getAccountsByType(AccountUtil.ACCOUNT_TYPE);
        if (accounts.length > 0){

            Bundle extras = new Bundle();
            extras.putString(SyncService.FileSyncAdapter.KEY_CHANGED_REMOTE_FILE_SPEC, spec);
            extras.putInt( SyncService.FileSyncAdapter.KEY_RESOLVE_CONFLICT, type);

            extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

            ContentResolver
                    .requestSync(
                            accounts[0],
                            AccountUtil.FILE_SYNC_AUTHORITY,
                            extras
                    );
        }
    }

    /**
     * Handle broadcast from "remote file not exist" notification.
     * @param ctx - application context.
     * @param i - result intent from notification.
     */
    private void receiveRemoteNotExist(Context ctx, Intent i){
        String spec = i.getStringExtra(SyncService.FileSyncAdapter.KEY_CHANGED_REMOTE_FILE_SPEC);
        boolean saveLocal = i.getBooleanExtra(KEY_SAVE_LOCAL, false);

        //Hide notification
        NotificationManagerCompat.from(ctx)
                .cancel(spec, NotificationUtil.SYNC_FILE_CONFLICT);

        DBHelper db = new DBHelper(ctx);
        WatchingFileDAO dao = db.getWatchingFileDAO();
        try {
            WatchingFile file = dao.queryForId(spec);
            if (file != null){
                File local = new File(file.getLocalFilePath());

                if (saveLocal && local.exists()){
                    AuroraFile remote =
                            AuroraFile.parse(
                                    file.getRemoteFilePath(), file.getRemoteAuroraType(), false);
                    File target = FileUtil.getDownloadsFile(remote);
                    if (local.renameTo(target)){
                        DownloadManager dm = (DownloadManager)
                                ctx.getSystemService(Context.DOWNLOAD_SERVICE);
                        dm.addCompletedDownload(target.getName(),
                                ctx.getString(R.string.prompt_downloaded_file_description), true,
                                FileUtil.getFileMimeType(target), target.getPath(), target.length(), true);
                        dao.delete(file);
                    }
                } else {
                    dao.delete(file);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
