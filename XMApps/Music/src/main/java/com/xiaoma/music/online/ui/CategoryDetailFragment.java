package com.xiaoma.music.online.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.music.R;
import com.xiaoma.music.common.adapter.CategoryDetailAdapter;
import com.xiaoma.music.common.constant.EventBusTags;
import com.xiaoma.music.common.constant.EventConstants;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.model.ShowHideEvent;
import com.xiaoma.music.common.ui.MainFragment;
import com.xiaoma.music.common.view.MusicLoadMoreView;
import com.xiaoma.music.common.view.RecyclerDividerItem;
import com.xiaoma.music.online.model.Category;
import com.xiaoma.music.online.model.CategoryDetailModel;
import com.xiaoma.music.online.vm.CategoryDetailVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import static com.xiaoma.guide.utils.NewGuide.RIGHT_AND_TOP;

/**
 * Created by ZYao.
 * Date ：2018/10/13 0013
 */
@PageDescComponent(EventConstants.PageDescribe.categoryFragmentDetail)
public class CategoryDetailFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener, KwPlayInfoManager.AlbumTypeChangedListener {

    private static final String TAG = "CategoryDetailFragment";
    private static final String KEY_CATEGORY = "key_category";
    private TextView categoryTitle;
    private CategoryDetailVM categoryVM;
    private CategoryDetailAdapter<CategoryDetailModel> mAdapter;
    private RecyclerView categoryDetailRv;
    private XmScrollBar scrollBar;
    private Category category;
    private ConstraintLayout mNetWorkView;
    private LinearLayout mLlContent;
    private NewGuide newGuide;
    private boolean hasShowedGuide;

    public static CategoryDetailFragment newInstance(Category category) {
        CategoryDetailFragment fragment = new CategoryDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        KwPlayInfoManager.getInstance().addAlbumTypeChangedListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_category_detail_list, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
        initData();
    }

    private void initEvent() {
        showContentView();
        categoryDetailRv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        int offset = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_margin);
        int first = mContext.getResources().getDimensionPixelOffset(R.dimen.size_item_first_margin);
        RecyclerDividerItem dividerItem = new RecyclerDividerItem(mContext, DividerItemDecoration.HORIZONTAL);
        dividerItem.setRect(0, 0, offset, 0, first);
        categoryDetailRv.addItemDecoration(dividerItem);
        mAdapter = new CategoryDetailAdapter<>(this, null);
        categoryDetailRv.setItemAnimator(null);
        categoryDetailRv.setAdapter(mAdapter);
        scrollBar.setRecyclerView(categoryDetailRv);
        mAdapter.setOnItemClickListener(this);
        setLoadMoreListener();
        initVM();
    }

    private void setLoadMoreListener() {
        mAdapter.setEnableLoadMore(true);
        mAdapter.setLoadMoreView(new MusicLoadMoreView());
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (categoryVM != null) {
                    categoryVM.fetchData(category, false);
                }
            }
        }, categoryDetailRv);
    }

    private void initVM() {
        categoryVM = ViewModelProviders.of(this).get(CategoryDetailVM.class);
        categoryVM.getSongList().observe(this, new Observer<XmResource<List<CategoryDetailModel>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<CategoryDetailModel>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<CategoryDetailModel>>() {
                        @Override
                        public void onSuccess(List<CategoryDetailModel> modelList) {
                            showContentView();
                            if (modelList != null) {
                                if (categoryVM.isFirstPage()) {
                                    checkEmptyView(modelList);
                                }
                                if (modelList.size() < CategoryDetailVM.PAGE_SIZE) {
                                    mAdapter.addData(modelList);
                                    mAdapter.loadMoreEnd();
                                    return;
                                }
                                mAdapter.addData(modelList);
                                mAdapter.loadMoreComplete();
                                mAdapter.setEnableLoadMore(true);
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
                            dismissProgress();
                            if (categoryVM.isFirstPage()) {
                                showNoNetView();
                            } else {
                                mAdapter.loadMoreFail();
                            }
                        }
                    });
                }
            }
        });
        categoryVM.getPlayPosition().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if (integer != null && mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void checkEmptyView(List<CategoryDetailModel> modelList) {
        if (modelList.isEmpty()) {
            showEmptyView();
            scrollBar.setVisibility(View.GONE);
        } else {
            scrollBar.setVisibility(View.VISIBLE);
            showContentView();
        }
    }

    private void initData() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            category = arguments.getParcelable(KEY_CATEGORY);
            if (category != null) {
                categoryTitle.setText(category.getName());
                fetchData();
            }
        }
    }

    private void fetchData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }
        categoryVM.fetchData(category, true);
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

    private void initView(View view) {
        categoryTitle = view.findViewById(R.id.tv_category_title);
        categoryDetailRv = view.findViewById(R.id.fragment_category_detail);
        scrollBar = view.findViewById(R.id.scroll_bar);
        mNetWorkView = view.findViewById(R.id.rl_detail_no_network);
        mLlContent = view.findViewById(R.id.rl_detail_content);
        view.findViewById(R.id.tv_retry).setOnClickListener(this);
    }

    @Override
    protected void showContentView() {
        mLlContent.setVisibility(View.VISIBLE);
        mNetWorkView.setVisibility(View.GONE);
    }

    @Override
    protected void showNoNetView() {
        mLlContent.setVisibility(View.GONE);
        mNetWorkView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (!NetworkUtils.isConnected(mContext)) {
            showToastException(mContext.getString(R.string.net_error));
            return;
        }
        if (categoryVM != null) {
            categoryVM.playSongList(position, mAdapter.getData());
            Log.d(TAG, "onItemClick: " + mAdapter.getData().size());
        } else {
            Log.d(TAG, "onItemClick: categoryVM is null");
        }
        dismissGuideWindow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_retry:
                showContentView();
                fetchData();
                break;

        }
    }

    @Subscriber(tag = EventBusTags.SHOW_OR_HIDE_MINE)
    public void onHideEvent(ShowHideEvent event) {
        if (event.isShowOnline()) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        KwPlayInfoManager.getInstance().removeAlbumTypeChangedListener(this);
    }

    @Override
    public void onChanged(String currentAlbumId, int currentType) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Subscriber(tag = EventBusTags.CATEGORY_DETAIL_ITEMVIEW_LOADED)
    public void itemViewLoaded(String s) {
        showFirstGuideWindow();
    }

    /**
     * 判断
     */
    private void showFirstGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, false))
            return;
        categoryDetailRv.postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = categoryDetailRv.getLayoutManager().findViewByPosition(0);
                View targetView = view.findViewById(R.id.cover);
                showFirstGuide(targetView);
            }
        }, 200);
    }

    private void showFirstGuide(View targetView) {
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.MUSIC_SHOWED)
                .setTargetView(targetView)
                .setTargetRect(NewGuide.getViewRect(targetView))
                .setGuideLayoutId(R.layout.guide_view_category_detail_one)
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

    @Subscriber(tag = EventBusTags.MUSIC_FETCH_SUCCESS)
    private void showSecondGuideWindow(String s) {
        if (hasShowedGuide) return;
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.MUSIC_SHOWED, GuideConstants.MUSIC_GUIDE_FIRST, false))
            return;
        hasShowedGuide = true;
        MainFragment fragment = (MainFragment) getActivity().getSupportFragmentManager().getFragments().get(0);
        View miniPlayer = fragment.getThumbPlayer();
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.MUSIC_SHOWED)
                .setTargetView(miniPlayer)
                .setTargetRect(NewGuide.getViewRect(miniPlayer))
                .setGuideLayoutId(R.layout.guide_view_category_detail_two)
                .setNeedHande(true)
                .setNeedShake(false)
                .setViewHandId(R.id.iv_gesture)
                .setHandLocation(RIGHT_AND_TOP)
                .setTargetViewTranslationX(0.01f)
                .setViewSkipId(R.id.tv_guide_skip)
                .build();
        newGuide.showGuide();

    }

    @Subscriber(tag = EventBusTags.MINI_PLAYER_CLICKED)
    public void minePlayerClicked(String s) {
        dismissGuideWindow();
    }


    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }
}
