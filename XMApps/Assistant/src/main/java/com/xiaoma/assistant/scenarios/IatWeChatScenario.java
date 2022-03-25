package com.xiaoma.assistant.scenarios;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.Interceptor.interceptor.MessageContentInterceptor;
import com.xiaoma.assistant.Interceptor.manager.InterceptorManager;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IChooseCallback;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.model.StksCmd;
import com.xiaoma.assistant.model.StksCmdDimension;
import com.xiaoma.assistant.model.StksCmdNliScene;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.ui.adapter.WeChatContactAdapter;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;
import com.xiaoma.vrfactory.tts.XmTtsManager;
import com.xiaoma.wechat.callback.SimpleWeChatCallback;
import com.xiaoma.wechat.manager.WeChatManager;
import com.xiaoma.wechat.model.WeChatContact;
import com.xiaoma.wechat.manager.WeChatManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by qiuboxiang on 2019/2/20 19:34
 * Desc: 车载微信场景
 */
public class IatWeChatScenario extends IatScenario implements BaseMultiPageAdapter.OnMultiPageItemClickListener {

    private int notFoundCount;
    private String message;
    private String targetContactID;
    private WeChatContactAdapter adapter;
    private int[] askForContentWord = new int[]{R.string.ask_for_send_content_1, R.string.ask_for_send_content_2};
    private String currentMessageID;
    private boolean onlyCheck;
    private List<WeChatContact> contactList = new ArrayList<>();
    private boolean isWeixinForeground;
    private boolean isSended;

    public IatWeChatScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {
        WeChatManagerFactory.getManager().addCallback(new SimpleWeChatCallback() {
            @Override
            public void onSendMessageResult(String messageId, boolean success) {
                if (AssistantManager.getInstance().isShowing() && messageId.equals(currentMessageID)) {
                    currentMessageID = null;
                    speakContent(context.getString(success ? R.string.send_success : R.string.send_failed),new WrapperSynthesizerListener(){
                        @Override
                        public void onCompleted() {
                            closeVoicePopup();
//                            WeChatManagerFactory.getManager().openChatPage(targetContactID);
                            targetContactID = null;
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        isSended = false;
        boolean foundResult = true;
        if (!WeChatManager.getInstance().checkWxExist()) {
            addFeedbackAndSpeak(context.getString(R.string.please_install_wechat_first), new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    closeVoicePopup();
                }
            });
            return;
        }
        boolean logined = WeChatManagerFactory.getManager().isLogined();
        if (!logined) {
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI_SET_HOME);
            WeChatManagerFactory.getManager().startWechat();
            closeVoicePopup();
            /*addFeedbackAndSpeak(context.getString(R.string.please_login_weixin_first), new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    closeVoicePopup();
                }

                @Override
                public void onError(int code) {
                    closeVoicePopup();
                }
            });*/
            return;
        }
        this.parseResult = parseResult;
        String slotJson = parseResult.getSlots();
        if (TextUtils.isEmpty(slotJson) || slotJson.equals("{}")) {
            if ("SYNTH".equals(parseResult.getOperation())) {
                playMsg();
            } else {
                askForReceiver();
            }
        } else {
            MessageSlots phone = GsonHelper.fromJson(slotJson, MessageSlots.class);
            if (phone == null) {
                speakUnderstand();
                return;
            }
            if ("unread".equals(phone.category)) {
                playMsg();
                return;
            }
            setRobAction(AssistantConstants.RobActionKey.CHECK_RECORDS);
            if ("OPEN".equals(phone.insType)) {
                addFeedbackAndSpeak(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        closeVoicePopup();
                        WeChatManagerFactory.getManager().startWechat();
                    }
                });
                return;
            }
            if (!TextUtils.isEmpty(phone.name)) {
                // 查找联系人 交互7
                onlyCheck = true;
                foundResult = searchContact(foundResult, phone.name, true);
            } else {
                targetContactID = null;
                onlyCheck = false;
                if (!TextUtils.isEmpty(phone.receiver) || !TextUtils.isEmpty(targetContactID)) {
                    // 查找联系人 交互6
                    if (!TextUtils.isEmpty(phone.receiver)) {
                        message = phone.content;
                        uploadWeChatState(false);
                        foundResult = searchContact(foundResult, phone.receiver, false);

                        String contactID;
                        if (!TextUtils.isEmpty((contactID = getContactID()))) {
                            targetContactID = contactID;
                        }
                        message = null;
                    }
                } else {
                    askForReceiver();
                }

                if ((!TextUtils.isEmpty(phone.content) || !TextUtils.isEmpty(message)) && !isSended) {
                    if (!sendMessage(phone.content)) {
                        message = phone.content;
                    }
                }
               /* else {
                    if (!TextUtils.isEmpty(getContactID()) || !TextUtils.isEmpty(targetContactID)) {
                        askForMessageContent();
                    }
                }*/
            }

            if (foundResult) {
                notFoundCount = 0;
            }
        }
    }

    public void uploadWeChatState(boolean isForeground) {
        if (isWeixinForeground == isForeground) {
            return;
        }
        isWeixinForeground = isForeground;
        RemoteIatManager.getInstance().uploadAppState(isForeground, AppType.WEIXIN);
    }

    public void playMsg() {
        // 播报消息
        setRobAction(AssistantConstants.RobActionKey.CALL);
        WeChatManagerFactory.getManager().playMsg();
        closeVoicePopup();
    }

    private void askForReceiver() {
        uploadWeChatState(true);
        speakThenListening(context.getString(R.string.who_is_receiver));
    }

    private void askForMessageContent() {
        String ttsContent = context.getString(getAskForContentWord());
        InterceptorManager.getInstance().setCurrentInterceptor(new MessageContentInterceptor(), ttsContent);
    }

    private boolean sendMessage(String content) {
        String contactID;
        if (!TextUtils.isEmpty((contactID = getContactID()))) {
            sendMessage(contactID, content);
            return true;
        } else if (!TextUtils.isEmpty(targetContactID)) {
            sendMessage(targetContactID, content);
            return true;
        }
        return false;
    }

    private String getContactID() {
        if (adapter == null || ListUtils.isEmpty(contactList) || contactList.size() != 1) {
            return null;
        }
        return contactList.get(0).getId();
    }

    private void sendMessage(String contactID, String message) {
        isSended = true;
        currentMessageID = WeChatManagerFactory.getManager().sendMessage(contactID, message);
        this.message = null;
    }

    private boolean searchContact(boolean foundResult, String name, boolean onlyCheck) {
        List<WeChatContact> list = WeChatManager.getInstance().queryContacts(name);
        contactList = list;
        if (ListUtils.isEmpty(list)) {
            uploadWeChatState(true);
            speakThenListening(StringUtil.format(context.getString(notFoundCount > 0 ? R.string.not_found_any_wx_contact_again : R.string.not_found_any_wx_contact), name));
            foundResult = false;
            notFoundCount++;
        } else if (list.size() == 1) {
//            showData(list, context.getString(notFoundCount > 0 ? R.string.found_result : R.string.ok));
            targetContactID = list.get(0).getId();
            chooseTargetContact();
        } else {
            if (onlyCheck) {
                showData(list, StringUtil.format(context.getString(notFoundCount > 0 ? R.string.which_to_check : R.string.found_friends), list.size()));
            } else {
                showData(list, StringUtil.format(context.getString(notFoundCount > 0 ? R.string.which_to_check_with_send_type : R.string.found_friends_with_send_type), list.size()));
            }
        }
        return foundResult;
    }

    private int getAskForContentWord() {
        Random random = new Random();
        int index = random.nextInt(askForContentWord.length);
        return askForContentWord[index];
    }

    private void showData(List<WeChatContact> list, String ttsContent) {
        if (adapter == null) {
            adapter = new WeChatContactAdapter(context, list);
            adapter.setOnMultiPageItemClickListener(IatWeChatScenario.this);
        } else {
            adapter.setData(list);
        }
        if (list.size() != 1) {
            showMultiPageData(adapter, ttsContent);
        }
    }

    @Override
    public void onChoose(String voiceText) {
        final boolean isFirstPage = assistantManager.getMultiPageView().isFirstPage();
        final boolean isLastPage = assistantManager.getMultiPageView().isLastPage();
        switchChooseAction(voiceText, new IChooseCallback() {
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
                speakContent(context.getString(R.string.ok));
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

        });
    }

    @Override
    protected String getSrSceneStksCmd() {
        {
            if (assistantManager == null) {
                return "";
            }
            if (assistantManager.getMultiPageView() == null) {
                return "";
            }
            if (adapter == null) {
                return "";
            }
            List<WeChatContact> list = adapter.getCurrentList();
            if (ListUtils.isEmpty(list)) {
                return "";
            }
            StksCmd stksCmd = new StksCmd();
            stksCmd.setType("phone");
            stksCmd.setNliScene("phone");
            ArrayList<String> search = new ArrayList<>();
            search.add("semantic.slots.name");
            search.add("semantic.slots.item");
            search.add("semantic.slots.default");
            stksCmd.setNliFieldSearch(search);
            ArrayList<StksCmdNliScene> stksCmdNliScenes = new ArrayList<>();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                StksCmdNliScene stksCmdNliScene = new StksCmdNliScene();
                stksCmdNliScene.setId(i + 1);
                ArrayList<StksCmdDimension> dimensions = new ArrayList<>();
                addNameCmd(dimensions, list.get(i).getNick());
                addNameCmd(dimensions, list.get(i).getRemark());
                addDefaultCmdByNumber(i, size, dimensions);
                if (!ListUtils.isEmpty(dimensions))
                    stksCmdNliScene.setDimension(dimensions);
                stksCmdNliScenes.add(stksCmdNliScene);
            }
            stksCmdNliScenes.addAll(getDefaultCmd(size));
            stksCmd.setList(stksCmdNliScenes);
            String result = GsonHelper.toJson(stksCmd);
            KLog.json(result);
            return result;
        }
    }

    private void addNameCmd(ArrayList<StksCmdDimension> dimensions, String name) {
        if (!TextUtils.isEmpty(name)) {
            StksCmdDimension stksCmdDimension = new StksCmdDimension();
            stksCmdDimension.setField("name");
            stksCmdDimension.setVal(name);
            dimensions.add(stksCmdDimension);
        }
    }

    @Override
    public void onItemClick(int position) {
        targetContactID = adapter.getCurrentList().get(position).getId();
        chooseTargetContact();
    }

    private void chooseTargetContact() {
        if (onlyCheck) {
            speakContent(context.getString(notFoundCount > 0 ? R.string.found_result : R.string.ok), new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    closeVoicePopup();
                    WeChatManagerFactory.getManager().openChatPage(targetContactID);
                    targetContactID = null;
                }
            });
        } else {
            if (TextUtils.isEmpty(message)) {
                assistantManager.hideMultiPageView();
                askForMessageContent();
            } else {
                sendMessage(targetContactID, message);
            }
        }
    }

    @Override
    public boolean isIntercept() {
        return false;
    }

    @Override
    public void onEnd() {

    }

    public void setMessageContent(String text) {
        sendMessage(text);
    }

    class MessageSlots {
        public String name;
        public String content;
        public String contentType;
        public String receiver;
        public String insType;
        public String category;
    }
}
