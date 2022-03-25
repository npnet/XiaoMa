package com.xiaoma.assistant.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.config.ConfigConstants;
import com.xiaoma.login.LoginManager;
import com.xiaoma.model.pratice.SkillBean;
import com.xiaoma.model.pratice.UserSkillItemsBean;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Thomas on 2019/6/17 0017
 * 语音训练技能拦截执行
 */

public class VrPracticeManager {

    private static VrPracticeManager vrPracticeManager = new VrPracticeManager();
    private SkillBean currentSkillBean = new SkillBean();
    private List<SkillBean> currentSkillBeans = new ArrayList<>();
    private Context context;

    public static VrPracticeManager getInstance() {
        return vrPracticeManager;
    }

    public void init(Context context) {
        if (context == null || this.context != null) {
            return;
        }
        this.context = context;
        syncVrPracticeContent();
        initVrPracticeReceived();
    }

    private void initVrPracticeReceived() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConfigConstants.VR_PRACTICE_ACTION);
        this.context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                syncVrPracticeContent();
            }
        }, intentFilter);
    }

    private void syncVrPracticeContent() {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                String vrPracticeContent = XmProperties.build(LoginManager.getInstance().getLoginUserId()).get(ConfigConstants.VR_PRACTICE_CONTENT, "");
                if (!TextUtils.isEmpty(vrPracticeContent)) {
                    List<SkillBean> skillBeans = GsonHelper.fromJson(vrPracticeContent, new TypeToken<List<SkillBean>>() {}.getType());
                    if (!ListUtils.isEmpty(skillBeans)) {
                        currentSkillBeans.clear();
                        currentSkillBeans.addAll(skillBeans);
                    } else {
                        currentSkillBeans.clear();
                    }
                } else {
                    currentSkillBeans.clear();
                }
            }
        });
    }

    public boolean processVrPracticeKeyWord(String voiceText) {
        if (TextUtils.isEmpty(voiceText)) {
            return false;
        }
        KLog.d("VrPracticeManager voiceText is " + voiceText);
        SkillBean hasPracticeSkill = isHasPracticeSkill(currentSkillBeans, voiceText);
        if (hasPracticeSkill != null) {
            currentSkillBean = hasPracticeSkill;
            doHandlePracticeSkill(hasPracticeSkill);
            return true;
        }
        return false;
    }

    private SkillBean isHasPracticeSkill(@NonNull List<SkillBean> skillBeans, @NonNull String voiceText) {
        for (SkillBean skillBean : skillBeans) {
            if (voiceText.equals(skillBean.getWord())) {
                return skillBean;
            }
        }
        return null;
    }

    private void doHandlePracticeSkill(SkillBean skillBean) {
        List<UserSkillItemsBean> userSkillItems = skillBean.getUserSkillItems();
        Collections.sort(userSkillItems, new Comparator<UserSkillItemsBean>() {
            @Override
            public int compare(UserSkillItemsBean userSkillItemsBeanV1, UserSkillItemsBean userSkillItemsBeanV2) {
                return userSkillItemsBeanV1.getSkillItem().getSort() - userSkillItemsBeanV2.getSkillItem().getSort();
            }
        });

        currentSkillBean.setUserSkillItems(userSkillItems);
        //userSkillItems 串行执行任务
        VrSkillManager.getInstance().excute(currentSkillBean);
    }

}
