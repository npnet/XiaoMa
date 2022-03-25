package com.xiaoma.carlib.manager;

import com.xiaoma.carlib.model.CarEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: iSun
 * @date: 2019/4/2 0002
 */
public class XmCarEventDispatcher {
    private static XmCarEventDispatcher instance;
    private List<ICarEvent> mEventList = new CopyOnWriteArrayList<>();

    public static XmCarEventDispatcher getInstance() {
        if (instance == null) {
            synchronized (XmCarEventDispatcher.class) {
                if (instance == null) {
                    instance = new XmCarEventDispatcher();
                }
            }
        }
        return instance;
    }

    private XmCarEventDispatcher() {

    }

    public void registerEvent(ICarEvent event) {
        mEventList.add(event);
    }

    public void unregisterEvent(ICarEvent event) {
        mEventList.remove(event);
    }

    public void dispatcherEvent(CarEvent event) {
        for (ICarEvent iCarEvent : mEventList) {
            iCarEvent.onCarEvent(event);
        }
    }

}
