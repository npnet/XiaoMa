package com.xiaoma.assistant.scenarios;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.assistant.Interceptor.interceptor.DialbackInterceptor;
import com.xiaoma.assistant.Interceptor.interceptor.RedialInterceptor;
import com.xiaoma.assistant.Interceptor.manager.InterceptorManager;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.SortChooseCallback;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.api.ApiManager;
import com.xiaoma.assistant.manager.api.BluetoothPhoneApiManager;
import com.xiaoma.assistant.model.ConfirmWord;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.ContactListAdapter;
import com.xiaoma.assistant.ui.adapter.RouteListAdapter;
import com.xiaoma.assistant.utils.BluetoothUtils;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.assistant.utils.OpenAppUtils;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.config.ConfigConstants;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：拨号场景
 */
public class IatCallScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {

    private ContactListAdapter adapter;
    private String ttsCallContent;
    private String ttsFailedContent;
    private List<ContactBean> contactList;
    private String phoneNumber;

    private String[] speakContents = {"好嘞，你可以说打电话给张三，让我来帮你搜索哦", "打开了，你可以说打电话给张三，让我来帮你搜索哦"};

    public IatCallScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {

    }

    @Override
    public void onParser(String voiceJson, final LxParseResult parseResult, ParserLocationParam location, long session) {
        ttsCallContent = null;
        ttsFailedContent = null;
        String slotJson = parseResult.getSlots();
        final PhoneSlots phone = GsonHelper.fromJson(slotJson, PhoneSlots.class);
        if (phone == null) {
            speakThenListening(context.getString(R.string.no_call_target));
            setRobAction(AssistantConstants.RobActionKey.CALL);
            return;
        }
        if (!(!TextUtils.isEmpty(phone.name)
                && (phone.name.equals(Constants.ParseKey.ICALL)
                || phone.name.equals(Constants.ParseKey.BCALL)))) {
            if (!checkBluetoothConnected()) {
                return;
            }
        }
        this.parseResult = parseResult;
        if (TextUtils.isEmpty(slotJson) || slotJson.equals("{}")) {
            speakThenListening(context.getString(R.string.no_call_target));
            setRobAction(AssistantConstants.RobActionKey.CALL);
            return;
        }
        if (!TextUtils.isEmpty(phone.insType)) {
            handleInsType(phone.insType);

        } else if (!TextUtils.isEmpty(phone.code)) {
            setRobAction(AssistantConstants.RobActionKey.CALL);
            String text = !TextUtils.isEmpty(phone.name) ? "即将呼叫" + phone.name : "正在拨打" + phone.code;
            BluetoothPhoneApiManager.getInstance().dial(phone.code, text);

        } else if (!TextUtils.isEmpty(phone.name)) {
            switch (phone.name) {
                case Constants.ParseKey.ICALL:
                    setRobAction(AssistantConstants.RobActionKey.IB_CALL);
                    addFeedBackConversation(getString(R.string.right_dial_icall));
                    speakContent(getString(R.string.right_dial_icall), new WrapperSynthesizerListener() {
                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            closeVoicePopup();
                            sendCallBroadcast(ConfigConstants.XIAOMA_ASSISTANT_ICALL_ACTION);
                        }
                    });
                    return;
                case Constants.ParseKey.BCALL:
                    setRobAction(AssistantConstants.RobActionKey.IB_CALL);
                    addFeedBackConversation(getString(R.string.right_dial_bcall));
                    speakContent(getString(R.string.right_dial_bcall), new WrapperSynthesizerListener() {
                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            closeVoicePopup();
                            sendCallBroadcast(ConfigConstants.XIAOMA_ASSISTANT_BCALL_ACTION);
                        }
                    });
                    return;
            }
            setRobAction(parseResult.getOperation().equals("QUERY") ? AssistantConstants.RobActionKey.CHECK_RECORDS : AssistantConstants.RobActionKey.CALL);
            ArrayList<String> searchTargetList = new ArrayList<>();
            if (!TextUtils.isEmpty(parseResult.getData())) {
                ParseResult.DataBean dataBean = GsonHelper.fromJson(parseResult.getData(), ParseResult.DataBean.class);
                if (dataBean != null && dataBean.getResult() != null && dataBean.getResult().size() > 1) {
                    for (ParseResult.DataBean.ResultBean resultBean : dataBean.getResult()) {
                        if (!TextUtils.isEmpty(resultBean.getName()) && !searchTargetList.contains(resultBean.getName())) {
                            searchTargetList.add(resultBean.getName());
                        }
                    }
                }
            }
            BluetoothPhoneApiManager.getInstance().isContactBookSynchronized(new ApiManager.OnTrueListener() {
                @Override
                public void onTrue() {

                    BluetoothPhoneApiManager.getInstance().getAllContact(new IClientCallback.Stub() {
                        @Override
                        public void callback(Response response) throws RemoteException {
                            Bundle extra = response.getExtra();
                            List<ContactBean> list = extra.getParcelableArrayList(CenterConstants.BluetoothPhoneThirdBundleKey.GET_CALL_CONTACTS_RESULT);
                            if (!ListUtils.isEmpty(list)) {
                                filterNumber(list, phone);
                                filterName(list, searchTargetList, phone.name);
                                contactList = new ArrayList<>(list);

                                if (ListUtils.isEmpty(list)) {
                                    speakThenListening(!TextUtils.isEmpty(ttsFailedContent) ? ttsFailedContent : context.getString(R.string.not_found_any_contact));
                                } else if (list.size() == 1) {
                                    if (parseResult.getOperation().equals("QUERY")) {
                                        // 查找联系人的号码
                                        ttsCallContent = null;
                                        phoneNumber = list.get(0).getPhoneNum();
                                        String text = String.format(getString(R.string.found_one_number), phone.name, phoneNumber);
//                                        InterceptorManager.getInstance().setCurrentInterceptor(new CallInterceptor(list.get(0).getPhoneNum()), text);
                                        setAdapter(list);
                                        showMultiPageData(adapter, text);

                                    } else if (parseResult.getOperation().equals("DIAL")) {
                                        BluetoothPhoneApiManager.getInstance().dial(list.get(0).getPhoneNum(),
                                                !TextUtils.isEmpty(ttsCallContent) ? ttsCallContent : String.format(getString(R.string.call_target_name), phone.name));
                                        ttsCallContent = null;
                                    }
                                } else {
                                    if (parseResult.getOperation().equals("QUERY")) {
                                        // 查找联系人的号码
                                        setAdapter(list);
                                        String text = String.format(getString(R.string.found_several_number), phone.name, list.size());
                                        showMultiPageData(adapter, text);
                                        ttsCallContent = null;

                                    } else if (parseResult.getOperation().equals("DIAL")) {
                                        setAdapter(list);
                                        String text = String.format(getString(R.string.find_multi_number), list.size());
                                        showMultiPageData(adapter, text);
                                    }
                                }
                            } else {
                                speakThenListening(context.getString(R.string.not_found_any_contact));
                            }
                        }
                    });
                }
            });

        }
    }

    private void sendCallBroadcast(String type) {
        if (!AppUtils.isAppInstalled(context, CenterConstants.CAR_SERVICE)) {
            closeAfterSpeak(getString(R.string.please_install_service));
            return;
        }
        Intent intent = new Intent();
        intent.setAction(type);
        intent.setComponent(new ComponentName(CenterConstants.CAR_SERVICE, CenterConstants.CAR_SERVICE_RECEIVER));
        context.sendBroadcast(intent);
    }

    private void filterName(List<ContactBean> list, ArrayList<String> searchTargetList, String searchName) {
        for (int i = 0; i < list.size(); i++) {
            ContactBean bean = list.get(i);
            if (searchTargetList.size() <= 1 && !searchName.equals(bean.getName())) {
                list.remove(i--);
            } else if (searchTargetList.size() > 1) {
                boolean match = false;
                for (String name : searchTargetList) {
                    if (bean.getName().equals(name)) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    list.remove(i--);
                }
            }
        }
    }

    private void setAdapter(List<ContactBean> list) {
        if (adapter == null) {
            adapter = new ContactListAdapter(context, list);
            adapter.setOnMultiPageItemClickListener(IatCallScenario.this);
        } else {
            adapter.setData(list);
        }
    }

    private boolean checkBluetoothConnected() {
        if (!BluetoothUtils.isBluetoothConnected()) {
            if (OpenAppUtils.openApp(context.getApplicationContext(), CenterConstants.SETTING)) {
                NodeUtils.jumpTo(context, Constants.SystemSetting.PACKAGE_NAME_SETTING,
                        "com.xiaoma.setting.main.ui.MainActivity",
                        NodeConst.Setting.ASSISTANT_ACTIVITY + "/" + NodeConst.Setting.BLUETOOTH_CONNECT_FRAGMENT);
                closeVoicePopup();
            } else {
                closeAfterSpeak(getString(R.string.please_install_settings_first));
            }
            return false;
        }
        return true;
    }

    private void setTtsCallContent(String name, String type) {
        ttsCallContent = String.format(getString(R.string.calling_target_info), name, type);
        ttsFailedContent = context.getString(R.string.not_found_any_contact_two);
    }

    private void filterNumber(List<ContactBean> list, PhoneSlots phone) {

       /* if (list.size() > 0 && phone.location != null) {
            setTtsCallContent(phone.name, phone.location.cityAddr);
            // 筛选号码：指定地区

        }*/
        /*if (list.size() > 0 && !TextUtils.isEmpty(phone.teleOperator)) {
            setTtsCallContent(phone.name, phone.teleOperator);
            // 筛选号码：指定运营商(移动、电信、联通)

        }*/
        if (list.size() > 0 && !TextUtils.isEmpty(phone.category)) {
            setTtsCallContent(phone.name, phone.category);
            // 筛选号码：指定分类
            if (!TextUtils.isEmpty(phone.name)) {
                int category = -1;
                switch (phone.category) {
                    case "手机":
                        category = AssistantConstants.PHONE_TYPE.PBAP_NUMBER_TYPE_CELL;
                        break;
                    case "工作":
                    case "办公":
                        category = AssistantConstants.PHONE_TYPE.PBAP_NUMBER_TYPE_WORK;
                        break;
                    case "住宅":
                    case "家庭":
                        category = AssistantConstants.PHONE_TYPE.PBAP_NUMBER_TYPE_HOME;
                        break;
                }
                if (category != -1) {
                    for (int i = 0; i < list.size(); i++) {
                        ContactBean contactBean = list.get(i);
                        if (contactBean.getPhoneType() != category) {
                            list.remove(i--);
                        }
                    }
                }
            }
        }
        if (list.size() > 0 && !TextUtils.isEmpty(phone.headNum)) {
            setTtsCallContent(phone.name, phone.headNum);

            for (int i = 0; i < list.size(); i++) {
                ContactBean bean = list.get(i);
                int length = bean.getPhoneNum().length();
                if (length < 3 || !phone.headNum.equals(bean.getPhoneNum().substring(0, 3))) {
                    list.remove(i--);
                }
            }
        }
        if (list.size() > 0 && !TextUtils.isEmpty(phone.tailNum)) {
            setTtsCallContent(phone.name, phone.tailNum);

            for (int i = 0; i < list.size(); i++) {
                ContactBean bean = list.get(i);
                int length = bean.getPhoneNum().length();
                if (length < 4 || !phone.tailNum.equals(bean.getPhoneNum().substring(length - 4, length))) {
                    list.remove(i--);
                }
            }
        }
    }

    private void handleInsType(String insType) {
        switch (insType) {
            case "REDIAL":
                setRobAction(AssistantConstants.RobActionKey.CALL);
                BluetoothPhoneApiManager.getInstance().isContactBookSynchronized(new ApiManager.OnTrueListener() {
                    @Override
                    public void onTrue() {
                        BluetoothPhoneApiManager.getInstance().getRedialNumber(new ApiManager.onGetStringResultListener() {
                            @Override
                            public void onSuccess(String number) {
                                String handledNumber = StringUtil.format(getString(R.string.handled_pronunciation_phone_num), number);
                                String text = StringUtil.format(getString(R.string.make_sure_you_want_to_call_with_tab), handledNumber);
                                InterceptorManager.getInstance().setCurrentInterceptorWithoutAddFeedback(new RedialInterceptor(), text);
                                addFeedBackConversation(StringUtil.format(getString(R.string.make_sure_you_want_to_call), number));
                            }

                            @Override
                            public void onFailed() {
                                addFeedbackAndSpeak(getString(R.string.find_no_call_out_record));
                            }
                        });
                    }
                });
                break;

            case "CALLBACK":
                setRobAction(AssistantConstants.RobActionKey.CALL);
                BluetoothPhoneApiManager.getInstance().isContactBookSynchronized(new ApiManager.OnTrueListener() {
                    @Override
                    public void onTrue() {
                        BluetoothPhoneApiManager.getInstance().getDialBackNumber(new ApiManager.onGetStringResultListener() {
                            @Override
                            public void onSuccess(String number) {
                                String handledNumber = StringUtil.format(getString(R.string.handled_pronunciation_phone_num), number);
                                String text = StringUtil.format(getString(R.string.make_sure_you_want_to_call_with_tab), handledNumber);
                                InterceptorManager.getInstance().setCurrentInterceptorWithoutAddFeedback(new DialbackInterceptor(), text);
                                addFeedBackConversation(StringUtil.format(getString(R.string.make_sure_you_want_to_call), number));
                            }

                            @Override
                            public void onFailed() {
                                addFeedbackAndSpeak(getString(R.string.find_no_incoming_record));
                            }
                        });
                    }
                });
                break;

            case "SYNCHRONIZE":
                setRobAction(AssistantConstants.RobActionKey.CHECK_RECORDS);
                BluetoothPhoneApiManager.getInstance().synchronizeContactBook(null);
                break;

            case "received":// 打开已接电话界面
            case "dialed": // 打开已拨电话界面
            case "records":// 打开通话记录界面
            case "之前拨打":
                setRobAction(AssistantConstants.RobActionKey.CHECK_RECORDS);
                BluetoothPhoneApiManager.getInstance().isContactBookSynchronized(new ApiManager.OnTrueListener() {
                    @Override
                    public void onTrue() {
                        if (OpenAppUtils.openApp(context.getApplicationContext(), CenterConstants.BLUETOOTH_PHONE)) {
                            NodeUtils.jumpTo(context, CenterConstants.BLUETOOTH_PHONE,
                                    "com.xiaoma.bluetooth.phone.main.ui.MainActivity",
                                    NodeConst.BluetoothPhone.MAIN_ACTIVITY + "/"
                                            + NodeConst.BluetoothPhone.CALL_RECORDS);
                            closeVoicePopup();
                        } else {
                            closeAfterSpeak(getString(R.string.please_install_bluetooth_phone_first));
                        }
                    }
                });
                return;

            case "missed":// 打开未接电话界面
                setRobAction(AssistantConstants.RobActionKey.CHECK_RECORDS);
                BluetoothPhoneApiManager.getInstance().isContactBookSynchronized(new ApiManager.OnTrueListener() {
                    @Override
                    public void onTrue() {
                        if (OpenAppUtils.openApp(context.getApplicationContext(), CenterConstants.BLUETOOTH_PHONE)) {
                            NodeUtils.jumpTo(context,
                                    CenterConstants.BLUETOOTH_PHONE,
                                    "com.xiaoma.bluetooth.phone.main.ui.MainActivity",
                                    NodeConst.BluetoothPhone.MAIN_ACTIVITY + "/" + NodeConst.BluetoothPhone.MISSED_CALL);
                            closeVoicePopup();
                        } else {
                            closeAfterSpeak(getString(R.string.please_install_bluetooth_phone_first));
                        }
                    }
                });
                return;

            case "CONTACTS":
                setRobAction(AssistantConstants.RobActionKey.CHECK_RECORDS);
                BluetoothPhoneApiManager.getInstance().isContactBookSynchronized(new ApiManager.OnTrueListener() {
                    @Override
                    public void onTrue() {
                        if (OpenAppUtils.openApp(context.getApplicationContext(), CenterConstants.BLUETOOTH_PHONE)) {
                            NodeUtils.jumpTo(context, CenterConstants.BLUETOOTH_PHONE,
                                    "com.xiaoma.bluetooth.phone.main.ui.MainActivity",
                                    NodeConst.BluetoothPhone.MAIN_ACTIVITY + "/"
                                            + NodeConst.BluetoothPhone.CONTACT_AND_COLLECTION
                                            + "/" + NodeConst.BluetoothPhone.CONTACT);
                            closeAfterSpeak(speakContents[new Random().nextInt(speakContents.length)]);
                        } else {
                            closeAfterSpeak(getString(R.string.please_install_bluetooth_phone_first));
                        }
                    }
                });
                return;
        }
    }


    @Override
    public void onChoose(String voiceText) {
        final boolean isFirstPage = assistantManager.getMultiPageView().isFirstPage();
        final boolean isLastPage = assistantManager.getMultiPageView().isLastPage();
        switchChooseAction(voiceText, new SortChooseCallback() {
            @Override
            public void previousPageAction() {
                if (isFirstPage) {
                    KLog.d("It is the last page");
                } else {
                    assistantManager.getMultiPageView().setPage(-1);
                }
            }

            @Override
            public void nextPageAction() {
                if (isLastPage) {
                    KLog.d("It is the last page");
                } else {
                    assistantManager.getMultiPageView().setPage(1);
                }
            }

            @Override
            public void chooseItemAction(int action) {
                KLog.d("call for some one");
                onItemClick(action);
            }

            @Override
            public void lastAction() {
                KLog.d(adapter.getCurrentList().get(adapter.getCurrentList().size() - 1));
                KLog.d("choose last one");
            }

            @Override
            public void cancelChooseAction() {
//                stopListening();
//                assistantManager.hideMultiPageView();
//                startListening();
                assistantManager.closeAssistant();
            }

            @Override
            public void errorChooseActon() {
                KLog.d("choose error");
            }

            @Override
            public void assignPageAction(int page) {
                assistantManager.getMultiPageView().setPageForIndex(page);
            }

            @Override
            public void filterByHeadNumber(String headNumber) {
                if (ListUtils.isEmpty(contactList)) return;
                List<ContactBean> list = new ArrayList<>();
                int position = 0;
                for (int i = 0; i < contactList.size(); i++) {
                    ContactBean contactBean = contactList.get(i);
                    String phoneNum = contactBean.getPhoneNum();
                    if (phoneNum.length() >= 3 && phoneNum.substring(0, 3).equals(headNumber)) {
                        position = i;
                        list.add(contactBean);
                    }
                }
                adapter.setData(list);
                if (list.size() == 1) {
                    onItemClick(position);
                }
            }

            @Override
            public void confirm() {
                onItemClick(0);
                phoneNumber = null;
            }

            @Override
            public void cancel() {
                closeVoicePopup();
                phoneNumber = null;
            }

            @Override
            public void correct() {
                assistantManager.hideMultiPageView();
                speakThenListening(context.getString(R.string.please_say_contact_name));
            }

        });
    }

    @Override
    public boolean isIntercept() {
        return true;
    }

    @Override
    public void onEnd() {

    }

    @Override
    protected String getSrSceneStksCmd() {
        if (assistantManager == null) {
            return "";
        }
        if (assistantManager.getMultiPageView() == null) {
            return "";
        }
        if (adapter == null) {
            return "";
        }
        List<ContactBean> list = adapter.getCurrentList();
        if (ListUtils.isEmpty(list)) {
            return "";
        }
        StksCmd stksCmd = new StksCmd();
        stksCmd.setType("phone");
        stksCmd.setNliScene("phone");
        ArrayList<String> search = new ArrayList<>();
        search.add("semantic.slots.num");
        search.add("semantic.slots.item");
        search.add("semantic.slots.default");
        stksCmd.setNliFieldSearch(search);
        ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
        if (!TextUtils.isEmpty(phoneNumber)) {
            addConfirmCmd(stksCmdNliScenes, search, new ConfirmWord("呼叫", true));
        } else {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
                stksCmdNliScene.setId(i + 1);
                ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
                if (!TextUtils.isEmpty(list.get(i).getPhoneNum())) {
                    StksCmdDimension stksCmdDimension = new StksCmdDimension();
                    stksCmdDimension.setField("num");
                    String phoneNumber = list.get(i).getPhoneNum();
                    stksCmdDimension.setVal(phoneNumber);
                    dimensions.add(stksCmdDimension);
                }
                addDefaultCmdByNumber(i, size, dimensions);
                if (!ListUtils.isEmpty(dimensions))
                    stksCmdNliScene.setDimension(dimensions);
                stksCmdNliScenes.add(stksCmdNliScene);
            }
            stksCmdNliScenes.addAll(getDefaultCmd(size));
            addFilterByHeadNumberCmd(stksCmdNliScenes, search, list);
            addCorrectCmd(stksCmdNliScenes, search);
        }
        stksCmd.setList(stksCmdNliScenes);
        String result = GsonHelper.toJson(stksCmd);
        KLog.json(result);
        return result;
    }

    @Override
    public void onItemClick(int position) {
        adapter.setSelectPosition(position);
        BluetoothPhoneApiManager.getInstance()
                .dial(adapter.getCurrentList()
                        .get(position)
                        .getPhoneNum(), !TextUtils.isEmpty(ttsCallContent) ? ttsCallContent : String.format(getString(R.string.call_target_position), position + 1));
        ttsCallContent = null;
    }

    class PhoneSlots {
        public String code;
        public String name;
        public String category;
        public String headNum;
        public String tailNum;
        public String insType;
        public String teleOperator; //运营商
        public Location location;
        public String alias_name;

        public class Location {
            /**
             * city : 合肥市
             * cityAddr : 合肥
             * type : LOC_BASIC
             */
            public String city;
            public String cityAddr;
            public String type;
        }
    }

}
