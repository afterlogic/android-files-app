package com.afterlogic.aurora.drive._unrefactored.core.util.api;

import android.text.TextUtils;

import com.afterlogic.aurora.drive.model.error.ApiError;
import com.afterlogic.aurora.drive.model.error.ApiResponseError;
import com.afterlogic.aurora.drive._unrefactored.model.project7.ApiResponseP7;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by sashka on 21.03.16.
 * mail: sunnyday.development@gmail.com
 *
 * Deserialize api response.
 * Check on error and if not deserialize with default or custom deserializer.
 */
public class ApiResponseDeserializer implements JsonDeserializer<ApiResponseP7> {

    private Gson mResultGson;

    public ApiResponseDeserializer(Gson resultGson) {
        if (resultGson == null){
            mResultGson = new Gson();
        }else{
            mResultGson = resultGson;
        }
    }

    @Override
    public ApiResponseP7 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonElement result = json.getAsJsonObject().get("Result");

        boolean resultFalse = false;
        if (result.isJsonPrimitive() &&
                result.getAsJsonPrimitive().isBoolean() &&
                !result.getAsJsonPrimitive().getAsBoolean()){

            resultFalse = true;

            int errorCode = ApiError.ERROR_CODE_NOT_EXIST;
            if (json.getAsJsonObject().has("ErrorCode")){
                errorCode = json.getAsJsonObject().get("ErrorCode").getAsInt();
            }

            String errorMessage = null;
            if (json.getAsJsonObject().has("ErrorMessage")){
                errorMessage = json.getAsJsonObject().get("ErrorMessage").getAsString();
            }

            if (errorCode != ApiError.ERROR_CODE_NOT_EXIST || !TextUtils.isEmpty(errorMessage)){
                ApiError error = new ApiResponseError(errorCode, errorMessage);
                return new ApiResponseP7<>(error);
            }
        }

        try {
            return mResultGson.fromJson(json, typeOfT);
        }catch (JsonSyntaxException ex){
            if (resultFalse) {
                return new ApiResponseP7<>(new ApiResponseError(ApiResponseError.RESULT_FALSE, null));
            }else{
                throw ex;
            }
        }
    }
}
