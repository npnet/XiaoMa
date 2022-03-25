package com.xiaoma.assistant.model.result;

/**
 * Created by qiuboxiang on 2019/3/8 19:50
 * Desc:
 */
public interface SpecialResultCallback <T extends SpecialResult> {
        void onSuccess(T result);

        void onFailure(int code, String msg);
    }
