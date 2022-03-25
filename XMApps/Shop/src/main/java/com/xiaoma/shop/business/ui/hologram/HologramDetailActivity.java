package com.xiaoma.shop.business.ui.hologram;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.carlib.store.HologramRepo;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.hologram.HologramUsing;
import com.xiaoma.shop.business.model.HoloListModel;
import com.xiaoma.shop.business.model.HoloManInfo;
import com.xiaoma.shop.business.model.HologramDress;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.ui.view.DisableSwipeViewPager;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.business.vm.HologramDetailVm;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.track.ShopTrackManager;
import com.xiaoma.shop.common.util.UnitConverUtils;
import com.xiaoma.ui.toast.XMToast;

import java.util.Objects;

/**
 * Created by Gillben
 * date: 2019/3/4 0004
 * <p>
 * 全息影像详情
 */
@PageDescComponent(EventConstant.PageDesc.HOLOGRAM_DETAILS)
public class HologramDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String ARG_PARCEL = "holoModel";

    private LinearLayout prePageLayout;
    private LinearLayout nextPageLayout;
    private ImageView mPreviewImage;
    private TextView mPreviewName;
    private TextView mPreviewSize;
    private TabLayout mTabLayout;
    private DisableSwipeViewPager mViewPager;
    private ProgressButton mBuyButton;

    private BaseFragment[] fragments;
    private HologramDetailVm mHologramDetailVM;
    private HoloListModel mHoloModel;
    private View mLayoutRMB;
    private View mLayoutCoin;
    private TextView mTvRMBDiscount;
    private TextView mTvRMBOriginal;
    private TextView mTvScoreDiscount;
    private TextView mTvScoreOriginal;
    private String[] mTitle;
    private ViewHolder mHolder;
    private @ShopContract.Pay
    int mPayType = ShopContract.Pay.DEFAULT;
    private int mShowTabIndex = 0;

    public static void newInstanceActivity(Context context, HoloListModel bean) {
        Intent intent = new Intent(context, HologramDetailActivity.class);
        intent.putExtra(ARG_PARCEL, bean);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHoloModel = getIntent().getParcelableExtra(ARG_PARCEL);
//        fragments = new BaseFragment[]{HologramClothingFragment.newInstance(mHoloModel), HologramSkillFragment.newInstance()};
        fragments = new BaseFragment[]{HologramClothingFragment.newInstance(mHoloModel)};
        setContentView(R.layout.activity_hologram_detail);
        initView();
        initComponent();
    }

    private IOnPreNextClickListener mOnPreNextClickListener;

    private void initComponent() {
//        mTitle = new String[]{getResources().getString(R.string.clothing_title), getResources().getString(R.string.skill_title)};
        mTitle = new String[]{getResources().getString(R.string.clothing_title)};
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments == null ? null : fragments[position];
            }

            @Override
            public int getCount() {
                return fragments == null ? 0 : fragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle[position];
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabView(mTabLayout);
        mTabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent("全息影像详情TabLayout", "0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
//                changePageStatus(tab.getPosition(), fragments.length);
                mShowTabIndex = tab.getPosition();
                changeTab(tab);

            }
        });

        initVM();
    }

    private void initVM() {
        mHologramDetailVM = ViewModelProviders.of(this).get(HologramDetailVm.class);
        mHologramDetailVM.getHoloManInfoLiveData().observe(this, new Observer<XmResource<HoloManInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<HoloManInfo> holoManInfoXmResource) {
                holoManInfoXmResource.handle(new OnCallback<HoloManInfo>() {
                    @Override
                    public void onSuccess(HoloManInfo data) {
                        showContentView();
                        ImageLoader.with(HologramDetailActivity.this)
                                .load(data.getPicUrl())
                                .placeholder(R.drawable.place_holder)
                                .into(mPreviewImage);
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        showEmptyView();
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        showErrorView();
                    }
                });
            }
        });
        bindView(true);
    }

    private void bindView(boolean showLoading) {
        if (mHoloModel != null) {
            if (showLoading) {
                showLoadingView();
            }
            mPreviewName.setText(mHoloModel.getName());
            mPreviewSize.setText(getString(R.string.str_buy_count, UnitConverUtils.moreThanToConvert(mHoloModel.getDefaultUsedNum() + mHoloModel.getUsedNum())));

            if (mHoloModel.getUserBuyFlag() == 1 || isFree()) {
                int usingRole = HologramRepo.getUsingRoleId(this);
                if (Objects.equals(mHoloModel.getCode(), String.valueOf(usingRole))) {
                    mBuyButton.updateText(getString(R.string.state_using));
                    mBuyButton.setEnabled(false);
                    mBuyButton.setOnClickListener(null);
                } else {
                    mBuyButton.updateText(HologramDetailActivity.this.getString(R.string.state_use));
                    mBuyButton.setEnabled(true);
                    mBuyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Context context = v.getContext();
                            HologramUsing.useRole(context, mHoloModel);
                            XMToast.toastSuccess(context, R.string.successful_use);
                            bindView(false);
                        }
                    });
                }

                mLayoutRMB.setVisibility(View.INVISIBLE);
                mLayoutCoin.setVisibility(View.INVISIBLE);
            } else {
                mBuyButton.updateText(HologramDetailActivity.this.getString(R.string.state_buy));
                mBuyButton.setEnabled(true);
                mBuyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShopTrackManager.newSingleton().setBaseInfo(ResourceType.HOLOGRAM, mHoloModel.toTrackString(), this.getClass().getName());
//                        ShopTrackManager.newSingleton().manualUpdateEvent(TrackerEventType.ONCLICK, EventConstant.NormalClick.ACTION_BUY);
                        int saleScorePrice = mHoloModel.getDiscountScorePrice();
                        double saleRMBPrice = mHoloModel.getDiscountPrice();

                        if (saleRMBPrice > 0) {
                            payByScanCode(mHoloModel.getId(), mHoloModel.getName(),
                                    saleScorePrice, saleRMBPrice,
                                    getPayResultCallback(mHoloModel, mBuyButton));
                        } else {
                            PayHandler.getInstance().carCoinPayWindow(HologramDetailActivity.this, ResourceType.HOLOGRAM, mHoloModel.getId(),
                                    mHoloModel.getName(), saleScorePrice,false, getPayResultCallback(mHoloModel, mBuyButton));
                        }
                    }
                });

                handleUnFreePrice();
            }
            if(showLoading){
                mHologramDetailVM.fetchHoloManInfo(mHoloModel.getId());
            }
        }
    }

    private void payByScanCode(int id, String name, int scorePrice, double rmbPrice, PaySuccessResultCallback callback) {
        PayHandler.getInstance().scanCodePayWindow(this,
                ResourceType.HOLOGRAM, id, name,
                String.valueOf(rmbPrice),
                scorePrice, scorePrice == 0, callback);
    }

    private PaySuccessResultCallback getPayResultCallback(final HoloListModel bean, final ProgressButton pbBtn) {
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                bean.setUserBuyFlag(1);
                pbBtn.updateText("使用");
                XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYHOLOGRAM.getType());
                RequestManager.addUseNum(ResourceType.HOLOGRAM, bean.getId(), new ResultCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> result) {
                        bean.setUsedNum(bean.getUsedNum() + 1);
                        bindView(false);
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
            }

            @Override
            public void cancel() {

            }
        };
    }

    private void handleUnFreePrice() {
        int scoreOriginal = mHoloModel.getNeedScore();
        if (scoreOriginal > 0) {
            mPayType = ShopContract.Pay.COIN;
            int discountScorePrice = mHoloModel.getDiscountScorePrice();
            if (discountScorePrice > 0) {
                mTvScoreDiscount.setText(UnitConverUtils.moreThanToConvert(String.valueOf(discountScorePrice)));
                mTvScoreOriginal.setText(UnitConverUtils.moreThanToConvert(String.valueOf(scoreOriginal)));
            } else {
                mTvScoreDiscount.setText(UnitConverUtils.moreThanToConvert(String.valueOf(scoreOriginal)));
                mTvScoreOriginal.setVisibility(View.GONE);
            }
        } else {
            mLayoutCoin.setVisibility(View.GONE);
        }

        double priceOriginal = mHoloModel.getPrice();
        if (priceOriginal > 0) {
            mPayType += ShopContract.Pay.RMB;
            double discountPrice = mHoloModel.getDiscountPrice();
            if (discountPrice > 0) {
                mTvRMBDiscount.setText(getString(R.string.rmb_price, UnitConverUtils.moreThanToConvert(String.valueOf(discountPrice))));
                mTvRMBOriginal.setText(UnitConverUtils.moreThanToConvert(String.valueOf(priceOriginal)));
            } else {
                mTvRMBDiscount.setText(getString(R.string.rmb_price, UnitConverUtils.moreThanToConvert(String.valueOf(priceOriginal))));
                mTvRMBOriginal.setVisibility(View.GONE);
            }
        } else {
            mLayoutRMB.setVisibility(View.GONE);
        }
    }


    private void initView() {
        prePageLayout = findViewById(R.id.iv_pre_page);
        prePageLayout.setOnClickListener(this);
        nextPageLayout = findViewById(R.id.iv_next_page);
        nextPageLayout.setOnClickListener(this);
        mPreviewImage = findViewById(R.id.iv_preview_image);
        mPreviewName = findViewById(R.id.tv_preview_name);
        mPreviewSize = findViewById(R.id.tv_preview_size);
        mTabLayout = findViewById(R.id.hologram_tab_layout);
        mViewPager = findViewById(R.id.hologram_view_pager);
        mBuyButton = findViewById(R.id.bt_hologram_buy);
        mLayoutRMB = findViewById(R.id.layout_rmb);
        mLayoutCoin = findViewById(R.id.layout_coin);
        mTvRMBDiscount = findViewById(R.id.tvDiscountRMB);
        mTvRMBOriginal = findViewById(R.id.tvOriginalRMB);
        mTvScoreDiscount = findViewById(R.id.tvScoreDiscount);
        mTvScoreOriginal = findViewById(R.id.tvScoreOriginal);
        mBuyButton.setOnClickListener(this);
    }

    private void handleUnFreeBuy() {
        switch (mPayType) {
            case ShopContract.Pay.COIN:
                int discountScorePrice = mHoloModel.getDiscountScorePrice();
                PayHandler.getInstance().carCoinPayWindow(this, ResourceType.HOLOGRAM, mHoloModel.getId(), mHoloModel.getName(),
                        discountScorePrice > 0 ? discountScorePrice : mHoloModel.getNeedScore(),false, getPaySuccessCallback());
                break;
            case ShopContract.Pay.RMB:
                double discountPrice = mHoloModel.getDiscountPrice();
                PayHandler.getInstance().scanCodePayWindow(this, ResourceType.HOLOGRAM, mHoloModel.getId(), mHoloModel.getName(),
                        String.valueOf(discountPrice > 0 ? discountPrice : mHoloModel.getPrice()), 0, true,
                        getPaySuccessCallback());
                break;
            case ShopContract.Pay.COIN_AND_RMB:
                discountPrice = mHoloModel.getDiscountPrice();
                discountScorePrice = mHoloModel.getDiscountScorePrice();
                PayHandler.getInstance().scanCodePayWindow(this, ResourceType.HOLOGRAM, mHoloModel.getId(), mHoloModel.getName(),
                        String.valueOf(discountPrice > 0 ? discountPrice : mHoloModel.getPrice()),
                        discountScorePrice > 0 ? discountScorePrice : mHoloModel.getNeedScore(),
                        false, getPaySuccessCallback());
                break;
        }
    }

    private PaySuccessResultCallback getPaySuccessCallback() {
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                mHoloModel.setUserBuyFlag(1);
                mHoloModel.setUsedNum(mHoloModel.getUsedNum() + 1);
                mBuyButton.updateText(getString(R.string.state_use));
                XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYHOLOGRAM.getType());
            }

            @Override
            public void cancel() {

            }
        };
    }

    private void pageToggle(boolean next) {
        int totalPage = fragments.length;
        int currentPage = mViewPager.getCurrentItem();
        if (next) {
            if (currentPage == totalPage - 1) {
                return;
            }
            ++currentPage;
        } else {
            if (currentPage == 0) {
                return;
            }
            --currentPage;
        }

        changePageStatus(currentPage, totalPage);
        mViewPager.setCurrentItem(currentPage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_hologram_buy:
                if (mHoloModel.getUserBuyFlag() == 1
                        || isFree()) {
                    manualUpdateTrack(mHoloModel.toTrackString(), EventConstant.NormalClick.ACTION_USE);
                    HologramUsing.useRole(view.getContext(), mHoloModel);
                } else {
                    manualUpdateTrack(mHoloModel.toTrackString(), EventConstant.NormalClick.ACTION_BUY);
                    handleUnFreeBuy();
                }
                break;
            case R.id.iv_pre_page:
                if (mOnPreNextClickListener != null) {
                    mOnPreNextClickListener.onPlayPre();
                }
//                pageToggle(false);
                break;

            case R.id.iv_next_page:
                if (mOnPreNextClickListener != null) {
                    mOnPreNextClickListener.onPlayNext();
                }
//                pageToggle(mTabLayout.getSelectedTabPosition() == 0);
                break;
        }
    }

    private HologramDress searchSelectedCloth() {
     HologramClothingFragment clothingFragment = (HologramClothingFragment) fragments[0];
        return clothingFragment.searchSelectedCloth();
    }

    public void showHideArrow(int tabIndex, boolean showPreArrow, boolean showNextArrow) {
        if (mShowTabIndex != tabIndex) return;
        prePageLayout.setVisibility(showPreArrow ? View.VISIBLE : View.GONE);
        nextPageLayout.setVisibility(showNextArrow ? View.VISIBLE : View.GONE);
    }

    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < fragments.length; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new ViewHolder(view);
                    mHolder.tabTv.setText(mTitle[i]);
                }
            }
            if (i == 0) {
                mHolder.tabTv.setSelected(true);
                mHolder.tabTv.setTextAppearance(this, R.style.text_view_light_blue);
            }
        }
    }

    private String changeTab(TabLayout.Tab tab) {
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab1 = mTabLayout.getTabAt(i);
            View customView = tab1.getCustomView();
            mHolder = new ViewHolder(customView);
            mHolder.tabTv.setTextAppearance(this, R.style.text_view_normal);
        }
        View customView = tab.getCustomView();
        if (customView == null) {
            return "";
        }
        mHolder = new ViewHolder(customView);
        mHolder.tabTv.setTextAppearance(this, R.style.text_view_light_blue);
        return mHolder.tabTv.getText().toString();
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }

    public void changePageStatus(int currentPage, int totalPage) {
        if (currentPage == totalPage - 1) {
            nextPageLayout.setVisibility(View.GONE);
            prePageLayout.setVisibility(View.VISIBLE);
        } else if (currentPage == 0) {
            prePageLayout.setVisibility(View.GONE);
            nextPageLayout.setVisibility(View.VISIBLE);
        } else {
            prePageLayout.setVisibility(View.VISIBLE);
            nextPageLayout.setVisibility(View.VISIBLE);
        }
    }

    public void setOnOnPreNextClickListener(IOnPreNextClickListener listener) {
        this.mOnPreNextClickListener = listener;
    }

    public interface IOnPreNextClickListener {
        void onPlayPre();

        void onPlayNext();
    }

    private boolean isFree() {
        return mHoloModel.getDiscountPrice() == 0
                && mHoloModel.getDiscountScorePrice() == 0;
    }

    private void manualUpdateTrack(String content, String eventAction) {
        XmAutoTracker.getInstance().onEvent(eventAction, content, this.getClass().getName(), EventConstant.PageDesc.HOLOGRAM_DETAILS);
    }
}
