package com.qiming.fawcard.synthesize.base;

import com.qiming.fawcard.synthesize.base.util.SignUtil;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;

/**
 * Created by summit on 6/5/17.
 */

public class BaseRequest {

    private String path;
    private Map<String, Object> query;
    private Map<String, Object> form;
    private Map<String, Object> header;
    private Object body;
    private String filePath;
    private MultipartBody.Part part;

    private BaseRequest() {
        query = new HashMap<>();
        form = new HashMap<>();
        header = new HashMap<>();
    }


    public Object getBody() {
        return body;
    }

    public Map<String, Object> getForm() {
        return form;
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return AppConfigManager.getInstance().getServerRoot() + path;
    }

    public Map<String, Object> getHeader() {
        return header;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public MultipartBody.Part getPart() {
        return part;
    }

    public void setPart(MultipartBody.Part part) {
        this.part = part;
    }

    public static class Builder {

        private String path;
        private Map<String, Object> query;
        private Map<String, Object> form;
        private Map<String, Object> header;
        private Object body;
        private String filePath;
        private MultipartBody.Part part;

        public Builder() {
            query = new HashMap<>();
            form = new HashMap<>();
            header = new HashMap<>();

            //设置接口通用URL参数
            query.put("signt", System.currentTimeMillis());
//            query.put("nonce", UUID.randomUUID().toString() + System.currentTimeMillis());
            query.put("appkey", AppConfigManager.getInstance().getAppKey());
        }

        public Builder path(String path) {
            this.path = path;
            if (path.startsWith("rldetail-super")) { // 润霖的接口要加上以下参数
                header.put("ip", "192.168.215.1");
                header.put("name", "audi");
                header.put("secretKey", AppConfigManager.getInstance().getAppKey());
            }
            return this;
        }


        public Map<String, Object> getQuery() {
            return query;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder query(String key, Object value) {
            query.put(key, value);
            return this;
        }

        public Builder form(String key, Object value) {
            form.put(key, value);
            return this;
        }

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public Builder part(MultipartBody.Part part) {
            this.part = part;
            return this;
        }

        public BaseRequest build() {
            query.put("sign", SignUtil.sign(path, query, AppConfigManager.getInstance()
                    .getAppSecretKey()));
            BaseRequest request = new BaseRequest();
            request.path = path;
            request.form = form;
            request.query = query;
            request.body = body;
            request.header = header;
            request.filePath = filePath;
            request.part = part;
            return request;
        }
    }
}
