package com.xiaoma.vr.skill.logic;

/**
 * @author youthyJ
 * @date 2019/6/13
 */
public interface ExecCallback {
    void onSuccess(String command, ExecResult result);

    void onCancel(String command);

    void onFailure(int code, String command);
}
