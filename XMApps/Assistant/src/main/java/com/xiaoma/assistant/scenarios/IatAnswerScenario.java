package com.xiaoma.assistant.scenarios;

import android.content.Context;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.processor.AssistantProcessorChain;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.utils.VrConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：问答场景
 */
public class IatAnswerScenario extends IatScenario {

    //随机动作
    public static int[] mAction = new int[]{AssistantConstants.RobActionKey.ROB_ACTION_ANSWER_RANDOM1
            , AssistantConstants.RobActionKey.ROB_ACTION_ANSWER_RANDOM2
            , AssistantConstants.RobActionKey.ROB_ACTION_ANSWER_RANDOM3};

    public IatAnswerScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
//        String result = parseResult.getAnswer();
        XmCarFactory.getCarVendorExtensionManager().setRobAction(getAction());
        if (parseResult.getService().equals("images")) {
//            IatImageScenario iatImageScenario = new IatImageScenario(context);
            IatImageScenario iatImageScenario = ScenarioDispatcher.getInstance().getIatImageScenario(context);
            AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().setCurrentScenario(iatImageScenario);
            iatImageScenario.onParser(voiceJson, parseResult, location, session);
            return;
        }
        String result = null;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(parseResult.getSlots());
            result = jsonObject.getString("tts");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject==null){
            try {
                jsonObject = new JSONObject(parseResult.getAnswer());
                if (jsonObject.has("text")){
                    result = jsonObject.getString("text");
                    addFeedbackAndSpeak(result);
                    return;
                }
                speakUnderstand();
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (jsonObject == null) {
            speakUnderstand();
            return;
        }
        String data = jsonObject.optString("text");
        if (parseResult.getInputResult().contains(getString(R.string.name_to_you))) {
            data = getString(R.string.answer_to_name);
        }
        addFeedBackConversation("百科查询结果如下：");
        closeAfterSpeak(result);
        //创建一个conversation并展示
        ConversationItem conversationItem = parseResult.createReceiveConversation(
                VrConstants.ConversationType.OTHER, result);
        addConversationToList(conversationItem);
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

    /**
     * 随机动作
     */
    public static int getAction() {
        Random random = new Random();
        int i = random.nextInt(mAction.length);
        return mAction[i];
    }
}
