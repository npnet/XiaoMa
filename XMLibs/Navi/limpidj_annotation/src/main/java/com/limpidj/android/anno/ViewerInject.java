package com.limpidj.android.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewerInject {

    /**
     * 不设置则不查找和注入 view ，只注入 viewer
     *
     * @return
     */
    int value() default 0;

    /**
     * @return
     */
    int parentId() default 0;

    String id() default "";

}

