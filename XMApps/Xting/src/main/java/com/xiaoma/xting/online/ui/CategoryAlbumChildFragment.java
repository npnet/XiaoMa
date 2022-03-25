package com.xiaoma.xting.online.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.xting.HomeFragment;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.adapter.GalleryAdapter;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.view.MiniPlayerView;
import com.xiaoma.xting.common.view.XmDividerDecoration;
import com.xiaoma.xting.online.model.AlbumBean;
import com.xiaoma.xting.online.model.TagBean;
import com.xiaoma.xting.online.vm.CategoryAlbumVM;
import com.xiaoma.xting.online.vm.PlayWithPlayerInfoVM;

import java.util.List;

import static com.xiaoma.guide.utils.NewGuide.RIGHT_AND_TOP;


/**
 * @author KY
 * @date 2018/10/9
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_CATEGORY_ALBUM_CHILD)
public class CategoryAlbumChildFragment extends VisibilityFragment {

    public static final String TAG = "TAG";
    public static final String CATEGORY = "CATEGORY";
    private RecyclerView mGallery;
    private XmScrollBar scrollBar;
    private CategoryAlbumVM categoryVM;
    private long categoryId;
    private TagBean tagBean;
    private GalleryAdapter<AlbumBean> mAdapter;
    private boolean Loaded;
    private NewGuide newGuide;
    private Handler handler;
    private Runnable runnable;
    private boolean guideLoaded;

    public static CategoryAlbumChildFragment newInstance(long categoryId, TagBean childCategoryBean) {
        CategoryAlbumChildFragment categoryAlbumChildFragment = new CategoryAlbumChildFragment();
        Bundle args = new Bundle();
        args.putString(TAG, GsonHelper.toJson(childCategoryBean));
        args.putLong(CATEGORY, categoryId);
        categoryAlbumChildFragment.setArguments(args);
        return categoryAlbumChildFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_gallery, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            categoryId = bundle.getLong(CATEGORY);
            tagBean = GsonHelper.fromJson(bundle.getString(TAG), TagBean.class);
        }
        bindView(view);
        initView();
        initData();
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
        decor.setExtraMargin(extra);
        mGallery.addItemDecoration(decor);
    }

    private void initData() {
        final PlayWithPlayerInfoVM playVM = ViewModelProviders.of(getActivity()).get(PlayWithPlayerInfoVM.class);
        mAdapter = new GalleryAdapter<>(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (NetworkUtils.isConnected(getContext())) {
                    playVM.play(BeanConverter.toPlayerInfo(mAdapter.getItem(position)));
                } else {
                    XMToast.showToast(mContext, ResUtils.getString(getContext(), R.string.net_not_connect));
                }
                showSecondGuideWindow();
            }
        });
        if (GuideDataHelper.shouldShowGuide(getContext(),GuideConstants.XTING_SHOWED,GuideConstants.XTING_GUIDE_FIRST,false)){
            mAdapter.setAlbumItemClickListener(new GalleryAdapter.AlbumItemClickListener() {
                @Override
                public void onItemClick() {
                    showSecondGuideWindow();
                }
            });
        }
        mAdapter.bindToRecyclerView(mGallery);
        scrollBar.setRecyclerView(mGallery);
        categoryVM = ViewModelProviders.of(this).get(CategoryAlbumVM.class);
        categoryVM.getChildCategoryAlbums().observe(this, new Observer<XmResource<List<AlbumBean>>>() {

            @Override
            public void onChanged(@Nullable XmResource<List<AlbumBean>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<AlbumBean>>() {
                        @Override
                        public void onSuccess(List<AlbumBean> data) {
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
                        }

                        @Override
                        public void onError(int code, String message) {
                            if (!Loaded) {
                                super.onError(code, message);
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 判断
     */
    private void showFirstGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.XTING_SHOWED, GuideConstants.XTING_GUIDE_FIRST, false))
            return;
        initHandlerAndRunnable();
        mGallery.postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = mGallery.getLayoutManager().findViewByPosition(0);
                if (view == null) {
                    handler.postDelayed(runnable, 100);
                    return;
                }
                View targetView = view.findViewById(R.id.iv_cover);
                showFirstGuide(targetView);
                guideLoaded = true;
            }
        }, 500);
    }

    private void initHandlerAndRunnable() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                View view = mGallery.getLayoutManager().findViewByPosition(0);
                if (view == null)
                    handler.postDelayed(runnable, 100);
                else
                    showFirstGuide(view.findViewById(R.id.iv_cover));
            }
        };
    }

    private void showFirstGuide(View targetView) {
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.XTING_SHOWED)
                .setTargetView(targetView)
                .setTargetRect(NewGuide.getViewRect(targetView))
                .setGuideLayoutId(R.layout.guide_view_category_child)
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

    private void showSecondGuideWindow() {
        dismissGuideWindow();
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.XTING_SHOWED, GuideConstants.XTING_GUIDE_FIRST, false))
            return;
        HomeFragment fragment = (HomeFragment) getActivity().getSupportFragmentManager().getFragments().get(0);
        MiniPlayerView miniPlayer = fragment.getMiniPlayer();
        miniPlayer.setGuideClickCallBack(new MiniPlayerView.GuideClickCallBack() {
            @Override
            public void onClick() {
                dismissGuideWindow();
            }
        });
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.XTING_SHOWED)
                .setTargetView(miniPlayer)
                .setTargetRect(NewGuide.getViewRect(miniPlayer))
                .setGuideLayoutId(R.layout.guide_view_category_child_two)
                .setNeedHande(true)
                .setNeedShake(false)
                .setViewHandId(R.id.iv_gesture)
                .setHandLocation(RIGHT_AND_TOP)
                .setViewSkipId(R.id.tv_guide_skip)
                .build();
        newGuide.showGuide();
    }

    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            categoryVM.fetchChildCategoryAlbums(categoryId, tagBean);
        } else {
            showNoNetView();
        }
    }

    @Override
    protected void noNetworkOnRetry() {
        if (checkVisible()) checkNetWork();
    }

    @Override
    protected void emptyOnRetry() {
        if (checkVisible()) checkNetWork();
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible && !Loaded) {
            checkNetWork();
        } else {
            mAdapter.stopMarquee();
        }
        if (realVisible && !guideLoaded) {
            showFirstGuideWindow();
        }
    }

    private void dismissGuideWindow() {
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }
}
