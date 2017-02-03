package com.afterlogic.aurora.drive.data.common.network;

import android.content.Context;

import com.afterlogic.aurora.drive.model.FileInfo;
import com.afterlogic.aurora.drive._unrefactored.data.common.api.ApiTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by sashka on 25.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class ExtRequestBody extends RequestBody {

    private InputStreamCreator mInputStreamCreator;
    private FileInfo mFileInfo;

    private ApiTask.ProgressUpdater mProgressUpdater;

    public ExtRequestBody(FileInfo fileInfo, Context ctx){
        mFileInfo = fileInfo;
        mInputStreamCreator = getStreamCreator(fileInfo, ctx);
    }

    private InputStreamCreator getStreamCreator( final FileInfo fileInfo, final Context ctx){
        String uri = fileInfo.getUri().toString();
        if (uri.startsWith("file://")) {
            return () -> {
                File file = new File(fileInfo.getUri().getPath());
                return new FileInputStream(file);
            };
        } else if (uri.startsWith("content://")) {
            return () -> ctx.getContentResolver().openInputStream(fileInfo.getUri());
        } else {
            return null;
        }
    }

    @Override
    public MediaType contentType() {
        return mFileInfo.getMime();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Source source = null;
        try {
            long totalRead = 0;
            notifyProgress(totalRead);

            source = Okio.source(mInputStreamCreator.getInputStream());
            Buffer buffer = new Buffer();
            long read;
            while ((read = source.read(buffer, 2048)) != -1){
                sink.write(buffer, read);

                totalRead += read;
                notifyProgress(totalRead);
            }
        } finally {
            Util.closeQuietly(source);
        }
    }

    private void notifyProgress(long progress){
        if (mProgressUpdater != null){
            mProgressUpdater.notifyChanged(progress, mFileInfo.getSize());
        }
    }

    @Override
    public long contentLength() throws IOException {
        return mFileInfo.getSize();
    }

    public String getFileName() {
        return mFileInfo.getName();
    }

    public void setProgressUpdater(ApiTask.ProgressUpdater progressUpdater) {
        mProgressUpdater = progressUpdater;
    }

    private interface InputStreamCreator {
        InputStream getInputStream() throws IOException;
    }
}
