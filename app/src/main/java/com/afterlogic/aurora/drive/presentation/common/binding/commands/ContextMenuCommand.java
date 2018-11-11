package com.afterlogic.aurora.drive.presentation.common.binding.commands;

import androidx.databinding.BaseObservable;
import androidx.annotation.Nullable;
import android.view.View;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.core.common.util.OptWeakRef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aleksandrcikin on 05.08.17.
 * mail: mail@sunnydaydev.me
 */

public class ContextMenuCommand extends BaseObservable {

    private ContextMenuRequest contextMenuRequest;

    public ContextMenuCommand() {

    }

    @Nullable
    public ContextMenuRequest getAndClear() {
        ContextMenuRequest current = contextMenuRequest;
        contextMenuRequest = null;
        return current;
    }

    public ContextMenuRequest.Builder fireFor(View view) {
        return new ContextMenuRequest.Builder(view, this::setContextMenuRequest);
    }

    public void clear() {
        contextMenuRequest = null;
    }

    private void setContextMenuRequest(ContextMenuRequest request) {
        contextMenuRequest = request;
        notifyChange();
    }

    public static class ContextMenuRequest {

        private final OptWeakRef<View> view;

        private final List<Action> actions;

        public ContextMenuRequest(OptWeakRef<View> view, List<Action> actions) {
            this.view = view;
            this.actions = actions;
        }

        @Nullable
        public View getView() {
            return view.get();
        }

        public List<Action> getActions() {
            return actions;
        }

        public static class Builder {

            private final View view;
            private final Consumer<ContextMenuRequest> onBuild;
            private List<Action> actions = new ArrayList<>();

            public Builder(View view, Consumer<ContextMenuRequest> onBuild) {
                this.view = view;
                this.onBuild = onBuild;
            }

            public Builder addAction(String text, Runnable onClick) {
                actions.add(new Action(text, onClick));
                return this;
            }

            public void buildAndSet() {
                onBuild.consume(new ContextMenuRequest(new OptWeakRef<>(view), actions));
            }
        }
    }

    public static class Action {

        private final String title;
        private final Runnable onClick;

        public Action(String title, Runnable onClick) {
            this.title = title;
            this.onClick = onClick;
        }

        public String getTitle() {
            return title;
        }

        public void onClick() {
            this.onClick.run();
        }
    }
}
