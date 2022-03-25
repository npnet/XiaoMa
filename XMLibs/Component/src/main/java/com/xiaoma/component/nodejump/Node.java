package com.xiaoma.component.nodejump;

/**
 * Created by kaka
 * on 19-3-14 下午5:32
 * <p>
 * desc: #a
 * </p>
 */
public interface Node {
    /**
     * 获取当前节点的信息
     *
     * @return 当前节点信息
     */
    String getThisNode();

    /**
     * 根据节点链，获取当前节点的下一个子节点
     *
     * @param nextNode 下一个子节点名
     * @return 下一个子节点
     */
    ChildNode getNextNode(String nextNode);

    /**
     * 子节点解析从父节点传递过来的节点链，分析出下一个跳转任务
     *
     * @param jumpNodes 节点链
     * @return 当前节点是否成功处理了节点跳转任务
     */
    boolean parsingNodes(String jumpNodes);

    /**
     * 子节点处解析出的跳转任务的具体逻辑，需要当前fragment自己去处理子fragment的切换以及相应的UI变化等逻辑
     *
     * @param nextNode 下一个需要跳转的节点
     * @return 是否成功处理了节点跳转，这个返回十分重要！！！！节点的步进，跳转处理逻辑都需要依赖这个返回！！！！
     * 所以在未成功处理节点跳转时，一定要返回false，这样父节点才知道跳转被终止，整个节点分发才会被结束。
     */
    boolean handleJump(String nextNode);
}
