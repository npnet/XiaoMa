package com.xiaoma.carpark.webview.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.xiaoma.carpark.R;
import com.xiaoma.carpark.webview.manager.WebviewManager;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ShareUtils;
import com.xiaoma.utils.log.KLog;

import java.util.Timer;

/**
 * Created by Thomas on 2019/5/27 0027
 * h5功能插件页面加载
 */

public class WebviewActivity extends BaseActivity {

    public static final String TAG="[WebviewActivity]";
    BridgeWebView mBridgeWebView;
    FrameLayout fTransition;
    String command;

    private Timer timer;//计时器
    private long timeout = 10000;//超时时间
    private Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            KLog.i(TAG,"onPageStarted() Progress="+mBridgeWebView.getProgress());
            if(mBridgeWebView.getProgress() < 100) {
                showNoNetView();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);
        Log.i(TAG,"onCreate......");
        checkNet();
        initView();
        initData();
    }

    public void initView(){
        fTransition=findViewById(R.id.fl_transition);
        mBridgeWebView=findViewById(R.id.group_activity_web);
        mBridgeWebView.setBackgroundColor(0);
        mBridgeWebView.setBackgroundResource(R.drawable.webbackground);
        WebviewManager.getInstance().init(this,mBridgeWebView);
        mBridgeWebView.setWebViewClient(new BridgeWebViewClient(mBridgeWebView){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                KLog.i(TAG,"onPageStarted()");
                handler.sendEmptyMessageDelayed(1,timeout);
            }

            @Override
            public void onPageFinished(WebView view, String url){
                KLog.i(TAG,"onPageFinished()");
                fTransition.setVisibility(View.INVISIBLE);
                handler.removeMessages(1);
                super.onPageFinished(view,url);
            }
        });
    }

    public void initData(){
        command = getIntent().getStringExtra(ShareUtils.SHARE_KEY);
        if (!TextUtils.isEmpty(command)) {
            Log.i(TAG,"command="+command);
            WebviewManager.getInstance().toShareOrInvite(command);
            return;
        }
        WebviewManager.getInstance().loadUrl(WebviewManager.mainUrl);
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart......");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"onResume......");

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG,"onNewIntent......");
        command = intent.getStringExtra(ShareUtils.SHARE_KEY);
        if (!TextUtils.isEmpty(command)) {
            Log.i(TAG,"command="+command);
            WebviewManager.getInstance().toShareOrInvite(command);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"onPause......");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop......");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy......");
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        KLog.d(TAG, "noNetworkOnRetry()");
        if (NetworkUtils.isConnected(this)) {
            initData();
        }
    }
}
