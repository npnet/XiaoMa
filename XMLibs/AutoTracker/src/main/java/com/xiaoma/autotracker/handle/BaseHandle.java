package com.xiaoma.autotracker.handle;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.xiaoma.autotracker.R;
import com.xiaoma.autotracker.util.AopUtil;
import com.xiaoma.autotracker.util.XmEventException;
import com.xiaoma.model.annotation.PageDescClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.SingleClick;
import com.xiaoma.utils.log.KLog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by Thomas on 2018/12/6 0006
 * aop切点方法处理 base
 */
public abstract class BaseHandle {

    protected static final String TAG = "com.xiaoma.autotracker.click";
    protected String pageUIPath;
    protected String pageUIPathDesc;
    protected final static long DEFAULT_CLICK_TIME = 500;
    protected boolean isQuicklyClick = false;

    public boolean isQuicklyClick() {
        return isQuicklyClick;
    }

    public void resetIsQuicklyClick() {
        isQuicklyClick = false;
    }

    protected synchronized boolean isQuicklyClick(@NonNull View target, Method method) {
        long defaultTime = DEFAULT_CLICK_TIME;
        SingleClick singleClick = method.getAnnotation(SingleClick.class);
        if (singleClick != null) {
            long value = singleClick.value();
            if (value >= 0) {
                defaultTime = value;
            }
        }
        long curTimeStamp = SystemClock.uptimeMillis();
        long lastClickTimeStamp;
        Object time = target.getTag(R.id.invalid_click);
        if (time == null) {
            target.setTag(R.id.invalid_click, curTimeStamp);
            return false;
        }
        lastClickTimeStamp = (Long) time;
        long clickTime = curTimeStamp - lastClickTimeStamp;
        boolean isInvalid = clickTime < defaultTime;
        if (!isInvalid) {
            target.setTag(R.id.invalid_click, curTimeStamp);
        } else {
            isQuicklyClick = true;
            KLog.d(TAG, "BaseHandle isQuicklyClick view is " + target + ", click time is " + clickTime);
        }
        return isInvalid;
    }

    //处理方法切点下发
    public abstract void handleProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint);

    //出现异常
    public void doException(String errorMsg) {
        XmEventException.doException(errorMsg);
    }

    protected boolean checkPageDescribeIsLegal(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        PageDescClick pageDescClick = method.getAnnotation(PageDescClick.class);
        if (pageDescClick != null) {
            String[] pageDescribes = pageDescClick.value();
            if (pageDescribes.length == 2) {
                pageUIPath = pageDescribes[0];
                pageUIPathDesc = pageDescribes[1];
                if (TextUtils.isEmpty(this.pageUIPath) || TextUtils.isEmpty(this.pageUIPathDesc)) {
                    implementsPageDescribeAnnotationError(proceedingJoinPoint);
                    return false;
                } else {
                    return true;
                }
            } else {
                implementsPageDescribeAnnotationError(proceedingJoinPoint);
                return false;
            }
        } else {
            notImplementsAnnotationError(proceedingJoinPoint);
            return false;
        }
    }

    protected boolean checkPageDescFromClickView(View view) {
        Object attachedHost = null;
        try {
            attachedHost = AopUtil.getAttachedHost(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (attachedHost == null) {
            KLog.e(TAG, "checkPageDescFromClickView attachedHost is null...");
            return false;
        }
        Class fragmentClass = attachedHost.getClass();
        pageUIPath = fragmentClass.getSimpleName();
        Annotation annotation = fragmentClass.getAnnotation(PageDescComponent.class);
        if (!(annotation instanceof PageDescComponent)) {
            KLog.e(TAG, "no instanceof PageDescComponent from component...");
            return false;
        }
        PageDescComponent pageDescComponent = (PageDescComponent) fragmentClass.getAnnotation(PageDescComponent.class);
        if (pageDescComponent == null) {
            implementsPageDescComponentAnnotationError(pageUIPath);
            return false;
        }
        String value = pageDescComponent.value();
        if (TextUtils.isEmpty(value)) {
            implementsPageDescComponentAnnotationError(pageUIPath);
            return false;
        }
        pageUIPathDesc = value;
        return true;
    }

    protected void implementsPageDescComponentAnnotationError(String path) {
        String errorMsg = " you must be use annotation(PageDescComponent) \n class:"
                + path
                + "\n must be implements annotation for collect UI page event msg...please check..."
                + "\n 参考AutoTracker module readme 关于Component annotation使用：（请参考AppStore使用）";
        XmEventException.doException(errorMsg);
    }

    protected void notImplementsAnnotationError(ProceedingJoinPoint proceedingJoinPoint) {
        String errorMsg = " you must be use annotation(PageDescClick or BusinessOnClick or (NormalOnClick and ResId)) \n class:"
                + proceedingJoinPoint.getSignature().getDeclaringTypeName() + "\n method:" + proceedingJoinPoint.getSignature().getName()
                + "\n must be implements annotation for collect UI page event msg...please check..."
                + "\n 参考AutoTracker module readme 关于click场景统计：（请参考AppStore使用）";
        XmEventException.doException(errorMsg);
    }

    protected void implementsPageDescribeAnnotationError(ProceedingJoinPoint proceedingJoinPoint) {
        String errorMsg = " implements annotation PageDescClick error, because annotation PageDescClick value is null or length is not two \n class:"
                + proceedingJoinPoint.getSignature().getDeclaringTypeName() + "\n method:" + proceedingJoinPoint.getSignature().getName()
                + "\n must be implements annotation success for collect UI page event msg...please check..."
                + "\n 参考AutoTracker module readme 关于click场景统计：（请参考AppStore使用）";
        XmEventException.doException(errorMsg);
    }

    protected void implementsNormalClickOrResIdAnnotationError(ProceedingJoinPoint proceedingJoinPoint) {
        String errorMsg = " implements annotation NormalOnClick and ResId error, because annotation NormalOnClick length != ResId length \n class:"
                + proceedingJoinPoint.getSignature().getDeclaringTypeName() + "\n method:" + proceedingJoinPoint.getSignature().getName()
                + "\n must be implements annotation success for collect UI page event msg...please check..."
                + "\n 参考AutoTracker module readme 关于click场景统计：（请参考AppStore使用）";
        XmEventException.doException(errorMsg);
    }

    protected void notImplementsXMAutoTrackerEventOnClickListenerError(ProceedingJoinPoint proceedingJoinPoint) {
        String errorMsg = " you must be use XMAutoTrackerEventOnClickListener,but implements android View.OnClickListener \n class:"
                + proceedingJoinPoint.getSignature().getDeclaringTypeName() + "\n method:" + proceedingJoinPoint.getSignature().getName()
                + "\n must be implements annotation success for collect UI page event msg...please check..."
                + "\n 参考AutoTracker module readme 关于click场景统计：（请参考AppStore使用）";
        XmEventException.doException(errorMsg);
    }

    protected void notImplementsAdapterViewItemTrackPropertiesError(ProceedingJoinPoint proceedingJoinPoint) {
        String errorMsg = " you must be use interface AdapterViewItemTrackProperties for RecycleView adapter \n class:"
                + proceedingJoinPoint.getSignature().getDeclaringTypeName() + "\n method:" + proceedingJoinPoint.getSignature().getName()
                + "\n must be implements success for collect UI page event msg...please check..."
                + "\n 参考AutoTracker module readme 关于click场景统计：（请参考AppStore使用）";
        XmEventException.doException(errorMsg);
    }

}
