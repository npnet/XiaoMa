package com.xiaoma.club.msg.chat.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.xiaoma.club.R;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.ui.toast.XMToast;

/**
 * Author: loren
 * Date: 2019/2/11 0011
 */

public class GroupActivityWeb extends BaseActivity {

    private static final String EXTRA_URL = "group_activity_url";
    WebView webView;

    public static void start(Context context, String url) {
        context.startActivity(new Intent(context, GroupActivityWeb.class)
                .putExtra(EXTRA_URL, url));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_group_activity);
        initWeb();
    }

    private void initWeb() {
        final String url = getIntent().getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(url)) {
            XMToast.toastException(this, R.string.enable_activity_url);
            finish();
            return;
        }
        webView = findViewById(R.id.group_activity_web);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webView.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
}
