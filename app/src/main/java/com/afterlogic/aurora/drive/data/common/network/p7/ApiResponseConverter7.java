package com.afterlogic.aurora.drive.data.common.network.p7;

import com.afterlogic.aurora.drive.data.model.project7.ApiResponseP7;
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
        JsonObject response = json.getAsJsonObject();

        checkAndHandleError(response);

        checkAndHandleFalseResult(response);

        if (response.has(ApiResponseP7.TAG_ERROR_CODE)) {
            response.remove(ApiResponseP7.TAG_RESULT);
        }

        MyLog.d(this, "Final response: " + response.toString());

        return mGson.fromJson(response, typeOfT);
    }

    private void checkAndHandleError(JsonObject response) {
        if (response.has("Error")){
            JsonElement error = response.get("Error");
            if (error.isJsonPrimitive() && error.getAsJsonPrimitive().isString()){
                response.addProperty(ApiResponseP7.TAG_ERROR_CODE, 999);
                response.addProperty(ApiResponseP7.TAG_ERROR_MESSAGE, "Error: " + error.getAsString());
            } else if (error.isJsonPrimitive() && error.getAsJsonPrimitive().isNumber()){
                response.addProperty(ApiResponseP7.TAG_ERROR_CODE, error.getAsInt());
            } else {
                response.addProperty(ApiResponseP7.TAG_ERROR_CODE, 999);
            }

            response.remove("Error");
        }
    }

    private void checkAndHandleFalseResult(JsonObject response) {
        if (response.has("Result")) {
            JsonElement result = response.get("Result");
            if (isBoolean(result) && !result.getAsBoolean()) {
                response.addProperty(ApiResponseP7.TAG_ERROR_CODE, 999);
            }
        }
    }

    private boolean isBoolean(JsonElement element) {
        return element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
    }
}
