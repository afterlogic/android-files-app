package com.afterlogic.aurora.drive.presentation.modules.login.view;

import android.annotation.SuppressLint;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afterlogic.aurora.drive.presentation.modules.login.viewModel.LoginViewModel;

/**
 * Created by aleksandrcikin on 22.08.17.
 * mail: mail@sunnydaydev.me
 */

public class LoginBindings {

    @BindingAdapter("login_webClient")
    public static void bindWebClient(WebView webView, LoginViewModel vm) {
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                vm.onPageLoadingStarted(url);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                vm.onPageLoadingFinished(url);
            }

        });
    }

    @BindingAdapter("login_url")
    public static void bindUrl(WebView webView, String url) {
        String currentUrl = webView.getUrl();
        if (currentUrl == null || !currentUrl.equals(url)) {
            webView.loadUrl(url);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @BindingAdapter("login_webViewConfig")
    public static void bindWebViewConfig(WebView webView, boolean useConfig) {
        if (!useConfig) return;

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);

        settings.setUserAgentString(
                settings.getUserAgentString()
                        //.replace(" Mobile ", "")
                        .replace("; wv)", ")")

        );
    }
}
