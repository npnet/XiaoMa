package com.xiaoma.component.base;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.xiaoma.component.nodejump.ChildNode;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.component.nodejump.RootNode;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.annotation.CommandProcessor;

import java.util.List;

/**
 * @author KY
 * @date 12/21/2018
 */
public class VisibleFragment extends BaseFragment implements ChildNode {
    private static final String TAG = VisibleFragment.class.getSimpleName();
    private boolean isHide;
    private boolean realVisible;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        checkVisible();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getLifecycle().addObserver(new GenericLifecycleObserver() {
            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                checkVisible();
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isHide = hidden;
        checkVisible();
    }

    /**
     * 检查自身的可见性
     *
     * @return 自身可见性
     */
    public boolean checkVisible() {
        // 1. 附属的Activity生命周期是否处于可见状态
        FragmentActivity activity = getActivity();

        if (activity != null) {
            Lifecycle.State activityState = activity.getLifecycle().getCurrentState();
            if (activityState == Lifecycle.State.RESUMED) {
                // 2.如果有父Fragment，判断父Fragment的可见性
                Fragment parentFragment = getParentFragment();
                boolean parentFragmentVisible = true;
                if (parentFragment != null) {
                    if (!(parentFragment instanceof VisibleFragment)) {
                        throw new RuntimeException("if you want use this fragment to get fragment visibility, pls extends this fragment in you all fragment");
                    }
                    /**
                     * 在检查其父Fragment的可见性时，会递归向上检查这个fragment的所有父级fragment的可见性
                     * 直到到达顶级的父Fragment为止。
                     */
                    parentFragmentVisible = ((VisibleFragment) parentFragment).checkVisible();
                }

                // 3.判断自身的可见性，考虑 在viewpager中切换、使用show/hide控制以及回退栈调用的情况
                boolean userVisibleHint = getUserVisibleHint();
                Lifecycle.State currentState = getLifecycle().getCurrentState();

                // 综合 附属Activity、父Fragment以及自身的可见性 最后得出本Fragment 是否可见
                if (userVisibleHint && !isHide && parentFragmentVisible && currentState == Lifecycle.State.RESUMED) {
                    if (!realVisible) {
                        onRealVisibleChange(true);
                    }
                    return true;
                }
            }
        }

        if (realVisible) {
            onRealVisibleChange(false);
        }
        return false;
    }

    /**
     * 当Fragment自身的可见性发生变化时通知其所有的子Fragment重新检查可见性
     *
     * @param visible
     */
    private void onRealVisibleChange(boolean visible) {
        realVisible = visible;
        if (realVisible && getRootNode() != null && getRootNode().isJumping()
                && NodeUtils.isInChains(getThisNode(), getRootNode().getJumpNodes())) {
            getRootNode().setJumping(parsingNodes(getRootNode().getJumpNodes()));
        }
        onVisibleChange(visible);
        List<Fragment> childFragments = getChildFragmentManager().getFragments();
        for (Fragment childFragment : childFragments) {
            if (childFragment instanceof VisibleFragment) {
                if (visible) {
                    // 如果是变为可见了，则还需要子Fragment检查其自身可见性
                    ((VisibleFragment) childFragment).checkVisible();
                } else {
                    // 如果是变为不可见了，则直接通知所有子Fragment不可见
                    ((VisibleFragment) childFragment).onRealVisibleChange(false);
                }
            }
        }

    }

    @CallSuper
    public void onVisibleChange(boolean realVisible) {
        KLog.d(TAG, "Class Name：" + this.getClass().getName() + "\t\tvisible：" + realVisible);
        if (realVisible) {
            CommandProcessor.register(this);
        } else {
            CommandProcessor.remove(this);
        }
    }

    @Override
    public String getThisNode() {
        return this.getClass().getSimpleName();
    }

    @Override
    public ChildNode getNextNode(String nextNode) {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ChildNode
                    && ((ChildNode) fragment).getThisNode().equals(nextNode)) {
                return (ChildNode) fragment;
            }
        }
        return null;
    }

    @Override
    public final boolean parsingNodes(String jumpNodes) {
        String nextNode = NodeUtils.getNextNode(getThisNode(), jumpNodes);
        KLog.d(TAG, "parsingNodes: " + nextNode);
        if (!TextUtils.isEmpty(nextNode)) {
            ChildNode nextChildNode = getNextNode(nextNode);
            if (nextChildNode != null
                    && nextChildNode.isShowing()
                    && nextChildNode.parsingNodes(jumpNodes)) {
                return true;
            } else {
                boolean jumpContinue = handleJump(nextNode);
                if (NodeUtils.isLastNode(nextNode, jumpNodes)) {
                    return false;
                } else {
                    return jumpContinue;
                }
            }
        } else {
            return false;
        }
    }

    @Override
    @CallSuper
    public boolean handleJump(String nextNode) {
        return false;
    }

    @Override
    public final boolean isShowing() {
        return checkVisible();
    }

    @Override
    public final RootNode getRootNode() {
        if (mActivity instanceof RootNode) {
            return (RootNode) mActivity;
        } else {
            return null;
        }
    }
}
