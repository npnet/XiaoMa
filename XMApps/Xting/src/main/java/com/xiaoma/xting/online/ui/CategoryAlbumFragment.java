package com.xiaoma.xting.online.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.guide.listener.ViewLayoutCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.view.XmDividerDecoration;
import com.xiaoma.xting.online.adapter.GridTextAdapter;
import com.xiaoma.xting.online.model.CategoryBean;
import com.xiaoma.xting.online.vm.CategoryVM;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_CATEGORY_SUB_ALBUM)
public class CategoryAlbumFragment extends VisibilityFragment {

    private static final int SPAN_COUNT = 2;

    private RecyclerView rvCategory;
    private XmScrollBar scrollBar;
    private CategoryVM categoryVM;
    private GridTextAdapter<CategoryBean> mAdapter;
    private boolean Loaded;
    private NewGuide newGuide;
    private boolean guideLoaded;

    public static CategoryAlbumFragment newInstance() {
        return new CategoryAlbumFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_category, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initView();
        initData();
    }

    private void bindView(@NonNull View view) {
        rvCategory = view.findViewById(R.id.rv_category);
        scrollBar = view.findViewById(R.id.scroll_bar);
    }

    private void initView() {
        rvCategory.setLayoutManager(new GridLayoutManager(mContext, SPAN_COUNT, LinearLayoutManager.HORIZONTAL, false));
//        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);

        DividerItemDecoration divider = new DividerItemDecoration(mContext, DividerItemDecoration.HORIZONTAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                int position = parent.getLayoutManager().getPosition(view);
                if (position % 2 == 0) {
                    outRect.top = mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_sub_item_vertical_margin);
                }else{
                    outRect.top=mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_vertical_margin);

                }

                int extraMargin =mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_extra_margin);

                if (position == 0 || position == 1) {
                    outRect.left += mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_horizontal_margin) + extraMargin;
                }

                outRect.right += mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_horizontal_margin) + extraMargin+20;
                // 通过设置一个空的drawable来隐藏默认分割线
                setDrawable(new ColorDrawable());

            }
        };
        rvCategory.addItemDecoration(divider);
//        int horizontal = mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_horizontal_margin);
//        int vertical = mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_vertical_margin);
//        int extra = mContext.getResources().getDimensionPixelOffset(R.dimen.size_category_item_extra_margin);
//        decor.setRect(horizontal, vertical, horizontal, vertical);
//        decor.setExtraMargin(extra, SPAN_COUNT);
//        rvCategory.addItemDecoration(decor);
    }

    private void initData() {
        mAdapter = new GridTextAdapter<CategoryBean>(null) {
            @Override
            public ItemEvent returnPositionEventMsg(int position) {
                return new ItemEvent(getData().get(position).getName(), EventConstants.PageDescribe.TAG_ALBUM);
            }
        };
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (NetworkUtils.isConnected(getContext())) {
                    CategoryBean data = (CategoryBean) adapter.getData().get(position);
                    if (data.isHeader) return;
                    if (getParentFragment() != null && getParentFragment().getParentFragment() != null
                            && getParentFragment().getParentFragment().getParentFragment() != null) {
                        NetFMFragment parentFragment = (NetFMFragment) getParentFragment().getParentFragment().getParentFragment();
                        parentFragment.addFragment(CategoryMultiAlbumListFragment.newInstance(data),
                                NetFMFragment.FRAGMENT_TAG_CHILD_ALBUM_CLASS);
                    }
                } else {
                    XMToast.showToast(mContext, ResUtils.getString(getContext(), R.string.net_not_connect));
                }
                dismissGuideWindow();
            }
        });
        rvCategory.setAdapter(mAdapter);
        scrollBar.setRecyclerView(rvCategory);

        categoryVM = ViewModelProviders.of(this).get(CategoryVM.class);
        categoryVM.getAlbumCategories().observe(this, new Observer<XmResource<List<CategoryBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<CategoryBean>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<CategoryBean>>() {
                        @Override
                        public void onLoading() {
                            if (checkVisible()) {
                                super.onLoading();
                            }
                        }

                        @Override
                        public void onSuccess(List<CategoryBean> data) {
                            if (CollectionUtil.isListEmpty(data)) {
                                showEmptyView();
                            } else {
                                Loaded = true;
                                mAdapter.setNewData(data);
                                showContentView();
                            }
                        }

                        @Override
                        public void onFailure(String msg) {
                            if (!Loaded) {
                                super.onFailure(msg);
                            }
                            finishGuide();
                        }

                        @Override
                        public void onError(int code, String message) {
                            if (!Loaded) {
                                super.onError(code, message);
                            }
                            finishGuide();
                        }
                    });
                }
            }
        });
    }


    private void shouldShowGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.XTING_SHOWED, GuideConstants.XTING_GUIDE_FIRST, false))
            return;
        addGuideWindow();
        guideLoaded = true;
        mAdapter.setCallBack(new ViewLayoutCallBack() {
            @Override
            public void onViewLayouted(View itemView) {
                if (itemView != null) {
//                    showGuide(itemView);
                    startGuide(itemView);
                }
            }
        });
        categoryVM.setViewLayoutCallBack(new CategoryVM.ViewLayoutCallBack() {
            @Override
            public void getDataUpdateCount(int count) {
                mAdapter.setDataUpdateCount(count);
            }
        });
    }

    private void finishGuide(){
        dismissGuideWindow();
        GuideDataHelper.finishGuideData(GuideConstants.XTING_SHOWED);
        TPUtils.put(getContext(),GuideConstants.XTING_SHOWED,true);
        TPUtils.put(getContext(),GuideConstants.XTING_GUIDE_FIRST,false);
    }

    private void startGuide(final View itemView) {
        itemView.post(new Runnable() {
            @Override
            public void run() {
                if (newGuide != null) {
                    newGuide.setTargetViewAndRect(itemView);
                    newGuide.startGuide();
                }
            }
        });
    }


    private void showGuide(final View targetView) {
        targetView.post(new Runnable() {
            @Override
            public void run() {
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.XTING_SHOWED)
                        .setTargetView(targetView)
                        .setTargetRect(NewGuide.getViewRect(targetView))
                        .setGuideLayoutId(R.layout.guide_view_category)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setTargetViewTranslationX(0.02f)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .build();
                newGuide.showGuide();
            }
        });
    }

    private void addGuideWindow() {
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.XTING_SHOWED)
                .setGuideLayoutId(R.layout.guide_view_category)
                .setNeedHande(true)
                .setNeedShake(true)
                .setViewHandId(R.id.iv_gesture)
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
                .setTargetViewTranslationX(0.02f)
                .setViewSkipId(R.id.tv_guide_skip)
                .build();
        newGuide.addGuideWindow();
    }


    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            categoryVM.fetchAlbumCategory();
        } else {
            showNoNetView();
            finishGuide();
        }
    }

    @Override
    protected void emptyOnRetry() {
        if (checkVisible()) checkNetWork();
    }

    @Override
    protected void noNetworkOnRetry() {
        if (checkVisible()) checkNetWork();
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible && !guideLoaded) {
            shouldShowGuideWindow();
        }
        if (realVisible && !Loaded) {
            checkNetWork();
        }
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }
}
