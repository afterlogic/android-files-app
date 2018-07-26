package com.afterlogic.aurora.drive.data.common.network.p8.converter;

import androidx.annotation.NonNull;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 26.07.2018.
 * mail: mail@sunnydaydev.me
 */
public class ToStringConverter implements Converter<Object, RequestBody> {

    public static Converter<?, RequestBody> create() {
        return new ToStringConverter();
    }

    private ToStringConverter() {
    }

    @Override
    public RequestBody convert(@NonNull Object value) {

        MediaType mediaType = MediaType.parse("text/plain");
        return RequestBody.create(mediaType, value.toString());

    }

}
