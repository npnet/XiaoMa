package com.xiaoma.carpark.webview.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebSettings;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;
import com.xiaoma.carpark.BuildConfig;
import com.xiaoma.carpark.R;
import com.xiaoma.carpark.webview.model.ClubMessage;
import com.xiaoma.carpark.webview.model.JsMessage;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ShareUtils;
import com.xiaoma.utils.share.ShareCallBack;
import com.xiaoma.utils.share.ShareClubBean;
import com.xiaoma.utils.share.ShareConstants;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Thomas on 2019/5/27 0027
 */

public class WebviewManager {
    public static final String TAG="[WebviewManager]";

    public static final String SHARE_KEY="key:share";
    public static final String INVITE_KEY="key:invite";

    public static final String mainUrl="http://193.112.87.252:8008/#/";
//    public static final String mainUrl="http://192.168.0.130:8081/#/home";

    WeakReference<Context> mContextWeakReference;
    WeakReference<BridgeWebView> mWebViewWeakReference;

    ClubMessage clubMessage;

    private WebviewManager(){

    }

    public static class WebMapManagerHolder {
        static WebviewManager instance = new WebviewManager();
    }

    public static WebviewManager getInstance() {
        return WebMapManagerHolder.instance;
    }

    public void init(Context context, BridgeWebView webView){
        mContextWeakReference=new WeakReference<>(context);
        mWebViewWeakReference=new WeakReference<>(webView);
        initWebView();
    }

    public void initWebView(){
        BridgeWebView mWebView=mWebViewWeakReference.get();
        if(mWebView!=null){
            mWebView.setDefaultHandler(new DefaultHandler());
            setWebSetting(mWebView);
            registerHandler(mWebView);//注册被js调用的android方法
        }
    }

    public void loadUrl(String url){
        BridgeWebView mWebView=mWebViewWeakReference.get();
        if(mWebView!=null){
            mWebView.loadUrl(url);
        }
    }

    public void setWebSetting(BridgeWebView webView){
        if(webView==null){
            return;
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        }
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //关闭webview中缓存
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
    }

    /**
     * 注册被js调用的android方法
     * @param webView
     */
    public void registerHandler(BridgeWebView webView){
        if(webView==null){
            return;
        }
        webView.registerHandler("native_closed", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i(TAG,"handler data="+data+"Thread.currentThread()="+Thread.currentThread());
                if(Thread.currentThread()==Looper.getMainLooper().getThread()){
                    close();
                    return;
                }
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        close();
                    }
                });
            }
        });;

        webView.registerHandler("native_share", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i(TAG, "handler data=" + data);
                share(data);
            }
        });

        webView.registerHandler("native_invite", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                Log.i(TAG, "handler data=" + data);
                invite(data);
            }
        });

        webView.registerHandler("native_network_state", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                StringBuilder stringBuilder=new StringBuilder();

                NetworkState networkState=new NetworkState();
                networkState.state=networkState();
                stringBuilder.append(new Gson().toJson(networkState));
                Log.i(TAG,"stringBuilder="+stringBuilder.toString());
                function.onCallBack(stringBuilder.toString());
            }
        });

        webView.registerHandler("native_user_channel_id",new BridgeHandler(){
            @Override
            public void handler(String data, CallBackFunction function) {
                StringBuilder stringBuilder=new StringBuilder();
                UserInfo userC=new UserInfo();
                if (LoginManager.getInstance().isUserLogin()) {
                    User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
                    userC.userId=String.valueOf( user.getId());
                    userC.channelId=user.getChannelId();
                }else{
                    userC.channelId="AA1090";
                    userC.userId="847352519377166336";
                }
                stringBuilder.append(new Gson().toJson(userC));
                Log.i(TAG,"retData="+stringBuilder.toString());
                function.onCallBack(stringBuilder.toString());
            }
        });

        webView.registerHandler("native_get_message", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                StringBuilder stringBuilder=new StringBuilder();
                if(clubMessage!=null){
                    stringBuilder.append(GsonHelper.toJson(clubMessage));
                }
                Log.i(TAG,"str="+stringBuilder.toString());
                function.onCallBack(stringBuilder.toString());
            }
        });

        webView.registerHandler("native_get_environment", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                StringBuilder stringBuilder=new StringBuilder();
                if ("TEST".equals(BuildConfig.SERVICE_ENV)){
                    stringBuilder.append(1);
                }else if("EXP".equals(BuildConfig.SERVICE_ENV)){
                    stringBuilder.append(2);
                }else if("OFFICE".equals(BuildConfig.SERVICE_ENV)){
                    stringBuilder.append(3);
                }else {
                    stringBuilder.append(4);
                }
                Log.i(TAG,"str="+stringBuilder.toString());
                function.onCallBack(stringBuilder.toString());
            }
        });
    }

    class UserInfo{
        String userId;
        String channelId;
    }

    class NetworkState{
        boolean state;
    }

    public void close(){
        Log.i(TAG,"close()");
        Context mContext=mContextWeakReference.get();
        if(mContext!=null){
            ((Activity)mContext).finish();
        }
    }

    public void share(String data){
        Log.i(TAG,"share()");
        JsMessage jsMessage=new Gson().fromJson(data,JsMessage.class);
        Context mContext=mContextWeakReference.get();
        if(mContext!=null){
            ShareClubBean bean = new ShareClubBean();
            bean.setFromPackage(mContext.getPackageName());
            bean.setBackAction(ShareConstants.CAR_PARK_HANDLE_SHARE_ACTION);

            ClubMessage clubMessage=new ClubMessage();
            clubMessage.setKey(SHARE_KEY);
            clubMessage.setCurrentPath(jsMessage.getCurrentPath());
            clubMessage.setAvatarUrl(jsMessage.getAvatarUrl());
            clubMessage.setName(jsMessage.getName());
            clubMessage.setUid(jsMessage.getUid());
            bean.setCoreKey(GsonHelper.toJson(clubMessage));
            bean.setShareImage(jsMessage.getAvatarUrl());
            bean.setShareTitle(mContext.getResources().getString(R.string.share_title));
            bean.setShareContent(mContext.getResources().getString(R.string.share_content));
            ShareUtils.shareToClub(mContext, bean, new ShareCallBack() {
                @Override
                public void shareError(String errorMsg) {
                    Log.i(TAG,"shareError errorMsg="+errorMsg);
                }

                @Override
                public void shareSuccess() {
                    Log.i(TAG,"shareSuccess");
                }
            });
        }
    }

    public void invite(String data){
        Log.i(TAG,"invite()");
        JsMessage jsMessage=new Gson().fromJson(data,JsMessage.class);
        Context mContext=mContextWeakReference.get();
        if(mContext!=null){
            ShareClubBean bean = new ShareClubBean();
            bean.setFromPackage(mContext.getPackageName());
            bean.setBackAction(ShareConstants.CAR_PARK_HANDLE_SHARE_ACTION);

            ClubMessage clubMessage=new ClubMessage();
            clubMessage.setKey(INVITE_KEY);
            clubMessage.setCurrentPath(jsMessage.getCurrentPath());
            clubMessage.setAvatarUrl(jsMessage.getAvatarUrl());
            clubMessage.setName(jsMessage.getName());
            clubMessage.setUid(jsMessage.getUid());
            bean.setCoreKey(GsonHelper.toJson(clubMessage));
            bean.setShareImage(jsMessage.getAvatarUrl());
            bean.setShareTitle(mContext.getResources().getString(R.string.invite_title));
            bean.setShareContent(mContext.getResources().getString(R.string.invite_content));
            ShareUtils.shareToClub(mContext, bean, new ShareCallBack() {
                @Override
                public void shareError(String errorMsg) {
                    Log.i(TAG,"shareError errorMsg="+errorMsg);
                }

                @Override
                public void shareSuccess() {
                    Log.i(TAG,"shareSuccess");
                }
            });
        }
    }

    public boolean networkState(){
        Log.i(TAG,"networkState()");
        Context mContext=mContextWeakReference.get();
        if(mContext!=null){
            return NetworkUtils.isConnected(mContext);
        }
        return false;
    }

    public void toShareOrInvite(String data){
        Log.i(TAG,"shareOrInvite()");
        if (TextUtils.isEmpty(data)) {
            return;
        }
        clubMessage=GsonHelper.fromJson(data,ClubMessage.class);
        if(clubMessage==null){
            return;
        }
        loadUrl(clubMessage.getCurrentPath());
    }

    public static void hookWebView(){
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                Log.i(TAG,"sProviderInstance isn't null");
                return;
            }

            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                Log.i(TAG,"Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> factoryProviderClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> delegateConstructor = delegateClass.getDeclaredConstructor();
            delegateConstructor.setAccessible(true);
            if(sdkInt < 26){//低于Android O版本
                Constructor<?> providerConstructor = factoryProviderClass.getConstructor(delegateClass);
                if (providerConstructor != null) {
                    providerConstructor.setAccessible(true);
                    sProviderInstance = providerConstructor.newInstance(delegateConstructor.newInstance());
                }
            } else {
                Field chromiumMethodName = factoryClass.getDeclaredField("CHROMIUM_WEBVIEW_FACTORY_METHOD");
                chromiumMethodName.setAccessible(true);
                String chromiumMethodNameStr = (String)chromiumMethodName.get(null);
                if (chromiumMethodNameStr == null) {
                    chromiumMethodNameStr = "create";
                }
                Method staticFactory = factoryProviderClass.getMethod(chromiumMethodNameStr, delegateClass);
                if (staticFactory!=null){
                    sProviderInstance = staticFactory.invoke(null, delegateConstructor.newInstance());
                }
            }

            if (sProviderInstance != null){
                field.set("sProviderInstance", sProviderInstance);
                Log.i(TAG,"Hook success!");
            } else {
                Log.i(TAG,"Hook failed!");
            }
        } catch (Throwable e) {
            Log.w(TAG,e);
        }
    }
}
