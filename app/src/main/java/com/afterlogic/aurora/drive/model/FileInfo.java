package com.afterlogic.aurora.drive.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import okhttp3.MediaType;

/**
 * Created by sashka on 13.04.16.
 * mail: sunnyday.development@gmail.com
 */
public class FileInfo implements Parcelable{
    private String mName;
    private long mSize;
    private MediaType mMime;
    private Uri mUri;

    public FileInfo(String name, long size, MediaType mime, Uri uri) {
        mName = name;
        mSize = size;
        mMime = mime;
        mUri = uri;
    }

    protected FileInfo(Parcel in) {
        mName = in.readString();
        mSize = in.readLong();
        mUri = in.readParcelable(Uri.class.getClassLoader());
        mMime = MediaType.parse(in.readString());
    }

    public static final Creator<FileInfo> CREATOR = new Creator<FileInfo>() {
        @Override
        public FileInfo createFromParcel(Parcel in) {
            return new FileInfo(in);
        }

        @Override
        public FileInfo[] newArray(int size) {
            return new FileInfo[size];
        }
    };

    public String getName() {
        return mName;
    }

    public long getSize() {
        return mSize;
    }

    public MediaType getMime() {
        return mMime;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeLong(mSize);
        dest.writeParcelable(mUri, flags);
        dest.writeString(mMime.toString());
    }
}
