package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view;

import android.accounts.Account;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.NotificationUtil;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive._unrefactored.presentation.receivers.SyncResolveReceiver;
import com.afterlogic.aurora.drive.core.consts.NotificationConst;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.modules.ModulesFactoryComponent;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.common.modules.view.BaseService;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.presenter.SyncPresenter;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncViewModel;

import java.io.File;

import javax.inject.Inject;

/**
 * Created by sashka on 15.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class SyncService extends BaseService implements SyncView {

    public static final String ACTION_SYNC_STARTED =
            FileSyncAdapter.class.getName() + ".ACTION_SYNC_STARTED";
    public static final String ACTION_SYNC_STATUS_CHANGED =
            FileSyncAdapter.class.getName() + ".ACTION_SYNC_STATUS_CHANGED";
    public static final String ACTION_BIND_SYNC_LISTENER =
            FileSyncAdapter.class.getName() + ".ACTION_BIND_SYNC_LISTENER";
    public static final String KEY_TARGET =
            FileSyncAdapter.class.getName() + ".KEY_TARGET";
    public static final String KEY_MAX_PROGRESS =
            FileSyncAdapter.class.getName() + ".KEY_MAX_PROGRESS";
    public static final String KEY_PROGRESS =
            FileSyncAdapter.class.getName() + ".KEY_PROGRESS";
    public static final String KEY_SYNC_TARGET =
            FileSyncAdapter.class.getName() + ".KEY_SYNC_TARGET";
    public static final String KEY_RESOLVE_CONFLICT =
            FileSyncAdapter.class.getName() + ".KEY_RESOLVE_CONFLICT";

    private final SimpleListener mProgressListener = new SimpleListener(this::updateProgressNotify);

    @Inject @ViewPresenter
    protected SyncPresenter mPresenter;

    @Inject
    protected SyncViewModel mViewModel;

    private FileSyncAdapter mFileSyncAdapter;
    private NotificationManagerCompat mNotificationManager;

    private SyncMessenger mMessenger;

    /**
     * Request sync for all files.
     */
    public static void requestSync(AuroraFile file, Context context) {
        Bundle extras = new Bundle();
        extras.putString(KEY_TARGET, file.getType() + "/" + file.getFullPath());
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        Account account = AccountUtil.getCurrentAccount(context);
        ContentResolver.requestSync(
                account,
                AccountUtil.FILE_SYNC_AUTHORITY,
                extras
        );
    }

    /**
     * Request sync for all files.
     */
    public static void requestSync(Context context) {
        requestSync(null, context);
    }

    @Override
    protected void assembly(ModulesFactoryComponent modulesFactory) {
        modulesFactory.sync().inject(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
        mFileSyncAdapter = new FileSyncAdapter(getApplicationContext(), mPresenter);

        mViewModel.getCurrentSyncProgress().addOnPropertyChangedCallback(mProgressListener);
        mMessenger = new SyncMessenger();

        sendBroadcast(new Intent(ACTION_SYNC_STARTED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.getCurrentSyncProgress().removeOnPropertyChangedCallback(mProgressListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (ACTION_BIND_SYNC_LISTENER.equals(intent.getAction())){
            return mMessenger.getBinder();
        } else {
            return mFileSyncAdapter.getSyncAdapterBinder();
        }
    }

    private void updateProgressNotify(){
        SyncProgress progress = mViewModel.getCurrentSyncProgress().get();
        if (progress == null){
            mNotificationManager.cancel(NotificationConst.SYNC_PROGRESS);
        } else {
            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle(getString(R.string.prompt_syncing))
                    .setProgress(100, progress.getProgress(), progress.getProgress() == -1)
                    .setContentText(progress.getFileName())
                    .setSmallIcon(R.drawable.ic_folder)
                    .setOngoing(true);

            mNotificationManager.notify(NotificationConst.SYNC_PROGRESS, notifyBuilder.build());
            mMessenger.notifyProgress(progress);
        }
    }


    /**
     * Created by sashka on 15.04.16.
     * mail: sunnyday.development@gmail.com
     */
    public static class FileSyncAdapter extends AbstractThreadedSyncAdapter {

        private final SyncPresenter mPresenter;

        @SuppressWarnings("WeakerAccess")
        public FileSyncAdapter(Context context, SyncPresenter presenter) {
            super(context, false);
            mPresenter = presenter;
        }

        @Override
        public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
            mPresenter.onSyncPerformed();
        }

        /**
         * Show notification when remote sync target not exist.
         * Static because used in
         *
         * @param watching - current watching file.
         * @param ctx      - application context.
         */
        public static void showRemoteDeletedNotify(WatchingFile watching, Context ctx) {
            String filename = watching.getLocalFilePath().
                    substring(watching.getLocalFilePath().lastIndexOf(File.separator) + 1);
            String title = ctx.getString(R.string.notify_remote_not_exist, filename);

            //Create notification
            NotificationCompat.Builder builder =
                    NotificationUtil.getDefaultNotifyBuilder(ctx)
                            .setContentTitle(title)
                            .setContentText(ctx.getString(R.string.notify_remote_not_exise_save))
                            .setTicker(title)
                            .addAction(R.drawable.ic_ok,
                                    ctx.getString(R.string.dialog_button_yes),
                                    getRemoteDeltedResolveIntent(true, watching, ctx))
                            .addAction(R.drawable.ic_cancel,
                                    ctx.getString(R.string.dialog_button_no),
                                    getRemoteDeltedResolveIntent(false, watching, ctx));

            //Notify
            NotificationManagerCompat.from(ctx)
                    .notify(
                            watching.getRemoteUniqueSpec(),
                            NotificationUtil.SYNC_FILE_CONFLICT,
                            builder.build()
                    );
        }

        /**
         * Generate resolve conflict for not deleted remote file {@link PendingIntent} for notification.
         *
         * @param save     - if true local will be saved in {@link android.os.Environment#DIRECTORY_DOWNLOADS}
         *                 else just deleted.
         * @param watching - current {@link WatchingFile}.
         * @param ctx      - application context.
         * @return - prepared {@link PendingIntent} with unique id and only for current package.
         */
        private static PendingIntent getRemoteDeltedResolveIntent(boolean save, WatchingFile watching,
                                                                  Context ctx) {
            Intent saveIntent = new Intent(
                    SyncResolveReceiver.ACTION_RESOLVE_REMOTE_NOT_EXIST);
            saveIntent.putExtra(SyncResolveReceiver.KEY_SAVE_LOCAL, save);
            saveIntent.putExtra(
                    KEY_TARGET,
                    watching.getRemoteUniqueSpec()
            );
            saveIntent.setPackage(BuildConfig.APPLICATION_ID);
            return PendingIntent
                    .getBroadcast(ctx, NotificationUtil.getNextUniquePendingRequsetId(),
                            saveIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}
