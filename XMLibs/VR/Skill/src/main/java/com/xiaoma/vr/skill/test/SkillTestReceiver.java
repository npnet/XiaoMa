package com.xiaoma.vr.skill.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.vr.skill.SkillManager;
import com.xiaoma.vr.skill.logic.ExecCallback;
import com.xiaoma.vr.skill.logic.ExecResult;
import com.xiaoma.vr.skill.model.Skill;

/**
 * @author youthyJ
 * @date 2019/6/18
 */
public class SkillTestReceiver extends BroadcastReceiver {
    private static final String TAG = SkillTestReceiver.class.getSimpleName() + "_LOG";
    private static final String ACTION = "com.xiaoma.test.SKILL";

    public static void register(Context context) {
        if (!ConfigManager.ApkConfig.isDebug()) {
            return;
        }
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        SkillTestReceiver receiver = new SkillTestReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SkillTestReceiver.ACTION);
        appContext.registerReceiver(receiver, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 模拟技能分发
        Skill skill = new Skill(
                "com.xiaoma.pet",
                "弹吐司",
                "toast",
                null
        );

        SkillManager.getInstance().execSkill("测试测试", skill, "aaa", new ExecCallback() {
            @Override
            public void onSuccess(String command, ExecResult result) {
                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * onSuccess"
                        + "\n * command" + command
                        + "\n * result" + result
                );
            }

            @Override
            public void onCancel(String command) {
                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * onCancel"
                        + "\n * command" + command
                );
            }

            @Override
            public void onFailure(int code, String command) {
                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * onFailure"
                        + "\n * command" + command
                        + "\n * code" + code
                );
            }
        });

    }

}
