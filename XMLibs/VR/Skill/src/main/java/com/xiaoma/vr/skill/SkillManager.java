package com.xiaoma.vr.skill;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;

import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.ErrorCode;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.skill.client.SkillClient;
import com.xiaoma.vr.skill.client.WakeupService;
import com.xiaoma.vr.skill.logic.ExecCallback;
import com.xiaoma.vr.skill.logic.ExecResult;
import com.xiaoma.vr.skill.model.Skill;
import com.xiaoma.vr.skill.test.SkillTestReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2019/6/13
 */
public class SkillManager {
    public static final String REQ_KEY_CMD = "command";
    public static final String REQ_KEY_SKILL = "skill";
    public static final String RSP_KEY_EXEC = "exec_result";

    private static final String TAG = SkillManager.class.getSimpleName() + "_LOG";
    private static final String HOST_APP = "com.xiaoma.xting";
    private static final String META_DATA_KEY = "com.xiaoma.skill";
    private static final String SPLIT_SEPARATOR = "\\|";

    private static SkillManager instance;
    private Context appContext;
    private List<String> tagList = new ArrayList<>();

    public static SkillManager getInstance() {
        if (instance == null) {
            synchronized (SkillManager.class) {
                if (instance == null) {
                    instance = new SkillManager();
                }
            }
        }
        return instance;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty() || str.trim().isEmpty();
    }

    public void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        appContext = context.getApplicationContext();
        String packageName = appContext.getPackageName();
        initAppCenter();
        if (packageName.equals(HOST_APP)) {
            SkillTestReceiver.register(appContext);
        }
    }

    public boolean cancelSkill(String tag) {
        if (isEmpty(tag)) {
            return false;
        }
        if (tagList.contains(tag)) {
            return false;
        }
        return tagList.remove(tag);
    }

    public boolean isSkillAvailable(Skill skill) {
        if (skill == null) {
            return false;
        }
        String targetPackage = skill.getPackageName();
        String targetSkillDesc = skill.getSkillDesc();
        if (isEmpty(targetPackage)) {
            return false; // 目标包名为空
        }
        if (isEmpty(targetSkillDesc)) {
            return false; // 目标技能描述为空
        }
        PackageManager pm = appContext.getPackageManager();
        ApplicationInfo appInfo = null;
        try {
            appInfo = pm.getApplicationInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (Exception e) {
        }
        if (appInfo == null) {
            return false; // 目标应用未安装
        }
        String metaDataSkills = appInfo.metaData.getString(META_DATA_KEY);
        if (isEmpty(metaDataSkills)) {
            return false; // 技能不存在
        }
        String[] skillArray = metaDataSkills.split(SPLIT_SEPARATOR);
        if (skill == null || metaDataSkills.length() <= 0) {
            return false; // 技能不存在
        }
        for (String skillDesc : skillArray) {
            if (isEmpty(skillDesc)) {
                continue;
            }
            if (targetSkillDesc.equalsIgnoreCase(skillDesc)) {
                return true;
            }
        }
        return false; // 技能不存在
    }

    public void execSkill(String command, Skill skill, ExecCallback callback) {
        String tag = String.valueOf(System.currentTimeMillis());
        execSkill(command, skill, tag, callback);
    }

    public void execSkill(final String command, final Skill skill, final String tag, final ExecCallback callback) {
        tagList.add(tag);
        boolean skillAvailable = isSkillAvailable(skill);
        if (!skillAvailable) {
            if (callback != null) {
                callback.onFailure(-1, command); // 技能无效
            }
            return;
        }
        String targetPackageName = skill.getPackageName();
        final SourceInfo remote = new SourceInfo(targetPackageName, SkillClient.PORT);
        boolean clientAlive = Center.getInstance().isClientAlive(remote);
        if (!clientAlive) {
            boolean success = tryWakeup(targetPackageName);
            if (!success) {
                if (callback != null) {
                    callback.onFailure(-2, command); // 远程客户端无法唤醒
                }
                return;
            }
            StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
                @Override
                public void onClientIn(SourceInfo source) {
                    if (!remote.equals(source)) {
                        return;
                    }
                    if (!isTagAvailable(tag)) {
                        callback.onCancel(command); // 客户端激活,任务被取消
                        return;
                    }
                    postSkill(remote, command, skill, tag, callback); // 等待客户端启动
                    StateManager.getInstance().removeCallback(this);
                }
            });
        } else {
            postSkill(remote, command, skill, tag, callback); // 客户端已启动
        }
    }

    private void initAppCenter() {
        Center.getInstance().init(appContext);
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                SkillClient client = new SkillClient(appContext);
                boolean success = Center.getInstance().register(client);
                if (!success) {
                    KLog.e(TAG, "register client failure");
                }
            }
        });
    }

    private void postSkill(SourceInfo remote, final String command, Skill skill, final String tag, final ExecCallback callback) {
        if (!isTagAvailable(tag)) {
            callback.onCancel(command); // 开始分发技能,任务已被取消
            return;
        }
        final int localPort = SkillClient.PORT;
        final int requestAction = SkillClient.ACTION;
        SourceInfo local = new SourceInfo(appContext.getPackageName(), localPort);
        RequestHead head = new RequestHead(remote, requestAction);
        Bundle extra = new Bundle();
        extra.putParcelable(REQ_KEY_SKILL, skill);
        extra.putString(REQ_KEY_CMD, command);
        Request request = new Request(local, head, extra);
        int code = Linker.getInstance().request(request, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) {
                if (!isTagAvailable(tag)) {
                    callback.onCancel(command); // 收到技能执行结果,任务已被取消
                    return;
                }
                Bundle result = response.getExtra();
                if (result == null) {
                    callback.onFailure(-4, command); // 结果为null
                    return;
                }
                ExecResult execResult = result.getParcelable(RSP_KEY_EXEC);
                callback.onSuccess(command, execResult); // 成功,回调执行结果
            }
        });
        if (code != ErrorCode.CODE_SUCCESS) {
            callback.onFailure(-3, command); // 请求出错
        }
    }

    private boolean isTagAvailable(String tag) {
        return tagList.contains(tag);
    }

    private boolean tryWakeup(String targetPackageName) {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(targetPackageName, WakeupService.class.getCanonicalName());
        intent.setComponent(cn);
        return appContext.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // ignore
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // ignore
            }
        }, Context.BIND_AUTO_CREATE);
    }
}
