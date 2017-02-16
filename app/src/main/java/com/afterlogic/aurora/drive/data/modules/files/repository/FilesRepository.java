package com.afterlogic.aurora.drive.data.modules.files.repository;

import android.net.Uri;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.Progressible;

import java.io.File;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by sashka on 19.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public interface FilesRepository {

    Single<List<String>> getAvailableFileTypes();

    Single<List<AuroraFile>> getFiles(AuroraFile folder);

    Single<Uri> getFileThumbnail(AuroraFile file);

    Single<Uri> viewFile(AuroraFile file);

    Completable createFolder(AuroraFile file);

    Single<AuroraFile> rename(AuroraFile file, String newName);


    Completable checkFileExisting(AuroraFile file);
    Single<AuroraFile> checkFile(AuroraFile file);

    Completable delete(AuroraFile files);
    Completable delete(List<AuroraFile> files);

    Observable<Progressible<File>> downloadOrGetOffline(AuroraFile file, File target);

    Observable<Progressible<AuroraFile>> uploadFile(AuroraFile folder, Uri fileUri);

    Observable<Progressible<AuroraFile>> rewriteFile(AuroraFile target, Uri source);

    Completable setOffline(AuroraFile file, boolean offline);

    Maybe<AuroraFile> getOfflineFile(String pathSpec);
    Single<List<AuroraFile>> getOfflineFiles();

    Single<Boolean> getOfflineStatus(AuroraFile file);
}
