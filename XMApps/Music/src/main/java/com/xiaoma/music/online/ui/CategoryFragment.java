package com.xiaoma.music.online.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xiaoma.component.base.LazyLoadFragment;
import com.xiaoma.guide.listener.ViewLayoutCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.R;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.view.RecyclerDividerItem;
import com.xiaoma.music.online.adapter.CategoryAdapter;
import com.xiaoma.music.online.model.Category;
import com.xiaoma.music.online.vm.CategoryVM;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zs
 * @date 2018/10/9 0009.
 */
@PageDescComponent(EventConstants.PageDescribe.categoryFragment)
public class CategoryFragment extends LazyLoadFragment {

    public static final int SPAN_COUNT = 2;
    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;
    private CategoryVM mCategoryVM;
    private XmScrollBar scrollBar;
    private NewGuide newGuide;
    private boolean hasShowedGuide;

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_category;
    }

    @Override
    protected void initView(View view) {
        EventBus.getDefault().register(this);
        bindView(view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, SPAN_COUNT, GridLayoutManager.HORIZONTAL, false));
        int offset = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_category_margin);
        int first = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_category_first_margin);
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(mContext, DividerItemDecoration.HORIZONTAL);
        dividerItem.setRect(0, 0, offset, 0, first);
        mRecyclerView.addItemDecoration(dividerItem);
        mAdapter = new CategoryAdapter(mContext, new ArrayList<>(), R.layout.item_category);
        mRecyclerView.setAdapter(mAdapter);
        scrollBar.setRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new XMBaseAbstractRyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, RecyclerView.ViewHolder holder, int position) {

                EventBus.getDefault().post(mAdapter.getDatas().get(position), EventBusTags.SKIP_TO_CATEGORY_FRAGMENT);
                dismissGuideWindow();
            }
        });
    }

    private void bindView(@NonNull View view) {
        mRecyclerView = view.findViewById(R.id.fragment_category_rv);
        scrollBar = view.findViewById(R.id.scroll_bar);
    }

    @Override
    protected void loadData() {
        super.loadData();
        showGuide();
        mCategoryVM = ViewModelProviders.of(this).get(CategoryVM.class);
        mCategoryVM.getCategoryList().observe(this, new Observer<XmResource<List<Category>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<Category>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<Category>>() {
                        @Override
                        public void onSuccess(List<Category> categories) {
                            if (categories != null) {
                                mAdapter.setDatas(categories);
                                checkEmptyView(categories);
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            super.onError(code, message);
                            dismissGuideWindow();
                        }
                    });
                }
            }
        });
        fetchData();
    }

    private void fetchData() {
//        showLoadingView();
        mCategoryVM.initVM();
    }

    private void checkEmptyView(List<Category> categories) {
        if (categories.isEmpty()) {
            showEmptyView();
            scrollBar.setVisibility(View.GONE);
        } else {
            scrollBar.setVisibility(View.VISIBLE);
            showContentView();
        }
    }

    @Override
    protected void errorOnRetry() {
        super.errorOnRetry();
        fetchData();
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        fetchData();
    }

    @Override
    protected void cancelData() {
        KLog.d("cancelData");
    }


    private void shouldShowGuideWindow(View targetView) {
        if (targetView != null) {
            startGuide(targetView);
            return;
        }
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                View targetView = mRecyclerView.getLayoutManager().findViewByPosition(0);
                if (targetView == null) return;
                startGuide(targetView);
            }
        });
    }

    private void startGuide(View targetView) {
        targetView.post(new Runnable() {
            @Override
            public void run() {
                if (newGuide != null) {
                    newGuide.setTargetViewAndRect(targetView);
                    newGuide.startGuide();
                }
            }
        });
    }

    private void showGuide(View targetView) {
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.MUSIC_SHOWED)
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

    private void showGuide() {
        if (!shouldShowGuideWindow() || hasShowedGuide) return;
        hasShowedGuide = true;
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.MUSIC_SHOWED)
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

    @Subscriber(tag = "music_data_update")
    public void startGuide(int count) {
        mAdapter.setCallBack(new ViewLayoutCallBack() {
            @Override
            public void onViewLayouted(View targetView) {
                shouldShowGuideWindow(targetView);
            }
        });
        mAdapter.setDataUpdateCount(count);
    }

    @Subscriber(tag = "finish_guide")
    public void finishGuide(int i){
        dismissGuideWindow();
        GuideDataHelper.finishGuideData(GuideConstants.MUSIC_SHOWED);
        TPUtils.put(getContext(),GuideConstants.MUSIC_SHOWED,true);
        TPUtils.put(getContext(),GuideConstants.MUSIC_GUIDE_FIRST,false);
    }

    private boolean shouldShowGuideWindow() {
        return GuideDataHelper.shouldShowGuide(getActivity(), GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, false);
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
