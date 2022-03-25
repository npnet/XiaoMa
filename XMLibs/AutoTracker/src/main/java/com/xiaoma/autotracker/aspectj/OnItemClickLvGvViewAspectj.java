package com.xiaoma.autotracker.aspectj;

import com.xiaoma.autotracker.factory.AspectHandleFactory;
import com.xiaoma.utils.log.KLog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by Thomas on 2018/12/5 0005
 * OnItemClickLvGvViewAspectj aop intercept
 */

@Aspect
public class OnItemClickLvGvViewAspectj extends BaseAspectj {

    private static final String EXPRESSION = "execution(* android.widget.AdapterView.OnItemClickListener.onItemClick(..))";

    @Override
    @Pointcut(EXPRESSION)
    public void pointCutMethod() {
    }

    @Override
    @Before(METHOD)
    public void before(JoinPoint point) {
        KLog.d(TAG, "OnItemClickLvGvViewAspectj before point cut execute...");
    }

    @Override
    @Around(METHOD)
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        KLog.d(TAG, "OnItemClickLvGvViewAspectj around point cut execute...");
        AspectHandleFactory.getInstance().getOnItemClickLvGvHandle().handleProceedingJoinPoint(proceedingJoinPoint);
        proceedingJoinPoint.proceed();
    }

    @Override
    @After(METHOD)
    public void after(JoinPoint point) {
        KLog.d(TAG, "OnItemClickLvGvViewAspectj after point cut execute...");
    }

}
