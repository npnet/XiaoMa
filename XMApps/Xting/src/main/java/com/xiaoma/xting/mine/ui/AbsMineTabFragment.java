package com.xiaoma.xting.mine.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.adapter.EditableAdapter;
import com.xiaoma.xting.common.adapter.IGalleryData;
import com.xiaoma.xting.common.view.XmDividerDecoration;
import com.xiaoma.xting.mine.vm.MineVM;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/17
 *     desc   :
 * </pre>
 */
public abstract class AbsMineTabFragment<T extends IGalleryData> extends VisibilityFragment {
    protected MineVM mVM;
    protected EditableAdapter<T> mAdapter;
    protected RecyclerView mGallery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        return super.onCreateWrapView(view);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    protected void initView(View view) {
        mGallery = view.findViewById(R.id.gallery);
        XmScrollBar scrollBar = view.findViewById(R.id.scroll_bar);
        mGallery.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        int horizontal = mContext.getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_horizontal_margin);
        int extra = mContext.getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_extra_margin);
        decor.setRect(horizontal, 0, horizontal, 0);
        decor.setExtraMargin(extra);
        mGallery.addItemDecoration(decor);
        mAdapter = getAdapter();
        mAdapter.bindToRecyclerView(mGallery);
        scrollBar.setRecyclerView(mGallery);
        view.findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    mAdapter.setOnEdit(false);
                }
            }
        });
    }

    protected void initData() {
        mVM = ViewModelProviders.of(this).get(MineVM.class);
        subscriberList();
    }

    protected abstract EditableAdapter<T> getAdapter();

    protected abstract void subscriberList();

    public abstract void clearAllItem();

    @Override
    public boolean onBackPressed() {
        if (mAdapter.isOnEdit()) {
            mAdapter.setOnEdit(false);
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    public interface IOnClearAllVisibleListener {
        void clearAllVisible(boolean isVisible);
    }

    protected IOnClearAllVisibleListener mClearAllVisibleListener;

    public void setOnClearAllVisibleListener(IOnClearAllVisibleListener listener) {
        this.mClearAllVisibleListener = listener;
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (!realVisible) {
            if (mAdapter != null) {
                mAdapter.stopMarquee();
                if (mAdapter.isOnEdit()) {
                    mAdapter.setOnEdit(false);
                }
            }
        } else {
            if (mVM != null) {
                refreshData();
            }
        }
    }

    /**
     * 刷新数据
     */
    protected abstract void refreshData();
}
