package com.afterlogic.aurora.drive.model;

import okhttp3.HttpUrl;

/**
 * Aurora session.
 * Handle account id, app token, auth token.
 */

public class AuthorizedAuroraSession {

    private String user;
    private String appToken;
    private String authToken;
    private long accountId;
    private String email;
    private String password;
    private HttpUrl domain;
    private int apiVersion;

    public AuthorizedAuroraSession(String user,
                                   String appToken,
                                   String authToken,
                                   long accountId,
                                   String email,
                                   String password,
                                   HttpUrl domain,
                                   int apiVersion) {

        this.user = user;
        this.appToken = appToken;
        this.authToken = authToken;
        this.accountId = accountId;
        this.email = email;
        this.password = password;
        this.domain = domain;
        this.apiVersion = apiVersion;
    }

    public String getUser() {
        return user;
    }

    public String getAppToken() {
        return appToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public long getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public HttpUrl getDomain() {
        return domain;
    }

    public int getApiVersion() {
        return apiVersion;
    }
}
