package com.xiaoma.component.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 懒加载Fragment
 *
 * @author zs
 * @date 2018/10/17 0017.
 */
public abstract class LazyLoadFragment extends BaseFragment {

    //当前Fragment是否处于可见状态标志，防止因ViewPager的缓存机制而导致回调函数的触发
    private boolean isFragmentVisible;
    //是否是第一次网络加载
    protected boolean isFirst = true;
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutResource(), container, false);
        return super.onCreateWrapView(mRootView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        //页面可见，并且是第一次加载
        if (isFragmentVisible && isFirst) {
            onFragmentVisibleChange(true);
        }
    }

    protected abstract @LayoutRes
    int getLayoutResource();

    protected abstract void initView(View view);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isFragmentVisible = true;
        }
        if (mRootView == null) {
            return;
        }
        //可见，并且没有加载过
        if (isFirst && isFragmentVisible) {
            onFragmentVisibleChange(true);
            return;
        }
        //由可见—>不可见 已经加载过
        if (isFragmentVisible) {
            onFragmentVisibleChange(false);
            isFragmentVisible = false;
        }
    }

    /**
     * 当前fragment可见状态发生变化时会回调该方法
     * 如果当前fragment是第一次加载，等待onCreateView后才会回调该方法，其它情况回调时机跟 {@link #setUserVisibleHint(boolean)}一致
     * 在该回调方法中可以做一些加载数据操作，甚至是控件的操作.
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            if (!isFirst) {
                return;
            }
            isFirst = false;
            loadData();
        } else {
            cancelData();
        }
    }

    protected void loadData() {

    }

    /**
     * 取消加载
     */
    protected abstract void cancelData();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirst = true;
    }
}
