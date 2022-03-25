package com.xiaoma.xting.local.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.LocalPlayList;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.adapter.EditableAdapter;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.view.XmDividerDecoration;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.vm.LocalFMVM;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/11
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_LOCAL_AM)
public class AMFragment extends VisibilityFragment {

    private RecyclerView mGallery;
    private XmScrollBar scrollBar;
    private EditableAdapter<AMChannelBean> mAdapter;
    private LocalFMVM localFMVM;

    public static AMFragment newInstance() {
        return new AMFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_gallery, container, false);
        return super.onCreateWrapView(inflate);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initView();
        initData();
        initListener();
    }

    private void bindView(@NonNull View view) {
        mGallery = view.findViewById(R.id.gallery);
        scrollBar = view.findViewById(R.id.scroll_bar);
    }

    private void initView() {
        mGallery.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        int horizontal = mContext.getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_horizontal_margin);
        int extra = mContext.getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_extra_margin);
        decor.setRect(horizontal, 0, horizontal, 0);
        ((ConstraintLayout.LayoutParams) mGallery.getLayoutParams()).setMargins(extra, 0, extra, 0);
        mGallery.addItemDecoration(decor);
    }

    private void initData() {
        mAdapter = new EditableAdapter<AMChannelBean>(null) {
            @Override
            public ItemEvent returnPositionEventMsg(int position) {
                CharSequence title = getData().get(position).getTitleText(mContext);
                if (title == null) {
                    return new ItemEvent(mContext.getString(R.string.state_unknown), String.valueOf(position));
                }
                return new ItemEvent(getData().get(position).getTitleText(mContext).toString(), getString(R.string.single_radio));
            }
        };
        mAdapter.setMarqueeResId(R.id.bottom);
        localFMVM = ViewModelProviders.of(getActivity()).get(LocalFMVM.class);
        localFMVM.getAMChannels().observe(this, new Observer<List<AMChannelBean>>() {
            @Override
            public void onChanged(@Nullable List<AMChannelBean> beans) {
                if (ListUtils.isEmpty(beans)) {
                    showEmptyView();
                } else {
                    showContentView();
                    mAdapter.setNewData(beans);
                }
            }

        });
        mAdapter.bindToRecyclerView(mGallery);
        scrollBar.setRecyclerView(mGallery);
        localFMVM.fetchAMChannels();
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!mAdapter.isOnEdit()) {
                    PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.RADIO_YQ);
                    AMChannelBean amChannelBean = (AMChannelBean) adapter.getData().get(position);
                    boolean b = LocalFMOperateManager.newSingleton().playChannel(amChannelBean);
                    if (!b) return;
                    LocalPlayList.getInstance().setAmPosition(position);
                }
            }
        });
        mAdapter.setOnEditListener(new EditableAdapter.OnEditListener<AMChannelBean>() {
            @Override
            public void onChange(List<AMChannelBean> data) {
                localFMVM.saveNewAMChannels(data);
                if (ListUtils.isEmpty(data)) {
                    showContentView();
                }
            }

            @Override
            public void onRemove(AMChannelBean data) {
                localFMVM.deleteAMChannels(data);
                if (mAdapter != null && mAdapter.getItemCount() == 0) {
                    showEmptyView();
                }
            }
        });
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
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mAdapter.isOnEdit()) {
            mAdapter.setOnEdit(false);
            return true;
        }
        return super.onBackPressed();
    }
}
