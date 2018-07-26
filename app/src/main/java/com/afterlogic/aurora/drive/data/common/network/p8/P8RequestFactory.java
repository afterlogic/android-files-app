package com.afterlogic.aurora.drive.data.common.network.p8;

import com.afterlogic.aurora.drive.data.common.annotations.P8;
import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.ApiRequest;
import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.FormatHeader;
import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.JsonField;
import com.afterlogic.aurora.drive.data.common.network.p8.apiAnnotations.ToString;
import com.afterlogic.aurora.drive.data.common.network.p8.converter.ApiRequestConverter;
import com.afterlogic.aurora.drive.data.common.network.p8.converter.ToStringConverter;
import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.google.gson.Gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Locale;

import androidx.annotation.Nullable;

import okhttp3.RequestBody;
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
    public Converter<?, RequestBody> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations,
            Retrofit retrofit) {

        try {

            Converter<?, RequestBody> apiRequestConverter =
                    checkApiRequestConverter(parameterAnnotations, methodAnnotations);

            if (apiRequestConverter != null) {
                return apiRequestConverter;
            }

            Converter<?, RequestBody> toStringConverter =
                    checkToStringConverter(parameterAnnotations);

            if (toStringConverter != null) {
                return toStringConverter;
            }

            return super.requestBodyConverter(
                    type, parameterAnnotations, methodAnnotations, retrofit);

        } catch (Exception e) {

            e.printStackTrace();
            throw e;

        }

    }

    @Nullable
    @Override
    public Converter<?, String> stringConverter(Type type,
                                                Annotation[] annotations,
                                                Retrofit retrofit) {

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

    @Nullable
    private Converter<?, RequestBody> checkToStringConverter(Annotation[] parameterAnnotations) {

        Optional<ToString> apiRequestAnnotation = Stream.of(Arrays.asList(parameterAnnotations))
                .filter(a -> a instanceof ToString)
                .map(a -> (ToString) a)
                .findFirst();

        if (apiRequestAnnotation.isPresent()) {
            return ToStringConverter.create();
        } else {
            return null;
        }

    }

    @Nullable
    private Converter<?, RequestBody> checkApiRequestConverter(
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations) {

        Converter<?, RequestBody> parameter = apiRequestConverter(parameterAnnotations);

        return parameter != null ? parameter : apiRequestConverter(methodAnnotations);

    }

    @Nullable
    private Converter<?, RequestBody> apiRequestConverter(Annotation[] parameterAnnotations) {

        Optional<ApiRequest> apiRequestAnnotation = Stream.of(Arrays.asList(parameterAnnotations))
                .filter(a -> a instanceof ApiRequest)
                .map(a -> (ApiRequest) a)
                .findFirst();

        if (apiRequestAnnotation.isPresent()) {
            return ApiRequestConverter.create(apiRequestAnnotation.get(), gson);
        } else {
            return null;
        }

    }

}
