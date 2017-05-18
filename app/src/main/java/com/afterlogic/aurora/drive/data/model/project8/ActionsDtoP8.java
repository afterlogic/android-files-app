package com.afterlogic.aurora.drive.data.model.project8;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aleksandrcikin on 12.05.17.
 * mail: mail@sunnydaydev.me
 */

public class ActionsDtoP8 {

    @SerializedName("list")
    private Object list;


    @SerializedName("open")
    private UrlActionDto open;

    @SerializedName("view")
    private UrlActionDto view;

    public Object getList() {
        return list;
    }

    public UrlActionDto getOpen() {
        return open;
    }

    public UrlActionDto getView() {
        return view;
    }

    public static class UrlActionDto {

        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }
    }
}
