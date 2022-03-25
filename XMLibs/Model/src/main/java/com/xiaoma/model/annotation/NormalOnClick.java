package com.xiaoma.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Thomas on 2018/12/5 0005
 * 事件收集只需按钮文本意图
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NormalOnClick {
    String[] value() default "";
}
