package com.xiaoma.wechat.manager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.WeChatContactList;
import com.xiaoma.wechat.callback.WeChatCallback;
import com.xiaoma.wechat.model.WeChatContact;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import cn.xylink.faw.bab2019.WxContactInfo;
import cn.xylink.faw.bab2019.WxContactListener;
import cn.xylink.faw.bab2019.WxFaceListener;
import cn.xylink.faw.bab2019.WxKeyEventListener;
import cn.xylink.faw.bab2019.WxLibrary;
import cn.xylink.faw.bab2019.WxSendMsgListener;
import cn.xylink.faw.bab2019.WxStatusListener;

/**
 * Created by qiuboxiang on 2019/5/21 11:39
 * Desc: 车载微信
 */
public class WeChatManager implements IWeChatManager, WxContactListener, WxSendMsgListener, WxStatusListener, WxFaceListener, WxKeyEventListener {

    private static final String TAG = "QBX " + WeChatManager.class.getSimpleName();
    private static WeChatManager instance;
    private List<WeChatContact> contactList;
    private Context context;
    private List<WeChatCallback> callbackList = new CopyOnWriteArrayList<>();
    private AtomicInteger sequenceGenerator = new AtomicInteger();
    private String cookie;
    private SparseArray<String> keyEventArray;
    private String    weChatPackageName = "com.xylink.mc.faw.bab2019";

    public static WeChatManager getInstance() {
        if (instance == null) {
            synchronized (WeChatManager.class) {
                if (instance == null) {
                    instance = new WeChatManager();
                }
            }
        }
        return instance;
    }

    @Override
    public void init(Context context) {
        Log.d(TAG, "init: ");
        this.context = context;
        RemoteIatManager.getInstance().init(context);
        initKeyEventMap();
        WxLibrary.getInstance().init(context);
        WxLibrary.getInstance().setWeContactListener(this);//获取微信联系人列表
        WxLibrary.getInstance().setWeSendMsgListener(this);//发送消息监听器
        WxLibrary.getInstance().setWeStatusListener(this);//登录状态监听器
        WxLibrary.getInstance().setWeFaceListener(this);//车载微信界面可见性监听器
        WxLibrary.getInstance().setWxKeyEventListener(this);//转发按键事件结果监听器
    }

    private void initKeyEventMap() {
        keyEventArray = new SparseArray<>();
        keyEventArray.put(WxLibrary.KEY_PREV, "KEY_PREV");
        keyEventArray.put(WxLibrary.KEY_NEXT, "KEY_NEXT");
        keyEventArray.put(WxLibrary.KEY_VOICE_DOWN, "KEY_VOICE_DOWN");
        keyEventArray.put(WxLibrary.KEY_VOICE_UP, "KEY_VOICE_UP");
    }

    @Override
    public List<WeChatContact> getContactList() {
        Log.d(TAG, "getContactList: " + (contactList == null ? 0 : contactList.size()));
        return new ArrayList<>(contactList);
    }

    @Override
    public boolean isLogined() {
        boolean isLogined = WxLibrary.getInstance().isLogined();
        Log.d(TAG, "getContactList: " + isLogined);
        return isLogined;
    }

    @Override
    public String sendMessage(String contactId, String message) {
        String messageId = getId();
        Log.d(TAG, "sendMessage: contactId=" + contactId + "   messageId=" + messageId + "   message=" + message);
        WxLibrary.getInstance().sendMessage(contactId, messageId, message);
        return messageId;
    }

    @Override
    public void playMsg() {
        Log.d(TAG, "playMsg: ");
        WxLibrary.getInstance().playMsg();
    }

    @Override
    public void startWechat() {
        Log.d(TAG, "startWechat: ");
        WxLibrary.getInstance().startWechat();
    }

    @Override
    public boolean isWeMain() {
        boolean isWeMain = WxLibrary.getInstance().isWeMain();
        Log.d(TAG, "isWeMain: " + isWeMain);
        return isWeMain;
    }

    @Override
    public boolean isContact() {
        boolean isContact = WxLibrary.getInstance().isContact();
        Log.d(TAG, "isContact: " + isContact);
        return isContact;
    }

    @Override
    public boolean isConversion() {
        boolean isConversion = WxLibrary.getInstance().isConversion();
        Log.d(TAG, "isConversion: " + isConversion);
        return isConversion;
    }

    @Override
    public void onPrevKeyEvent() {
        Log.d(TAG, "onPrevKeyEvent: ");
        WxLibrary.getInstance().onKeyEvent(WxLibrary.KEY_PREV);
    }

    @Override
    public void onNextKeyEvent() {
        Log.d(TAG, "onNextKeyEvent: ");
        WxLibrary.getInstance().onKeyEvent(WxLibrary.KEY_NEXT);
    }

    @Override
    public void onDownKeyEvent() {
        Log.d(TAG, "onDownKeyEvent: ");
        WxLibrary.getInstance().onKeyEvent(WxLibrary.KEY_VOICE_DOWN);
    }

    @Override
    public void onUpKeyEvent() {
        Log.d(TAG, "onUpKeyEvent: ");
        WxLibrary.getInstance().onKeyEvent(WxLibrary.KEY_VOICE_UP);
    }

    @Override
    public List<WeChatContact> queryContacts(String name) {
        Log.d(TAG, "queryContacts: ");
        List<WeChatContact> list = new ArrayList<>();
        List<WxContactInfo> wxContactInfos = WxLibrary.getInstance().queryContacts(name);
        if (!ListUtils.isEmpty(wxContactInfos)) {
            for (WxContactInfo wxContactInfo : wxContactInfos) {
                list.add(convertIntoWxContactBean(wxContactInfo));
            }
        }
        return list;
    }

    @Override
    public WeChatContact convertIntoWxContactBean(Object object) {
        if (object == null) {
            return null;
        }
        WxContactInfo data = (WxContactInfo) object;
        return new WeChatContact(data.getId(), data.getNick(), data.getRemark(), data.getHeaderImg());
    }

    @Override
    public void addCallback(WeChatCallback callback) {
        Log.d(TAG, "addCallback: ");
        if (!callbackList.contains(callback)) {
            callbackList.add(callback);
        }
    }

    @Override
    public void removeCallback(WeChatCallback callback) {
        Log.d(TAG, "removeCallback: ");
        callbackList.remove(callback);
    }

    @Override
    public String getCookie() {
        Log.d(TAG, "getCookie: " + cookie);
        return cookie;
    }

    @Override
    public void openChatPage(String id) {
        Log.d(TAG, "openChatPage: ");
        WxLibrary.getInstance().startConversion(id);
    }

    @Override
    public void onContactChange(final String cookie, final List<WxContactInfo> contacts) {
        Log.d("QBX", "onContactChange: ");
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                updateContacts(cookie, contacts);
            }
        });
    }

    private synchronized void updateContacts(String cookie, List<WxContactInfo> contacts) {
        this.cookie = cookie;
        ArrayList<WeChatContact> contactList = new ArrayList<>();
        if (contacts != null) {
            for (WxContactInfo contact : contacts) {
                contactList.add(convertIntoWxContactBean(contact));
//                Log.d(TAG, contact.getNick() + "  " + contact.getRemark() + "  " + contact.getId());
            }
        }
        int oldListSize = this.contactList != null ? this.contactList.size() : 0;
        this.contactList = contactList;
        for (WeChatCallback callback : callbackList) {
            callback.onContactChanged(cookie, contactList);
        }
        if (contactList.size() > oldListSize) {
            upLoadContact(contactList);
        }
    }

    @Override
    public void onResult(String messageid, boolean success) {
        Log.d(TAG, "onSendMessageResult: messageid=" + messageid + "    success=" + success);
        for (WeChatCallback callback : callbackList) {
            callback.onSendMessageResult(messageid, success);
        }
    }

    @Override
    public void onChange(boolean logined) {
        Log.d(TAG, "onLoginStateChanged: logined=" + logined);
        if (!logined) {
            contactList = new ArrayList<>();
        }
        for (WeChatCallback callback : callbackList) {
            callback.onLoginStateChanged(logined);
        }
    }

    @Override
    public void onChange(boolean mainVisiable, boolean contactVisiable, boolean conversionViaiable) {
        Log.d(TAG, "onPageVisibilityChanged: mainVisiable=" + mainVisiable + "    contactVisiable=" + contactVisiable + "    conversionViaiable=" + conversionViaiable);
        for (WeChatCallback callback : callbackList) {
            callback.onPageVisibilityChanged(mainVisiable, contactVisiable, conversionViaiable);
        }
    }

    @Override
    public void onResult(int keyEvent, boolean success) {
        Log.d(TAG, "onKeyEventResult: keyEvent=" + keyEventArray.get(keyEvent) + "    success=" + success);
        for (WeChatCallback callback : callbackList) {
            callback.onKeyEventResult(keyEvent, success);
        }
    }

    private void upLoadContact(List<WeChatContact> contactBeans) {
        Log.d(TAG, "upLoadContact: ");
        if (ListUtils.isEmpty(contactBeans)) return;
        WeChatContactList contactList = new WeChatContactList();
        for (WeChatContact bean : contactBeans) {
            contactList.dictcontant.add(new WeChatContactList.Contact(bean.getNick()));
            if (!TextUtils.isEmpty(bean.getRemark())) {
                contactList.dictcontant.add(new WeChatContactList.Contact(bean.getRemark()));
            }
        }
        RemoteIatManager.getInstance().upLoadContact(false, GsonHelper.toJson(contactList));
    }

    private String getId() {
        return String.valueOf(sequenceGenerator.incrementAndGet());
    }


    public boolean checkWxExist(){
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(weChatPackageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
