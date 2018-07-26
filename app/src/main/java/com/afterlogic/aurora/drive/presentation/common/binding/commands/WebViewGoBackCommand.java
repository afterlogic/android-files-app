package com.afterlogic.aurora.drive.presentation.common.binding.commands;

import androidx.databinding.BaseObservable;
import androidx.annotation.Nullable;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;

/**
 * Created by sunny on 04.09.17.
 * mail: mail@sunnydaydev.me
 */

public class WebViewGoBackCommand extends BaseObservable {

    private Consumer<Boolean> resultConsumer;

    public void goBack(Consumer<Boolean> resultConsumer) {

        this.resultConsumer = resultConsumer;
        notifyChange();

    }

    @Nullable
    public Consumer<Boolean> handle() {

        return resultConsumer;

    }

}
