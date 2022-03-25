package com.xiaoma.assistant.scenarios;

import android.content.Context;

import com.xiaoma.aidl.model.ScheduleBean;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.api.LauncherApiManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：日程场景
 */
public class IatScheduleScenario extends IatScenario {


    public IatScheduleScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        ScheduleBean scheduleBean = GsonHelper.fromJson(parseResult.getSlots(), ScheduleBean.class);
        if ("VIEW".equals(parseResult.getOperation())) {
            setRobAction(14);
            if (scheduleBean != null && scheduleBean.getDatetime() != null) {
                String date = scheduleBean.getDatetime().getDate();
                if (StringUtil.isNotEmpty(date)) {
                    openCalendar(date);
                } else {
                    openCalendar("");
                }
            } else {
                openCalendar("");
            }
        } else if ("CREATE".equals(parseResult.getOperation())) {
            setRobAction(AssistantConstants.RobActionKey.PLAY_CONTROL);
            if (scheduleBean != null && scheduleBean.getDatetime() != null) {
                LauncherApiManager.getInstance().createSchedule(scheduleBean);
            } else {
                addFeedbackAndSpeak(context.getString(R.string.create_schedule_about_what));
            }
        }
    }

    @Override
    public void onChoose(String voiceText) {

    }

    @Override
    public boolean isIntercept() {
        return false;
    }

    @Override
    public void onEnd() {

    }
}
