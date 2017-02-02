package com.afterlogic.aurora.drive._unrefactored.core.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive._unrefactored.data.common.repository.FilesRepository;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;
import com.bumptech.glide.Glide;

import java.io.File;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;

/**
 * Created by sashka on 24.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileUtil {

    /**
     * File extensions-icons map.
     */
    private static final Map<String, Integer> EXTENSIONS_ICON_MAP;
    static {
        HashMap<String, Integer> extMap = new HashMap<>();
        extMap.put("psd", R.drawable.ic_content_photoshop);
        extMap.put("pdf", R.drawable.ic_content_pdf);
        extMap.put("eml", R.drawable.ic_content_eml);
        extMap.put("vcard", R.drawable.ic_content_vcard);

        putIconForGroup(R.drawable.ic_content_doc, extMap, "doc", "docx", "docm", "docb");
        putIconForGroup(R.drawable.ic_content_exel, extMap, "xls", "xlm", "xlsx", "xlsm", "xlsb");
        putIconForGroup(R.drawable.ic_content_powerpoint, extMap,
                "ppt", "pps", "pptx", "ppsx", "pptm", "ppsm", "sldx", "sldm");
        putIconForGroup(R.drawable.ic_content_video, extMap,
                "avi", "mkv", "mp4", "mov", "ogv", "3gp", "mpeg", "mpg", "m2v", "m4v", "flv");
        putIconForGroup(R.drawable.ic_content_audio, extMap,
                "mp3", "wav", "wma", "ogg", "oga", "mpc", "m4a", "aac");
        putIconForGroup(R.drawable.ic_content_archive, extMap,
                "zip", "rar", "7z", "s7z", "tar", "mar", "cab");
        putIconForGroup(R.drawable.ic_content_ical, extMap, "ical", "icalendar", "ics");
        EXTENSIONS_ICON_MAP = Collections.unmodifiableMap(extMap);
    }

    public static final Comparator<AuroraFile> AURORA_FILE_COMPARATOR = (lhs, rhs) -> {
        if (lhs.isFolder() && !rhs.isFolder()){
            return -1;
        }else if (rhs.isFolder() && !lhs.isFolder()){
            return 1;
        }else{
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    };

    private static void putIconForGroup(@DrawableRes int icon,
                                        @NonNull Map<String, Integer> iconMap,
                                        @NonNull String... extensions){
        for (String ext:extensions){
            iconMap.put(ext, icon);
        }
    }

    /**
     * Update file icon.
     *
     * @param icon - icon {@link ImageView}. It used when set default icon.
     * @param file - current {@link AuroraFile}.
     * @param ctx - application context.
     */
    public static void updateIcon(@NonNull ImageView icon, @NonNull AuroraFile file,
                                  @NonNull FilesRepository filesRepository, @NonNull Context ctx){
        Disposable currentDisposable = (Disposable) icon.getTag(R.id.load_thumb_disposable);
        if (currentDisposable != null){
            currentDisposable.dispose();
            icon.setTag(R.id.load_thumb_disposable, null);
        }

        if (file.isFolder()){
            icon.setImageDrawable(
                    DrawableUtil.getTintedDrawable(R.drawable.ic_folder, R.color.colorPrimary, ctx)
            );
        }else{

            //Set default icon
            icon.setImageDrawable(
                    getContentIcon(file, ctx)
            );

            //[START Try get thumbnail]
            if (file.hasThumbnail()) {
                Disposable disposable = getThumbnailRequest(file, filesRepository)
                        .doFinally(() -> icon.setTag(R.id.load_thumb_disposable, null))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                uri -> loadThumbnail(uri, icon),
                                error -> MyLog.majorException("FileUtil", error)
                        );
                icon.setTag(R.id.load_thumb_disposable, disposable);

            }
            //[END Try get thumbnail]
        }
    }

    private static Maybe<Uri> getThumbnailRequest(AuroraFile file, FilesRepository repository){
        return Maybe.defer(() -> {
            if (file.isOfflineMode()){
                return Maybe.just(Uri.fromFile(new File(file.getThumbnailLink())));
            } else {
                if (!TextUtils.isEmpty(file.getThumbnailLink())) {
                    return Maybe.just(Uri.parse(file.getThumbnailLink()));
                } else if (!TextUtils.isEmpty(file.getContentType())) {
                    return repository.getFileThumbnail(file).toMaybe();
                }
            }
            return Maybe.empty();
        });
    }

    private static void loadThumbnail(Uri uri, ImageView image){
        try {
            Glide.with(image.getContext())
                    .load(uri)
                    .override(96, 96)
                    .centerCrop()
                    .into(image);
        } catch (Exception e){
            MyLog.majorException("FileUtil", e.getMessage());
        }
    }

    /**
     * Get icon by file content-type and file name extensions.
     *
     * @param file - target file.
     * @param ctx - application context.
     * @return - icon drawable for extension or icon for undefined type.
     */
    private static Drawable getContentIcon(AuroraFile file, Context ctx){
        int id;
        if (file.isLink()){
            id = R.drawable.ic_content_link;
        } else {
            id = getIconResByFileName(file);
            if (id == -1) {
                id = getIconResByContentType(file);
            }
        }
        if (id == -1) id = R.drawable.ic_content_udefined;
        return ContextCompat.getDrawable(ctx, id);
    }

    /**
     * Get icon for file by file extension from {@link #EXTENSIONS_ICON_MAP}.
     * @param file - target file.
     * @return - icon res for file extension or -1 if it not exist.
     */
    private static int getIconResByFileName(AuroraFile file){
        String fileName = file.getName();
        int point = fileName.lastIndexOf('.');
        if (point != -1){
            String ext = fileName.substring(point + 1);
            if (EXTENSIONS_ICON_MAP.containsKey(ext)){
                return EXTENSIONS_ICON_MAP.get(ext);
            }
        }
        return -1;
    }

    /**
     * Get icon resource id by {@link AuroraFile} content-type.
     * @param file - target file.
     * @return drawable id for requested type or -1 if type undefined.
     */
    private static int getIconResByContentType(AuroraFile file){
        MediaType type = MediaType.parse(file.getContentType());
        switch (type.type()){
            case "image":
                return R.drawable.ic_content_image;
            case "video":
                return R.drawable.ic_content_video;
            case "audio":
                return R.drawable.ic_content_audio;
            case "text":
                switch (type.subtype()){
                    case "calendar":
                        return R.drawable.ic_content_ical;
                    case "plain":
                        return R.drawable.ic_content_txt;
                    case "html":
                        return R.drawable.ic_content_html;
                }
                break;
            case "application":
                switch (type.subtype()){
                    case "vnd.ms-word":
                    case "msword":
                        return R.drawable.ic_content_doc;
                    case "vnd.ms-excel":
                        return R.drawable.ic_content_exel;
                    case "vnd.ms-powerpoint":
                        return R.drawable.ic_content_powerpoint;
                }
        }
        return -1;
    }

    /**
     * Get file in downloads folder for remote {@link AuroraFile}
     * @param file - target remote file.
     * @return - file in 'Downloads' folder.
     */
    public static File getDownloadsFile(AuroraFile file){
        //If to downloads folder
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File targetFile = new File(dir, file.getName());
        //Check is file exist
        if (targetFile.exists()) {
            //[START Create new unique name]
            int suffixInt = 0;
            do {
                suffixInt++;
                String fileName = file.getName();
                String suffix = "(" + suffixInt + ")";
                fileName = appendSufixToFileName(fileName, suffix);
                targetFile = new File(dir, fileName);
            } while (targetFile.exists());
            //[END Create new unique name]
        }
        return targetFile;
    }

    public static String appendSufixToFileName(String fileName, String suffix){
        int dot = fileName.lastIndexOf('.');
        int insertion;
        if (dot != -1 && dot != fileName.length() - 1) {
            insertion = dot;
        } else {
            insertion = fileName.length();
        }
        return fileName.substring(0, insertion) + suffix + fileName.substring(insertion);
    }

    /**
     * Get cached file for remote file.
     * @param file - target remote file.
     * @param ctx - application context.
     * @return - return cached local file.
     */
    public static File getCacheFile(AuroraFile file, Context ctx){
        File dir = new File(
                getCacheFileDir(ctx),
                file.getType().toLowerCase() + File.separator + file.getPath()
        );
        return new File(dir, file.getName());
    }

    /**
     * Get offline file for remote file.
     * @param file - target remote file.
     * @param ctx - application context.
     * @return - return local offline file.
     */
    public static File getOfflineFile(AuroraFile file, Context ctx){
        File dir = new File(
                getOfflineFileDir(ctx),
                file.getType().toLowerCase() + File.separator + file.getPath()
        );
        return new File(dir, file.getName());
    }

    /**
     * Get dir for cached files.
     * @param ctx - application context.
     * @return - return dir where store cached files.
     */
    public static File getCacheFileDir(Context ctx){
        return new File(ctx.getExternalCacheDir(), "files/");
    }

    /**
     * Get dir for offline files.
     * @param ctx - application context.
     * @return - return dir where store offline files.
     */
    public static File getOfflineFileDir(Context ctx){
        return new File(ctx.getExternalFilesDir(null), "offline/");
    }

    /**
     * Read file info. Prepare uri for uploading.
     * @param uri - target uri.
     * @param ctx - application context.
     * @return -
     */
    public static FileInfo fileInfo(Uri uri, Context ctx){
        if (uri.toString().startsWith("file://")) {
            return fileInfoFromFile(new File(uri.getPath()));
        } else if (uri.toString().startsWith("content://")) {
            return fileInfoFromContentUri(uri, ctx);
        } else {
            return null;
        }
    }

    /**
     * Create {@link FileInfo} from file uri.
     */
    public static FileInfo fileInfoFromFile(File file) {

        String fileName = file.getName();
        String mime = URLConnection.guessContentTypeFromName(file.getName());

        MediaType mediaType = mime != null ? MediaType.parse(mime) : null;
        long size = file.length();

        return new FileInfo(fileName, size, mediaType, Uri.fromFile(file));
    }

    /**
     * Create {@link FileInfo} from content uri.
     */
    public static FileInfo fileInfoFromContentUri(final Uri uri, final Context ctx) {
        Cursor cursor;
        String fileName;
        MediaType mediaType = null;
        long size;
        String[] proj = {
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.SIZE
        };
        cursor = ctx.getContentResolver().query(uri, proj, null, null, null);

        if (cursor == null) return null;

        int column_name = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        int column_mime = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
        int column_size = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE);

        cursor.moveToFirst();

        fileName = cursor.getString(column_name);
        if (column_mime != -1) {
            String mime = cursor.getString(column_mime);
            if (!TextUtils.isEmpty(mime)) {
                mediaType = MediaType.parse(mime);
            }
        }

        if (mediaType == null){
            mediaType = MediaType.parse("*/*");
        }
        size = cursor.getLong(column_size);

        cursor.close();

        return new FileInfo(fileName, size, mediaType, uri);
    }

    /**
     * Get {@link File} mime type.
     *
     * @param file - target file.
     * @return - file mime type or *\/*
     */
    public static String getFileMimeType(File file){
        String fileName = file.getAbsolutePath().substring(
                file.getAbsolutePath().lastIndexOf(File.separator) + 1);
        String fileExtension = getFileExtension(fileName);

        String mimeType = null;
        if (fileExtension != null){
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        if (mimeType == null){
            return "*/*";
        } else {
            return mimeType;
        }
    }

    public static String getFileExtension(String fileName){
        return fileName.contains(".") ?
                fileName.substring(fileName.lastIndexOf(".") + 1) : null;
    }

    public static File getTargetFileByType(AuroraFile file, DownloadType type, Context ctx){
        switch (type){
            case DOWNLOAD_TO_DOWNLOADS:
                return getDownloadsFile(file);
            case DOWNLOAD_FOR_OFFLINE:
                return getOfflineFile(file, ctx);
            default:
                return getCacheFile(file, ctx);
        }
    }
}
