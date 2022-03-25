package com.xiaoma.autotracker.handle;

import android.support.design.widget.TabLayout;

import com.xiaoma.autotracker.db.AutoTrackDBManager;
import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.utils.log.KLog;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by wutao on 2018/12/6 0006
 * TabLayout点击事件切点处理
 */
public class OnTabSelectedListenerHandle extends BaseHandle {

    //private TabLayout.Tab tab;

    @Override
    public void handleProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        Object tab = proceedingJoinPoint.getArgs()[0];
        TabLayout.Tab clickTab;
        if (tab instanceof TabLayout.Tab) {
            clickTab = (TabLayout.Tab) tab;
        } else {
            notImplementsAdapterViewItemTrackPropertiesError(proceedingJoinPoint);
            return;
        }
        try {
            Class viewClazz = clickTab.getClass();
            Field mParent = viewClazz.getDeclaredField("mParent");
            if (!mParent.isAccessible()) {
                mParent.setAccessible(true);
            }
            TabLayout tabLayout = (TabLayout) mParent.get(clickTab);
            if (!checkPageDescFromClickView(tabLayout)) {
                KLog.e(TAG, "not implements annotation for component...");
                if (!checkPageDescribeIsLegal(proceedingJoinPoint)) {
                    return;
                }
            }
            Class<? extends TabLayout> aClass = tabLayout.getClass();
            Field mSelectedListeners = null;
            try {
                mSelectedListeners = aClass.getDeclaredField("mSelectedListeners");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                if (aClass.getSuperclass() != null) {
                    mSelectedListeners = aClass.getSuperclass().getDeclaredField("mSelectedListeners");
                }
            }
            if (!mSelectedListeners.isAccessible()) {
                mSelectedListeners.setAccessible(true);
            }
            Object obj = mSelectedListeners.get(tabLayout);
            if (obj == null) {
                doException("onClickListenerField.get(mListenerInfo) return object is null...");
            }
            if (obj instanceof ArrayList) {
                ArrayList<Object> list = (ArrayList<Object>) mSelectedListeners.get(tabLayout);
                for (int i = 0; i < list.size(); i++) {
                    Object o = list.get(i);
                    if (o instanceof XmTrackerOnTabSelectedListener) {
                        XmTrackerOnTabSelectedListener xmTrackerOnTabSelectedListener = (XmTrackerOnTabSelectedListener) o;
                        ItemEvent itemEvent = xmTrackerOnTabSelectedListener.returnPositionEventMsg(tabLayout);
                        KLog.d(TAG, "xmTrackerOnTabSelectedListener  name：" + itemEvent.name + ", id：" + itemEvent.id
                                + ", pageUIPath:" + pageUIPath + ", pageUIPathDesc:" + pageUIPathDesc);
                        AutoTrackDBManager.getInstance().saveOnClickEvent(itemEvent.name, itemEvent.id, pageUIPath, pageUIPathDesc);
                    }
                }
            } else {
                notImplementsXMAutoTrackerEventOnClickListenerError(proceedingJoinPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

