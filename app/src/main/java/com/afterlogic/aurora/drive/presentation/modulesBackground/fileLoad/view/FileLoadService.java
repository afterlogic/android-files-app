package com.afterlogic.aurora.drive.presentation.modulesBackground.fileLoad.view;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.core.util.AccountUtil;
import com.afterlogic.aurora.drive._unrefactored.core.util.DownloadType;
import com.afterlogic.aurora.drive._unrefactored.core.util.WatchingFileManager;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Api;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiTask;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.Task;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive._unrefactored.model.UploadResult;
import com.afterlogic.aurora.drive._unrefactored.presentation.services.SyncService;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.core.common.util.IOUtil;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;

import static android.R.attr.type;

/**
 * Created by sashka on 30.05.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileLoadService extends Service {

    public static final int REGISTER_TASK_HANDLER = 0;
    public static final int CANCEL_TASK = 2;

    public static final int REGISTER_RESULT = 3;
    public static final int TASK_PROGRESS = 4;
    public static final int TASK_RESULT = 5;
    public static final int TASK_ENDED = 6;

    public static final int SUCCESS = 1;
    public static final int NOT_EXIST = 0;

    public static final String CLASS_NAME = FileLoadService.class.getName();

    private static final String ACTION_UPLOAD = "com.afterlogic.aurora.ACTION_UPLOAD";
    private static final String ACTION_DOWNLOAD = "com.afterlogic.aurora.ACTION_DOWNLOAD";

    private static final String TARGET_FOLDER = CLASS_NAME + ".TARGET_FOLDER";
    private static final String TARGET_FILE = CLASS_NAME + ".TARGET_FILE";
    private static final String FILES = CLASS_NAME + ".FILES";
    private static final String TASK_ID = CLASS_NAME + ".TASK_ID";
    private static final String OVERRIDE = CLASS_NAME + ".OVERRIDE";
    private static final String DOWNLOAD_TYPE = CLASS_NAME + ".DOWNLOAD_TYPE";

    public static final String FILE_NAME = CLASS_NAME + ".FILE_NAME";
    public static final String FILE = CLASS_NAME + ".FILE";
    public static final String PROGRESS = CLASS_NAME + ".PROGRESS";
    public static final String MAX_PROGRESS = CLASS_NAME + ".MAX_PROGRESS";


    @SuppressLint("UseSparseArrays")
    private volatile HashMap<Integer, FileTask> mAsyncTaskHashMap = new HashMap<>();

    @SuppressLint("UseSparseArrays")
    private volatile HashMap<Integer, Messenger> mClients = new HashMap<>();

    @SuppressLint("UseSparseArrays")
    private volatile HashMap<Integer, TaskEndListener> mEndListeners = new HashMap<>();

    private Messenger mMessenger;

    private PowerManager.WakeLock mWakeLock;

    public static Intent makeDownload(AuroraFile files, File target, Context context){
        Intent intent = new Intent(context, FileLoadService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(FILE, files);
        intent.putExtra(TARGET_FILE, target);
        intent.putExtra(DOWNLOAD_TYPE, type);
        return intent;
    }

    public static Intent makeUpload(int taskId, AuroraFile targetFolder,
                                    List<FileInfo> files, boolean override, Intent intent){
        intent.setAction(ACTION_UPLOAD);
        FileInfo[] result = new FileInfo[files.size()];
        intent.putExtra(FILES, files.toArray(result));
        intent.putExtra(OVERRIDE, override);
        intent.putExtra(TASK_ID, taskId);
        intent.putExtra(TARGET_FOLDER, targetFolder);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IncomingHandler handler = new IncomingHandler(mAsyncTaskHashMap, mClients);
        mMessenger = new Messenger(handler);

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "file_task");

        startForeground(0, null);
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()){
            case ACTION_UPLOAD:
                startNewUploading(startId, intent);
                break;
            case ACTION_DOWNLOAD:
                startNewDownloading(startId, intent);
                break;
            default:
                stopSelf(startId);
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    /**
     * Start new download task.
     * @param startId - service start id.
     * @param intent - start intent.
     */
    private void startNewDownloading(final int startId, final Intent intent){
        int taskId = intent.getIntExtra(TASK_ID, -1);
        FileTask task = new FileTask(intent, startId, taskId, mWakeLock) {
            @Override
            public int proceed(Intent intent, int taskId) {
                DownloadType type = (DownloadType) intent.getSerializableExtra(DOWNLOAD_TYPE);

                Parcelable[] parcelables = intent.getParcelableArrayExtra(FILES);
                AuroraFile[] files = new AuroraFile[parcelables.length];
                for (int i = 0; i < parcelables.length; i++){
                    files[i] = (AuroraFile) parcelables[i];
                }

                try {
                    for (AuroraFile file:files) {
                        notifyProgress(taskId, 0, -1, file.getName());
                        File localResult = handleCachedOrOffline(taskId, file, type);
                        if (localResult == null) {
                            localResult = dowloadFromWeb(taskId, file, type);
                        }
                        if (localResult != null){
                            Messenger client = mClients.get(taskId);
                            if (client != null){
                                try {
                                    Bundle data = new Bundle();
                                    data.putSerializable(FILE, localResult);
                                    Message msg = Message.obtain(null, TASK_RESULT);
                                    msg.setData(data);
                                    client.send(msg);
                                } catch (RemoteException e) {
                                    //e.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return -1;
                }
                return SUCCESS;
            }
        };
        if (taskId >= 0) {
            mAsyncTaskHashMap.put(taskId, task);
        }
        task.execute();
    }

    /**
     * Download file by {@link DownloadType}
     * @param file - remote file.
     * @param type - download type.
     */
    private File dowloadFromWeb(int taskId, final AuroraFile file, DownloadType type) throws IOException {
        File target = FileUtil.getTargetFileByType(file, type, this);
        File dir;
        if (target.isDirectory()){
            dir = target;
            target = new File(dir, file.getName());
        } else {
            dir = target.getParentFile();
        }

        //Check dirs to file and create it if not exist
        if (dir.exists() || dir.mkdirs()) {
            //Start task
            ResponseBody fileBody = Api.callSyncResponse(Api.downloadFile(file));
            if (fileBody != null) {
                if (saveFile(taskId, fileBody.byteStream(), file.getSize(), target)) {

                    if (!target.setLastModified(file.getLastModified())){
                        MyLog.majorException(this, "Last modified not setted: " + file.getPath());
                    }

                    switch (type){
                        case DOWNLOAD_OPEN:
                            WatchingFileManager.from(this)
                                    .addWatching(file, target, WatchingFile.TYPE_CACHE, true);
                            break;
                        case DOWNLOAD_TO_DOWNLOADS:
                            addFileToDownloads(target);
                            break;
                    }
                    return target;
                }
            } else {
                //TODO Error
                MyLog.majorException(this, "Unhandled error");
            }
        } else {
            //TODO Error
            MyLog.majorException(this, "Unhandled error");
        }
        return null;
    }

    /**
     * Read {@link ResponseBody} to local file.
     * @param taskId - task id.
     * @param is - resource input stream.
     * @param target - local file target.
     * @return - true if success.
     */
    private boolean saveFile(int taskId, InputStream is, long size, File target){
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(target);
            notifyProgress(taskId, 0, size, target.getName());

            byte[] buffer = new byte[2048];
            int count;
            long totalRead = 0;
            while ((count = is.read(buffer)) != -1 && totalRead < size){
                count = (int) Math.min(size - totalRead, count);
                fos.write(buffer, 0, count);
                totalRead += count;
                notifyProgress(taskId, totalRead, size, target.getName());
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            IOUtil.closeQuietly(is);
            IOUtil.closeQuietly(fos);
        }
    }

    /**
     * Check is file cached or offline mode. If cached compare with remote file.
     * @param file - requested {@link AuroraFile}
     * @return - true if file cached and it's opening already handled.
     */
    private File handleCachedOrOffline(int taskId, final AuroraFile file, DownloadType downloadType) throws IOException {
        if (file.isOfflineMode()) {
            return null;
        }

        DBHelper db = new DBHelper(this);
        final WatchingFile watching = db.getWatchingFileDAO().getWatching(file);
        db.close();

        if (watching != null){
            //if cached or offline
            if (watching.getSyncStatus() == WatchingFile.SYNCED){
                //If sync state is synced check file
                AuroraFile remote = Api.callSync(Api.checkFile(file));
                if (remote != null) {
                    File local = new File(watching.getLocalFilePath());
                    if (local.lastModified() >= remote.getLastModified()) {
                        handleLocal(taskId, file, local, downloadType);

                        if (local.lastModified() > remote.getLastModified()){
                            //Request sync after task ending
                            mEndListeners.put(taskId, () -> {
                                Account account = AccountUtil.getCurrentAccount(
                                        FileLoadService.this);
                                //Request sync cause local file is newer than remote
                                SyncService.FileSyncAdapter
                                        .requestSync(watching.getRemoteUniqueSpec(), account);
                            });
                        }
                        return local;
                    }
                } else {
                    //TODO error
                    MyLog.majorException(this, "Unhandled error");
                }
            }
        }
        return null;
    }

    /**
     * Get download result from local file.
     * If download type is {@link DownloadType#DOWNLOAD_TO_DOWNLOADS}
     * copy local file to downloads folder.
     * (Cached and offline files)
     *
     * @param file - remote {@link AuroraFile} file.
     * @param local - local {@link File} equivalent.
     * @param downloadType - download type (from {@link DownloadType})
     */
    private void handleLocal(int taskId, final AuroraFile file, File local, DownloadType downloadType) throws FileNotFoundException {

        if (downloadType == DownloadType.DOWNLOAD_TO_DOWNLOADS) {
            //Copy to downloads
            File target = FileUtil.getDownloadsFile(file);
            if (saveFile(taskId, new FileInputStream(local), file.getSize(), target)){
                addFileToDownloads(target);
            }
        }
    }

    private void addFileToDownloads(File target){
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.addCompletedDownload(
                target.getName(),
                getString(R.string.prompt_downloaded_file_description),
                true,
                FileUtil.getFileMimeType(target),
                target.getAbsolutePath(),
                target.length(),
                false
        );
    }

    /**
     * Start upload files task.
     * @param startId - service start id.
     * @param intent - start intent.
     */
    private void startNewUploading(int startId, Intent intent){
        int taskId = intent.getIntExtra(TASK_ID, -1);

        final boolean override = intent.getBooleanExtra(OVERRIDE, false);
        Parcelable[] parcelables = intent.getParcelableArrayExtra(FILES);

        final FileInfo[] infos = new FileInfo[parcelables.length];
        for(int i = 0; i < parcelables.length; i++){
            infos[i] = (FileInfo) parcelables[i];
        }

        FileTask task = new FileTask(intent, startId, taskId, mWakeLock) {
            @Override
            public int proceed(Intent intent, int taskId) {

                AuroraFile targetFolder = intent.getParcelableExtra(TARGET_FOLDER);
                try {
                    for (FileInfo file:infos){
                        notifyProgress(taskId, 0, -1, file.getName());
                        uploadFile(targetFolder, file, override, taskId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return  -1;
                }
                return SUCCESS;
            }

            @Override
            protected void onPostExecute(Integer status) {
                super.onPostExecute(status);
                if (status == SUCCESS){

                    String tag;
                    if (infos.length == 1){
                        tag = infos[0].getName();
                    } else {
                        tag = getResources().getQuantityString(R.plurals.prompt_upload_files_count, infos.length, infos.length);
                    }

                    Toast.makeText(
                            FileLoadService.this,
                            getString(R.string.prompt_uploaded_successfully, tag),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        };

        if (taskId != -1){
            mAsyncTaskHashMap.put(taskId, task);
        }

        task.execute();
    }

    /**
     * Upload one file.
     * @param targetFolder - target remote folder.
     * @param file - target local file info.
     * @param override - override trigger. If not true generate unique name for remote file else override remote file.
     * @param taskId - {@link Task} id for progress updating (may not be setted).
     * @throws IOException
     */
    private void uploadFile(AuroraFile targetFolder, final FileInfo file, boolean override,
                            final int taskId) throws IOException {

        ApiTask.ProgressUpdater progressUpdater = ApiTask.ProgressUpdater.create(
                (progress, max) -> notifyProgress(taskId, progress, max, file.getName())
        );

        FileInfo targetFile = file;
        AuroraFile remoteTarget = AuroraFile.parse(targetFolder.getFullPath() +
                "/" + targetFile.getName(), targetFolder.getType(), false);

        int suffix = 0;
        while (!override && Api.callSync(Api.checkFile(remoteTarget)) != null){
            suffix++;
            String name = FileUtil.appendSufixToFileName(file.getName(), "(" + suffix + ")");
            targetFile = new FileInfo(name, file.getSize(), file.getMime(), file.getUri());
            remoteTarget = AuroraFile.parse(targetFolder.getFullPath() +
                    "/" + targetFile.getName(), targetFolder.getType(), false);
        }

        UploadResult result = Api.callSync(Api.uploadFile(targetFolder, targetFile, progressUpdater));
        if (result == null){
            //TODO notify error
            MyLog.majorException(this, "Unhandled error");
        }
    }

    /**
     * Trigger task ending.
     * @param taskId - {@link Task} id in {@link #mAsyncTaskHashMap}.
     * @param status - result status.
     */
    private void endTask(int taskId, int status){
        Messenger client = mClients.get(taskId);
        try {
            if (client != null) {
                client.send(Message.obtain(null, TASK_ENDED, status, 0));
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mAsyncTaskHashMap.remove(taskId);
        mClients.remove(taskId);
        TaskEndListener endListener = mEndListeners.remove(taskId);
        if (endListener != null){
            endListener.onEnd();
        }
    }

    /**
     * Notify task progress.
     * @param taskId - task id.
     * @param progress - task progress.
     * @param max - task max progress.
     * @param name - task name.
     */
    private void notifyProgress(int taskId, long progress, long max, String name){
        Messenger client = mClients.get(taskId);
        if (client != null){
            try {
                Bundle data = new Bundle();
                data.putString(FILE_NAME, name);
                data.putLong(PROGRESS, progress);
                data.putLong(MAX_PROGRESS, max);
                Message msg = Message.obtain(null, TASK_PROGRESS);
                msg.setData(data);
                client.send(msg);
            } catch (RemoteException e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * Message handler for IBinder.
     */
    private static class IncomingHandler extends Handler{

        private HashMap<Integer, FileTask> mAsyncTaskHashMap;
        private HashMap<Integer, Messenger> mClients;

        private IncomingHandler(HashMap<Integer, FileTask> asyncTaskHashMap, HashMap<Integer, Messenger> clients) {
            mAsyncTaskHashMap = asyncTaskHashMap;
            mClients = clients;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REGISTER_TASK_HANDLER:
                    try {
                        int status = mAsyncTaskHashMap.containsKey(msg.arg1) ? SUCCESS : NOT_EXIST;
                        if (status == SUCCESS){
                            mClients.put(msg.arg1, msg.replyTo);
                        }
                        Message reply = Message.obtain(null, REGISTER_RESULT, status, 0);
                        msg.replyTo.send(reply);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case CANCEL_TASK:
                    AsyncTask task = mAsyncTaskHashMap.get(msg.arg1);
                    if (task != null){
                        mAsyncTaskHashMap.remove(msg.arg1);
                        mClients.remove(msg.arg1);
                        task.cancel(true);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    private abstract class FileTask extends AsyncTask<Void, Void, Integer>{
        private Intent mIntent;
        private int mStartId;
        private int mTaskId;
        private PowerManager.WakeLock mWakeLock;

        private boolean mWakeLocked = false;

        private FileTask(Intent intent, int startId, int taskId, PowerManager.WakeLock wakeLock) {
            mIntent = intent;
            mStartId = startId;
            mTaskId = taskId;
            mWakeLock = wakeLock;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            wakeLock();
            int status = proceed(mIntent, mTaskId);

            endTask(mTaskId, status);
            stopSelf(mStartId);
            releaseWakeLock();
            return status;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            endTask(mTaskId, -1);
            stopSelf(mStartId);
            releaseWakeLock();
        }

        private void wakeLock(){
            mWakeLocked = true;
            mWakeLock.acquire();
        }

        private void releaseWakeLock(){
            if (mWakeLocked){
                mWakeLocked = false;
                mWakeLock.release();
            }
        }

        public abstract int proceed(Intent intent, int taskId);

        public FileTask execute(){
            super.execute();
            return this;
        }
    }

    private interface TaskEndListener{
        void onEnd();
    }
}
