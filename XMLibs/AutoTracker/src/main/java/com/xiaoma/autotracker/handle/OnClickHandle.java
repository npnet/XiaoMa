package com.xiaoma.autotracker.handle;

import android.text.TextUtils;
import android.view.View;

import com.xiaoma.autotracker.db.AutoTrackDBManager;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.Ignore;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.utils.log.KLog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Thomas on 2018/12/6 0006
 * 点击事件切点处理
 */
public class OnClickHandle extends BaseHandle {

    //private String viewClass = "android.view.View";
    //private String viewMethod = "getListenerInfo";
    //private String view$ListenerInfoClass = "android.view.View$ListenerInfo";
    //private String view$ListenerInfoMethod = "mOnClickListener";
    // private View view;
    //private String rvBqAdapter = "com.chad.library.adapter.base.BaseQuickAdapter";
    //private String rvBqAdapterOnclick = "onClick";

    //处理list item click 埋点
    public void doListItemClickAopMethod(ProceedingJoinPoint proceedingJoinPoint, View clickView) {
        try {
            Class viewClazz = Class.forName("android.view.View");
            Method getListenerInfoMethod = viewClazz.getDeclaredMethod("getListenerInfo");
            if (!getListenerInfoMethod.isAccessible()) {
                getListenerInfoMethod.setAccessible(true);
            }
            Object mListenerInfo = getListenerInfoMethod.invoke(clickView);
            Class listenerInfoClazz = Class.forName("android.view.View$ListenerInfo");
            Field onClickListenerField = listenerInfoClazz.getDeclaredField("mOnClickListener");
            if (!onClickListenerField.isAccessible()) {
                onClickListenerField.setAccessible(true);
            }
            Object object = onClickListenerField.get(mListenerInfo);
            if (object == null) {
                doException("onClickListenerField.get(mListenerInfo) return object is null...");
            } else if (object instanceof XMAutoTrackerEventOnClickListener) {
                XMAutoTrackerEventOnClickListener xmAutoTrackerEventOnClickListener = (XMAutoTrackerEventOnClickListener) object;
                ItemEvent itemEvent = xmAutoTrackerEventOnClickListener.returnPositionEventMsg(clickView);
                KLog.d(TAG, "ListItemClickContents  name：" + itemEvent.name + ", id：" + itemEvent.id
                        + ", pageUIPath:" + pageUIPath + ", pageUIPathDesc:" + pageUIPathDesc);
                AutoTrackDBManager.getInstance().saveOnClickEvent(itemEvent.name, itemEvent.id, pageUIPath, pageUIPathDesc);
            } else {
                notImplementsXMAutoTrackerEventOnClickListenerError(proceedingJoinPoint);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //处理normal click 埋点
    public void doNormalClickMethod(int[] resIds, String[] normalClicks, View clickView) {
        if (resIds == null) {
            KLog.d(TAG, "normalClickContents value: " + normalClicks[0] + ", pageUIPath:" + pageUIPath + ", pageUIPathDesc:" + pageUIPathDesc);
            AutoTrackDBManager.getInstance().saveOnClickEvent(normalClicks[0], null, pageUIPath, pageUIPathDesc);
            return;
        }
        for (int i = 0; i < resIds.length; i++) {
            if (resIds[i] == clickView.getId()) {
                KLog.d(TAG, "normalClickContents value: " + normalClicks[i] + ", pageUIPath:" + pageUIPath + ", pageUIPathDesc:" + pageUIPathDesc);
                AutoTrackDBManager.getInstance().saveOnClickEvent(normalClicks[i], null, pageUIPath, pageUIPathDesc);
            }
        }
    }

    @Override
    public void handleProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = signature.getName();
        String declaringTypeName = signature.getDeclaringTypeName();
        Ignore annotation = method.getAnnotation(Ignore.class);
        if ((annotation != null) || (!TextUtils.isEmpty(methodName) && !TextUtils.isEmpty(declaringTypeName) &&
                "onClick".equals(methodName) && declaringTypeName.contains("com.chad.library.adapter.base.BaseQuickAdapter"))) {
            KLog.d(TAG, "Ignore annotation for click collect...");
            return;
        }
        Object view = proceedingJoinPoint.getArgs()[0];
        View clickView;
        if (view instanceof View) {
            clickView = (View) view;
        } else {
            doException("get proceedingJoinPoint.getArgs()[1] is no instanceof View");
            return;
        }
        if (isQuicklyClick(clickView, method)) {
            return;
        }
        if (!checkPageDescFromClickView(clickView)) {
            KLog.e(TAG, "not implements annotation for component...");
            if (!checkPageDescribeIsLegal(proceedingJoinPoint)) {
                return;
            }
        }
        BusinessOnClick businessOnClick = method.getAnnotation(BusinessOnClick.class);
        NormalOnClick normalOnClick = method.getAnnotation(NormalOnClick.class);
        ResId resId = method.getAnnotation(ResId.class);
        if (businessOnClick != null) {
            doListItemClickAopMethod(proceedingJoinPoint, clickView);
        } else if (normalOnClick != null && resId != null) {
            int[] resIds = resId.value();
            String[] normalClicks = normalOnClick.value();
            if (resIds.length == 0 || normalClicks.length == 0 || resIds.length != normalClicks.length) {
                implementsNormalClickOrResIdAnnotationError(proceedingJoinPoint);
            } else {
                doNormalClickMethod(resIds, normalClicks, clickView);
            }
        } else if (resId == null && normalOnClick != null) {
            String[] normalClicks = normalOnClick.value();
            if (normalClicks.length == 1) {
                doNormalClickMethod(null, normalClicks, clickView);
            } else {
                implementsNormalClickOrResIdAnnotationError(proceedingJoinPoint);
            }
        } else {
            notImplementsAnnotationError(proceedingJoinPoint);
        }
    }

}
