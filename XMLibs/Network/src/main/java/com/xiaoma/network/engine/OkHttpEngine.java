//package com.xiaoma.network.engine;
//
//import android.app.Application;
//import android.content.Context;
//import android.util.Log;
//
//import com.google.gson.reflect.TypeToken;
//import com.xiaoma.network.NetworkConstants;
//import com.xiaoma.network.R;
//import com.xiaoma.network.XmHttp;
//import com.xiaoma.network.callback.FileCallback;
//import com.xiaoma.network.callback.StringCallback;
//import com.xiaoma.network.interceptor.LoggingInterceptor;
//import com.xiaoma.network.interceptor.TokenInterceptor;
//import com.xiaoma.network.model.HttpHeaders;
//import com.xiaoma.network.model.HttpParams;
//import com.xiaoma.network.utils.HttpUtils;
//import com.xiaoma.network.utils.HttpsUtils;
//import com.xiaoma.thread.Priority;
//import com.xiaoma.utils.GsonHelper;
//
//import java.io.File;
//import java.io.InputStream;
//import java.lang.reflect.Type;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//import java.util.logging.Level;
//
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.RequestBody;
//
///**
// * <pre>
// *     author : wutao
// *     e-mail : ldlywt@163.com
// *     time   : 2018/09/11
// *     desc   :
// *     version: 1.0
// * </pre>
// */
//public class OkHttpEngine implements IHttpEngine {
//    private static final String TAG = OkHttpEngine.class.getSimpleName() + "_LOG";
//    private static final String REQUEST_LOG_TAG = "XMHTTP_LOG";
//    private Context mContext;
//    private Map<String, String> mLaterCommonParams = new HashMap<>();
//
//    private RequestBody asBody(Map<String, Object> params) {
//        FormBody.Builder body = new FormBody.Builder();
//        if (params == null || params.isEmpty()) {
//            return body.build();
//        }
//        for (Map.Entry<String, Object> entry : params.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            if (key == null) {
//                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
//                        + "\n * null key!!");
//                continue;
//            }
//            if (value instanceof String) {
//                body.add(key, (String) value);
//            } else {
//                body.add(key, String.valueOf(value));
//            }
//        }
//        return body.build();
//    }
//
//    private HttpParams asParams(Map<String, Object> params) {
//        HttpParams httpParams = new HttpParams();
//        if (params == null) {
//            return httpParams;
//        }
//        for (Map.Entry<String, Object> entry : params.entrySet()) {
//            if (entry == null) {
//                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
//                        + "\n * null key!!");
//                continue;
//            }
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            if (key == null) {
//                continue;
//            }
//            if (value instanceof String) {
//                httpParams.put(key, (String) value);
//            } else if (value instanceof File) {
//                httpParams.put(key, (File) value);
//            } else {
//                httpParams.put(key, String.valueOf(value));
//            }
//        }
//        return httpParams;
//    }
//
//    @Override
//    public void init(Context context) {
//        mContext = context;
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        // Log拦截器
//        LoggingInterceptor loggingInterceptor = new LoggingInterceptor(context, REQUEST_LOG_TAG);
//        loggingInterceptor.setPrintLevel(LoggingInterceptor.Level.BODY);
//        loggingInterceptor.setColorLevel(Level.INFO);
//        builder.addInterceptor(loggingInterceptor);
//
//        // Token拦截器
//        builder.addInterceptor(new TokenInterceptor());
//
//        // 超时时间设置,默认60秒
//        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
//        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
//        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
//
//        // http相关
//        InputStream clientStream = context.getResources().openRawResource(R.raw.xmlx_client_3);
//        InputStream cacertStream = context.getResources().openRawResource(R.raw.cacert);
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(
//                clientStream,
//                NetworkConstants.CLIENT_PASSWORD,
//                cacertStream
//        );
//        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
//        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
//
//        // https相关
//        OkGo.getInstance().init((Application) context.getApplicationContext())
//                .setOkHttpClient(builder.build())
//                .setCacheMode(NetworkConstants.CACHE_MODE)
//                .setCacheTime(NetworkConstants.CACHE_TIME)
//                .setRetryCount(NetworkConstants.RETRY_COUNT);
//    }
//
//    @Override
//    public void cancelTag(String tag) {
//        OkGo.getInstance().cancelTag(tag);
//    }
//
//    @Override
//    public void cancelAll() {
//        OkGo.getInstance().cancelAll();
//    }
//
//    @Override
//    public void setRetryCount(int retryCount) {
//        OkGo.getInstance().setRetryCount(retryCount);
//    }
//
//    @Override
//    public void getString(String url, StringCallback callBack) {
//        getString(url, null, url, callBack);
//    }
//
//    @Override
//    public void getString(String url, Map<String, Object> params, StringCallback callBack) {
//        getString(url, params, "", callBack);
//    }
//
//    @Override
//    public void getString(String url, Map<String, Object> params, String tag, StringCallback callBack) {
//        getString(url, params, tag, callBack, null);
//    }
//
//    @Override
//    public void getString(String url, Map<String, Object> params, StringCallback callBack, Priority priority) {
//        getString(url, params, "", callBack, priority);
//    }
//
//    @Override
//    public void getString(String url, Map<String, Object> params, String tag, StringCallback callBack, Priority priority) {
//        NetworkConstants.NEED_TOKEN = true;
//        // url = url + HttpUtils.getUrlParamsByMap(params);
//        HttpParams httpParams = asParams(params);
//        OkGo.<String>get(url)
//                .tag(tag)
//                .params(httpParams)
//                .priority(priority)
//                .execute(callBack);
//    }
//
//    @Override
//    public void postString(String url, Map<String, Object> params, StringCallback callBack) {
//        postString(url, params, "", callBack);
//    }
//
//    @Override
//    public void postObject(String url, Object object, StringCallback callBack) {
//        NetworkConstants.NEED_TOKEN = true;
//        String json = GsonHelper.toJson(object);
//        Type type = new TypeToken<Map<String, String>>() {
//        }.getType();
//        Map<String, Object> map = GsonHelper.fromJson(json, type);
//        OkGo.<String>post(url)
//                .upRequestBody(appendBody(map))
//                .execute(callBack);
//    }
//
//    @Override
//    public void postString(String url, Map<String, Object> params, String tag, StringCallback callBack) {
//        postString(url, params, tag, callBack, null);
//    }
//
//    @Override
//    public void postString(String url, Map<String, Object> params, StringCallback callBack, Priority priority) {
//        postString(url, params, "", callBack, priority);
//    }
//
//    /**
//     * 所有up开头的方法不能与params()方法混用,如果混用,将按up方法的行为来,所有params()设置的参数将丢失。
//     */
//    @Override
//    public void postString(String url, Map<String, Object> params, String tag, StringCallback callBack, Priority priority) {
//        NetworkConstants.NEED_TOKEN = true;
//        OkGo.<String>post(url)
//                .tag(tag)
//                .offset(params.get("offset") == null ? 1: (Integer) params.get("offset"))
//                .upRequestBody(appendBody(params))
//                .priority(priority)
//                .execute(callBack);
//    }
//
//    private String queryKey(Map<String, Object> params){
//        if(params == null){
//            return "";
//        }
//        String queryKey = "";
//        if(params.get("type") != null){
//            queryKey = (String) params.get("type");
//        }else if(params.get("query") != null){
//            queryKey = (String) params.get("query");
//        }
//        return queryKey;
//    }
//    private RequestBody appendBody(Map<String, Object> params) {
//        Map<String, Object> temp = new HashMap<>(params);
//        temp.putAll(XmHttp.getDefault().genCommonParams(mContext));
//        temp.putAll(mLaterCommonParams);
//        FormBody.Builder body = new FormBody.Builder();
//        if (temp.isEmpty()) {
//            return body.build();
//        }
//        for (Map.Entry<String, Object> entry : temp.entrySet()) {
//            String key = entry.getKey();
//            String value = String.valueOf(entry.getValue());
//            body.add(key, value);
//        }
//        return body.build();
//    }
//
//    @Override
//    public void postJsonString(String url, Object object, StringCallback callBack) {
//        NetworkConstants.NEED_TOKEN = true;
//        OkGo.<String>post(url)
//                .upJson((String) object)
//                .execute(callBack);
//    }
//
//    @Override
//    public void postFormData(String url, Map<String, Object> params, StringCallback callback) {
//        NetworkConstants.NEED_TOKEN = true;
//        HttpParams httpParams = asParams(params);
//        OkGo.<String>post(url)
//                .isMultipart(true)
//                .params(httpParams)
//                .execute(callback);
//    }
//
//    @Override
//    public void getFile(String url, FileCallback callBack) {
//        getFile(url, null, url, callBack);
//    }
//
//    @Override
//    public void getFile(String url, Map<String, Object> params, FileCallback callBack) {
//        getFile(url, params, "", callBack);
//    }
//
//    @Override
//    public void getFile(String url, Map<String, Object> params, String tag, FileCallback callBack) {
//        getFile(url, params, tag, callBack, null);
//    }
//
//    @Override
//    public void getFile(String url, Map<String, Object> params, FileCallback callBack, Priority priority) {
//        getFile(url, params, "", callBack, priority);
//    }
//
//    @Override
//    public void getFile(String url, Map<String, Object> params, String tag, FileCallback callBack, Priority priority) {
//        NetworkConstants.NEED_TOKEN = false;
//        OkGo.<File>get(url + HttpUtils.getUrlParamsByMap(params))
//                .tag(tag)
//                .priority(priority)
//                .execute(callBack);
//
//    }
//
//    @Override
//    public void upFile(String url, Map<String, Object> params, File file, StringCallback callBack) {
//        upFile(url, params, file, "", callBack);
//    }
//
//    @Override
//    public void upFile(String url, Map<String, Object> params, File file, String tag, StringCallback callBack) {
//        upFile(url, params, file, tag, callBack, null);
//    }
//
//    @Override
//    public void upFile(String url, Map<String, Object> params, File file, StringCallback callBack, Priority priority) {
//        upFile(url, params, file, "", callBack, priority);
//    }
//
//    @Override
//    public void upFile(String url, Map<String, Object> params, File file, String tag, StringCallback callBack, Priority priority) {
//        NetworkConstants.NEED_TOKEN = true;
//        RequestBody body = asBody(params);
//        OkGo.<String>post(url)
//                .tag(tag)
//                .upRequestBody(body)
//                .upFile(file)
//                .priority(priority)
//                .execute(callBack);
//    }
//
//    @Override
//    public void upBytes(String url, Map<String, Object> params, byte[] bytes, StringCallback callBack) {
//        upBytes(url, params, bytes, "", callBack);
//    }
//
//    @Override
//    public void upBytes(String url, Map<String, Object> params, byte[] bytes, String tag, StringCallback callBack) {
//        upBytes(url, params, bytes, tag, callBack, null);
//    }
//
//    @Override
//    public void upBytes(String url, Map<String, Object> params, byte[] bytes, StringCallback callBack, Priority priority) {
//        upBytes(url, params, bytes, "", callBack, priority);
//    }
//
//    @Override
//    public void upBytes(String url, Map<String, Object> params, byte[] bytes, String tag, StringCallback callBack, Priority priority) {
//        NetworkConstants.NEED_TOKEN = true;
//        RequestBody body = asBody(params);
//        OkGo.<String>post(url)
//                .tag(tag)
//                .upRequestBody(body)
//                .upBytes(bytes)
//                .priority(priority)
//                .execute(callBack);
//    }
//
//    @Override
//    public void addCommonParams(Map<String, String> params) {
//        if (params == null) {
//            return;
//        }
//        Map<String, String> temp = new HashMap<>();
//        temp.putAll(params);
//        temp.putAll(XmHttp.getDefault().genCommonParams(mContext));
//        for (Map.Entry<String, String> entry : temp.entrySet()) {
//            if (entry == null) {
//                continue;
//            }
//            String key = String.valueOf(entry.getKey());
//            String value = String.valueOf(entry.getValue());
//            OkGo.getInstance().addCommonParams(key, value);
//        }
//    }
//
//    @Override
//    public void clearCommonParams() {
//        OkGo.getInstance().clearCommonParams();
//    }
//
//    @Override
//    public void addCommonParams(String key, String value) {
//        mLaterCommonParams.put(key, value);
//        OkGo.getInstance().addCommonParams(key, value);
//    }
//
//    @Override
//    public void removeCommonParams(String key) {
//        OkGo.getInstance().removeCommonParams(key);
//    }
//
//    @Override
//    public void setCommonHeaders(Map<String, String> params) {
//        if (params == null) {
//            return;
//        }
//        HttpHeaders headers = new HttpHeaders();
//        for (Map.Entry<String, String> entry : params.entrySet()) {
//            if (entry == null) {
//                continue;
//            }
//            headers.put(entry.getKey(), entry.getValue());
//        }
//        OkGo.getInstance().addCommonHeaders(headers);
//    }
//}
