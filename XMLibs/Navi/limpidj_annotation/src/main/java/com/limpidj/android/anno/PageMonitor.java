package com.limpidj.android.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PageMonitor {

    /**
     * @return
     * @see PageProcess
     */
    PageProcess value();

    /**
     * 一个都不指定代表接收所有页面的回调
     *
     * @return
     */
    Class[] page() default {};

}
