package com.xiaoma.component.nodejump;

import java.util.List;

/**
 * Created by kaka
 * on 19-3-14 下午3:02
 * <p>
 * desc: #a
 * </p>
 */
public interface RootNode extends Node {
    /**
     * 获取根节点保存的节点链
     *
     * @return 节点链
     */
    String getJumpNodes();

    /**
     * 给根节点设置初始化的节点链
     *
     * @param mJumpNode 节点链
     */
    void setJumpNodes(String mJumpNode);

    /**
     * 判断在该根节点下是否还在进行跳转任务
     *
     * @return 是否正在进行跳转
     */
    boolean isJumping();

    /**
     * 设置是否开始跳转的标志位
     *
     * @param mJumping 是否开始跳转
     */
    void setJumping(boolean mJumping);

    /**
     * 清除附属在根节点实现类(即Activity)中的初始化节点信息，防止RootNode(即Activity)在onResume时多次触发跳转
     */
    void clearInitNodes();

    /**
     * 获取根节点下的所有子节点
     *
     * @return 所有子节点
     */
    List<ChildNode> getChildNodes();
}
