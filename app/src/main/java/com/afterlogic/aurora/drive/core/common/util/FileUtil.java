package com.afterlogic.aurora.drive.core.common.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.afterlogic.aurora.drive.R;
import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.model.AuroraFile;
import com.afterlogic.aurora.drive.model.FileInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
     * Get icon by file content-type and file name extensions.
     *
     * @param file - target file.
     * @return - icon drawable for extension or icon for undefined type.
     */
    public static int getFileIconRes(AuroraFile file){
        int id;
        if (file.isLink()){
            id = R.drawable.ic_content_link;
        } else {
            id = getIconResByFileExtension(file);
            if (id == -1) {
                id = getIconResByContentType(file);
            }
        }
        if (id == -1) id = R.drawable.ic_content_udefined;
        return id;
    }

    /**
     * Get icon for file by file extension from {@link #EXTENSIONS_ICON_MAP}.
     * @param file - target file.
     * @return - icon res for file extension or -1 if it not exist.
     */
    private static int getIconResByFileExtension(AuroraFile file){
        String fileName = file.getName();
        int point = fileName.lastIndexOf('.');
        if (point != -1){
            String ext = fileName.substring(point + 1).toLowerCase();
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
    private static FileInfo fileInfoFromFile(File file) {

        String fileName = file.getName();
        String mime = URLConnection.guessContentTypeFromName(file.getName());

        MediaType mediaType = mime != null ? MediaType.parse(mime) : null;
        long size = file.length();

        return new FileInfo(fileName, size, mediaType, Uri.fromFile(file));
    }

    /**
     * Create {@link FileInfo} from content uri.
     */
    private static FileInfo fileInfoFromContentUri(final Uri uri, final Context ctx) {
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

    public static void writeFile(InputStream is, File target) throws IOException {
        writeFile(is, target, -1, null);
    }

    public static void writeFile(InputStream is, File target, @Nullable Consumer<Long> writtenConsumer) throws IOException {
        writeFile(is, target, -1, writtenConsumer);
    }

    public static void writeFile(InputStream is, File target, long maxSize, @Nullable Consumer<Long> writtenConsumer) throws IOException {
        if (target.exists() && !target.delete()){
            throw new IOException("Cant delete exists file..");
        }

        File dir = target.getParentFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Make dirs failed.");
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(target);

            int readed;
            long totalReaded = 0;
            byte[] buffer = new byte[2048];

            if (writtenConsumer != null) {
                writtenConsumer.consume(totalReaded);
            }
            while ((readed = is.read(buffer)) != -1 && (maxSize == -1 && totalReaded < maxSize)) {
                fos.write(buffer, 0, readed);
                totalReaded += readed;
                if (writtenConsumer != null) {
                    writtenConsumer.consume(totalReaded);
                }
            }
        } finally {
            IOUtil.closeQuietly(fos);
        }
    }

    public static File getFile(File rootDir, AuroraFile file){
        return new File(rootDir, file.getPathSpec());
    }
}
