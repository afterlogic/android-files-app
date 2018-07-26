package com.afterlogic.aurora.drive.data.common.network.p8.converter;

import com.afterlogic.aurora.drive.data.common.network.p8.Api8;
import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.ApiRequest;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URLEncoder;

import androidx.annotation.NonNull;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */
public class ApiRequestConverter implements Converter<Object, RequestBody> {

    public static Converter<?, RequestBody> create(ApiRequest requestInfo, Gson gson) {
        return new ApiRequestConverter(requestInfo, gson);
    }

    private ApiRequest requestInfo;

    private Gson gson;

    private ApiRequestConverter(ApiRequest requestInfo, Gson gson) {
        this.requestInfo = requestInfo;
        this.gson = gson;
    }

    @Override
    public RequestBody convert(@NonNull Object value) throws IOException {

        String params = URLEncoder.encode(gson.toJson(value), "utf-8");
        String content = Api8.ApiField.MODULE + "=" + requestInfo.module() + "&" +
                Api8.ApiField.METHOD + "=" + requestInfo.method() + "&" +
                Api8.ApiField.PARAMS + "=" + params;

        MediaType mediaType = MediaType.parse(Api8.ApiHeader.CONTENT_TYPE_X_FORM_URL_ENCODED);
        return RequestBody.create(mediaType, content);

    }

}
