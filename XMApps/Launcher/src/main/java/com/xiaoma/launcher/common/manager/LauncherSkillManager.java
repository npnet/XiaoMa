package com.xiaoma.launcher.common.manager;

import android.content.Context;

import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.skill.SkillManager;
import com.xiaoma.vr.skill.client.SkillCallback;
import com.xiaoma.vr.skill.client.SkillDispatcher;
import com.xiaoma.vr.skill.client.SkillHandler;
import com.xiaoma.vr.skill.logic.ExecResult;
import com.xiaoma.vr.skill.model.Skill;

/**
 * @author taojin
 * @date 2019/6/20
 */
public class LauncherSkillManager {


    private LauncherSkillManager() {

    }

    public static LauncherSkillManager getInstance() {
        return LauncherSkillManagerHolder.instance;
    }

    private static class LauncherSkillManagerHolder {
        static final LauncherSkillManager instance = new LauncherSkillManager();
    }

    public void init(Context context) {
        SkillManager.getInstance().init(context);
        SkillDispatcher.getInstance().addSkillHandler(new SkillHandler() {
            @Override
            public boolean onSkill(String command, Skill skill, SkillCallback callback) {

                if (VrPracticeConstants.SKILL_GUIDE.equals(skill.getSkillDesc())) {
                    //调用导航去处理导航
                    String content = skill.getExtra();
                    SearchAddressInfo addressInfo = GsonHelper.fromJson(content, SearchAddressInfo.class);
                    if (addressInfo != null) {
                        XmMapNaviManager.getInstance().startNaviToPoi(addressInfo.title, addressInfo.addressName, addressInfo.latLonPoint.getLongitude(), addressInfo.latLonPoint.getLatitude());
                    }
                    KLog.d("startnavi onSkill");
                    ExecResult execResult = new ExecResult("start navi", VrPracticeConstants.SKILL_SUCCESS);
                    callback.onExec(execResult);
                    return true;
                }

                return false;
            }
        });
    }

}
