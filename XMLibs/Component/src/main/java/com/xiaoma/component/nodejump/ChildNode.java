package com.xiaoma.component.nodejump;

/**
 * Created by kaka
 * on 19-3-14 下午3:06
 * <p>
 * desc: #a
 * </p>
 */
public interface ChildNode extends Node {

    /**
     * 判断子节点对用户是否可见
     *
     * @return 是否可见
     */
    boolean isShowing();

    /**
     * 获取子节点的根节点
     *
     * @return 根节点
     */
    RootNode getRootNode();
}
