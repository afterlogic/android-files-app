package com.afterlogic.aurora.drive.data.model.project7;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class AuroraFileP7{

    @SerializedName("Name")
    private String mName;

    @SerializedName("Path")
    private String mPath;

    @SerializedName("FullPath")
    private String mFullPath;

    @SerializedName("IsFolder")
    private boolean mIsFolder;

    @SerializedName("IsLink")
    private boolean mIsLink;

    @SerializedName("LinkUrl")
    private String mLinkUrl;

    @SerializedName("LinkType")
    private int mLinkType;

    @SerializedName("ThumbnailLink")
    private String mThumbnailLink;

    @SerializedName("Thumb")
    private boolean mThumb;

    @SerializedName("ContentType")
    private String mContentType;

    @SerializedName("Hash")
    private String mHash;

    @SerializedName("Type")
    private String mType;

    @SerializedName("Size")
    private long mSize;

    @SerializedName("LastModified")
    private long mLastModified;

    public AuroraFileP7() {
    }

    public AuroraFileP7(String name, String path, String fullPath, boolean isFolder, boolean isLink, String linkUrl, int linkType, String thumbnailLink, boolean thumb, String contentType, String hash, String type, long size, long lastModified) {
        mName = name;
        mPath = path;
        mFullPath = fullPath;
        mIsFolder = isFolder;
        mIsLink = isLink;
        mLinkUrl = linkUrl;
        mLinkType = linkType;
        mThumbnailLink = thumbnailLink;
        mThumb = thumb;
        mContentType = contentType;
        mHash = hash;
        mType = type;
        mSize = size;
        mLastModified = lastModified;
    }

    public String getContentType() {
        return mContentType;
    }

    public String getHash() {
        return mHash;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mFullPath = mFullPath.replace(mName, name);
        mName = name;
    }

    public boolean isFolder() {
        return mIsFolder;
    }

    public String getPath() {
        return mPath;
    }

    public String getFullPath() {
        return mFullPath;
    }

    public String getType() {
        return mType;
    }

    public boolean isLink() {
        return mIsLink;
    }

    public String getLinkUrl() {
        return mLinkUrl;
    }

    public int getLinkType() {
        return mLinkType;
    }

    public String getThumbnailLink() {
        return mThumbnailLink;
    }

    public boolean hasThumbnail() {
        return mThumb;
    }

    public long getSize() {
        return mSize;
    }

    public long getLastModified() {
        return mLastModified;
    }

    public static AuroraFileP7 parse(@NonNull String fullPath, @NonNull String type, boolean mIsFolder){
        AuroraFileP7 file = new AuroraFileP7();
        file.mFullPath = fullPath;
        file.mType = type;
        file.mIsFolder = mIsFolder;
        file.mSize = -1;
        if (fullPath.contains("/")){
            int lastSeparator = fullPath.lastIndexOf("/");
            file.mName = fullPath.substring(lastSeparator + 1);
            file.mPath = fullPath.substring(0, lastSeparator);
        }else{
            file.mName = fullPath;
            file.mPath = "";
        }
        return file;
    }
}
