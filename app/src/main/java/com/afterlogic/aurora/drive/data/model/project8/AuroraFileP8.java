package com.afterlogic.aurora.drive.data.model.project8;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sashka on 18.03.16.
 * mail: sunnyday.development@gmail.com
 */
public class AuroraFileP8 {
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
    private String mLinkType;

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

    @SerializedName("Actions")
    private ActionsDtoP8 mActions;

    public String getName() {
        return mName;
    }

    public String getPath() {
        return mPath;
    }

    public String getFullPath() {
        return mFullPath;
    }

    public boolean isFolder() {
        return mIsFolder;
    }

    public boolean isLink() {
        return mIsLink;
    }

    public String getLinkUrl() {
        return mLinkUrl;
    }

    public String getLinkType() {
        return mLinkType;
    }

    public String getThumbnailLink() {
        return mThumbnailLink;
    }

    public boolean isThumb() {
        return mThumb;
    }

    public String getContentType() {
        return mContentType;
    }

    public String getHash() {
        return mHash;
    }

    public String getType() {
        return mType;
    }

    public long getSize() {
        return mSize;
    }

    public long getLastModified() {
        return mLastModified;
    }

    public ActionsDtoP8 getActions() {
        return mActions;
    }
}
