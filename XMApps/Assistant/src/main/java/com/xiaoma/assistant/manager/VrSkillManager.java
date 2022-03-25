package com.xiaoma.assistant.manager;

import android.text.TextUtils;

import com.xiaoma.assistant.callback.WakeUpListener;
import com.xiaoma.component.AppHolder;
import com.xiaoma.model.pratice.SkillBean;
import com.xiaoma.model.pratice.SkillItemBean;
import com.xiaoma.model.pratice.UserSkillItemsBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.skill.SkillManager;
import com.xiaoma.vr.skill.logic.ExecCallback;
import com.xiaoma.vr.skill.logic.ExecResult;
import com.xiaoma.vr.skill.model.Skill;

import java.util.List;

/**
 * @author taojin
 * @date 2019/6/13
 */
public class VrSkillManager implements WakeUpListener {
    private final String TAG = "VrSkillManager";

    private SeriesAsyncWorker mWorker;
    private String currentVoice = "";
    private String lastSkillType;

    private VrSkillManager() {
        mWorker = SeriesAsyncWorker.create();
        AssistantManager.getInstance().setWakeUpListener(this);
    }

    @Override
    public void onWakeUp() {
        if (StringUtil.isNotEmpty(currentVoice)) {
            currentVoice = "";
            mWorker.interrupt();
        }
    }

    private static class VrSkillManagerHolder {
        static final VrSkillManager instance = new VrSkillManager();
    }

    public static VrSkillManager getInstance() {
        return VrSkillManagerHolder.instance;
    }

    public void excute(SkillBean skillBean) {

        //先判断技能是否正在执行
        if (!TextUtils.isEmpty(currentVoice)) {
            if (currentVoice.equals(skillBean.getWord())) {
                XMToast.showToast(AppHolder.getInstance().getAppContext(), "该技能已经在执行...");
                return;
            } else {
                mWorker.interrupt();
            }
        }
        mWorker.interrupt();

        List<UserSkillItemsBean> userSkillItems = skillBean.getUserSkillItems();
        lastSkillType = userSkillItems.get(userSkillItems.size() - 1).getSkillItem().getTextEng();

        for (int i = 0; i < userSkillItems.size(); i++) {
            int finalI = i;
            mWorker.next(new Work(true) {
                @Override
                public void doWork(Object lastResult) {
                    UserSkillItemsBean userSkillItemsBean = userSkillItems.get(finalI);
                    SkillItemBean skillItem = userSkillItemsBean.getSkillItem();
                    Skill excuteSkill = new Skill(skillItem.getPackageName(), skillItem.getText(), skillItem.getTextEng(), userSkillItemsBean.getContent());

                    SkillManager.getInstance().execSkill(currentVoice, excuteSkill, currentVoice, new ExecCallback() {
                        @Override
                        public void onSuccess(String command, ExecResult result) {


                            //如果执行的技能是导航或者是最后一个技能就关闭语音助手的页面
                            String excuteSkillType = excuteSkill.getSkillDesc();
                            if (excuteSkillType.equals(VrPracticeConstants.SKILL_GUIDE)) {
                                AssistantManager.getInstance().closeAssistant();
                            }
                            if (excuteSkillType.equals(lastSkillType)) {
                                currentVoice = "";
                                AssistantManager.getInstance().closeAssistant();
                            }
                            KLog.d(TAG, "ExecResult: " + result.toString());
                            if (command.equals(currentVoice) && result.getCode() == VrPracticeConstants.SKILL_SUCCESS_CODE) {
                                doNext();
                            } else {
                                KLog.d(TAG, "onSuccess SKILL CANCEL");
                            }
                        }

                        @Override
                        public void onCancel(String command) {
                            KLog.d(TAG, "onCancel" + command);
                        }

                        @Override
                        public void onFailure(int code, String command) {
                            KLog.d(TAG, String.format("onFailure code = %s,command = %s", code, command));
                        }
                    });

                }
            });
        }

        mWorker.start();

        currentVoice = skillBean.getWord();

    }

}
