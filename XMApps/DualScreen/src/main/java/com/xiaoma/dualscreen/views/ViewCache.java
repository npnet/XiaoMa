package com.xiaoma.dualscreen.views;

import android.content.Context;
import android.telecom.Log;

import com.xiaoma.dualscreen.constant.Constant;
import com.xiaoma.dualscreen.constant.TabState;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.dualscreen.eol.EOLView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: iSun
 * @date: 2019/3/7 0007
 * 子View容器
 */
public class ViewCache {
    //是否显示概要信息
    private Map<String, BaseView> viewMap = new HashMap<>();
    private List<BaseView> views = new ArrayList<>();
    private Context mContext;
    private static ViewCache instance;

    public static ViewCache getInstance() {
        if (instance == null) {
            synchronized (ViewCache.class) {
                if (instance == null) {
                    instance = new ViewCache();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.mContext = context;
    }


    private ViewCache() {
    }

    public void clear(){
        if(viewMap.get(Constant.VIEW_SIMPLE) != null){
            viewMap.get(Constant.VIEW_SIMPLE).onDestory();
        }
        if(viewMap.get(Constant.VIEW_MEDIA) != null){
            viewMap.get(Constant.VIEW_MEDIA).onDestory();
        }
        if(viewMap.get(Constant.VIEW_PHONE) != null){
            viewMap.get(Constant.VIEW_PHONE).onDestory();
        }
        if(viewMap.get(Constant.VIEW_NAVI) != null){
            ((NaviView)viewMap.get(Constant.VIEW_NAVI)).resetText();
        }
        viewMap.remove(Constant.VIEW_SIMPLE);
        viewMap.remove(Constant.VIEW_MEDIA);
        viewMap.remove(Constant.VIEW_PHONE);
    }

    public BaseView getSimpleView() {
        BaseView view = viewMap.get(Constant.VIEW_SIMPLE);
        if (view == null) {
            view = new SimpleView(mContext);
            viewMap.put(Constant.VIEW_SIMPLE, view);
        } else {
            view.onRefresh();
        }
        return view;
    }

    //是否显示左侧仪表导航
    public BaseView getLeftNaviView() {
        BaseView view = viewMap.get(Constant.VIEW_LEFT_NAVI);
        if (view == null) {
            view = new LeftNaviView(mContext);
            viewMap.put(Constant.VIEW_LEFT_NAVI, view);
        }
        return view;
    }


    public MediaView getCenterMediaView() {
        BaseView view = viewMap.get(Constant.VIEW_MEDIA);
        if (view == null) {
            view = new MediaView(mContext);
            viewMap.put(Constant.VIEW_MEDIA, view);
        }
        return (MediaView) view;
    }

    public PhoneView getCenterPhoneView() {
        BaseView view = viewMap.get(Constant.VIEW_PHONE);
        if (view == null) {
            view = new PhoneView(mContext);
            view.onResume();
            viewMap.put(Constant.VIEW_PHONE, view);
        }
        return (PhoneView) view;
    }

    public BaseView getCenterNaviView() {
        BaseView view = viewMap.get(Constant.VIEW_NAVI);
        if (view == null) {
            KLog.d("getCenterMediaView new NaviView");
            view = new NaviView(mContext);
            viewMap.put(Constant.VIEW_NAVI, view);
        }
        return view;
    }

    public BaseView getEOLView() {
        BaseView view = viewMap.get(Constant.VIEW_EOL);
        if (view == null) {
            Log.d("jun", "getEOLView");
            view = new EOLView(mContext);
            viewMap.put(Constant.VIEW_EOL, view);
        }
        return view;
    }

    public List<BaseView> getViews() {
        return new ArrayList<>(viewMap.values());
    }

    public BaseView getCenterView(TabState showCenterT) {
        BaseView baseView = null;
        switch (showCenterT) {
            case MEDIA:
                baseView = getCenterMediaView();
                break;
            case PHONE:
                baseView = getCenterPhoneView();
                break;
            case NAVI:
                baseView = getCenterNaviView();
                break;
        }
        return baseView;
    }
}
