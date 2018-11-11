package com.afterlogic.aurora.drive.presentation.modules.login.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import androidx.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afterlogic.aurora.drive.R;
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

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return vm.shouldOverrideUrlLoading(request.getUrl().toString())
                        || super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return vm.shouldOverrideUrlLoading(url)
                        || super.shouldOverrideUrlLoading(view, url);
            }


            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                vm.onWebViewError(errorResponse.getStatusCode());
            }

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                vm.onWebViewError(error.getErrorCode());
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                vm.onWebViewError(errorCode);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                vm.onWebViewError(error.getPrimaryError());
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

        webView.setBackgroundColor(ContextCompat.getColor(webView.getContext(), R.color.white));

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
