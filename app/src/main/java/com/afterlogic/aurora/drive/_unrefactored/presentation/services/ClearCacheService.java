package com.afterlogic.aurora.drive._unrefactored.presentation.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive._unrefactored.data.common.db.DBHelper;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.dao.WatchingFileDAO;
import com.afterlogic.aurora.drive._unrefactored.data.common.db.model.WatchingFile;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.annimon.stream.Stream;
import com.j256.ormlite.stmt.DeleteBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;

import static com.afterlogic.aurora.drive.core.common.rx.Observables.Collectors.concatCompletable;

/**
 * Created by sashka on 04.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class ClearCacheService extends Service {

    public static final long DAY = 24 * 60 * 60 * 1000;
    public static final long EXPIRE = 3 * DAY;
    private DBHelper mDb;

    private File mCacheDir;
    private File mOfflineDir;
    private WatchingFileDAO mDao;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyLog.d(this, "onCreate");

        mDb = new DBHelper(this);
        mDao = mDb.getWatchingFileDAO();

        mCacheDir = getExternalCacheDir();
        mOfflineDir = getExternalFilesDir("offline");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.d(this, "onDestroy");
        mDb.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.d(this, "onStartCommand: " + startId);

        createTask()
                .doFinally(() -> stopSelf(startId))
                .subscribe();
        return START_STICKY_COMPATIBILITY;
    }

    /**
     * Full clear cache task.
     */
    private Completable createTask(){
        Completable completable = Completable.defer(() -> {
            List<Completable> tasks = new ArrayList<>();

            tasks.add(clearUnusedFiles(mCacheDir, true));
            tasks.add(clearUnusedFiles(mOfflineDir, false));
            tasks.add(clearCacheDB());

            return Completable.concat(tasks);
        });

        return completable.doOnError(error -> MyLog.majorException(this, error))
                .onErrorComplete();
                //.subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Check 'watchers' and local files. If local file expired or not present in 'watchers' delete it.
     * @param dir - for check.
     * @param checkExpire - need check expiration.
     */
    private Completable clearUnusedFiles(File dir, boolean checkExpire){
        return Completable.defer(() -> {
            File[] files = dir.listFiles();
            if (files == null) return Completable.complete();

            Completable checkEachFile = Stream.of(files).map(file -> {
                if (file.isDirectory()){
                    return clearUnusedFiles(file, checkExpire)
                            .doOnError(error -> MyLog.majorException(this, error))
                            .onErrorComplete();
                }else{
                    return checkFile(file, checkExpire)
                            .doOnComplete(() -> MyLog.d(this, "File checked: " + file.getPath()))
                            .doOnError(error -> MyLog.majorException(this, error))
                            .onErrorComplete();
                }
            }).collect(concatCompletable());

            return checkEachFile.andThen(Completable.fromAction(() -> {
                if (dir.listFiles().length == 0 && dir != mCacheDir && dir != mOfflineDir){
                    deleteFile(dir);
                }
            })).doOnComplete(() -> MyLog.d(this, "Dir checked: " + dir.getPath()));
        });
    }

    /**
     * Check 'watcher' and local file. If local file expired or 'watcher' not present for it delete it.
     * @param file - file for check.
     * @param checkExpire - need check expiration.
     */
    private Completable checkFile(File file, boolean checkExpire){
        return Completable.fromAction(() -> {
            WatchingFile watchingFile = mDao.queryBuilder()
                    .where()
                    .eq(WatchingFile.COLUMN_LOCAL_FILE, file.getAbsolutePath())
                    .queryForFirst();

            if (watchingFile != null) {
                //check expiration
                if (checkExpire && watchingFile.getLocalCreateDate() + EXPIRE < System.currentTimeMillis()) {
                    //Delete from db
                    DeleteBuilder<WatchingFile, String> deleteBuilder = mDao.deleteBuilder();
                    deleteBuilder.where()
                            .eq(WatchingFile.COLUMN_LOCAL_FILE, file.getAbsolutePath());
                    deleteBuilder.delete();

                    MyLog.d(this, "File expired: " + file.getPath());
                    deleteFile(file);
                }
            } else {
                MyLog.d(this, "File not present in db: " + file.getPath());
                deleteFile(file);
            }
        });
    }

    /**
     * Delete local file. If deletion failed log it.
     */
    private void deleteFile(File file){
        MyLog.d(this, "Delete file: " + file.getPath());
        if (!file.delete()){
            MyLog.majorException(this, new IOException("Can't delete file:" + file.getPath()));
        }
    }

    /**
     * Remove rows with not exists files from db.
     */
    private Completable clearCacheDB(){
        return Stream.of(mDao.getFilesList(WatchingFile.TYPE_CACHE))
                .map(this::checkDbRow)
                .collect(concatCompletable())
                .doOnSubscribe(disposable -> MyLog.d(this, "Start remove not exists files from db."))
                .doFinally(() -> MyLog.d(this, "Finish remove not exists files from db."));
    }

    /**
     * Check db row on local file existing.
     */
    private Completable checkDbRow(WatchingFile file){
        Completable completable = Completable.fromAction(() -> {
            File local = new File(file.getLocalFilePath());
            if (!local.exists()){
                MyLog.d(this, "Watcher's file not exist: " + local.getPath() + ". Delete row.");
                mDao.delete(file);
            }
        });

        return completable.doOnError(error -> MyLog.majorException(this, error))
                .onErrorComplete();
    }
}
