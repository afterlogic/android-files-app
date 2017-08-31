package com.afterlogic.aurora.drive.model;

import com.afterlogic.aurora.drive.core.common.parceller.HttpUrlParcelConverter;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.ParcelPropertyConverter;

import okhttp3.HttpUrl;

/**
 * Aurora session.
 * Handle account id, app token, auth token.
 */

@Parcel
public class AuroraSession {

    protected String appToken;
    protected String authToken;
    protected long accountId;

    protected String login;
    protected String password;

    @ParcelPropertyConverter(HttpUrlParcelConverter.class)
    protected HttpUrl domain;

    protected int apiVersion;

    @ParcelConstructor
    protected AuroraSession() {
    }

    public AuroraSession(HttpUrl domain, String login, String password, int apiVersion) {
        this.domain = domain;
        this.login = login;
        this.password = password;
        this.apiVersion = apiVersion;
    }

    public AuroraSession(String appToken, String authToken, long accountId, String login, String password, HttpUrl domain, int apiVersion) {
        this.appToken = appToken;
        this.authToken = authToken;
        this.accountId = accountId;
        this.login = login;
        this.password = password;
        this.domain = domain;
        this.apiVersion = apiVersion;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HttpUrl getDomain() {
        return domain;
    }

    public void setDomain(HttpUrl domain) {
        this.domain = domain;
    }

    public int getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(int apiVersion) {
        this.apiVersion = apiVersion;
    }

}
