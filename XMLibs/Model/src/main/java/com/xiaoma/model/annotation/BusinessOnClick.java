package com.xiaoma.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Thomas on 2018/12/6 0006
 * recycleview listview gridview 等 list item children view click event
 * 或者基于NormalOnClick需要上报动态数据
 * 前提必现实现XMAutoTrackerEventOnClickListener 点击事件listener
 * 需要收集信息，如name="音乐列表",id="10001"
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface BusinessOnClick {

}
