package com.xiaoma.autotracker.aspectj;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.model.annotation.PageDescComponent;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.annotation.Annotation;

/**
 * Created by Thomas on 2018/12/5 0005
 * BaseFragment aop intercept
 */

@Aspect
public class OnBaseFragmentAspectj {

    private static final String TAG = "com.xiaoma.aspectj";
    private static final String expressionOnHiddenChanged = "execution(* com.xiaoma.component.base.BaseFragment.onHiddenChangedForAop(..))";
    private static final String expressionSetUserVisibleHintForAop = "execution(* com.xiaoma.component.base.BaseFragment.setUserVisibleHintForAop(..))";
    private Boolean hidden;
    private Boolean isVisibleToUser;
    private Fragment fragment;

    @Around(expressionOnHiddenChanged)
    public void onHiddenChanged(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object hidden = proceedingJoinPoint.getArgs()[0];
        if (hidden instanceof Boolean) {
            this.hidden = (Boolean) hidden;
        } else {
            return;
        }
        Object fragment = proceedingJoinPoint.getArgs()[1];
        if (fragment instanceof Fragment) {
            this.fragment = (Fragment) fragment;
        } else {
            return;
        }
        XmAutoTracker.getInstance().onHiddenChanged(this.fragment, this.hidden);
        proceedingJoinPoint.proceed();
    }

    @Around(expressionSetUserVisibleHintForAop)
    public void setUserVisibleHintForAop(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object isVisibleToUser = proceedingJoinPoint.getArgs()[0];
        if (isVisibleToUser instanceof Boolean) {
            this.isVisibleToUser = (Boolean) isVisibleToUser;
        } else {
            return;
        }
        Object fragment = proceedingJoinPoint.getArgs()[1];
        if (fragment instanceof Fragment) {
            this.fragment = (Fragment) fragment;
        } else {
            return;
        }
        XmAutoTracker.getInstance().setUserVisibleHint(this.fragment, this.isVisibleToUser);
        proceedingJoinPoint.proceed();
    }

    private String getFragmentPageDesc() {
        Class<? extends Fragment> fragmentClass = this.fragment.getClass();
        Annotation annotation = fragmentClass.getAnnotation(PageDescComponent.class);
        if (!(annotation instanceof PageDescComponent)) {
            return "";
        }
        PageDescComponent pageDescComponent = fragmentClass.getAnnotation(PageDescComponent.class);
        if (pageDescComponent == null) {
            return "";
        }
        String value = pageDescComponent.value();
        if (TextUtils.isEmpty(value)) {
            return "";
        }
        return value;
    }

}
