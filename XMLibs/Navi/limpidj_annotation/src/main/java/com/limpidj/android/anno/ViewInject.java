package com.limpidj.android.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewInject {

    /**
     * @return
     */
//    int value();

    /**
     * @return
     */
    int parentId() default 0;

    String id() default "";
}
