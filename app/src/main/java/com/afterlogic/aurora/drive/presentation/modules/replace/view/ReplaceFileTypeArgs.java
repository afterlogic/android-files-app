package com.afterlogic.aurora.drive.presentation.modules.replace.view;

import android.os.Bundle;

import com.afterlogic.aurora.drive.presentation.common.modules.v3.view.Args;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class ReplaceFileTypeArgs extends Args{

    private static final String KEY_TYPE = ReplaceFileTypeArgs.class.getName() + ".type";

    public ReplaceFileTypeArgs(Bundle args) {
        super(args);
    }

    public String getType() {
        return getBundle().getString(KEY_TYPE);
    }

    public static class Builder {
        private final Bundle args = new Bundle();

        public Builder setType(String type) {
            args.putString(KEY_TYPE, type);
            return this;
        }

        public ReplaceFileTypeArgs build() {
            return new ReplaceFileTypeArgs(args);
        }
    }
}
