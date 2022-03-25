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
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.Type;
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
import com.xiaoma.xting.online.model.CategoryBean;
import com.xiaoma.xting.online.model.RadioBean;
import com.xiaoma.xting.online.vm.CategoryRadioVM;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/9
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_CATEGORY_SUB_RADIO_SINGLE)
public class CategorySingleRadioListFragment extends VisibilityFragment {

    public static final String CATEGORY = "CATEGORY";
    private RecyclerView mGallery;
    private XmScrollBar scrollBar;
    private CategoryRadioVM categoryRadioVM;
    private CategoryBean categoryBean;
    private GalleryAdapter<RadioBean> mAdapter;
    private TextView categoryName;
    private boolean Loaded;

    public static BaseFragment newInstance(CategoryBean categoryBean) {
        CategorySingleRadioListFragment categoryChildFragment = new CategorySingleRadioListFragment();
        Bundle args = new Bundle();
        args.putString(CATEGORY, GsonHelper.toJson(categoryBean));
        categoryChildFragment.setArguments(args);
        return categoryChildFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_radio_single_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            categoryBean = GsonHelper.fromJson(bundle.getString(CATEGORY), CategoryBean.class);
        }
        bindView(view);
        initView();
        initData();
    }

    private void bindView(@NonNull View view) {
        mGallery = view.findViewById(R.id.rv_category);
        categoryName = view.findViewById(R.id.category_name);
        scrollBar = view.findViewById(R.id.scroll_bar);
        mStateView = view.findViewById(R.id.state_view);
        mStateView.setOnRetryClickListener(new OnRetryClickListener() {
            @Override
            public void onRetryClick(View view, Type type) {
                switch (type) {
                    case ERROR:
                        errorOnRetry();
                        break;
                    case EEMPTY:
                        emptyOnRetry();
                        break;
                    case NONETWORK:
                        noNetworkOnRetry();
                        break;
                }
            }
        });
    }

    private void initView() {
        mGallery.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        int horizontal = mContext.getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_horizontal_margin);
        int extra = mContext.getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_extra_margin);
        decor.setRect(horizontal, 0, horizontal, 0);
        decor.setExtraMargin(extra);
        mGallery.addItemDecoration(decor);
        categoryName.setText(categoryBean.getName());
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
                    PlayerInfo playerInfo = BeanConverter.toPlayerInfo(radioBean);

                    playerInfo.setProgramId(-1);
                    PlayerInfoImpl.newSingleton().onPlayerInfoChanged(playerInfo);
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
                        }
                    });
//                    }
                } else {
                    XMToast.showToast(mContext, ResUtils.getString(getContext(), R.string.net_not_connect));
                }
            }
        });
        mAdapter.bindToRecyclerView(mGallery);
        scrollBar.setRecyclerView(mGallery);
        categoryRadioVM = ViewModelProviders.of(this).get(CategoryRadioVM.class);

        if (getString(R.string.radio_country).equals(categoryBean.getName())) {
            categoryRadioVM.getCountryRadios().observe(this, getObserver());
        } else if (getString(R.string.radio_network).equals(categoryBean.getName())) {
            categoryRadioVM.getNetworkRadios().observe(this, getObserver());
        } else {
            categoryRadioVM.getCategoryRadios().observe(this, getObserver());
        }
    }

    @NonNull
    private Observer<XmResource<List<RadioBean>>> getObserver() {
        return new Observer<XmResource<List<RadioBean>>>() {

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
        };
    }

    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            if (getString(R.string.radio_country).equals(categoryBean.getName())) {
                categoryRadioVM.fetchCountryRadios();
            } else if (getString(R.string.radio_network).equals(categoryBean.getName())) {
                categoryRadioVM.fetchNetworkRadios();
            } else {
                categoryRadioVM.fetchCategoryRadios(categoryBean.getId());
            }
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

    @Override
    public boolean onBackPressed() {
        return popBack();
    }
}
