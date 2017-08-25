package com.afterlogic.aurora.drive.presentation.modules.login.v2.view;

import android.annotation.SuppressLint;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.os.Build;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afterlogic.aurora.drive.presentation.modules.login.v2.viewModel.LoginViewModel;

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
    public static void bindWebViewConfig(WebView view, boolean useConfig) {
        if (!useConfig) return;

        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(false);

        String userAgent = "Mozilla/5.0 (Linux; Android "
                + Build.VERSION.RELEASE
                + "; " + Build.MODEL +") "
                + " AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19";
        settings.setUserAgentString(userAgent);
    }
}
