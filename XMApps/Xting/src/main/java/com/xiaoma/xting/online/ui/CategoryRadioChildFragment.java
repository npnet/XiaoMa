package com.xiaoma.xting.online.ui;

import android.arch.lifecycle.Observer;
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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.adapter.GalleryAdapter;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.common.view.XmDividerDecoration;
import com.xiaoma.xting.online.model.ProvinceBean;
import com.xiaoma.xting.online.model.RadioBean;
import com.xiaoma.xting.online.vm.CategoryRadioVM;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/9
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_CATEGORY_RADIO_CHILD)
public class CategoryRadioChildFragment extends VisibilityFragment {

    public static final String CATEGORY = "CATEGORY";
    private RecyclerView mGallery;
    private XmScrollBar scrollBar;
    private CategoryRadioVM categoryRadioVM;
    private ProvinceBean provinceBean;
    private GalleryAdapter<RadioBean> mAdapter;
    private boolean Loaded;

    public static CategoryRadioChildFragment newInstance(ProvinceBean provinceBean) {
        CategoryRadioChildFragment categoryAlbumChildFragment = new CategoryRadioChildFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, GsonHelper.toJson(provinceBean));
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
            provinceBean = GsonHelper.fromJson(bundle.getString(CATEGORY), ProvinceBean.class);
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
        mAdapter = new GalleryAdapter<>(this);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (NetworkUtils.isConnected(getContext())) {
                    PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
                    PlayerSourceFacade.newSingleton().getPlayerControl().switchPlayerAlbum(null);

                    final RadioBean radioBean = (RadioBean) adapter.getData().get(position);
                    PlayerInfo tempPlayerInfo = new PlayerInfo();
                    PlayerInfo playerInfo = BeanConverter.toPlayerInfo(radioBean);

                    tempPlayerInfo.setType(playerInfo.getType());
                    tempPlayerInfo.setAlbumId(playerInfo.getAlbumId());
                    tempPlayerInfo.setAlbumName(playerInfo.getAlbumName());
                    tempPlayerInfo.setImgUrl(playerInfo.getImgUrl());
                    tempPlayerInfo.setSourceSubType(playerInfo.getSourceSubType());
                    PlayerInfoImpl.newSingleton().onPlayerInfoChanged(tempPlayerInfo);

                    if (radioBean.getScheduleID() == 0) {
                        PlayerSourceFacade.newSingleton().getPlayerControl().playWithModel(radioBean);
                    } else {

                        PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(playerInfo, new IFetchListener() {
                            @Override
                            public void onLoading() {
                                showProgressDialog(R.string.loading_data);
                            }

                            @Override
                            public void onSuccess(Object t) {
                                dismissProgress();
                            }

                            @Override
                            public void onFail() {
                                dismissProgress();
                                //获取数据列表为空,直接播放
                                PlayerSourceFacade.newSingleton().getPlayerControl().playWithModel(radioBean);
                            }

                            @Override
                            public void onError(int code, String msg) {
                                dismissProgress();
                                XMToast.showToast(mContext, R.string.net_work_error);
                            }
                        });
                    }
                } else {
                    XMToast.showToast(mContext, ResUtils.getString(getContext(), R.string.net_not_connect));
                }
            }
        });
        mAdapter.bindToRecyclerView(mGallery);
        scrollBar.setRecyclerView(mGallery);
        categoryRadioVM = ViewModelProviders.of(this).get(CategoryRadioVM.class);
        categoryRadioVM.getProvinceRadios().observe(this, new Observer<XmResource<List<RadioBean>>>() {

            @Override
            public void onChanged(@Nullable XmResource<List<RadioBean>> listXmResource) {
                if (listXmResource != null) {
                    listXmResource.handle(new OnCallback<List<RadioBean>>() {
                        @Override
                        public void onSuccess(List<RadioBean> data) {
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

    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            categoryRadioVM.fetchProvinceRadios(provinceBean.getProvinceCode());
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
    }
}
