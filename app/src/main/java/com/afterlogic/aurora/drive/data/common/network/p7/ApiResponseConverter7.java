package com.afterlogic.aurora.drive.data.common.network.p7;

import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;
import com.afterlogic.aurora.drive.core.common.logging.MyLog;
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
public class ApiResponseConverter7 implements JsonDeserializer<ApiResponseP7<?>> {

    private Gson mGson;

    public ApiResponseConverter7(Gson gson) {
        mGson = gson;
    }

    @Override
    public ApiResponseP7<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject result = json.getAsJsonObject();

        if (result.has("Error")){
            JsonElement error = result.get("Error");
            if (error.isJsonPrimitive() && error.getAsJsonPrimitive().isString()){
                result.addProperty(ApiResponseP7.TAG_ERROR_CODE, 999);
                result.addProperty(ApiResponseP7.TAG_ERROR_MESSAGE, "Error: " + error.getAsString());
            } else if (error.isJsonPrimitive() && error.getAsJsonPrimitive().isNumber()){
                result.addProperty(ApiResponseP7.TAG_ERROR_CODE, error.getAsInt());
            } else {
                result.addProperty(ApiResponseP7.TAG_ERROR_CODE, 999);
            }

            result.remove("Error");
        }

        if (result.has(ApiResponseP7.TAG_ERROR_CODE)) {
            result.remove(ApiResponseP7.TAG_RESULT);
        }

        MyLog.d(this, "Final response: " + result.toString());

        return mGson.fromJson(result, typeOfT);
    }
}
