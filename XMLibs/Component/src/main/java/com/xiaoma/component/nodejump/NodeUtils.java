package com.xiaoma.component.nodejump;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.text.TextUtils;

import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.log.KLog;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.xiaoma.component.nodejump.NodeConst.ACTION_NODES_JUMP;
import static com.xiaoma.component.nodejump.NodeConst.ACTIVITY_CLZ;
import static com.xiaoma.component.nodejump.NodeConst.ACTIVITY_PKG;
import static com.xiaoma.component.nodejump.NodeConst.ACTIVITY_URI;
import static com.xiaoma.component.nodejump.NodeConst.JUMP_NODES;

/**
 * Created by kaka
 * on 19-3-12 下午5:04
 * <p>
 * desc: 页面按节点跳转框架的辅助类
 * </p>
 */
public class NodeUtils {
    private static final String TAG = NodeUtils.class.getSimpleName();

    /**
     * 使用scheme协议的方式进行跳转，使用该方法时,传递的actUri中的host部分必须为包名
     *
     * @param context    context
     * @param actUri     跳转Activity的URI，注意其中的host必须是包名
     * @param nodeChains nodeChains
     * @return 是否跳转成功
     */
    @SuppressLint("WrongConstant")
    public static boolean jumpTo(Context context, String actUri, String nodeChains) {
        if (TextUtils.isEmpty(actUri) || TextUtils.isEmpty(nodeChains)) {
            KLog.e(TAG, "parameter error！");
            return false;
        }

        Uri uri = Uri.parse(actUri);
        String scheme = uri.getScheme();
        if (!"xiaoma".equals(scheme)) {
            KLog.e(TAG, "Invalid scheme！");
            return false;//scheme不符合规则
        }
        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            KLog.e(TAG, "Invalid host！");
            return false;//host不能为空
        }
        if (!AppUtils.isAppInstalled(context, host)) {
            KLog.e(TAG, "app not install！");
            return false;//app 未安装
        }
        if (!AppUtils.isExistActivity(context, uri)) {
            KLog.e(TAG, "activity not exist！");
            return false;//不存在activity
        }
        Intent intent = new Intent(ACTION_NODES_JUMP);
        intent.setPackage(host);
        intent.setComponent(new ComponentName(host, NodeJumpReceiver.class.getName()));
        intent.putExtra(JUMP_NODES, nodeChains);
        intent.putExtra(ACTIVITY_URI, actUri);
        intent.addFlags(0x01000000);
        context.sendBroadcast(intent);
        return true;
    }

    /**
     * 使用包名及类名的方式进行跳转
     *
     * @param context     context
     * @param pkg         包名
     * @param fullClzName activity全路径类名
     * @param nodeChains  nodeChains
     * @return 是否跳转成功
     */
    @SuppressLint("WrongConstant")
    public static boolean jumpTo(Context context, String pkg, String fullClzName, String nodeChains) {
        if (TextUtils.isEmpty(pkg) || TextUtils.isEmpty(fullClzName)) {
            KLog.e(TAG, "parameter error！");
            return false;//参数有误
        }

        if (!AppUtils.isAppInstalled(context, pkg)) {
            KLog.e(TAG, "app not install！");
            return false;//app 未安装
        }

        if (!AppUtils.isExistActivity(context, pkg, fullClzName)) {
            KLog.e(TAG, "activity not exist！");
            return false;//不存在activity
        }

        Intent intent = new Intent(ACTION_NODES_JUMP);
        intent.setPackage(pkg);
        intent.setComponent(new ComponentName(pkg, NodeJumpReceiver.class.getName()));
        intent.putExtra(JUMP_NODES, nodeChains);
        intent.putExtra(ACTIVITY_PKG, pkg);
        intent.putExtra(ACTIVITY_CLZ, fullClzName);
        intent.addFlags(0x01000000);
        context.sendBroadcast(intent);
        return true;
    }

    /**
     * 根据当前的节点信息，在节点链上查找下一个对应的节点信息
     *
     * @param current   当前节点
     * @param jumpNodes 节点链
     * @return 下一个节点信息
     */
    public static String getNextNode(String current, String jumpNodes) {
        if (TextUtils.isEmpty(current)
                || TextUtils.isEmpty(jumpNodes)) {
            return null;
        }

        String[] nodes = jumpNodes.split("/");
        if (nodes.length == 0) {
            return null;
        }

        for (int i = 0; i < nodes.length; i++) {
            if (current.equals(nodes[i])
                    && i < nodes.length - 1) {
                return nodes[i + 1];
            }
        }
        return null;
    }

    /**
     * 根据给出的节点信息，判断该节点是否在节点链中
     *
     * @param thisNode  给出的节点
     * @param jumpNodes 节点链
     * @return 是否存在
     */
    public static boolean isInChains(String thisNode, String jumpNodes) {
        if (TextUtils.isEmpty(thisNode)
                || TextUtils.isEmpty(jumpNodes)) {
            return false;
        }

        String[] nodes = jumpNodes.split("/");
        if (nodes.length == 0) {
            return false;
        }

        for (String node : nodes) {
            if (thisNode.equals(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据当前的节点信息，判断是否为节点链中的最后一个节点
     *
     * @param thisNode  当前节点
     * @param jumpNodes 节点链
     * @return 是否为最后一个节点
     */
    public static boolean isLastNode(String thisNode, String jumpNodes) {
        if (TextUtils.isEmpty(thisNode)
                || TextUtils.isEmpty(jumpNodes)) {
            return false;
        }

        String[] nodes = jumpNodes.split("/");
        if (nodes.length == 0) {
            return false;
        }

        return thisNode.equals(nodes[nodes.length - 1]);
    }

    /**
     * 根据当前App的Activity栈，判断第一个要跳转的Activity节点是否已经存在
     *
     * @param jumpNodes   节点链
     * @return Activity节点是否已经在当前的Activity栈中
     */
    public static boolean isFirstNodeAlive(String jumpNodes) {
        if (TextUtils.isEmpty(jumpNodes)) {
            return false;
        }

        String[] nodes = jumpNodes.split("/");
        if (nodes.length == 0) {
            return false;
        }

        CopyOnWriteArraySet<String> aliveNodes = ActNodeManager.getInstance().getAliveNodes();
        return aliveNodes.contains(nodes[0]);
    }

    public static boolean isFirstNodeShowing(String jumpNodes) {
        if (TextUtils.isEmpty(jumpNodes)) {
            return false;
        }

        String[] nodes = jumpNodes.split("/");
        if (nodes.length == 0) {
            return false;
        }

        Activity topActivity = ActNodeManager.getInstance().getTopActNode();
        return topActivity instanceof RootNode
                && ActNodeManager.getInstance().isTopActNodeShowing()
                && ((RootNode) topActivity).getThisNode().equals(nodes[0]);
    }

    /**
     * RootNode的代理类，主要用来管理根节点中的节点链，以及当前的跳转状态
     */
    public static class RootNodeProxy implements RootNode {
        public static final String TAG = RootNodeProxy.class.getSimpleName();
        private String mJumpNodes;
        private boolean mJumping;
        private WeakReference<RootNode> mRootNode;

        public RootNodeProxy(RootNode rootNode) {
            this.mRootNode = new WeakReference<>(rootNode);
        }

        public boolean parsingNodes(String jumpNodes) {
            //将初始化时附属在RootNode中的JumpNodes信息清除，防止RootNode(即Activity)在onResume时多次触发跳转
            if (!mJumping && mRootNode.get() != null) {
                mRootNode.get().clearInitNodes();
            }
            setJumpNodes(jumpNodes);
            setJumping(jumpToFragment());
            return true;
        }

        boolean jumpToFragment() {
            KLog.d(TAG, "jumpNodes: " + getJumpNodes());
            String nextNode = NodeUtils.getNextNode(getThisNode(), getJumpNodes());
            if (!TextUtils.isEmpty(nextNode)) {
                ChildNode childNode = getNextNode(nextNode);
                if (childNode != null
                        && childNode.isShowing()) {
                    return childNode.parsingNodes(getJumpNodes());
                } else {
                    return handleJump(nextNode);
                }
            } else {
                return false;
            }
        }

        @CallSuper
        public boolean handleJump(String nextNode) {
            if (mRootNode.get() == null) return false;
            return mRootNode.get().handleJump(nextNode);
        }

        public String getJumpNodes() {
            return mJumpNodes;
        }

        public void setJumpNodes(String mJumpNode) {
            this.mJumpNodes = mJumpNode;
        }

        public boolean isJumping() {
            return mJumping;
        }

        public void setJumping(boolean mJumping) {
            this.mJumping = mJumping;
        }

        public String getThisNode() {
            if (mRootNode.get() == null) return null;
            return mRootNode.get().getThisNode();
        }

        @Override
        public ChildNode getNextNode(String nextNode) {
            if (mRootNode.get() == null) return null;
            return mRootNode.get().getNextNode(nextNode);
        }

        @Override
        public void clearInitNodes() {
            if (mRootNode.get() == null) return;
            mRootNode.get().clearInitNodes();
        }

        @Override
        public List<ChildNode> getChildNodes() {
            if (mRootNode.get() == null) return null;
            return mRootNode.get().getChildNodes();
        }
    }

    /**
     * 用来管理Activity节点在栈内的存活和显示情况
     */
    public static class ActNodeManager {
        private static ActNodeManager customActivityManager = new ActNodeManager();
        private WeakReference<Activity> topActNode;
        private boolean topActNodeShowing;
        private CopyOnWriteArraySet<String> aliveNodes = new CopyOnWriteArraySet<>();

        private ActNodeManager() {
        }

        public static ActNodeManager getInstance() {
            return customActivityManager;
        }

        public void init(Application application) {
            application.registerActivityLifecycleCallbacks(new NodeUtils.LifecycleCallback());
            IntentFilter filter = new IntentFilter(ACTION_NODES_JUMP);
            application.registerReceiver(new NodeJumpReceiver(),filter);
        }

        public Activity getTopActNode() {
            if (topActNode != null) {
                return topActNode.get();
            }
            return null;
        }

        private void setTopActNode(Activity topActNode) {
            this.topActNode = new WeakReference<>(topActNode);
        }

        boolean isTopActNodeShowing() {
            return topActNodeShowing;
        }

        void setTopActNodeShowing(boolean topActNodeShowing) {
            this.topActNodeShowing = topActNodeShowing;
        }

        CopyOnWriteArraySet<String> getAliveNodes() {
            return aliveNodes;
        }

        void addAliveNodes(String aliveNode) {
            this.aliveNodes.add(aliveNode);
        }

        void removeAliveNodes(String aliveNode) {
            this.aliveNodes.remove(aliveNode);
        }
    }

    public static class LifecycleCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            if (activity instanceof RootNode) {
                ActNodeManager.getInstance().addAliveNodes(((RootNode) activity).getThisNode());
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (activity instanceof RootNode) {
                ActNodeManager.getInstance().setTopActNode(activity);
                ActNodeManager.getInstance().setTopActNodeShowing(true);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (activity instanceof RootNode
                    && activity == ActNodeManager.getInstance().getTopActNode()) {
                ActNodeManager.getInstance().setTopActNodeShowing(false);
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (activity instanceof RootNode) {
                ActNodeManager.getInstance().removeAliveNodes(((RootNode) activity).getThisNode());
            }
        }
    }
}
