package com.afterlogic.aurora.drive._unrefactored.data.common.db.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.afterlogic.aurora.drive.model.AuroraFile;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;

/**
 * Created by sashka on 13.04.16.
 * mail: sunnyday.development@gmail.com
 */
@DatabaseTable(tableName = "watching_file")
public class WatchingFile implements Parcelable{
    public static final String COLUMN_LOCAL_FILE = "file_path";
    public static final String COLUMN_REMOTE_FILE_SPEC = "remote_file_path";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_SYNC_DATE = "sync_date";
    public static final String COLUMN_SYNC_STATUS = "sync_status";

    public static final int TYPE_CACHE = 1;
    public static final int TYPE_OFFLINE = 2;

    public static final int SYNCED = 0;
    public static final int SYNC_WAITING_SYNC = 1;
    public static final int SYNC_IN_PROGRESS = 2;
    public static final int SYNC_NEED = 3;

    private static final String COLUMN_LOCAL_CREATE_DATE = "local_create_date";
    private static final String COLUMN_SYNC_CONFLICT = "sync_conflict";

    /**
     * This field is id.
     * For query by id need use full file path spec
     * {@link Spec#getRemoteUniqueSpec(String, String)}( for path use {@link AuroraFile#getFullPath()}).
     */
    @DatabaseField(columnName = COLUMN_REMOTE_FILE_SPEC, id = true)
    private String mRemoteUniqueSpec;

    /**
     * For query need use {@link File#getAbsolutePath()}.
     */
    @DatabaseField(columnName = COLUMN_LOCAL_FILE)
    private String mFilePath;

    @DatabaseField(columnName = COLUMN_TYPE)
    private int mType;

    @DatabaseField(columnName = COLUMN_SYNC_DATE)
    private long mLastModified;

    @DatabaseField(columnName = COLUMN_SYNC_STATUS)
    private int mSyncStatus = SYNCED;

    @DatabaseField(columnName = COLUMN_LOCAL_CREATE_DATE)
    private long mLocalCreateDate;

    @DatabaseField(columnName = COLUMN_SYNC_CONFLICT)
    private boolean mSyncConflict = false;

    public WatchingFile() {
        //Stub for DAO
    }

    public WatchingFile(AuroraFile remote, File local, int type) {
        this(remote, local, type, false);
    }

    public WatchingFile(AuroraFile remote, File local, int type, boolean synced) {
        mFilePath = local.getAbsolutePath();
        mRemoteUniqueSpec = Spec.getRemoteUniqueSpec(remote);
        mSyncStatus = synced ? SYNCED : SYNC_NEED;
        if (synced){
            mLastModified = remote.getLastModified();
        }
        mType = type;
        mLocalCreateDate = System.currentTimeMillis();
    }

    protected WatchingFile(Parcel in) {
        mRemoteUniqueSpec = in.readString();
        mFilePath = in.readString();
        mType = in.readInt();
        mLastModified = in.readLong();
        mSyncStatus = in.readInt();
        mLocalCreateDate = in.readLong();
        mSyncConflict = in.readByte() != 0;
    }

    public static final Creator<WatchingFile> CREATOR = new Creator<WatchingFile>() {
        @Override
        public WatchingFile createFromParcel(Parcel in) {
            return new WatchingFile(in);
        }

        @Override
        public WatchingFile[] newArray(int size) {
            return new WatchingFile[size];
        }
    };

    public String getLocalFilePath() {
        return mFilePath;
    }

    public String getRemoteUniqueSpec() {
        return mRemoteUniqueSpec;
    }

    public int getType() {
        return mType;
    }

    public String getRemoteAuroraType() {
        return Spec.getRemoteType(mRemoteUniqueSpec);
    }

    public String getRemoteFilePath(){
        return Spec.getRemotePath(mRemoteUniqueSpec);
    }

    public long getLastModified() {
        return mLastModified;
    }

    public void setLastModified(long syncDate) {
        mLastModified = syncDate;
    }

    public int getSyncStatus() {
        return mSyncStatus;
    }

    public void setSyncStatus(int status) {
        mSyncStatus = status;
    }

    public long getLocalCreateDate() {
        return mLocalCreateDate;
    }

    public boolean isSyncConflict() {
        return mSyncConflict;
    }

    public void setSyncConflict(boolean syncConflict) {
        mSyncConflict = syncConflict;
    }

    public boolean isNeedSync(@NonNull AuroraFile remote, @NonNull File local){
        long localLastModified = local.lastModified();
        long remoteLastModified = remote.getLastModified();
        boolean localSynced = localLastModified == mLastModified;
        boolean remoteSynced = remoteLastModified == mLastModified;
        return mSyncStatus == SYNC_NEED || !localSynced || !remoteSynced;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRemoteUniqueSpec);
        dest.writeString(mFilePath);
        dest.writeInt(mType);
        dest.writeLong(mLastModified);
        dest.writeInt(mSyncStatus);
        dest.writeLong(mLocalCreateDate);
        dest.writeByte((byte) (mSyncConflict ? 1 : 0));
    }

    public static final class Spec{

        public static String getRemoteUniqueSpec(AuroraFile remote){
            return getRemoteUniqueSpec(remote.getType(), remote.getFullPath());
        }

        public static String getRemoteUniqueSpec(String type, String path){
            return type.replaceAll("'", "''") + ":" + path.replaceAll("'", "''");
        }

        public static String getRemoteType(String spec){
            return spec.substring(0, spec.indexOf(':'));
        }

        public static String getRemotePath(String spec){
            return spec.substring(spec.indexOf(':') + 1);
        }
    }
}
