package com.afterlogic.aurora.drive.data.modules.project8.common.converter;

import com.afterlogic.aurora.drive.model.project8.ApiResponseP8;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by sashka on 17.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ApiResponseConverter8 implements JsonDeserializer<ApiResponseP8<?>> {

    private Gson mGson;

    public ApiResponseConverter8(Gson gson) {
        mGson = gson;
    }

    @Override
    public ApiResponseP8<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject result = json.getAsJsonObject();

        if (result.has(ApiResponseP8.TAG_ERROR_CODE)) {
            result.remove(ApiResponseP8.TAG_RESULT);
        }

        return mGson.fromJson(result, typeOfT);
    }
}
