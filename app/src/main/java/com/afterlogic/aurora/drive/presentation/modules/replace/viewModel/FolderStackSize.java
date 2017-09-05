package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

public class FolderStackSize {

    private final String type;
    private final int depth;

    public FolderStackSize(String type, int depth) {
        this.type = type;
        this.depth = depth;
    }

    public String getType() {
        return type;
    }

    public int getDepth() {
        return depth;
    }
}
