package com.afterlogic.aurora.drive.data.common.network.p8;

import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.FormatHeader;
import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.JsonField;
import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Locale;

import javax.annotation.Nullable;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.Header;

/**
 * Created by aleksandrcikin on 09.06.17.
 * mail: mail@sunnydaydev.me
 */

class P8RequestFactory extends Converter.Factory {

    private final Gson gson;

    public static P8RequestFactory create(Gson gson) {
        return new P8RequestFactory(gson);
    }

    private P8RequestFactory(@P8 Gson gson) {
        this.gson = gson;
    }

    @Nullable
    @Override
    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        Header header = null;
        FormatHeader formatHeader = null;
        JsonField jsonField = null;

        for (Annotation annotation:annotations) {
            if (annotation instanceof Header) {
                header = (Header) annotation;
            } else if (annotation instanceof FormatHeader) {
                formatHeader = (FormatHeader) annotation;
            } else if (annotation instanceof JsonField) {
                jsonField = (JsonField) annotation;
            }
        }

        if (header != null && formatHeader != null) {

            String formatHeaderValue = formatHeader.value();
            return value -> String.format(Locale.US, formatHeaderValue, value);

        }

        if (jsonField != null) {
            return gson::toJson;
        }

        return super.stringConverter(type, annotations, retrofit);
    }


}
