package com.afterlogic.aurora.drive._unrefactored.data.common;

import java.util.HashMap;

/**
 * Created by sashka on 28.10.16.<p/>
 * mail: sunnyday.development@gmail.com
 */
public class ParamsBuilder {
    private HashMap<String, Object> mParams = new HashMap<>();

    public ParamsBuilder put(String field, Object value) {
        if (value != null) {
            mParams.put(field, value);
        }
        return this;
    }

    public HashMap<String, Object> create() {
        return mParams;
    }
}
