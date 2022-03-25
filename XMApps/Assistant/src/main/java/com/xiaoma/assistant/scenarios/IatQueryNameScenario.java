package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: iSun
 * @date: 2019/5/14 0014
 */
class IatQueryNameScenario extends IatScenario {
    public IatQueryNameScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String key = context.getString(R.string.def_wakeup_text);
        String wakeupList = TPUtils.get(context, VrConfig.WAKE_UP_WORD_KEY, "");
        if (key != null && !TextUtils.isEmpty(key)) {
            List<String> strings = GsonHelper.fromJsonToList(wakeupList, String[].class);
            for (int i = 0; i < strings.size(); i++) {
                key += "或\"" + strings.get(i) + "\"";
            }
        }
        String text = context.getString(R.string.wakeup_text_start) + key + context.getString(R.string.wakeup_text_end);

        ConversationItem conversationItem = parseResult.createReceiveConversation(
                VrConstants.ConversationType.OTHER, text);
        addConversationToList(conversationItem);
        speakContent(text);
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
