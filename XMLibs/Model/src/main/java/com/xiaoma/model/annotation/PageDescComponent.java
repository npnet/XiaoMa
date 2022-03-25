package com.xiaoma.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Thomas on 2018/12/5 0005
 * 用于描述当前组件页面(用于activity， fragment)
 * 实例{"车应用首页"}
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PageDescComponent {
    String value() default "";
}
