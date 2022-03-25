package com.xiaoma.network.interceptor;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.config.bean.Env;

import java.io.IOException;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by LKF on 2019-5-8 0008.
 * 当Https请求失败时,降级为Http,目前主要为了防止证书问题导致的失败
 * <p>
 * TODO: 此拦截器暂不启用
 */
public class HttpsFailInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response resp = null;
        Request req = chain.request();
        boolean isHttps = "https".equalsIgnoreCase(req.url().scheme());
        try {
            resp = chain.proceed(req);
            if (resp != null && isHttps) {
                switch (resp.code()) {
                    case 400:
                    case 403:
                        Response httpResp = doHttpRequest(chain, req);
                        if (httpResp != null) {
                            resp = httpResp;
                        }
                        break;
                }
            }
        } catch (SSLHandshakeException e) {
            e.printStackTrace();
            if (isHttps) {
                Response httpResp = doHttpRequest(chain, req);
                if (httpResp != null) {
                    resp = httpResp;
                }
            }
        }
        return resp;
    }

    private Response doHttpRequest(Chain chain, Request req) throws IOException {
        Env standByEnv = ConfigManager.EnvConfig.getStandByEnv();
        if (standByEnv == null)
            return null;
        Env usingEnv = ConfigManager.EnvConfig.getEnv();
        HttpUrl httpUrl = req.url();
        String url = httpUrl.toString();
        if (url.startsWith(usingEnv.getBusiness())) {
            url = url.replace(usingEnv.getBusiness(), standByEnv.getBusiness());
        } else if (url.startsWith(usingEnv.getFile())) {
            url = url.replace(usingEnv.getFile(), standByEnv.getFile());
        } else if (url.startsWith(usingEnv.getLog())) {
            url = url.replace(usingEnv.getLog(), standByEnv.getLog());
        } else if (url.startsWith(usingEnv.getToken())) {
            url = url.replace(usingEnv.getToken(), standByEnv.getToken());
        }
        HttpUrl newUrl = HttpUrl.parse(url);
        if (newUrl == null)
            return null;
        req = req.newBuilder()
                .url(newUrl)
                .build();
        return chain.proceed(req);
    }
}
