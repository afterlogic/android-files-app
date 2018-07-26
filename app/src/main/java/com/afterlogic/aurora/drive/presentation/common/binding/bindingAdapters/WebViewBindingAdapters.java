package com.afterlogic.aurora.drive.presentation.common.binding.bindingAdapters;

import androidx.databinding.BindingAdapter;
import android.webkit.WebView;

import com.afterlogic.aurora.drive.core.common.interfaces.Consumer;
import com.afterlogic.aurora.drive.presentation.common.binding.commands.SimpleCommand;
import com.afterlogic.aurora.drive.presentation.common.binding.commands.WebViewGoBackCommand;

/**
 * Created by sunny on 04.09.17.
 * mail: mail@sunnydaydev.me
 */

public class WebViewBindingAdapters {

    @BindingAdapter("goBackCommand")
    public static void bindGoBackCommand(WebView webView, WebViewGoBackCommand command) {

        Consumer<Boolean> resultConsumer = command.handle();

        if (resultConsumer != null) {

            if (webView.canGoBack()) {

                webView.goBack();
                resultConsumer.consume(true);

            } else {

                resultConsumer.consume(false);

            }

        }

    }

    @BindingAdapter("reloadCommand")
    public static void bindReloadCommand(WebView webView, SimpleCommand command) {

        if (command.handle()) {

            webView.reload();

        }

    }

    @BindingAdapter("clearHistoryCommand")
    public static void bindClearHistoryCommand(WebView webView, SimpleCommand command) {

        if (command.handle()) {

            webView.clearHistory();

        }

    }

    @BindingAdapter("stopLoadingCommand")
    public static void bindStopLoadingCommand(WebView webView, SimpleCommand command) {

        if (command.handle()) {

            webView.stopLoading();

        }

    }

}
