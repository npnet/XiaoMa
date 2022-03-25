package com.xiaoma.network;

import android.app.Application;
import android.content.Context;

import com.xiaoma.network.callback.FileCallback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.engine.IHttpEngine;
import com.xiaoma.network.engine.OkGo;
import com.xiaoma.thread.Priority;

import java.io.File;
import java.net.ProxySelector;
import java.util.Map;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;


/**
 * <pre>
 *     author : wutao
 *     e-mail : ldlywt@163.com
 *     time   : 2018/08/31
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class XmHttp implements IHttpEngine {
    private static volatile XmHttp sInstance;

    public static XmHttp getDefault() {
        if (sInstance != null) {
            return sInstance;
        } else {
            synchronized (XmHttp.class) {
                if (sInstance == null) {
                    sInstance = new XmHttp();
                }
                return sInstance;
            }
        }
    }

    private final IHttpEngine httpEngine;
    private boolean mInit;

    private XmHttp() {
        httpEngine = HttpEngineImpl.instance();
    }

    @Override
    public synchronized void init(Context context) {
        if (mInit)
            return;
        mInit = true;
        if (context == null) {
            throw new RuntimeException("XmHttp Context is null");
        }
        httpEngine.init(context);
        // 由于下载上传等模块用到了OkGo,因此还需要初始化
        OkGo okGo = OkGo.getInstance();
        okGo.init((Application) context.getApplicationContext());
        okGo.setOkHttpClient(httpEngine.getDownloadClient());
    }

    @Override
    public void cancelTag(Object tag) {
        httpEngine.cancelTag(tag);
    }

    @Override
    public void cancelAll() {
        httpEngine.cancelAll();
    }

    /**
     * <p>不支持设置重试次数,框架内部会自动重试,并且根据不同的错误选择不同的重试机制,应用层不需要关心.
     * <p>具体原因可参考{@link OkHttpClient.Builder#retryOnConnectionFailure(boolean)}中关于重试的描述:
     * <br/><br/>
     * Configure this client to retry or not when a connectivity problem is encountered. By default,
     * this client silently recovers from the following problems:
     * <ul>
     * <li><strong>Unreachable IP addresses.</strong> If the URL's host has multiple IP addresses,
     * failure to reach any individual IP address doesn't fail the overall request. This can
     * increase availability of multi-homed services.
     * <li><strong>Stale pooled connections.</strong> The {@link ConnectionPool} reuses sockets
     * to decrease request latency, but these connections will occasionally time out.
     * <li><strong>Unreachable proxy servers.</strong> A {@link ProxySelector} can be used to
     * attempt multiple proxy servers in sequence, eventually falling back to a direct
     * connection.
     * </ul>
     * Set this to false to avoid retrying requests when doing so is destructive. In this case the
     * calling application should do its own recovery of connectivity failures.
     */
    @Deprecated
    @Override
    public void setRetryCount(int retryCount) {
        httpEngine.setRetryCount(retryCount);
    }

    @Override
    public void addCommonParams(Map<String, String> params) {
        httpEngine.addCommonParams(params);
    }

    @Override
    public void clearCommonParams() {
        httpEngine.clearCommonParams();
    }

    @Override
    public void addCommonParams(String key, String value) {
        httpEngine.addCommonParams(key, value);
    }

    @Override
    public void removeCommonParams(String key) {
        httpEngine.removeCommonParams(key);
    }

    @Override
    public void addCommonHeader(String name, String value) {
        httpEngine.addCommonHeader(name, value);
    }

    @Override
    public void addCommonHeaders(Map<String, String> params) {
        httpEngine.addCommonHeaders(params);
    }

    @Override
    public void removeCommonHeader(String name) {
        httpEngine.removeCommonHeader(name);
    }

    @Override
    public void clearCommonHeaders() {
        httpEngine.clearCommonHeaders();
    }

    @Override
    public OkHttpClient getHttpClient() {
        return httpEngine.getHttpClient();
    }

    @Override
    public OkHttpClient getDownloadClient() {
        return httpEngine.getDownloadClient();
    }

    @Override
    public void getString(String url, StringCallback callBack) {
        httpEngine.getString(url, callBack);
    }

    @Override
    public void getString(String url, Map<String, Object> params, StringCallback callBack) {
        httpEngine.getString(url, params, callBack);
    }

    @Override
    public void getString(String url, Map<String, Object> params, String tag, StringCallback callBack) {
        httpEngine.getString(url, params, tag, callBack);
    }

    @Override
    public void getString(String url, Map<String, Object> params, StringCallback callBack, Priority priority) {
        httpEngine.getString(url, params, callBack, priority);
    }

    @Override
    public void getString(String url, Map<String, Object> params, String tag, StringCallback callBack, Priority priority) {
        httpEngine.getString(url, params, tag, callBack, priority);
    }

    @Override
    public void postString(String url, Map<String, Object> params, StringCallback callBack) {
        httpEngine.postString(url, params, callBack);
    }

    @Override
    public void postString(String url, Map<String, Object> params, String tag, StringCallback callBack) {
        httpEngine.postString(url, params, tag, callBack);
    }

    @Override
    public void postString(String url, Map<String, Object> params, StringCallback callBack, Priority priority) {
        httpEngine.postString(url, params, callBack, priority);
    }

    @Override
    public void postString(String url, Map<String, Object> params, String tag, StringCallback callBack, Priority priority) {
        httpEngine.postString(url, params, tag, callBack, priority);
    }

    @Override
    public void postJsonString(String url, Object object, StringCallback callBack) {
        httpEngine.postJsonString(url, object, callBack);
    }

    @Override
    public void getFile(String url, FileCallback callBack) {
        httpEngine.getFile(url, callBack);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, FileCallback callBack) {
        httpEngine.getFile(url, params, callBack);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, String tag, FileCallback callBack) {
        httpEngine.getFile(url, params, tag, callBack);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, FileCallback callBack, Priority priority) {
        httpEngine.getFile(url, params, callBack, priority);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, String tag, FileCallback callBack, Priority priority) {
        httpEngine.getFile(url, params, tag, callBack, priority);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, StringCallback callBack) {
        httpEngine.upFile(url, params, file, callBack);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, String tag, StringCallback callBack) {
        httpEngine.upFile(url, params, file, tag, callBack);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, StringCallback callBack, Priority priority) {
        httpEngine.upFile(url, params, file, callBack, priority);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, String tag, StringCallback callBack, Priority priority) {
        httpEngine.upFile(url, params, file, tag, callBack, priority);
    }
}
