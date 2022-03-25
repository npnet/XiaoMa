/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaoma.network.interceptor;


import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.config.ConfigManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;

import static java.util.logging.Level.WARNING;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：OkHttp拦截器，主要用于打印日志
 * 修订历史：
 * ================================================
 */
public class LoggingInterceptor implements Interceptor {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private Context appContext;
    private volatile Level printLevel = Level.NONE;
    private java.util.logging.Level colorLevel;
    private Logger logger;

    public enum Level {
        NONE,       //不打印log
        BASIC,      //只打印 请求首行 和 响应首行
        HEADERS,    //打印请求和响应的所有 Header
        BODY        //所有数据全部打印
    }

    public LoggingInterceptor(Context context, String tag) {
        logger = Logger.getLogger(tag);
        appContext = context.getApplicationContext();
    }

    public void setPrintLevel(Level level) {
        if (level == null) throw new NullPointerException("level == null. Use Level.NONE instead.");
        printLevel = level;
    }

    public void setColorLevel(java.util.logging.Level level) {
        colorLevel = level;
    }

    private void log(String message) {
        log(this.colorLevel, message);
    }

    private void log(java.util.logging.Level colorLevel, String message) {
        if (!ConfigManager.ApkConfig.isDebug()) {
            return;
        }
        synchronized (LoggingInterceptor.class) {
            for (String line : message.split(NEW_LINE)) {
                logger.log(colorLevel, line);
            }
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (printLevel == Level.NONE) {
            return chain.proceed(chain.request());
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" ").append(NEW_LINE)
                .append("┌────────────────────────────────────────────────────────────────────────────────────────────").append(NEW_LINE)
                .append("│ #PACKAGE: ").append(appContext.getPackageName()).append(NEW_LINE)
                .append("╞════════════════════════════════════════════════════════════════════════════════════════════").append(NEW_LINE);
        Response response;
        try {
            response = chain.proceed(chain.request());
            genRequestLog(sb, response.request(), response.protocol());
        } catch (Exception e) {
            setColorLevel(java.util.logging.Level.WARNING);
            sb
                    .append("╞════════════════════════════════════════════════════════════════════════════════════════════").append(NEW_LINE)
                    .append("│ XXX  ERROR  XXX").append(NEW_LINE)
                    .append("╞════════════════════════════════════════════════════════════════════════════════════════════").append(NEW_LINE)
                    .append("│ [ PROCESS EXCEPTION ] ").append(NEW_LINE)
                    .append("├────────────────────────────────────────────────────────────────────────────────────────────").append(NEW_LINE)
                    .append("│ * EXCEPTION: ").append(e.getClass().getSimpleName()).append(NEW_LINE)
                    .append("│ * MESSAGE: ").append(e.getMessage()).append(NEW_LINE)
                    .append("└────────────────────────────────────────────────────────────────────────────────────────────");
            log(WARNING, sb.toString());
            throw e;
        }

        final long timeConsuming = response.receivedResponseAtMillis() - response.sentRequestAtMillis();
        sb
                .append("╞════════════════════════════════════════════════════════════════════════════════════════════").append(NEW_LINE)
                .append("│ ~~~  ").append(timeConsuming).append(" ms").append("  ~~~").append(NEW_LINE)
                .append("╞════════════════════════════════════════════════════════════════════════════════════════════").append(NEW_LINE);
        MediaType mediaType;
        ResponseBody body = response.body();
        if (body != null) {
            mediaType = body.contentType();
            response = genResponseLog(sb, response, mediaType);
        } else {
            sb.append("│ NULL Body");
        }
        sb.append("└────────────────────────────────────────────────────────────────────────────────────────────");
        log(sb.toString());

        return response;
    }

    private void genRequestLog(StringBuilder sb, Request request, Protocol protocol) {
        HttpUrl url = request.url();
        boolean logBody = (printLevel == Level.BODY);
        boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        try {
            sb
                    .append("│[ REQUEST ]").append(NEW_LINE)
                    .append("├────────────────────────────────────────────────────────────────────────────────────────────").append(NEW_LINE)
                    .append("│ * URL: ").append(url.scheme()).append("://").append(url.host()).append(url.encodedPath()).append(NEW_LINE)
                    .append("│ * METHOD: ").append(request.method()).append(NEW_LINE)
                    .append("│ * PROTOCOL: ").append(protocol).append(NEW_LINE);

            if (logHeaders) {
                if (hasRequestBody) {
                    if (requestBody.contentType() != null) {
                        sb.append("│ * CONTENT-TYPE: ").append(requestBody.contentType()).append(NEW_LINE);
                    }
                    if (requestBody.contentLength() != -1) {
                        sb.append("│ * CONTENT-LENGTH: ").append(requestBody.contentLength()).append(NEW_LINE);
                    }
                }

                Headers headers = request.headers();
                if (headers != null && headers.size() > 0) {
                    for (int i = 0, count = headers.size(); i < count; i++) {
                        String headerName = String.valueOf(headers.name(i));
                        String headerValue = String.valueOf(headers.value(i));
                        if ("CONTENT-TYPE".equalsIgnoreCase(headerName)
                                || "CONTENT-LENGTH".equalsIgnoreCase(headerName)) {
                            continue;
                        }
                        sb.append("│ * ").append(headerName).append(": ").append(headerValue).append(NEW_LINE);
                    }
                }

                if (logBody && hasRequestBody) {
                    RequestBody body = request.body();
                    if (body != null) {
                        Buffer buffer = new Buffer();
                        body.writeTo(buffer);
                        String bodyStr = null;
                        try {
                            bodyStr = buffer.readString(UTF8);
                        } catch (Exception ignored) {
                        }
                        sb.append("│ * BODY: ").append(bodyStr).append(NEW_LINE);
                    }
                }
            }

            Set<String> paramNames = url.queryParameterNames();
            List<String> list = new ArrayList<>(paramNames);
            if (list.size() > 0) {
                sb.append("├────────────────────────────────────────────────────────────────────────────────────────────").append(NEW_LINE);
                int index = 0;
                sb.append("│ * PARAMS: ").append(NEW_LINE);
                for (String paramName : list) {
                    if (TextUtils.isEmpty(paramName)) {
                        continue;
                    }
                    String paramValue = url.queryParameter(paramName);
                    boolean needNewLine = index % 2 == 0;
                    if (needNewLine) {
                        sb.append("│ * ");
                    }
                    sb.append(" [")
                            .append(paramName)
                            .append(" : ")
                            .append(paramValue)
                            .append("]  ");
                    boolean needEnd = (index + 1) % 2 == 0;
                    if (needEnd) {
                        sb.append(NEW_LINE);
                    }
                    index++;
                }
                boolean hasLastEnd = index % 2 != 0;
                if (hasLastEnd) {
                    sb.append(NEW_LINE);
                }
            }
        } catch (Exception e) {
            sb
                    .append("╞════════════════════════════════════════════════════════════════════════════════════════════").append(NEW_LINE)
                    .append("│ [ LOG EXCEPTION ] ").append(NEW_LINE)
                    .append("├────────────────────────────────────────────────────────────────────────────────────────────").append(NEW_LINE)
                    .append("│ * EXCEPTION: ").append(e.getClass().getSimpleName()).append(NEW_LINE)
                    .append("│ * MESSAGE: ").append(e.getMessage()).append(NEW_LINE);
        }

    }

    private Response genResponseLog(StringBuilder sb, Response response, MediaType mediaType) {
        boolean logBody = (printLevel == Level.BODY);
        boolean logHeaders = (printLevel == Level.BODY || printLevel == Level.HEADERS);
        try {
            sb
                    .append("│[ RESPONSE ]").append(NEW_LINE)
                    .append("├────────────────────────────────────────────────────────────────────────────────────────────").append(NEW_LINE)
                    .append("│ * CODE: ").append(response.code()).append(NEW_LINE);
            if (!TextUtils.isEmpty(response.message())) {
                sb.append("│ * MESSAGE: ").append(response.message()).append(NEW_LINE);
            }
            if (logHeaders) {
                Headers headers = response.headers();
                if (headers != null) {
                    for (int i = 0, count = headers.size(); i < count; i++) {
                        String headerName = String.valueOf(headers.name(i));
                        String headerValue = String.valueOf(headers.value(i));
                        sb.append("│ * ").append(headerName).append(": ").append(headerValue).append(NEW_LINE);
                    }
                }

                if (logBody && HttpHeaders.hasBody(response) && response.code() != 206) {
                    sb.append("├────────────────────────────────────────────────────────────────────────────────────────────").append(NEW_LINE);
                    ResponseBody body = response.body();
                    if (body != null) {
                        if (isPlaintext(mediaType)) {
                            sb.append("│ * BODY: ");
                            final byte[] bodyBytes = body.bytes();
                            response = response.newBuilder()
                                    .body(ResponseBody.create(mediaType, bodyBytes))
                                    .build();
                            String bodyStr;
                            // 解压gzip
                            if ("gzip".equalsIgnoreCase(response.header("Content-Encoding"))) {
                                final long zipLen = bodyBytes.length;
                                GZIPInputStream gInput = new GZIPInputStream(new ByteArrayInputStream(bodyBytes));
                                byte[] buf = new byte[4 * 1024];
                                int len;
                                ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                                while ((len = gInput.read(buf)) != -1) {
                                    bOut.write(buf, 0, len);
                                }
                                bOut.close();
                                byte[] unZipBytes = bOut.toByteArray();
                                final long unZipLen = unZipBytes.length;
                                sb.append(String.format("│ ~~~    zipLen: %s, unZipLen: %s    ~~~", zipLen, unZipLen)).append(NEW_LINE);
                                bodyStr = new String(unZipBytes, getCharset(mediaType));
                            } else {
                                bodyStr = new String(bodyBytes, getCharset(mediaType));
                            }
                            parseResultJson(sb, bodyStr);
                        } else {
                            sb.append(String.format("│ ~~~    contentLength: %s    ~~~", body.contentLength())).append(NEW_LINE);
                            sb.append("│ * BODY: maybe [binary body], omitted!").append(NEW_LINE);
                        }
                    } else {
                        sb.append("│ * BODY: body is null!").append(NEW_LINE);
                    }
                }
            }
        } catch (Exception e) {
            sb
                    .append("╞════════════════════════════════════════════════════════════════════════════════════════════").append(NEW_LINE)
                    .append("│ [ LOG EXCEPTION ] ").append(NEW_LINE)
                    .append("├────────────────────────────────────────────────────────────────────────────────────────────").append(NEW_LINE)
                    .append("│ * EXCEPTION: ").append(e.getClass().getSimpleName()).append(NEW_LINE)
                    .append("│ * MESSAGE: ").append(e.getMessage()).append(NEW_LINE);
        }
        return response;
    }

    private void parseResultJson(StringBuilder sb, String json) {
        if (TextUtils.isEmpty(json) || json.trim().isEmpty()) {
            sb.append("|    { }");
            return;
        }
        json = json.trim();
        final int indentSpaces = 2;
        String newJson;
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                newJson = jsonObject.toString(indentSpaces);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                newJson = jsonArray.toString(indentSpaces);
            } else {
                newJson = json;
            }
        } catch (JSONException e) {
            newJson = json;
        }
        String[] eachLine = newJson.split(NEW_LINE);
        sb.append(NEW_LINE);
        for (String line : eachLine) {
            sb.append("│ *  ").append(line).append(NEW_LINE);
        }
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }

    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }
        if (mediaType.type() != null && mediaType.type().equalsIgnoreCase("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            return subtype.contains("x-www-form-urlencoded")
                    || subtype.contains("json")
                    || subtype.contains("xml")
                    || subtype.contains("html");
        }
        return false;
    }

}
