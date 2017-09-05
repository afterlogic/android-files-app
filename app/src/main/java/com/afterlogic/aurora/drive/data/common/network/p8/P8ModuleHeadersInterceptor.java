package com.afterlogic.aurora.drive.data.common.network.p8;

import android.support.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by sunny on 31.08.17.
 *
 */

class P8ModuleHeadersInterceptor implements Interceptor {

    @Inject
    P8ModuleHeadersInterceptor() {
    }

    @Override
    public Response intercept(@NonNull  Chain chain) throws IOException {

        Request request = chain.request();

        Request resultRequest;

        Map<String, String> xHeaders = Stream.of(request.headers().names())
                .filter(name -> name.startsWith(Api8.DEFAULT_FIELD_CRUNCH))
                .collect(Collectors.toMap(
                        name -> name,
                        request::header
                ));


        if (xHeaders.size() > 0) {


            Request.Builder newRequestBuilder = request.newBuilder();

            for (Map.Entry<String, String> entry: xHeaders.entrySet()) {

                newRequestBuilder.removeHeader(entry.getKey());

            }

            RequestBody newRequestBody = getNewRequestBody(request, xHeaders);

            switch (request.method()) {

                case "POST":
                    newRequestBuilder.post(newRequestBody);
                    break;

                case "PUT":
                    newRequestBuilder.put(newRequestBody);
                    break;

                default:
                    throw new RuntimeException("Unhandled request method.");

            }

            resultRequest = newRequestBuilder.build();

        } else {

            resultRequest = request;

        }

        return chain.proceed(resultRequest);
    }

    private RequestBody getNewRequestBody(Request request, Map<String, String> headers) {

        RequestBody currentRequestBody = request.body();

        if (currentRequestBody instanceof FormBody) {

            return updateFormEncoded((FormBody) currentRequestBody, headers);

        } else if (currentRequestBody instanceof MultipartBody){

            return updateMultipart((MultipartBody) currentRequestBody, headers);

        } else {

            return createFormBody(headers);

        }

    }

    private FormBody updateFormEncoded(FormBody current, Map<String, String> headers) {

        FormBody.Builder builder = new FormBody.Builder();

        for (Map.Entry<String, String> entry: headers.entrySet()) {

            String name = entry.getKey().replace(Api8.DEFAULT_FIELD_CRUNCH,"");
            builder = builder.add(name, entry.getValue());

        }

        for (int i = 0; i < current.size(); i++) {

            builder = builder.addEncoded(current.encodedName(i), current.encodedValue(i));

        }

        return builder.build();
    }

    private MultipartBody updateMultipart(MultipartBody current, Map<String, String> headers) {
        MultipartBody.Builder newBuilder = new MultipartBody.Builder();

        newBuilder.setType(MultipartBody.FORM);

        for (Map.Entry<String, String> entry: headers.entrySet()) {

            String name = entry.getKey().replace(Api8.DEFAULT_FIELD_CRUNCH,"");
            newBuilder.addPart(MultipartBody.Part.createFormData(
                    name,
                    entry.getValue()
            ));

        }

        for (MultipartBody.Part part: current.parts()) {

            newBuilder.addPart(part);

        }

        return newBuilder.build();
    }

    private FormBody createFormBody(Map<String, String> headers) {

        FormBody.Builder builder = new FormBody.Builder();

        for (Map.Entry<String, String> entry: headers.entrySet()) {

            String name = entry.getKey().replace(Api8.DEFAULT_FIELD_CRUNCH,"");
            builder = builder.add(name, entry.getValue());

        }

        return builder.build();
    }
}
