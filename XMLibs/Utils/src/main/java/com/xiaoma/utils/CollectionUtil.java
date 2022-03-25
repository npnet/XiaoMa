package com.xiaoma.utils;

import java.util.Collection;

/**
 * @author zs
 * @date 2018/9/18 0018.
 */
public class CollectionUtil {

    public static boolean isListEmpty(Collection list) {
        return list == null || list.size() <= 0;
    }
}
