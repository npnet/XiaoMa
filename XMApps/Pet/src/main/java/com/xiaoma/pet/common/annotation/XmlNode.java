package com.xiaoma.pet.common.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/4/4 0004 11:05
 *       desc：xml节点
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({XmlNode.app,
        XmlNode.level,
        XmlNode.pet,
        XmlNode.scene,
        XmlNode.decorator,
        XmlNode.gift})
public @interface XmlNode {

    String app = "app";                 //app
    String level = "level";             //关卡
    String pet = "pet";                 //宠物
    String scene = "scene";             //场景
    String decorator = "decorator";     //饰品
    String gift = "gift";               //礼物
}
