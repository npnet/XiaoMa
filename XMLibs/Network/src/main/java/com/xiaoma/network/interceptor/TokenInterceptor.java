package com.xiaoma.network.interceptor;


import android.text.TextUtils;

import com.xiaoma.network.ErrorCodeConstants;
import com.xiaoma.network.NetworkConstants;
import com.xiaoma.network.TokenManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <pre>
 *     author : wutao
 *     e-mail : ldlywt@163.com
 *     time   : 2018/09/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class TokenInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(final Chain chain) throws IOException {
        // 不拦截token请求
        if (!NetworkConstants.NEED_TOKEN
                || TokenManager.getInstance().isTokenURL(chain.request().url().url())) {
            return chain.proceed(chain.request());
        }

        // 获取Token
        String currentToken = TokenManager.getInstance().getCurrentToken();
        if (TextUtils.isEmpty(currentToken)) {
            currentToken = TokenManager.getInstance().getTokenSync();
        }
        if (TextUtils.isEmpty(currentToken)) {
            return chain.proceed(chain.request());
        }

        // 插入Token
        Request.Builder reqBuilder = chain.request().newBuilder()
                .addHeader("token", currentToken);
        Response response = chain.proceed(reqBuilder.build());

        //206--文件下载
        if (response.code() == 206) {
            return chain.proceed(chain.request());
        }
        // 判断Token是否失效
        ResponseBody body = response.body();
        MediaType mediaType;
        if (body != null && (mediaType = body.contentType()) != null) {
            try {
                String contentType = mediaType.type();
                if ("application".equalsIgnoreCase(contentType)
                        || "text".equalsIgnoreCase(contentType)) {
                    String bodyStr = body.string();
                    response = response.newBuilder()
                            .body(ResponseBody.create(mediaType, bodyStr))
                            .build();
                    do {
                        JSONObject jsonObject = ensureJsonObj(bodyStr);
                        if (jsonObject != null)
                            break;
                        JSONArray jsonArray = ensureJsonArr(bodyStr);
                        if (jsonArray != null)
                            break;
                        return chain.proceed(chain.request());
                    } while (false);
                    JSONObject bean = new JSONObject(bodyStr);
                    if (bean.has("resultCode")) {
                        String resultCode = bean.getString("resultCode");
                        if ((ErrorCodeConstants.TOKEN_EXPIRED_ERROR + "").equals(resultCode)) {
                            // 重新获取Token
                            String newToken = TokenManager.getInstance().getTokenSync();
                            Request newRequest = reqBuilder.header("token", newToken).build();
                            return chain.proceed(newRequest);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    private JSONObject ensureJsonObj(String string) {
        try {
            return new JSONObject(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray ensureJsonArr(String string) {
        try {
            return new JSONArray(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }
}


