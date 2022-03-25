//package com.xiaoma.dualscreen.manager;
//
//import android.content.Context;
//
//import com.xiaoma.dualscreen.constant.TabState;
//import com.xiaoma.dualscreen.listener.PhoneListener;
//import com.xiaoma.dualscreen.listener.PresentationDataListener;
//import com.xiaoma.dualscreen.constant.DataType;
//import com.xiaoma.dualscreen.model.MediaModel;
//import com.xiaoma.dualscreen.model.PhoneModel;
//import com.xiaoma.dualscreen.model.SimpleModel;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * @author: iSun
// * @date: 2018/12/29 0029
// * 屏幕数据源管理(废弃)
// */
//public class ScreenDataManager {
//    private List<PresentationDataListener> dataListeners = new ArrayList<>();
//    private List<PhoneListener> phoneListeners = new ArrayList<>();
//    private static ScreenDataManager instance;
//    private MediaModel mediaModel;
//    private PhoneModel phoneModel;
//    private SimpleModel simpleMode;
//    private TabState tabState;
//    private boolean isCall;
//
//    public static ScreenDataManager getInstance(Context context) {
//        if (instance == null) {
//            synchronized (ScreenDataManager.class) {
//                if (instance == null) {
//                    instance = new ScreenDataManager(context);
//                }
//            }
//        }
//        return instance;
//    }
//
//    private ScreenDataManager(Context context) {
//
//    }
//
//    public void setMediaInfo(MediaModel mediaModel) {
//        this.mediaModel = mediaModel;
//        notifyDataChange(DataType.MEDIA);
//    }
//
//    public MediaModel getMediaInfo() {
//        return mediaModel;
//    }
//
//    public PhoneModel getPhoneInfo() {
//        return phoneModel;
//    }
//
//    public void setPhoneInfo(PhoneModel phoneInfo) {
//        this.phoneModel = phoneInfo;
//        notifyDataChange(DataType.PHONE);
//    }
//
//    public void getNaviInfo() {
//
//    }
//
//    public TabState getTabState() {
//        return tabState;
//    }
//
//    public void setNaviInfo() {
//        notifyDataChange(DataType.NAVI);
//    }
//
//    public void addDataListener(PresentationDataListener listener) {
//        dataListeners.add(listener);
//    }
//
//    public void addPhoneListener(PhoneListener listener) {
//        phoneListeners.add(listener);
//    }
//
//    public void removeDataListener(PresentationDataListener listener) {
//        dataListeners.remove(listener);
//    }
//
//    public void removePhoneListener(PresentationDataListener listener) {
//        phoneListeners.remove(listener);
//    }
//
//    private synchronized void notifyDataChange(DataType dataType) {
//        Iterator<PresentationDataListener> iterator = dataListeners.iterator();
//        while (iterator.hasNext()) {
//            PresentationDataListener onNext = iterator.onNext();
//            onNext.onDataChange(dataType);
//        }
//    }
//
//
//    private synchronized void notifyPhoneChange() {
//        Iterator<PhoneListener> iterator = phoneListeners.iterator();
//        while (iterator.hasNext()) {
//            PhoneListener onNext = iterator.onNext();
//            onNext.onCall(isCall);
//        }
//    }
//
//    public SimpleModel getSimpleInfo() {
//        return simpleMode;
//    }
//
//    public void setSimpleInfo(SimpleModel simpleInfo) {
//        this.simpleMode = simpleInfo;
//    }
//}
