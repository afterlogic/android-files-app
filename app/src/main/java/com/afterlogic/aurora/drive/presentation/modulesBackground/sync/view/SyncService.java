package com.afterlogic.aurora.drive.presentation.modulesBackground.sync.view;

import android.accounts.Account;
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

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.util.AccountUtil;
import com.afterlogic.aurora.drive.core.consts.NotificationConst;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.presentation.assembly.modules.InjectorsComponent;
import com.afterlogic.aurora.drive.presentation.common.binding.SimpleListener;
import com.afterlogic.aurora.drive.presentation.common.modules.view.MVPService;
import com.afterlogic.aurora.drive.presentation.common.modules.view.ViewPresenter;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.presenter.SyncPresenter;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncProgress;
import com.afterlogic.aurora.drive.presentation.modulesBackground.sync.viewModel.SyncViewModel;

import javax.inject.Inject;

/**
 * Created by sashka on 15.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class SyncService extends MVPService implements SyncView {

    public static final String ACTION_SYNC_STARTED =
            FileSyncAdapter.class.getName() + ".ACTION_SYNC_STARTED";
    public static final String ACTION_BIND_SYNC_LISTENER =
            FileSyncAdapter.class.getName() + ".ACTION_BIND_SYNC_LISTENER";
    public static final String KEY_TARGET =
            FileSyncAdapter.class.getName() + ".KEY_TARGET";

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
        if (file != null) {
            extras.putString(KEY_TARGET, file.getPathSpec());
        }
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
    protected void assembly(InjectorsComponent modulesFactory) {
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
    private static class FileSyncAdapter extends AbstractThreadedSyncAdapter {

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

    }
}
