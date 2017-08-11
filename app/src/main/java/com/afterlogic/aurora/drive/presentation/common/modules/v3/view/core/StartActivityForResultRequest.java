package com.afterlogic.aurora.drive.presentation.common.modules.v3.view.core;

import android.content.Intent;

/**
 * Created by aleksandrcikin on 11.08.17.
 * mail: mail@sunnydaydev.me
 */

public class StartActivityForResultRequest {

    private final int requestId;

    private final Intent intent;

    public StartActivityForResultRequest(int requestId, Intent intent) {
        this.requestId = requestId;
        this.intent = intent;
    }

    public int getRequestId() {
        return requestId;
    }

    public Intent getIntent() {
        return intent;
    }
}
