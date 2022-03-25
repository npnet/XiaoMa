package com.xiaoma.assistant.scenarios;


import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.utils.AssistantUtils;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.smarthome.common.callback.SMIatCallback;
import com.xiaoma.smarthome.common.manager.SmartHomeIatManager;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author: wuzongwei
 * @date: 2019/5/21 1458
 * 智能家居场景
 */
public class IatAllSmartHomeScenario extends IatScenario {

    private SourceInfo mLocalSourceInfo;

    public IatAllSmartHomeScenario(Context context) {
        super(context);
        mLocalSourceInfo = new SourceInfo(context.getPackageName(), 100);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        if (!NetworkUtils.isConnected(context)){
            closeAfterSpeak(getString(R.string.network_errors));
            return;
        }
        if (StringUtil.isNotEmpty(parseResult.getText())){
            sendMessageXiaomiSmartHome(parseResult.getText());
        }else {
            int speakRandom = AssistantUtils.getUnStandWord();
            String unStandSpeak = getString(AssistantUtils.mUnStands_Speak[speakRandom]);
            String unStand = getString(AssistantUtils.mUnStands[speakRandom]);
            speakAndFeedListening(unStandSpeak, unStand);
        }
    }

    private void sendMessageXiaomiSmartHome(String text) {
        setRobAction(AssistantConstants.SmartHomeType.XIAOMI_SMARTHOME);
        SourceInfo remote = new SourceInfo(CenterConstants.LAUNCHER, CenterConstants.LAUNCHER_PORT);
        Bundle bundle = new Bundle();
        bundle.putString(CenterConstants.AssistantSmartHomeBundleKey.XIAOMI_SMARTHOME_MESSAGE, text);
        Request request = new Request(mLocalSourceInfo, new RequestHead(remote, CenterConstants.AssistantSmartHomeAction.XIAOMI_SMARTHOME_MESSAGE), bundle);
        IClientCallback.Stub stub = new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle bundle = response.getExtra();
                String result = bundle.getString(CenterConstants.AssistantSmartHomeBundleKey.XIAOMI_SMARTHOME_CALLBACK);
                closeAfterSpeak(result);
            }
        };
        Linker.getInstance().request(request, stub);
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
