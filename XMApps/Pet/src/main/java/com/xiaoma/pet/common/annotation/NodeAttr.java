package com.xiaoma.pet.common.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/4/8 0008 09:55
 *       desc：节点属性
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({NodeAttr.version,
        NodeAttr.path})
public @interface NodeAttr {

    String version = "version";
    String path = "path";

}
