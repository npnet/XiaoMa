package com.xiaoma.assistant.scenarios;


import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.iflytek.speech.util.NetworkUtil;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
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
public class IatSmartHomeScenario extends IatScenario {

    private SourceInfo mLocalSourceInfo;

    public IatSmartHomeScenario(Context context) {
        super(context);
        mLocalSourceInfo = new SourceInfo(context.getPackageName(), 100);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        try {
            if (!NetworkUtils.isConnected(context)){
                closeAfterSpeak(getString(R.string.network_errors));
                return;
            }
            JSONObject slots = new JSONObject(parseResult.getSlots());
            if (slots.has("insType")) {
                String insType = slots.getString("insType");
                if (!LoginTypeManager.getInstance().canUse(LoginCfgConstant.XIAOMA_SMART_HOME, new OnBlockCallback() {
                    @Override
                    public void handle(LoginType loginType) {
                        addFeedbackAndSpeak(LoginTypeManager.getPrompt(context),new WrapperSynthesizerListener(){
                            @Override
                            public void onCompleted() {
                                AssistantManager.getInstance().closeAssistant();
                            }
                        });
                    }
                })) return;
                instructionDispatcher(parseResult, insType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void instructionDispatcher(LxParseResult parseResult, String insType) throws JSONException {

        switch (insType) {
            case "OPEN"://回家离家模式
                setGoHomeAndGowild(parseResult);

                break;
            case "QUERY_DEVICE"://哪些设备在线
                setDeviceOnline();
                break;
            case "REFRESH_SCENE_LIST"://刷新场景列表
                setRefreshList();
                break;
            case "SCENES"://启动XX场景
                openSomeScenes();
                break;
        }

    }

    //启动XX场景
    private void openSomeScenes() {

        setRobAction(AssistantConstants.SmartHomeType.SCENE_SWITCHING);
        SmartHomeIatManager.getInstance().excuteScene("XX场景", new SMIatCallback() {
            @Override
            public void callback(boolean result) {
                if (result) {
                    closeAfterSpeak(getString(R.string.scene_switching_success));
                } else {
                    closeAfterSpeak(getString(R.string.scene_switching_failure));
                }
            }

            @Override
            public void callback(boolean result, List<String> list) {

            }
        });
    }

    //刷新场景列表
    private void setRefreshList() {
        setRobAction(AssistantConstants.SmartHomeType.REFRESH_LIST);

        SourceInfo remote = new SourceInfo(CenterConstants.LAUNCHER, CenterConstants.LAUNCHER_PORT);
        Request request = new Request(mLocalSourceInfo, new RequestHead(remote, CenterConstants.AssistantSmartHomeAction.REFRESH_SCENE_LIST), null);
        IClientCallback.Stub stub = new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle bundle = response.getExtra();
                boolean result = bundle.getBoolean(CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME_REFRESH_SCENE_CALLBACK);
                if (result) {
                    closeAfterSpeak(getString(R.string.refresh_list_success));
                } else {
                    closeAfterSpeak(getString(R.string.refresh_list_failure));
                }

                Log.d(IatSmartHomeScenario.class.getSimpleName(),
                        "Refresh scenario list. result = " + result);
            }
        };
        Linker.getInstance().request(request, stub);
    }

    //哪些设备在线
    private void setDeviceOnline() {
        //我有哪些设备在线
        setRobAction(AssistantConstants.SmartHomeType.MY_DEVICE);
        SourceInfo remote = new SourceInfo(CenterConstants.LAUNCHER, CenterConstants.LAUNCHER_PORT);
        Request request = new Request(mLocalSourceInfo, new RequestHead(remote, CenterConstants.AssistantSmartHomeAction.WHICH_DEVICES_ONLINE), null);
        IClientCallback.Stub stub = new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle bundle = response.getExtra();
                String result = bundle.getString(CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME_DEVICES_ONLINE_CALLBACK);
                if (StringUtil.isNotEmpty(result)) {
                    if ("-1".equals(result)) {
                        closeAfterSpeak(getString(R.string.my_device_failure));
                    } else {
                        closeAfterSpeak(StringUtil.format(getString(R.string.my_device_success), result));
                    }
                } else {
                    closeAfterSpeak(getString(R.string.not_equipment_online));
                }
                Log.d("zs", "result:" + result);
            }
        };
        Linker.getInstance().request(request, stub);
    }

    //设置回家离家模式
    private void setGoHomeAndGowild(LxParseResult parseResult) throws JSONException {
        JSONObject jsonObject = new JSONObject(parseResult.getSlots());
        if (jsonObject.has("scene")) {
            String type = jsonObject.getString("scene");
            // TODO: 2019/7/29
            if ("回家".equals(type)) {
                SourceInfo remote = new SourceInfo(CenterConstants.LAUNCHER, CenterConstants.LAUNCHER_PORT);
                Bundle bundle = new Bundle();
                bundle.putString(CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME, type);
                Request request = new Request(mLocalSourceInfo, new RequestHead(remote, CenterConstants.AssistantSmartHomeAction.SCENE_CONTROL_ACTION), bundle);
                IClientCallback.Stub stub = new IClientCallback.Stub() {
                    @Override
                    public void callback(Response response) throws RemoteException {
                        Bundle bundle = response.getExtra();
                        Boolean result = bundle.getBoolean(CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME_CALLBACK);
                        if (result) {
                            closeAfterSpeak(getString(R.string.come_back_home_text));
                        } else {
                            closeAfterSpeak(getString(R.string.scene_switching_failure));
                        }
                        Log.d("zs", "result:" + result);
                    }
                };
                Linker.getInstance().request(request, stub);
                setRobAction(AssistantConstants.SmartHomeType.COME_BACK_HOME);
            } else if ("离家".equals(type)) {
                SourceInfo remote = new SourceInfo(CenterConstants.LAUNCHER, CenterConstants.LAUNCHER_PORT);
                Bundle bundle = new Bundle();
                bundle.putString(CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME, type);
                Request request = new Request(mLocalSourceInfo, new RequestHead(remote, CenterConstants.AssistantSmartHomeAction.SCENE_CONTROL_ACTION), bundle);
                IClientCallback.Stub stub = new IClientCallback.Stub() {
                    @Override
                    public void callback(Response response) throws RemoteException {
                        Bundle bundle = response.getExtra();
                        Boolean result = bundle.getBoolean(CenterConstants.AssistantSmartHomeBundleKey.SCENE_NAME_CALLBACK);
                        if (result) {
                            closeAfterSpeak(getString(R.string.leave_home_text));
                        } else {
                            closeAfterSpeak(getString(R.string.scene_switching_failure));
                        }
                        Log.d("zs", "result:" + result);
                    }
                };
                Linker.getInstance().request(request, stub);
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
