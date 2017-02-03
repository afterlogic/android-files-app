package com.afterlogic.aurora.drive._unrefactored.presentation.services;

import android.accounts.Account;
import android.app.PendingIntent;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.afterlogic.aurora.drive.BuildConfig;
import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.ApiCompatibilityUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.FileUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.NotificationUtil;
import com.afterlogic.aurora.drive._unrefactored.data.common.ApiProvider;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiResponseError;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiTask;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.AuroraApi;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive.data.modules.files.FilesRepository;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;
import com.afterlogic.aurora.drive._unrefactored.presentation.receivers.SyncResolveReceiver;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.Holder;
import com.afterlogic.aurora.drive.core.common.util.IOUtil;
import com.afterlogic.aurora.drive.core.consts.Const;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.AuroraSession;
import com.afterlogic.aurora.drive.model.FileInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by sashka on 15.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class SyncService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new FileSyncAdapter(this).getSyncAdapterBinder();
    }

    /**
     * Created by sashka on 15.04.16.
     * mail: sunnyday.development@gmail.com
     */
    public static class FileSyncAdapter extends AbstractThreadedSyncAdapter {

        public static final String ACTION_SYNC_STATUS_CHANGED =
                FileSyncAdapter.class.getName() + ".ACTION_SYNC_STATUS_CHANGED";

        public static final String KEY_CHANGED_REMOTE_FILE_SPEC =
                FileSyncAdapter.class.getName() + ".KEY_CHANGED_REMOTE_FILE_SPEC";
        public static final String KEY_MAX_PROGRESS =
                FileSyncAdapter.class.getName() + ".KEY_MAX_PROGRESS";
        public static final String KEY_PROGRESS =
                FileSyncAdapter.class.getName() + ".KEY_PROGRESS";
        public static final String KEY_SYNC_TARGET =
                FileSyncAdapter.class.getName() + ".KEY_SYNC_TARGET";
        public static final String KEY_RESOLVE_CONFLICT =
                FileSyncAdapter.class.getName() + ".KEY_RESOLVE_CONFLICT";

        @SuppressWarnings("WeakerAccess")
        public static final int TYPE_LOCAL= 1;
        @SuppressWarnings("WeakerAccess")
        public static final int TYPE_REMOTE = 2;

        private static final int FLAG_LOCAL_EXIST = 0x1;
        private static final int FLAG_REMOTE_EXIST = 0x2;
        private static final int FLAG_NEED_DOWNLOAD = 0x4;
        private static final int FLAG_NEED_UPLOAD = 0x8;
        private static final int FLAG_ALL =
                FLAG_LOCAL_EXIST | FLAG_REMOTE_EXIST | FLAG_NEED_UPLOAD | FLAG_NEED_DOWNLOAD;

        private Context mContext;
        private DBHelper mDB;
        private WatchingFileDAO mWatchingFileDAO;
        private List<SyncTarget> mSyncTargets = new ArrayList<>();

        private long mLastBroadcastTime = 0;

        private static final String TAG = FileSyncAdapter.class.getSimpleName();

        private ApiProvider mApi = AuroraApi.getApiProvider();

        @SuppressWarnings("WeakerAccess")
        public FileSyncAdapter(Context context) {
            super(context, false);
            mContext = context;
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority,
                                  ContentProviderClient provider, SyncResult syncResult) {
            Log.d(TAG, "onPerformSync");

            AuroraSession session = mApi.getSessionManager().getSession();

            if (session == null || !session.isComplete()) {
                MyLog.e(this, "Ignore sync cause session is null or incomplete");
                return;
            }

            mDB = new DBHelper(mContext);
            mWatchingFileDAO = mDB.getWatchingFileDAO();

            try {

                //Cancel all exists notifications
                NotificationManagerCompat.from(mContext)
                        .cancel(NotificationUtil.SYNC_FILE_CONFLICT);

                Log.d(TAG, "update credentials");
                if (!updateCredentials()) {
                    Log.e(TAG, "Error while updating credentials.");
                    return;
                }

                Log.d(TAG, "collect syncs");
                collectSync(extras);

                if (mSyncTargets.size() > 0) {
                    Log.d(TAG, "start sync");
                    sync();
                } else {
                    Log.d(TAG, "All files already synced.");
                }

                //After sync start clear cache service
                mContext.startService(new Intent(mContext, ClearCacheService.class));

            } catch (IOException | SQLException e) {
                e.printStackTrace();
                try {
                    for (SyncTarget target:mSyncTargets){
                        WatchingFile watchingFile = mWatchingFileDAO.queryForId(
                                WatchingFile.Spec.getRemoteUniqueSpec(target.getRemote())
                        );
                        if (watchingFile != null){
                            //TODO set need sync
                            watchingFile.setSyncStatus(WatchingFile.SYNCED);
                            sendChangedBroadcast(watchingFile, 0, 0);
                            mWatchingFileDAO.update(watchingFile);
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } finally {
                mDB.close();
            }
        }

        @Override
        public void onSyncCanceled() {
            super.onSyncCanceled();
            Log.d(TAG, "Sync cancelled.");
        }

        /**
         * Check credentials and update it if require.
         * @return - true if credentials are complete.
         * @throws IOException
         */
        @SuppressWarnings("JavaDoc")
        private boolean updateCredentials() throws IOException {
            Holder<Boolean> credentialResult = new Holder<>(false);
            mApi.getUserRepository()
                    .getSystemAppData()
                    .flatMap(systemAppData -> mApi.getUserRepository().relogin())
                    .compose(this::scheduleImmediate)
                    .subscribe(
                            token -> credentialResult.set(true),
                            error -> MyLog.majorException(this, error)
                    );
            return credentialResult.get();
        }

        /**
         * Collect targets ({@link SyncTarget}) for sync.
         * Check changes and compare remote and local.
         * Check all {@link WatchingFile} in db or concrete if it specified.
         *
         * @param extras - may contain specified target remote file spec.
         * @throws IOException
         * @throws SQLException
         */
        @SuppressWarnings("JavaDoc")
        private void collectSync(Bundle extras) throws IOException, SQLException {

            int resolveForSingleFile = 0;
            String changedFile = null;

            if (extras != null){
                changedFile = extras.getString(KEY_CHANGED_REMOTE_FILE_SPEC, null);
            }

            List<WatchingFile> offlineFiles;
            if (changedFile == null){
                //Sync all
                offlineFiles = mWatchingFileDAO.getFilesList();
            } else {
                //Sync only changed file
                WatchingFile watchingFile = mWatchingFileDAO.queryForId(changedFile);
                if (watchingFile != null) {
                    offlineFiles = Collections.singletonList(watchingFile);
                    resolveForSingleFile = extras.getInt(KEY_RESOLVE_CONFLICT, 0);
                }else {
                    Log.e(TAG, "Requested file not in watching : file = " + changedFile);
                    return;
                }
            }

            for (WatchingFile watching:offlineFiles){

                AuroraFile file =
                        AuroraFile.parse(watching.getRemoteFilePath(), watching.getRemoteAuroraType(), false);
                int state = 0;

                ApiResponseP7<AuroraFile> response = checkFile(file);
                //TODO handle null response
                if (response == null) return;

                //[START Check remote]
                AuroraFile remote = response.getResult();
                if (remote != null){
                    state |= FLAG_REMOTE_EXIST;

                    if (watching.getLastModified() == 0 || watching.getLastModified() < remote.getLastModified()) {
                        state |= FLAG_NEED_DOWNLOAD;
                    }
                    Log.d(TAG, watching.getRemoteUniqueSpec() + "|" + remote.getFullPath());
                } else {
                    if (response.getError() == null || response.getError().getCode() != ApiResponseError.UNKNOWN){
                        //TODO handle unexpected error
                        continue;
                        //Else file not exist and it is expected error
                    }
                }
                //[END Check remote]

                boolean newOffline = watching.getType() == WatchingFile.TYPE_OFFLINE && watching.getLastModified() == 0;

                //[START Check local]
                File local = new File(watching.getLocalFilePath());
                if (!newOffline && local.exists()) {
                    state |= FLAG_LOCAL_EXIST;

                    long lastModified = local.lastModified();
                    if (watching.getLastModified() != lastModified) {
                        state |= FLAG_NEED_UPLOAD;
                    }
                }
                //[END Check local]

                //[START Check conflict resolution]
                if (resolveForSingleFile != 0) {
                    int mask = FLAG_ALL;

                    switch (resolveForSingleFile) {
                        case TYPE_LOCAL:
                            //Exclude download flag
                            mask -= FLAG_NEED_DOWNLOAD;
                            break;
                        case TYPE_REMOTE:
                            //Exclude upload flag
                            mask -= FLAG_NEED_UPLOAD;
                            break;
                    }

                    //Apply result mask
                    state &= mask;
                }
                //[END Check conflict resolution]

                boolean remoteExist = (state & FLAG_REMOTE_EXIST) == FLAG_REMOTE_EXIST;

                boolean conflict = false;

                if (remoteExist) {
                    switch (state & (FLAG_NEED_DOWNLOAD | FLAG_NEED_UPLOAD)) {
                        case 0:
                            watching.setSyncStatus(WatchingFile.SYNCED);
                            break;

                        case FLAG_NEED_DOWNLOAD:
                            //Download sync only for offline files
                            if (watching.getType() == WatchingFile.TYPE_OFFLINE) {
                                mSyncTargets.add(new SyncTarget(remote, local, SyncTarget.DOWNLOAD));
                                watching.setSyncStatus(WatchingFile.SYNC_WAITING_SYNC);
                            } else {
                                //Delete local cache file because it not actual
                                //noinspection ResultOfMethodCallIgnored
                                local.delete();
                                mWatchingFileDAO.delete(watching);
                            }
                            break;

                        case FLAG_NEED_UPLOAD:
                            mSyncTargets.add(new SyncTarget(remote, local, SyncTarget.UPLOAD));
                            watching.setSyncStatus(WatchingFile.SYNC_WAITING_SYNC);
                            break;

                        case FLAG_NEED_UPLOAD | FLAG_NEED_DOWNLOAD:
                            Log.e(TAG, "Can't synchronize files. File versions conflict for: "
                                    + watching.getRemoteUniqueSpec());

                            watching.setSyncConflict(true);
                            mWatchingFileDAO.update(watching);

                            showConflictNotify(watching);
                            conflict = true;
                            break;
                    }
                } else {
                    conflict = true;
                    if ((state & FLAG_LOCAL_EXIST) == FLAG_LOCAL_EXIST) {
                        showRemoteDeletedNotify(watching, mContext);
                    } else {
                        //Remove db row if remote and local not exist
                        mWatchingFileDAO.delete(watching);
                    }
                }

                if (!conflict){
                    mWatchingFileDAO.update(watching);
                    sendChangedBroadcast(watching, 0, 0);
                }
            }
        }

        /**
         * Check remote file request.
         * @param remote - requested {@link AuroraFile}
         */
        private ApiResponseP7<AuroraFile> checkFile(AuroraFile remote) throws IOException {

            Single<AuroraFile> observable = mApi.getFilesRepository()
                    .checkFile(remote);

            Call<ApiResponseP7<AuroraFile>> call = ApiCompatibilityUtil.apiResponseCall(
                    observable,
                    mApi.getSessionManager()
            );

            Response<ApiResponseP7<AuroraFile>> response = call.execute();
            if (response.isSuccessful()) {
                //[START Check remote]
                return response.body();
            } else {
                return null;
            }
        }

        /**
         * Sync all collected {@link SyncTarget}'s.
         */
        private void sync() throws IOException, SQLException {

            Iterator<SyncTarget> syncIterator = mSyncTargets.iterator();
            while(syncIterator.hasNext()){
                SyncTarget target = syncIterator.next();
                WatchingFile watchingFile = mWatchingFileDAO
                        .queryForId(WatchingFile.Spec.getRemoteUniqueSpec(target.getRemote()));
                if (watchingFile == null) return;

                watchingFile.setSyncStatus(WatchingFile.SYNC_IN_PROGRESS);
                mWatchingFileDAO.update(watchingFile);
                boolean success = false;

                sendChangedBroadcast(watchingFile, 0, 0);

                if (target.getType() == SyncTarget.UPLOAD){
                    if (uploadSync(watchingFile, target)) {
                        ApiResponseP7<AuroraFile> response = checkFile(target.getRemote());
                        if (response != null) {
                            if (response.getResult() != null) {
                                watchingFile.setLastModified(response.getResult().getLastModified());
                                success = true;
                            }
                        }
                    }
                }else{
                    if (downloadSync(watchingFile, target)){
                        watchingFile.setLastModified(target.getRemote().getLastModified());
                        success = true;
                    }
                }

                File local = target.getLocal();
                if (!local.setLastModified(watchingFile.getLastModified())){
                    MyLog.majorException(this, "Last modified not setted.");
                    success = false;
                }

                if (success){
                    watchingFile.setSyncStatus(WatchingFile.SYNCED);
                    watchingFile.setSyncConflict(false);
                }else{
                    watchingFile.setSyncStatus(WatchingFile.SYNC_WAITING_SYNC);
                }
                mWatchingFileDAO.update(watchingFile);

                sendChangedBroadcast(watchingFile, 0, 0);
                syncIterator.remove();
            }
        }

        /**
         * Upload changes to remote file.
         * @return true if succes sync.
         */
        private boolean uploadSync(final WatchingFile watchingFile, SyncTarget target) throws IOException, SQLException {
            Holder<Boolean> result = new Holder<>(false);
            FilesRepository repository = mApi.getFilesRepository();
            AuroraFile file = target.getRemote();

            //TODO !!! Upload with override
            if (mApi.getSessionManager().getSession().getApiVersion() == Const.ApiVersion.API_P8){
                boolean deleted = repository.delete(Collections.singletonList(file))
                        .blockingGet();
                if (!deleted){
                    MyLog.e(this, "File not deleted: " + file.getName());
                    return false;
                }
            }

            FileInfo fileInfo = FileUtil.fileInfoFromFile(target.getLocal());

            ApiTask.ProgressUpdater progressUpdater = ApiTask.ProgressUpdater.create(
                    (progress, max) -> sendChangedBroadcast(watchingFile, (int)progress, (int)max)
            );

            repository.uploadFile(file.getParentFolder(), fileInfo, progressUpdater)
                    .subscribe(
                            response -> result.set(true),
                            error -> MyLog.e(this, error)
                    );
            return result.get();
        }

        /**
         * Download changes to local file.
         * @return true if succes sync.
         */
        private boolean downloadSync(WatchingFile watchingFile, SyncTarget target) throws IOException {
            Single<ResponseBody> observable = mApi.getFilesRepository()
                    .downloadFileBody(target.getRemote());
            Call<ResponseBody> call = ApiCompatibilityUtil.transparentCall(observable);

            Response<ResponseBody> response = call.execute();
            if (response.isSuccessful()){

                File dir = target.getLocal().getParentFile();
                if (dir.exists() || dir.mkdirs()) {
                    ResponseBody body = response.body();

                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        is = body.byteStream();
                        fos = new FileOutputStream(target.getLocal());

                        final long size = target.getRemote().getSize();
                        final byte[] buffer = new byte[2048];

                        int read;
                        long totalRead = 0;
                        while ((read = is.read(buffer)) != -1 && totalRead < size){
                            read = (int) Math.min(size - totalRead, read);
                            fos.write(buffer, 0, read);
                            totalRead += read;
                            sendChangedBroadcast(
                                    watchingFile,
                                    (int)totalRead,
                                    (int) target.getRemote().getSize()
                            );
                        }
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        IOUtil.closeQuietly(is);
                        IOUtil.closeQuietly(fos);
                    }
                }
            }
            return false;
        }

        /**
         * Show file sync conflict notification with actions (select local or remote)
         * @param watching - current {@link WatchingFile}
         */
        private void showConflictNotify(WatchingFile watching){
            //Create notification
            NotificationCompat.Builder builder =
                    NotificationUtil.getDefaultNotifyBuilder(mContext)
                            .setContentTitle(mContext.getString(R.string.notify_sync_conflict))
                            .setContentText(mContext.getString(
                                    R.string.notify_sync_conflict_description,
                                    watching.getRemoteUniqueSpec())
                            )
                            .setTicker(mContext.getString(R.string.notify_sync_conflict))
                            .addAction(R.drawable.ic_device,
                                    mContext.getString(R.string.notify_sync_conflict_action_local),
                                    getResolveConflictIntent(TYPE_LOCAL, watching)
                            )
                            .addAction(R.drawable.ic_cloud,
                                    mContext.getString(R.string.notify_sync_conflict_action_remote),
                                    getResolveConflictIntent(TYPE_REMOTE, watching)
                            );

            //Notify
            NotificationManagerCompat.from(mContext)
                    .notify(
                            watching.getRemoteUniqueSpec(),
                            NotificationUtil.SYNC_FILE_CONFLICT,
                            builder.build()
                    );
        }

        /**
         * Generate resolve conflict {@link PendingIntent} for notification.
         * @param chosenType - intent for selected type.
         * @param watching - current {@link WatchingFile}
         * @return - prepared {@link PendingIntent} with unique id and only for current package.
         */
        private PendingIntent getResolveConflictIntent(int chosenType, WatchingFile watching){
            Intent localIntent = new Intent(SyncResolveReceiver.ACTION_RESOLVE_SYNC_CONFLICT);
            localIntent.putExtra(KEY_RESOLVE_CONFLICT, chosenType);
            localIntent.putExtra(KEY_CHANGED_REMOTE_FILE_SPEC, watching.getRemoteUniqueSpec());
            localIntent.setPackage(BuildConfig.APPLICATION_ID);
            return PendingIntent
                    .getBroadcast(mContext, NotificationUtil.getNextUniquePendingRequsetId(),
                            localIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        /**
         * Show notification when remote sync target not exist.
         * Static because used in
         *
         * @param watching - current watching file.
         * @param ctx - application context.
         */
        public static void showRemoteDeletedNotify(WatchingFile watching, Context ctx){
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
         * @param save - if true local will be saved in {@link android.os.Environment#DIRECTORY_DOWNLOADS}
         *             else just deleted.
         * @param watching - current {@link WatchingFile}.
         * @param ctx - application context.
         * @return - prepared {@link PendingIntent} with unique id and only for current package.
         */
        private static PendingIntent getRemoteDeltedResolveIntent(boolean save, WatchingFile watching,
                                                                  Context ctx){
            Intent saveIntent = new Intent(
                    SyncResolveReceiver.ACTION_RESOLVE_REMOTE_NOT_EXIST);
            saveIntent.putExtra(SyncResolveReceiver.KEY_SAVE_LOCAL, save);
            saveIntent.putExtra(
                    SyncService.FileSyncAdapter.KEY_CHANGED_REMOTE_FILE_SPEC,
                    watching.getRemoteUniqueSpec()
            );
            saveIntent.setPackage(BuildConfig.APPLICATION_ID);
            return PendingIntent
                    .getBroadcast(ctx, NotificationUtil.getNextUniquePendingRequsetId(),
                            saveIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        /**
         * Send change sync status for file broadcast message.
         */
        private void sendChangedBroadcast(WatchingFile watching, int progress, int maxProgress){
            boolean sendBroadCast = false;

            if (watching.getSyncStatus() == WatchingFile.SYNC_IN_PROGRESS){
                long currentTime = SystemClock.elapsedRealtime();
                if (mLastBroadcastTime == 0 ||
                        mLastBroadcastTime + 100 < currentTime){ //Send progress broadcast less than 10 times in second
                    sendBroadCast = true;
                    mLastBroadcastTime = currentTime;
                }
            }else{
                mLastBroadcastTime = 0;
                sendBroadCast = true;
            }

            if (sendBroadCast) {
                sendSyncStateChangedBroadcast(watching, progress, maxProgress, mContext);
            }
        }

        /**
         * Send sync state changed event for file.
         *
         * @param watching - {@link WatchingFile} which sync state was changed.
         * @param progress - 0 or current sync progress.
         * @param maxProgress - 0 or current sync max progress.
         * @param ctx - application context.
         */
        public static void sendSyncStateChangedBroadcast(WatchingFile watching, int progress, int maxProgress, Context ctx){
            Intent intent = new Intent(ACTION_SYNC_STATUS_CHANGED);
            intent.putExtra(KEY_SYNC_TARGET, watching);
            intent.putExtra(KEY_MAX_PROGRESS, maxProgress);
            intent.putExtra(KEY_PROGRESS, progress);

            intent.setPackage(BuildConfig.APPLICATION_ID);

            Log.d(TAG, "Send changed broadcast: " + intent);

            ctx.sendBroadcast(intent);
        }

        /**
         * Request sync for all or concrete file.
         *
         * @param remoteSpec - target file remote spec "type:path".
         *                   Can get with {@link WatchingFile.Spec#getRemoteUniqueSpec(AuroraFile)}
         *                   or {@link WatchingFile#getRemoteUniqueSpec()}.
         *                   Can be null, and so all files will be sync.
         * @param account - account what need to be synced
         */
        public static void requestSync(@Nullable String remoteSpec, Account account){
            Bundle extras = new Bundle();
            if (remoteSpec != null) {
                extras.putString(FileSyncAdapter.KEY_CHANGED_REMOTE_FILE_SPEC,
                        remoteSpec);
            }
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

            ContentResolver.requestSync(
                    account,
                    AccountUtil.FILE_SYNC_AUTHORITY,
                    extras
            );
        }

        private <T> Single<T> scheduleImmediate(Single<T> observable){
            return observable
                    .subscribeOn(Schedulers.from(Runnable::run))
                    .observeOn(Schedulers.from(Runnable::run));
        }

        @SuppressWarnings("WeakerAccess")
        private class SyncTarget{
            public static final int UPLOAD = 1;
            public static final int DOWNLOAD = 2;

            private AuroraFile mRemote;
            private File mLocal;
            private int mType;

            public SyncTarget(AuroraFile remote, File local, int type) {
                mRemote = remote;
                mLocal = local;
                mType = type;
            }

            public AuroraFile getRemote() {
                return mRemote;
            }

            public File getLocal() {
                return mLocal;
            }

            public int getType() {
                return mType;
            }
        }
    }
}
