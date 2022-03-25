package com.xiaoma.autotracker.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Created by Thomas on 2018/12/13 0013
 * base aspectj aop handle
 */

public abstract class BaseAspectj {

    protected static final String TAG = "com.xiaoma.aspectj";
    protected static final String METHOD = "pointCutMethod()";

    //需要实现注解@Pointcut(EXPRESSION)
    public abstract void pointCutMethod();

    //需要实现注解@Before(METHOD)
    public abstract void before(JoinPoint point);

    //需要实现注解@Around(METHOD)
    public abstract void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable;

    //需要实现注解@After(METHOD)
    public abstract void after(JoinPoint point);

}
