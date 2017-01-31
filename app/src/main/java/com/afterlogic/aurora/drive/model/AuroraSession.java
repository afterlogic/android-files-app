package com.afterlogic.aurora.drive.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import okhttp3.HttpUrl;

/**
 * Aurora session.
 * Handle account id, app token, auth token.
 */
public class AuroraSession implements Parcelable{

    private String mAppToken;
    private String mAuthToken;
    private long mAccountId;

    private String mLogin;
    private String mPassword;
    private HttpUrl mDomain;

    private int mApiType;

    public AuroraSession(HttpUrl domain, String login, String password, int apiType) {
        mDomain = domain;
        mLogin = login;
        mPassword = password;
        mApiType = apiType;
    }

    public AuroraSession(String appToken, String authToken, long accountId, String login, String password, HttpUrl domain, int apiType) {
        mAppToken = appToken;
        mAuthToken = authToken;
        mAccountId = accountId;
        mLogin = login;
        mPassword = password;
        mDomain = domain;
        mApiType = apiType;
    }

    protected AuroraSession(Parcel in) {
        mAppToken = in.readString();
        mAuthToken = in.readString();
        mAccountId = in.readLong();
        mLogin = in.readString();
        mPassword = in.readString();
        mApiType = in.readInt();
        mDomain = HttpUrl.parse(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAppToken);
        dest.writeString(mAuthToken);
        dest.writeLong(mAccountId);
        dest.writeString(mLogin);
        dest.writeString(mPassword);
        dest.writeInt(mApiType);
        dest.writeString(mDomain.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AuroraSession> CREATOR = new Creator<AuroraSession>() {
        @Override
        public AuroraSession createFromParcel(Parcel in) {
            return new AuroraSession(in);
        }

        @Override
        public AuroraSession[] newArray(int size) {
            return new AuroraSession[size];
        }
    };

    public String getLogin() {
        return mLogin;
    }

    public String getPassword() {
        return mPassword;
    }

    public String getAppToken() {
        return mAppToken;
    }

    public void setAppToken(String appToken) {
        mAppToken = appToken;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public void setAuthToken(String authToken) {
        mAuthToken = authToken;
    }

    public long getAccountId() {
        return mAccountId;
    }

    public void setAccountId(long accountId) {
        mAccountId = accountId;
    }

    public void setDomain(@NonNull HttpUrl domain){
        mDomain = domain;
    }

    public HttpUrl getDomain() {
        return mDomain;
    }

    public int getApiVersion() {
        return mApiType;
    }

    public boolean isComplete(){
        return mAccountId != 0 && mDomain != null &&
                !hasEmpty(mAuthToken, mDomain.toString(), mLogin, mPassword, mAppToken);
    }

    private boolean hasEmpty(String... strings){
        for (String s:strings){
            if (TextUtils.isEmpty(s)){
                return true;
            }
        }
        return false;
    }

}
