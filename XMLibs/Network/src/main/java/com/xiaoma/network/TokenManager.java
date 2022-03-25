package com.xiaoma.network;

import com.xiaoma.config.ConfigManager;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <pre>
 *     author : wutao
 *     e-mail : ldlywt@163.com
 *     time   : 2018/09/11
 *     desc   : 警告警告：其他module和App不要调用这个类
 *     desc   : 警告警告：其他module和App不要调用这个类
 *     desc   : 警告警告：其他module和App不要调用这个类
 *     desc   : 警告警告：其他module和App不要调用这个类
 *     version: 1.0
 * </pre>
 */
public class TokenManager {
    private URL tokenURL;
    private String currentToken;

    private TokenManager() {
    }

    public static TokenManager getInstance() {
        return Holder.INSTANCE;
    }

    public String getCurrentToken() {
        return currentToken == null ? "" : currentToken;
    }

    public String getTokenSync() {
        try {
            final String TOKEN_URL = ConfigManager.EnvConfig.getEnv().getToken();
            OkHttpClient client = HttpEngineImpl.instance().getHttpClient();
            Request req = new Request.Builder()
                    .url(TOKEN_URL)
                    .get()
                    .build();
            Response response = client.newCall(req).execute();
            ResponseBody body = response.body();
            String result = body.string();
            JSONObject jsonObject = new JSONObject(result);
            currentToken = jsonObject.getString("token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentToken;
    }

    public boolean isTokenURL(URL url) throws IOException {
        if (url == null) {
            return false;
        }
        if (tokenURL == null) {
            tokenURL = new URL(ConfigManager.EnvConfig.getEnv().getToken());
        }
        String tokenURLStr = tokenURL.toString();
        String urlStr = url.toString();
        return urlStr.contains(tokenURLStr);
    }

    private static class Holder {
        private static final TokenManager INSTANCE = new TokenManager();
    }
}
