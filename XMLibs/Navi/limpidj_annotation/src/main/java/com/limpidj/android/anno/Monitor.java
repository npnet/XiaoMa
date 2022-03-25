package com.limpidj.android.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Monitor {
    int[] value() default {};

    PageMonitor[] page() default {};

    /**
     * 是否总是接收事件。默认否。如果否，对于一般 viewer 来说，未 use 或相应 page 不在栈顶时将不通知。
     *
     * @return
     * @see com.mapbar.android.mapbarmap.core.listener.EventReceiverProvider#isReady(Method)
     */
    boolean alwaysReceive() default false;
}
