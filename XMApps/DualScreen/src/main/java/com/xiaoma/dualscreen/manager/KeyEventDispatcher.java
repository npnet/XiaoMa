package com.xiaoma.dualscreen.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xiaoma.dualscreen.constant.TabState;
import com.xiaoma.dualscreen.listener.KeyEvent;
import com.xiaoma.dualscreen.listener.KeyEventListener;
import com.xiaoma.dualscreen.listener.TabChangeListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 盘控事件分发器(废弃)
 *
 * @author: iSun
 * @date: 2018/12/29 0029
 */
public class KeyEventDispatcher {
    private static KeyEventDispatcher instance;
    private KeyEventListener currentEventListener;
    private Context mContext;
    private List<TabChangeListener> tabChangeListeners = new CopyOnWriteArrayList<>();

    public static final String EVENT_KEY = "event_key";
    public static final String CHANGE_KEY_ACTION = "change_key_action";
    public static final String CHANGE_TAB_ACTION = "change_tab_action";
    public static final String AR_ACTION = "ar_action";
    public static final String MEDIA_TAB_EVENT = "media_tab_event";
    public static final String PHONE_TAB_EVENT = "phone_tab_event";
    public static final String NAV_TAB_EVENT = "nav_tab_event";
    public static final String LEFT_TAB_EVENT = "left_tab_event";
    public static final String LEFT_TAB_GONE_EVENT = "left_tab_gone_event";
    public static final String SIMPLE_TAB_EVENT = "simple_tab_event";
    public static final String KEY_UP_EVENT = "key_up_event";
    public static final String KEY_DOWN_EVENT = "key_down_event";
    public static final String KEY_LEFT_EVENT = "key_left_event";
    public static final String KEY_RIGHT_EVENT = "key_right_event";
    public static final String KEY_OK_EVENT = "key_ok_event";
    public static final String KEY_CANCEL_EVENT = "key_cancel_event";
    public static final String AR_ON_EVENT = "ar_on_event";
    public static final String AR_OFF_EVENT = "ar_off_event";

    public static KeyEventDispatcher getInstance() {
        if (instance == null) {
            synchronized (KeyEventDispatcher.class) {
                if (instance == null) {
                    instance = new KeyEventDispatcher();
                }
            }
        }
        return instance;
    }

    private KeyEventDispatcher() {

    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String eventKey = intent.getStringExtra(EVENT_KEY);
            if (action.equals(CHANGE_TAB_ACTION)){
                if (eventKey.equals(MEDIA_TAB_EVENT)){   //切换多媒体tab
                    dispatcherTabEvent(TabState.MEDIA);
                }else if (eventKey.equals(PHONE_TAB_EVENT)){   //切换电话tab
                    dispatcherTabEvent(TabState.PHONE);
                }else if (eventKey.equals(NAV_TAB_EVENT)){   //切换导航tab
                    dispatcherTabEvent(TabState.NAVI);
                }else if (eventKey.equals(LEFT_TAB_EVENT)){   //左边tab
                    dispatcherTabEvent(TabState.LEFT);
                }else if (eventKey.equals(LEFT_TAB_GONE_EVENT)){
                    dispatcherTabEvent(TabState.LEFT_GONE);
                }else if (eventKey.equals(SIMPLE_TAB_EVENT)){  //概要信息
                    dispatcherTabEvent(TabState.SIMPLE);
                }
            }else if (action.equals(CHANGE_KEY_ACTION)){
                if (eventKey.equals(KEY_UP_EVENT)){  //向上按键
                    dispatcherKeyEvent(KeyEvent.UP);
                }else if (eventKey.equals(KEY_DOWN_EVENT)){   //向下按键
                    dispatcherKeyEvent(KeyEvent.DOWN);
                }else if (eventKey.equals(KEY_LEFT_EVENT)){   //向左按键
                    dispatcherKeyEvent(KeyEvent.Left);
                }else if (eventKey.equals(KEY_RIGHT_EVENT)){   //向右按揭
                    dispatcherKeyEvent(KeyEvent.Right);
                }else if (eventKey.equals(KEY_OK_EVENT)){   //确定按键
                    dispatcherKeyEvent(KeyEvent.OK);
                }else if (eventKey.equals(KEY_CANCEL_EVENT)){   //取消按键
                    dispatcherKeyEvent(KeyEvent.CANCEL);
                }
            }else if (action.equals(AR_ACTION)){
                if (eventKey.equals(AR_OFF_EVENT)){
                    dispatcherKeyEvent(KeyEvent.AR_OFF);
                }else if (eventKey.equals(AR_ON_EVENT)){
                    dispatcherKeyEvent(KeyEvent.AR_ON);
                }
            }
        }
    };

    public void init(Context context) {
        this.mContext = context;
        initPartyControl();

        IntentFilter intentFilter = new IntentFilter(CHANGE_TAB_ACTION);
        intentFilter.addAction(CHANGE_KEY_ACTION);
        intentFilter.addAction(AR_ACTION);
        context.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void initPartyControl() {
        // TODO: 2019/3/7 0007 初始化方控按键监听
    }

    public synchronized void registerKeyEventListener(KeyEventListener listener) {
        this.currentEventListener = listener;
    }

    public synchronized void unRegisterKeyEventListener(KeyEventListener listener) {
        if (currentEventListener == listener) {
            this.currentEventListener = null;
        }
    }

    public synchronized void registerTabChangeListener(TabChangeListener listener) {
        tabChangeListeners.add(listener);
    }

    public synchronized void unRegisterTabChangeListener(TabChangeListener listener) {
        tabChangeListeners.remove(listener);
    }


    //双屏按键事件 上下，左右，确定，取消
    private synchronized void dispatcherKeyEvent(KeyEvent keyEvent) {
        if (currentEventListener != null) {
            currentEventListener.onKeyEvent(keyEvent);
        }
    }

    //双屏tab切换事件
    private synchronized void dispatcherTabEvent(TabState tabState) {
        for (TabChangeListener tabChangeListener : tabChangeListeners) {
            tabChangeListener.onTabChange(tabState);
        }
    }

}
