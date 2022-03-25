package com.xiaoma.network;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.config.bean.EnvType;
import com.xiaoma.network.callback.BaseCallback;
import com.xiaoma.network.callback.FileCallback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.convert.FileConvert;
import com.xiaoma.network.engine.IHttpEngine;
import com.xiaoma.network.interceptor.LoggingInterceptor;
import com.xiaoma.network.interceptor.TokenInterceptor;
import com.xiaoma.network.model.HttpParams;
import com.xiaoma.network.utils.HttpUtils;
import com.xiaoma.network.utils.HttpsUtils;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpEngineImpl implements IHttpEngine {
    private static final String TAG = "HttpEngineImpl";
    // 缓存目录
    private static final String CACHE_PATH = "/HttpCache";
    // 缓存大小
    private static final long CACHE_SIZE_BYTES = 16 * 1024 * 1024;

    private static volatile IHttpEngine sInstance;

    private Context mContext;

    public static IHttpEngine instance() {
        if (sInstance != null) {
            return sInstance;
        } else {
            synchronized (HttpEngineImpl.class) {
                if (sInstance == null) {
                    sInstance = new HttpEngineImpl();
                }
                return sInstance;
            }
        }
    }

    private OkHttpClient mHttpClient;
    private OkHttpClient mDownloadClient;

    private final Map<String, Object> mCommonParams = new ArrayMap<>();
    private final Map<String, String> mCommonHeaders = new ArrayMap<>();
    private final Map<Object, Call> mCallMap = new ArrayMap<>();

    private HttpEngineImpl() {
    }

    @Override
    public void init(Context context) {
        if (mContext != null)
            return;
        initCommonParams(context);
        mContext = context.getApplicationContext();//save application  context
    }

    // 公共参数
    private void initCommonParams(Context context) {
        String channelId = ConfigManager.ApkConfig.getChannelID();
        String versionCode = String.valueOf(ConfigManager.ApkConfig.getBuildVersionCode());
        String iccid = ConfigManager.DeviceConfig.getICCID(context);
        String imei = ConfigManager.DeviceConfig.getIMEI(context);
        String osVersion = ConfigManager.DeviceConfig.getOSVersion();
        String deviceModel = ConfigManager.DeviceConfig.getDeviceModel();

        Map<String, Object> params = mCommonParams;
        params.put("channelId", channelId);
        params.put("versionCode", versionCode);
        params.put("iccid", iccid);
        params.put("gimei", imei);
        params.put("osVersion", osVersion);
        params.put("deviceModel", deviceModel);
        params.put("packageName", context.getPackageName());
    }

    @Override
    public void cancelTag(Object tag) {
        if (tag == null)
            return;
        Call call = mCallMap.get(tag);
        if (call == null)
            return;
        call.cancel();
        mCallMap.remove(tag);
    }

    @Override
    public void cancelAll() {
        getHttpClient().dispatcher().cancelAll();
        mCallMap.clear();
    }

    /**
     * 该方法已废弃,具体原因参考{@link com.xiaoma.network.XmHttp#setRetryCount(int)}
     */
    @Deprecated
    @Override
    public void setRetryCount(int retryCount) {
        // 已废弃
    }

    @Override
    public void addCommonParams(Map<String, String> params) {
        mCommonParams.putAll(params);
    }

    @Override
    public void clearCommonParams() {
        mCommonParams.clear();
    }

    @Override
    public void addCommonParams(String key, String value) {
        mCommonParams.put(key, value);
    }

    @Override
    public void removeCommonParams(String key) {
        mCommonParams.remove(key);
    }

    @Override
    public void addCommonHeader(String name, String value) {
        mCommonHeaders.put(name, value);
    }

    @Override
    public void addCommonHeaders(Map<String, String> params) {
        mCommonHeaders.putAll(params);
    }

    @Override
    public void removeCommonHeader(String name) {
        mCommonHeaders.remove(name);
    }

    @Override
    public void clearCommonHeaders() {
        mCommonHeaders.clear();
    }

    @Override
    public void getString(String url, StringCallback callBack) {
        getString(url, null, callBack);
    }

    @Override
    public void getString(String url, Map<String, Object> params, StringCallback callBack) {
        getString(url, params, null, callBack);
    }

    @Override
    public void getString(String url, Map<String, Object> params, String tag, StringCallback callBack) {
        getString(url, params, tag, callBack, Priority.NORMAL);
    }

    @Override
    public void getString(String url, Map<String, Object> params, StringCallback callBack, Priority priority) {
        getString(url, params, null, callBack, priority);
    }

    @Override
    public void getString(String url, Map<String, Object> params, String tag, StringCallback callBack, Priority priority) {
        Request.Builder reqBuilder = new Request.Builder()
                .url(urlBuilder(url, params).build())
                .get();
        if (tag != null) {
            reqBuilder.tag(tag);
        }
        doRequest(reqBuilder.build(), callBack, new OkCallback(callBack));
    }

    @Override
    public void postString(String url, Map<String, Object> params, StringCallback callBack) {
        postString(url, params, null, callBack);
    }

    @Override
    public void postString(String url, Map<String, Object> params, String tag, StringCallback callBack) {
        postString(url, params, tag, callBack, Priority.NORMAL);
    }

    @Override
    public void postString(String url, Map<String, Object> params, StringCallback callBack, Priority priority) {
        postString(url, params, null, callBack, priority);
    }

    @Override
    public void postString(String url, Map<String, Object> params, String tag, StringCallback callBack, Priority priority) {
        Request.Builder reqBuilder = new Request.Builder()
                .url(url)
                .headers(Headers.of(mCommonHeaders))
                .post(formBuilder(params).build());
        if (tag != null) {
            reqBuilder.tag(tag);
        }
        doRequest(reqBuilder.build(), callBack, new OkCallback(callBack));
    }

    @Override
    public void postJsonString(String url, Object object, StringCallback callBack) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .addPart(formBuilder(null).build())
                .addPart(MultipartBody.create(HttpParams.MEDIA_TYPE_JSON, GsonHelper.toJson(object)));

        Request.Builder reqBuilder = new Request.Builder()
                .url(url)
                .headers(Headers.of(mCommonHeaders))
                .post(bodyBuilder.build());
        doRequest(reqBuilder.build(), callBack, new OkCallback(callBack));
    }

    private HttpUrl.Builder urlBuilder(String url, Map<String, Object> exParams) {
        HttpUrl oriUrl = HttpUrl.parse(url);
        if (oriUrl == null) {
            throw new IllegalArgumentException(String.format("Invalid url: %s", url));
        }
        HttpUrl.Builder urlBuilder = oriUrl.newBuilder();
        // 请求参数
        Map<String, Object> reqParams = genParams(exParams);
        for (Map.Entry<String, Object> entry : reqParams.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return urlBuilder;
    }

    private FormBody.Builder formBuilder(Map<String, Object> exParams) {
        Map<String, Object> postParams = genParams(exParams);
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, Object> entry : postParams.entrySet()) {
            builder.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return builder;
    }

    private Map<String, Object> genParams(Map<String, Object> exParams) {
        Map<String, Object> params = new ArrayMap<>();
        params.putAll(mCommonParams);
        if (exParams != null) {
            params.putAll(exParams);
        }
        // 添加请求Id,方便后台定位请求
        try {
            String reqId = UUID.randomUUID().toString();
            if (!TextUtils.isEmpty(reqId)) {
                params.put("reqId", reqId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    @Override
    public void getFile(String url, FileCallback callBack) {
        getFile(url, null, callBack);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, FileCallback callBack) {
        getFile(url, params, null, callBack);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, String tag, FileCallback callBack) {
        getFile(url, params, tag, callBack, Priority.NORMAL);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, FileCallback callBack, Priority priority) {
        getFile(url, params, null, callBack, priority);
    }

    @Override
    public void getFile(String url, Map<String, Object> params, String tag, final FileCallback callBack, Priority priority) {
        Request.Builder reqBuilder = new Request.Builder()
                .get()
                .url(urlBuilder(url, params).build())
                .headers(Headers.of(mCommonHeaders));
        if (tag != null) {
            reqBuilder.tag(tag);
        }
        doRequest(reqBuilder.build(), callBack, new OkFileCallback(callBack), true);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, StringCallback callBack) {
        upFile(url, params, file, null, callBack);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, String tag, StringCallback callBack) {
        upFile(url, params, file, tag, callBack, Priority.NORMAL);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, StringCallback callBack, Priority priority) {
        upFile(url, params, file, null, callBack, priority);
    }

    @Override
    public void upFile(String url, Map<String, Object> params, File file, String tag, StringCallback callBack, Priority priority) {
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder()
                .addFormDataPart("img", file.getName(), RequestBody.create(HttpParams.MEDIA_TYPE_STREAM, file));

        Map<String, Object> postParams = genParams(params);
        if (postParams != null) {
            for (Map.Entry<String, Object> entry : postParams.entrySet()) {
                bodyBuilder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }

        Request.Builder reqBuilder = new Request.Builder()
                .url(url)
                .post(bodyBuilder.build())
                .headers(Headers.of(mCommonHeaders));
        if (tag != null) {
            reqBuilder.tag(tag);
        }

        doRequest(reqBuilder.build(), callBack, new OkCallback(callBack));
    }

    public OkHttpClient getHttpClient() {
        if (mHttpClient == null) {
            synchronized (this) {
                if (mHttpClient == null) {
                    // https
                    Resources res = mContext.getResources();
                    EnvType envType = ConfigManager.EnvConfig.getEnvType();
                    InputStream clientInput;
                    InputStream certInput;
                    if (envType.equals(EnvType.OFFICE)) {
                        clientInput = res.openRawResource(R.raw.xmlx_client_office);
                        certInput = res.openRawResource(R.raw.xmlx_cacert_office);
                    } else {
                        clientInput = res.openRawResource(R.raw.xmlx_client_exp);
                        certInput = res.openRawResource(R.raw.xmlx_cacert_exp);
                    }
                    HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(
                            clientInput,
                            NetworkConstants.CLIENT_PASSWORD,
                            certInput);
                    //Cache
                    File cacheFile = new File(mContext.getFilesDir(), CACHE_PATH);

                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                            .cache(new Cache(cacheFile, CACHE_SIZE_BYTES))
                            .hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier)
                            .addInterceptor(new TokenInterceptor());
                    // Log
                    if (BuildConfig.DEBUG) {
                        LoggingInterceptor logInterceptor = new LoggingInterceptor(mContext, NetworkConstants.LOG_NAME);
                        logInterceptor.setPrintLevel(LoggingInterceptor.Level.BODY);
                        logInterceptor.setColorLevel(NetworkConstants.LOG_LEVEL);
                        builder.addNetworkInterceptor(logInterceptor);
                    }

                    mHttpClient = builder.build();
                    try {
                        clientInput.close();
                    } catch (Exception ignored) {
                    }
                    try {
                        certInput.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        return mHttpClient;
    }

    @Override
    public OkHttpClient getDownloadClient() {
        if (mDownloadClient == null) {
            synchronized (this) {
                if (mDownloadClient == null) {
                    // 下载用单独的Client
                    mDownloadClient = new OkHttpClient.Builder().build();
                }
            }
        }
        return mDownloadClient;
    }

    private <T> void doRequest(Request request, final BaseCallback<T> xmCallback, Callback okCallback) {
        doRequest(request, xmCallback, okCallback, false);
    }

    private <T> void doRequest(Request request, final BaseCallback<T> xmCallback, Callback okCallback, boolean isDownload) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, String.format("doRequest: { url: %s }", request.url().uri()));
        }
        if (xmCallback != null) {
            runOnMain(new Runnable() {
                @Override
                public void run() {
                    xmCallback.onStart();
                }
            });
        }
        OkHttpClient client = isDownload ? getDownloadClient() : getHttpClient();
        final Call call = client.newCall(request);

        //下面这段代码，为了统一管理 访客模式（无用户信息）下，无法使用在线功能， 所以拦截住请求发送，直接回调异常
        if (!LoginTypeManager.getInstance().canSendRequest(mContext)) {
            if (xmCallback != null) {
                runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        final com.xiaoma.network.model.Response<T> resp =
                                com.xiaoma.network.model.Response.error(
                                        false, call, null, new NetworkErrorException("Cannot access network in guest mode !!!"));
                        xmCallback.onError(resp);
                        xmCallback.onFinish();
                    }
                });
            }
            return;
        }

        Object tag = request.tag();
        if (tag != null) {
            Call lastCall = mCallMap.get(tag);
            if (lastCall != null) {
                lastCall.cancel();
            }
            mCallMap.put(tag, call);
        }
        call.enqueue(okCallback);
    }

    private <T> void doRequestFinish(Call call, final BaseCallback<T> xmCallback) {
        Request req;
        Object tag;
        if ((req = call.request()) != null
                && (tag = req.tag()) != null) {
            mCallMap.remove(tag);
        }
        if (xmCallback != null) {
            runOnMain(new Runnable() {
                @Override
                public void run() {
                    xmCallback.onFinish();
                }
            });
        }

    }

    class OkCallback implements Callback {
        private StringCallback mInnerCallback;

        OkCallback(StringCallback innerCallback) {
            mInnerCallback = innerCallback;
        }

        @Override
        public void onResponse(final Call call, final Response response) {
            if (mInnerCallback == null)
                return;
            boolean callOnFail = true;
            Exception ex = null;
            if (200 == response.code()) {
                String bodyContent = null;
                try {
                    if (response.body() != null) {
                        bodyContent = response.body().string();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ex = e;
                }
                if (!TextUtils.isEmpty(bodyContent)) {
                    final com.xiaoma.network.model.Response<String> resp = com.xiaoma.network.model.Response
                            .success(false, bodyContent, call, response);
                    runOnMain(new Runnable() {
                        @Override
                        public void run() {
                            mInnerCallback.onSuccess(resp);
                        }
                    });
                    doRequestFinish(call, mInnerCallback);
                    callOnFail = false;
                }
            }
            if (callOnFail) {
                if (ex instanceof IOException) {
                    onFailure(call, (IOException) ex);
                } else {
                    onFailure(call, new IOException(String.valueOf(ex)));
                }
            }
        }

        @Override
        public void onFailure(final Call call, IOException e) {
            if (mInnerCallback == null)
                return;
            final com.xiaoma.network.model.Response<String> resp = com.xiaoma.network.model.Response
                    .error(false, call, null, e);
            runOnMain(new Runnable() {
                @Override
                public void run() {
                    mInnerCallback.onError(resp);
                }
            });
            doRequestFinish(call, mInnerCallback);
        }
    }

    class OkFileCallback implements Callback {
        private FileCallback mInnerCallback;

        OkFileCallback(FileCallback innerCallback) {
            mInnerCallback = innerCallback;
        }

        @Override
        public void onResponse(final Call call, Response response) {
            Log.i(TAG, String.format("onResponse: Download begin... url: %s", call.request().url()));
            InputStream input = null;
            FileOutputStream output = null;
            try {
                ResponseBody body;
                if ((body = response.body()) != null
                        && (input = body.byteStream()) != null) {
                    long t0 = System.currentTimeMillis();
                    File downloadFile = downFile(response);
                    output = new FileOutputStream(downloadFile);
                    byte[] buf = new byte[8 * 1024];
                    int len;
                    long writeLen = 0;
                    long totalLen = body.contentLength();
                    if (totalLen <= 0) {
                        totalLen = Integer.MAX_VALUE;
                    }
                    dispatchProgress(writeLen, totalLen);
                    while ((len = input.read(buf)) != -1) {
                        final long lastProgress = writeLen * 100 / totalLen;
                        output.write(buf, 0, len);
                        writeLen += len;
                        final long curProgress = writeLen * 100 / totalLen;
                        // 下载进度百分比回调
                        if (curProgress != lastProgress) {
                            dispatchProgress(writeLen, totalLen);
                        }
                    }

                    long t1 = System.currentTimeMillis();
                    Log.i(TAG, String.format("onResponse: Download end, file: < %s >, timeUsage: %s ms", downloadFile.getPath(), t1 - t0));

                    if (mInnerCallback != null) {
                        final com.xiaoma.network.model.Response<File> resp = com.xiaoma.network.model.Response.success(false, downloadFile, call, response);
                        final long finalTotalLen = totalLen;
                        final long finalWriteLen = writeLen;
                        runOnMain(new Runnable() {
                            @Override
                            public void run() {
                                if (Integer.MAX_VALUE == finalTotalLen) {
                                    mInnerCallback.onProgress(finalWriteLen, finalTotalLen);
                                }
                                mInnerCallback.onSuccess(resp);
                            }
                        });
                    }
                    doRequestFinish(call, mInnerCallback);
                } else {
                    onFailure(call, new IOException(""));
                }
            } catch (IOException e) {
                onFailure(call, e);
            } finally {
                if (output != null)
                    try {
                        output.close();
                    } catch (IOException ignored) {
                    }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }

        @Override
        public void onFailure(final Call call, IOException e) {
            Log.i(TAG, String.format("onFailure: Download Failed !!! url: %s", call.request().url()));
            if (mInnerCallback != null) {
                final com.xiaoma.network.model.Response<File> resp = com.xiaoma.network.model.Response.error(false, call, null, e);
                runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        mInnerCallback.onError(resp);
                    }
                });
            }
            doRequestFinish(call, mInnerCallback);
        }

        private File downFile(okhttp3.Response response) {
            String destFileDir = mInnerCallback.getDestFileDir();
            if (TextUtils.isEmpty(destFileDir)) {
                destFileDir = Environment.getExternalStorageDirectory() + FileConvert.DM_TARGET_FOLDER;
            }

            String destFileName = mInnerCallback.getDestFileName();
            if (TextUtils.isEmpty(destFileName)) {
                String url = response.request().url().toString();
                destFileName = HttpUtils.getNetFileName(response, url);
            }
            return new File(destFileDir, destFileName);
        }

        private void dispatchProgress(final long curLen, final long totalLen) {
            Log.i(TAG, String.format("dispatchProgress: Download progress( %s / %s )", curLen, totalLen));
            if (mInnerCallback != null) {
                runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        mInnerCallback.onProgress(curLen, totalLen);
                    }
                });
            }
        }
    }

    private void runOnMain(Runnable r) {
        ThreadDispatcher.getDispatcher().runOnMain(r);
    }
}
