package com.afterlogic.aurora.drive.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class AuroraFile implements Parcelable, Cloneable{

    private static final String[] PREVIEWABLE_CONTENT_TYPES = {
            "image/jpeg",
            "image/pjpeg",
            "image/gif",
            "image/png",
            "image/bmp",
            "image/webp",
    };

    private String mName;

    private String mPath;

    private String mFullPath;

    private boolean mIsFolder;

    private boolean mIsLink;

    private String mLinkUrl;

    private int mLinkType;

    private String mThumbnailLink;

    private boolean mThumb;

    private String mContentType;

    private String mHash;

    private String mType;

    private long mSize;

    private long mLastModified;

    private boolean mShared;

    @Nullable
    private Actions mActions;

    public static AuroraFile create(@NonNull AuroraFile parent, @NonNull String name, boolean mIsFolder){
        AuroraFile file = new AuroraFile();
        String parentFullPath = parent.getFullPath();
        file.mFullPath = parentFullPath.equals("") ? "/" + name : parentFullPath + "/" + name;
        file.mType = parent.getType();
        file.mIsFolder = mIsFolder;
        file.mSize = -1;
        file.mPath = parentFullPath;
        file.mName = name;
        return file;
    }

    public static AuroraFile parse(@NonNull String fullPath, @NonNull String type, boolean mIsFolder){
        AuroraFile file = new AuroraFile();
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

    private AuroraFile() {
    }

    public AuroraFile(String name,
                      String path,
                      String fullPath,
                      boolean isFolder,
                      boolean isLink,
                      String linkUrl,
                      int linkType,
                      String thumbnailLink,
                      boolean thumb,
                      String contentType,
                      String hash,
                      String type,
                      long size,
                      long lastModified,
                      boolean shared) {
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
        mShared = shared;
    }

    protected AuroraFile(Parcel in) {
        mName = in.readString();
        mPath = in.readString();
        mFullPath = in.readString();
        mIsFolder = in.readByte() != 0;
        mIsLink = in.readByte() != 0;
        mLinkUrl = in.readString();
        mLinkType = in.readInt();
        mThumbnailLink = in.readString();
        mThumb = in.readByte() != 0;
        mContentType = in.readString();
        mHash = in.readString();
        mType = in.readString();
        mSize = in.readLong();
        mLastModified = in.readLong();
    }

    public static final Creator<AuroraFile> CREATOR = new Creator<AuroraFile>() {
        @Override
        public AuroraFile createFromParcel(Parcel in) {
            return new AuroraFile(in);
        }

        @Override
        public AuroraFile[] newArray(int size) {
            return new AuroraFile[size];
        }
    };

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
        int nameStart = mFullPath.lastIndexOf(mName);
        mFullPath = mFullPath.substring(0, nameStart) + name;
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

    public String getThumbnailUrl() {
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

    public void setThumbnailLink(String thumbnailLink) {
        mThumbnailLink = thumbnailLink;
    }

    public void setHasThumb(boolean thumb) {
        mThumb = thumb;
    }

    public void setLastModified(long lastModified) {
        mLastModified = lastModified;
    }

    public void setContentType(String contentType) {
        mContentType = contentType;
    }

    public boolean isShared() {
        return mShared;
    }

    public void setShared(boolean shared) {
        mShared = shared;
    }

    @Nullable
    public Actions getActions() {
        return mActions;
    }

    public void setActions(@Nullable Actions mActions) {
        this.mActions = mActions;
    }

    public String getPathSpec(){
        return mType + mFullPath;
    }

    public AuroraFile getParentFolder(){
        if ("".equals(mFullPath)) return null;

        if (mFullPath.contains("/")){
            return parse(mFullPath.substring(0, mFullPath.lastIndexOf('/')), mType, true);
        } else {
            return parse("", mType, true);
        }
    }

    public boolean isPreviewAble(){
        for (String previewable:PREVIEWABLE_CONTENT_TYPES){
            if (previewable.equals(mContentType.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mPath);
        dest.writeString(mFullPath);
        dest.writeByte((byte) (mIsFolder ? 1 : 0));
        dest.writeByte((byte) (mIsLink ? 1 : 0));
        dest.writeString(mLinkUrl);
        dest.writeInt(mLinkType);
        dest.writeString(mThumbnailLink);
        dest.writeByte((byte) (mThumb ? 1 : 0));
        dest.writeString(mContentType);
        dest.writeString(mHash);
        dest.writeString(mType);
        dest.writeLong(mSize);
        dest.writeLong(mLastModified);
    }

    public AuroraFile clone(){
        try {
            return (AuroraFile) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AuroraFile)) return false;
        AuroraFile check = (AuroraFile) obj;

        return mFullPath.equals(check.getFullPath());
    }
}
