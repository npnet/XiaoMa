package com.xiaoma.xkan.common.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.common.view.FilterView;
import com.xiaoma.xkan.common.view.XmScrollBar;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

/*
 *  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.xkan.common.base
 *  @文件名:   BaseFilterFragment
 *  @创建者:   Rookie
 *  @创建时间:  2018/11/26 15:26
 *  @描述：    文件排序等
 */

public abstract class BaseFilterFragment extends BaseFragment {

    protected RecyclerView rv;
    protected XmScrollBar xmScrollBar;
    private static final int SPAN_COUNT = 2;
    protected FilterView filterView;
    protected TextView tvPath;
    protected GridLayoutManager mGridLayoutManager;
    private View mBarContainer;
    private View mEmptyView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
    }

    private void bindView(View rootView) {
        //防止id或者控件crash
        try {
            rv = rootView.findViewById(R.id.rv_file);
            xmScrollBar = rootView.findViewById(R.id.scroll_bar);

            filterView = rootView.findViewById(R.id.filter_view);
            tvPath = rootView.findViewById(R.id.tv_path);
            mBarContainer = rootView.findViewById(R.id.fl_bar_container);

            initVM();
            initView();
            initAdapter(mEmptyView);
            xmScrollBar.setRecyclerView(rv);
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initView() {

        mGridLayoutManager = new GridLayoutManager(mContext, SPAN_COUNT, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(mGridLayoutManager);
        rv.setHasFixedSize(true);

        mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.empty_view, rv, false);
        TextView tvTips = mEmptyView.findViewById(R.id.tv_empty);
        ImageView ivTips = mEmptyView.findViewById(R.id.iv_empty);
        tvTips.setText(getResources().getString(getTipStr()));
        ivTips.setImageResource(getEmtpyImgId());

        filterView.setOnFilterListener(new FilterView.OnFilterListener() {
            @Override
            public void filterByName(final boolean isZ) {
                showProgressDialog(R.string.base_loading);
                ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                    @Override
                    public void run() {
                        filterName(isZ);
                        dismissProgress();
                    }
                });

            }

            @Override
            public void filterByDate(final boolean isFar) {
                showProgressDialog(R.string.base_loading);
                ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                    @Override
                    public void run() {
                        filterDate(isFar);
                        dismissProgress();
                    }
                });
            }

            @Override
            public void filterBySize(final boolean isBig) {
                showProgressDialog(R.string.base_loading);
                ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                    @Override
                    public void run() {
                        filterSize(isBig);
                        dismissProgress();
                    }
                });
            }
        });
    }

    @Subscriber(tag = XkanConstants.REFRESH_SCROLL)
    public void refreshScroll(boolean visible) {
        mBarContainer.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @Subscriber(tag = XkanConstants.UPDATE_DATA)
    public void updateData(String event) {
        //初始化数据和监听
        initData();
    }

    public void handlerEmptyView(List<UsbMediaInfo> mediaInfos) {
        filterView.setVisibility(ListUtils.isEmpty(mediaInfos) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void scrollToPosition(int position) {
        mGridLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    public abstract int getLayoutId();

    public abstract int getTipStr();

    public abstract int getEmtpyImgId();

    public abstract void initAdapter(View emptyView);

    public abstract void initVM();

    public abstract void initData();

    public abstract void filterName(boolean isZ);

    public abstract void filterDate(boolean isFar);

    public abstract void filterSize(boolean isBig);
}
