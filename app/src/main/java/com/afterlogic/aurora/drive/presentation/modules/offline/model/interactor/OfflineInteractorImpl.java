package com.afterlogic.aurora.drive.presentation.modules.offline.model.interactor;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.application.App;
import com.afterlogic.aurora.drive.core.common.util.FileUtil;
import com.afterlogic.aurora.drive.core.common.util.IOUtil;
import com.afterlogic.aurora.drive.data.common.network.SessionManager;
import com.afterlogic.aurora.drive.data.modules.files.repository.FilesRepository;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;
import com.annimon.stream.Stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Observable;
import io.reactivex.Single;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.afterlogic.aurora.drive.data.modules.files.FilesDataModule.DOWNLOADS_DIR;
import static com.afterlogic.aurora.drive.data.modules.files.FilesDataModule.OFFLINE_DIR;

/**
 * Created by sashka on 19.02.17.<p/>
 * mail: sunnyday.development@gmail.com
 */

public class OfflineInteractorImpl implements OfflineInteractor {

    private final FilesRepository mFilesRepository;
    private final File mDownloadsDir;
    private final File mOfflineDir;
    private final App mApp;
    private final SessionManager mSessionManager;
    private final Context mContext;

    @Inject
    OfflineInteractorImpl(FilesRepository filesRepository,
                          @Named(DOWNLOADS_DIR) File downloadsDir,
                          @Named(OFFLINE_DIR) File offlineDir,
                          App app,
                          SessionManager sessionManager,
                          Context context) {
        mFilesRepository = filesRepository;
        mDownloadsDir = downloadsDir;
        mOfflineDir = offlineDir;
        mApp = app;
        mSessionManager = sessionManager;
        mContext = context;
    }

    @Override
    public Single<List<AuroraFile>> getOfflineFiles() {
        return mFilesRepository.getOfflineFiles();
    }

    @Override
    public Observable<Progressible<File>> downloadForOpen(AuroraFile file) {
        return Observable.defer(() -> {
            File target = FileUtil.getFile(mOfflineDir, file);
            if (!target.exists()){
                return Observable.error(new FileNotFoundException());
            }
            return Observable.just(new Progressible<>(target, target.length(), target.length(), file.getName(), true));
        });
    }

    @Override
    public Observable<Progressible<File>> downloadToDownloads(AuroraFile file) {
        return downloadForOpen(file)
                .flatMap(progressible -> {
                    if (progressible.isDone()){
                        return Observable.create(emitter -> {
                            File source = progressible.getData();
                            File target = FileUtil.getFile(mDownloadsDir, file);

                            FileInputStream fis = new FileInputStream(source);
                            FileUtil.writeFile(fis, target, progress ->
                                    emitter.onNext(new Progressible<>(target, source.length(), progress, file.getName(), false))
                            );
                            IOUtil.closeQuietly(fis);

                            emitter.onNext(new Progressible<>(target, source.length(), source.length(), file.getName(), true));

                            DownloadManager dm = (DownloadManager) mApp.getSystemService(DOWNLOAD_SERVICE);
                            dm.addCompletedDownload(
                                    target.getName(),
                                    mApp.getString(R.string.prompt_downloaded_file_description),
                                    true,
                                    FileUtil.getFileMimeType(target),
                                    target.getAbsolutePath(),
                                    target.length(),
                                    false
                            );

                            emitter.onComplete();
                        });
                    } else {
                        return Observable.just(progressible);
                    }
                });
    }

    @Override
    public Single<Uri> getThumbnail(AuroraFile file){
        return Single.fromCallable(() -> {
            File localFile = new File(mOfflineDir, file.getPathSpec());
            return Uri.fromFile(localFile);
        });
    }

    @Override
    public Single<Boolean> getAuthStatus() {
        return Single.fromCallable(() -> mSessionManager.getSession() != null && mSessionManager.getSession().isComplete());
    }

    @Override
    public Observable<Boolean> listenNetworkState() {

        Set<Runnable> finalizers = new HashSet<>();
        return Observable.<Boolean>create( emitter -> {
            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, @Nullable Intent intent) {
                    ConnectivityManager cm =
                            (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                    emitter.onNext(isConnected);
                }
            };
            mContext.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

            receiver.onReceive(mContext, null);

            finalizers.add(() -> mContext.unregisterReceiver(receiver));

        })//----|
                .doFinally(() -> Stream.of(finalizers).forEach(Runnable::run));
    }
}
