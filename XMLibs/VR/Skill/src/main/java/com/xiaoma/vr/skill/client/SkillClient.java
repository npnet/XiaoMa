package com.xiaoma.vr.skill.client;

import android.content.Context;
import android.os.Bundle;

import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.vr.skill.SkillManager;
import com.xiaoma.vr.skill.logic.ExecResult;
import com.xiaoma.vr.skill.model.Skill;

/**
 * @author youthyJ
 * @date 2019/6/14
 */
public class SkillClient extends Client {
    public static final int PORT = 5759;
    public static final int ACTION = 0;

    public SkillClient(Context context) {
        super(context, PORT);
    }

    @Override
    protected final void onReceive(int action, Bundle data) {
        // empty
    }

    @Override
    protected final void onConnect(int action, Bundle data, ClientCallback callback) {
        // empty
    }

    @Override
    protected final void onRequest(int action, Bundle reqData, ClientCallback callback) {
        if (action != 0) {
            return;
        }
        reqData.setClassLoader(Skill.class.getClassLoader());
        Skill skill = reqData.getParcelable(SkillManager.REQ_KEY_SKILL);
        if (!SkillManager.getInstance().isSkillAvailable(skill)) {
            Bundle callbackData = new Bundle();
            ExecResult execResult = new ExecResult(null, null);
            execResult.setCode(-1);
            callbackData.putParcelable(SkillManager.RSP_KEY_EXEC, execResult);
            callback.setData(callbackData);
            callback.callback(); // skill不可用
            return;
        }
        String command = reqData.getString(SkillManager.REQ_KEY_CMD);
        boolean handled = onSkill(command, skill, callback);
        if (!handled) {
            Bundle callbackData = new Bundle();
            ExecResult execResult = new ExecResult(null, null);
            execResult.setCode(-2);
            callbackData.putParcelable(SkillManager.RSP_KEY_EXEC, execResult);
            callback.setData(callbackData);
            callback.callback(); // skill没被执行
            return;
        }
    }

    private boolean onSkill(String command, Skill skill, final ClientCallback callback) {
        SkillCallback proxyCallback = new SkillCallback() {
            @Override
            public void onExec(ExecResult result) {
                Bundle callbackData = new Bundle();
                result.setCode(0);
                callbackData.putParcelable(SkillManager.RSP_KEY_EXEC, result);
                callback.setData(callbackData);
                callback.callback(); // 执行成功
            }
        };
        return SkillDispatcher.getInstance().notifySkill(command, skill, proxyCallback);
    }
}
