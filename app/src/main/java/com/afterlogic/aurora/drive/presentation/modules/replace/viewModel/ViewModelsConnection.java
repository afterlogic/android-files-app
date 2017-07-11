package com.afterlogic.aurora.drive.presentation.modules.replace.viewModel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.annotation.scopes.ModuleScope;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by aleksandrcikin on 04.07.17.
 * mail: mail@sunnydaydev.me
 */

@ModuleScope
class ViewModelsConnection {

    private Map<String, ReplaceFileTypeViewModel> filesListViewModels = new HashMap<>();

    private OnChangedListener listener;

    @Inject
    ViewModelsConnection() {
    }

    public void setListener(OnChangedListener listener) {
        this.listener = listener;
    }

    synchronized public void register(String type, @NonNull ReplaceFileTypeViewModel vm) {
        ReplaceFileTypeViewModel previous = filesListViewModels.remove(type);
        if (previous != null) {
            listener.onUnregistered(previous);
        }
        filesListViewModels.put(type, vm);
        if (listener != null) {
            listener.onRegistered(type, vm);
        }
    }

    synchronized public void unregister(@NonNull ReplaceFileTypeViewModel vm) {
        Iterator<Map.Entry<String, ReplaceFileTypeViewModel>> iterator  = filesListViewModels.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ReplaceFileTypeViewModel> entry = iterator.next();
            if (entry.getValue() == vm) {
                iterator.remove();
                if (listener != null) {
                    listener.onUnregistered(vm);
                }
            }
        }
    }

    @Nullable
    synchronized ReplaceFileTypeViewModel get(String type) {
        return filesListViewModels.get(type);
    }

    public interface OnChangedListener {
        void onRegistered(String type, ReplaceFileTypeViewModel vm);
        void onUnregistered(ReplaceFileTypeViewModel vm);
    }
}
