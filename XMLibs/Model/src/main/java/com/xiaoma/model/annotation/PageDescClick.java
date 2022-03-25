package com.xiaoma.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Thomas on 2018/12/5 0005
 * 当前UI页面描述信息 url & desc (用于click事件注解上描述当前按钮响应的页面描述)
 * 实例{"com.xiaoma.app.ui.MainActivity", "车应用首页"}, please note order
 */

@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface PageDescClick {
    String[] value() default "";
}
