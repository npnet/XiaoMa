package com.xiaoma.shop.business.ui.theme;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.guide.listener.GuideCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.PersonalityThemeDetailsAdapter;
import com.xiaoma.shop.business.download.DownloadListener;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.SkinDownload;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.business.pay.PayHandler;
import com.xiaoma.shop.business.pay.PaySuccessResultCallback;
import com.xiaoma.shop.business.skin.SkinUsing;
import com.xiaoma.shop.business.ui.main.MainActivity;
import com.xiaoma.shop.business.ui.view.ProgressButton;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.track.ShopTrackManager;
import com.xiaoma.shop.common.util.PriceUtils;
import com.xiaoma.shop.common.util.UnitConverUtils;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

import java.util.Objects;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/6
 */
@PageDescComponent(EventConstant.PageDesc.FRAGMENT_SKIN_DETAILS)
public class ThemeDetailsFragment extends BaseFragment implements BackHandlerHelper.FragmentBackHandler {

    private static final String TAG = ThemeDetailsFragment.class.getSimpleName();

    public static final String ARG_BEAN = "bean";
    private PersonalityThemeDetailsAdapter mAdapter;
    private ShowBigPopView mShowBigPopView;
    private TextView mDescriptionTV;
    private TextView mDownloadTV;
    private TextView mUseCountTV;
    private XmScrollBar mXmScrollBar;
    private ProgressButton mLeftPbBtn;
    private ProgressButton mRightPbBtn;
    private SkinVersionsBean mSkins;
    private TextView mYellowCoinTV;
    private TextView mLatestRMBTV;
    private TextView mOriginalRMBTV;
    private ConstraintLayout mCoinLayout;
    private ConstraintLayout mRmbLayout;
    private TextView mCoinOriginal;
    private NewGuide newGuide;
    private View view; // contentView
    private RecyclerView thumbnailsRV;
    private Handler handler;
    private Runnable runnable;
    private DownloadListener mDownloadListener;
    private BroadcastReceiver mSkinChangeReceiver;
    private SkinDownload mSkinDownload;
    private PayHandler.PayCallback mPayCallback;

    public static ThemeDetailsFragment newInstance(Parcelable bean) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_BEAN, bean);
        ThemeDetailsFragment fragment = new ThemeDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return view = inflater.inflate(R.layout.fragment_theme_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 下载状态监听
        mSkinDownload = SkinDownload.getInstance();
        mSkinDownload.addDownloadListener(mDownloadListener = new DownloadListener() {
            @Override
            public void onDownloadStatus(DownloadStatus downloadStatus) {
                Log.i(TAG, String.format("onDownloadStatus: { name: %s, status: %s }",
                        mSkins.getAppName(), downloadStatus != null ? SkinDownload.getDownloadStatusStr(downloadStatus.status) : "null"));
                if (downloadStatus != null
                        && !Objects.equals(downloadStatus.downUrl, mSkins.getUrl()))
                    return;
                ViewCompat.postOnAnimation(view, new Runnable() {
                    @Override
                    public void run() {
                        if (isDestroy())
                            return;
                        handleBtnState();
                    }
                });

            }
        });
        // 监听皮肤变化
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SkinConstants.SKIN_ACTION);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_DAOMENG);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_QINGSHE);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_XM);
        getActivity().registerReceiver(mSkinChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isDestroy())
                    return;
                handleBtnState();
            }
        }, intentFilter);
        // 监听支付状态
        PayHandler.getInstance().addPayCallback(mPayCallback = new PayHandler.PayCallback() {
            @Override
            public void onPaySuccess(boolean buyWithQRCode, final long skuId) {
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (isDestroy())
                            return;
                        if (mSkins.getId() == skuId) {
                            mSkins.setIsBuy(true);
                            handleBtnState();
                        }
                    }
                });
            }
        });

        preBindView();
        bindView(view);
        afterBindView();
        showGuideWindow();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSkinDownload.removeDownloadListener(mDownloadListener);
        getActivity().unregisterReceiver(mSkinChangeReceiver);
        PayHandler.getInstance().removePayCallback(mPayCallback);
    }

    private void preBindView() {
        mShowBigPopView = new ShowBigPopView(getActivity());
        Bundle args = getArguments();
        if (args != null) {
            Parcelable parcelable = args.getParcelable(ARG_BEAN);
            if (parcelable instanceof SkinVersionsBean) {
                mSkins = (SkinVersionsBean) parcelable;
            }
        }
    }

    private void bindView(@NonNull View view) {
        mDescriptionTV = view.findViewById(R.id.tvDescription);
        mDownloadTV = view.findViewById(R.id.tvDown);
        mUseCountTV = view.findViewById(R.id.tvUseCount);
        mXmScrollBar = view.findViewById(R.id.scroll_bar);
        thumbnailsRV = view.findViewById(R.id.rvThumbnails);

        mLeftPbBtn = view.findViewById(R.id.pbBtnLeft);
        mRightPbBtn = view.findViewById(R.id.pbBtnRight);

        mCoinLayout = view.findViewById(R.id.layout_coin);
        mRmbLayout = view.findViewById(R.id.layout_rmb);

        mLatestRMBTV = view.findViewById(R.id.tvRMBDiscount);
        mOriginalRMBTV = view.findViewById(R.id.tvRMBOriginal);
        mYellowCoinTV = view.findViewById(R.id.tvScoreDiscount);
        mCoinOriginal = view.findViewById(R.id.tvScoreOriginal);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        thumbnailsRV.setLayoutManager(linearLayoutManager);

        mAdapter = new PersonalityThemeDetailsAdapter();
        thumbnailsRV.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mShowBigPopView.setBigPics(mAdapter.getData(), position);
                mShowBigPopView.showPopView();
                dismissGuideWindow();
                shouldShowSecondGuide();
            }
        });

        thumbnailsRV.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(80, 0, 0, 0);
            }
        });
        mXmScrollBar.setRecyclerView(thumbnailsRV);
    }

    private void afterBindView() {
        String skinName = StringUtil.optString(mSkins.getAppName());
        String versionDesc = StringUtil.optString(mSkins.getVersionDesc());
        mDescriptionTV.setText(getString(R.string.describe_skins, skinName, versionDesc));
        mDownloadTV.setText(mContext.getString(R.string.str_download_size, mSkins.getSizeFomat()));
        mUseCountTV.setText(mContext.getString(R.string.str_buy_count, mSkins.getUsedNum() + mSkins.getDefaultShowNum() + ""));
        mYellowCoinTV.setText(UnitConverUtils.formatIntValue(mSkins.getLatestScorePrice()));
        mAdapter.setNewData(mSkins.getThumbnails());

        handleBtnState();

        handlePrice();
    }

    private void handleBtnState() {
        // 已购买或免费使用
        final boolean hasBuy = mSkins.isIsBuy()
                || (mSkins.getScorePrice() <= 0 && mSkins.getPrice() <= 0);
        if (SkinUsing.isUsing(mSkins)) {
            if (hasBuy) {
                // 正在使用
                mLeftPbBtn.setVisibility(View.VISIBLE);
                mLeftPbBtn.updateStateAndProgress(0, mContext.getString(R.string.state_using));
                mLeftPbBtn.setEnabled(false);

                mRightPbBtn.setVisibility(View.GONE);
            } else {
                // 试用中
                mLeftPbBtn.setVisibility(View.VISIBLE);
                mLeftPbBtn.setEnabled(true);
                mLeftPbBtn.updateStateAndProgress(0, mContext.getString(R.string.state_buy));
                mLeftPbBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        payForSkins();
                    }
                });

                mRightPbBtn.setVisibility(View.VISIBLE);
                mRightPbBtn.setEnabled(false);
                mRightPbBtn.updateStateAndProgress(0, mContext.getString(R.string.state_trying));
            }
        } else if (mSkinDownload.isDownloading(mSkins)) {
            mLeftPbBtn.setVisibility(View.VISIBLE);
            mLeftPbBtn.setEnabled(true);
            mLeftPbBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast(R.string.tips_wait_download_complete);
                }
            });
            DownloadStatus downloadStatus = mSkinDownload.getDownloadStatus(mSkins);
            if (downloadStatus != null) {
                mLeftPbBtn.updateStateAndProgress(downloadStatus.currentLength, downloadStatus.totalLength);
            }

            mRightPbBtn.setVisibility(View.GONE);
        } else {
            if (hasBuy) {
                // 已购买,未使用,不是下载中
                mLeftPbBtn.setVisibility(View.VISIBLE);
                mLeftPbBtn.setEnabled(true);
                boolean hasDownload = mSkinDownload.isDownloadSuccess(mSkins);
                Log.i(TAG, String.format("handleBtnState: hasDownload: %s", hasDownload));
                if (hasDownload) {
                    mLeftPbBtn.updateStateAndProgress(0, mContext.getString(R.string.state_use));
                    mLeftPbBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            manualUpdateTrack(EventConstant.NormalClick.ACTION_USE);
                            useThisSkin();
                        }
                    });
                } else {
                    mLeftPbBtn.updateStateAndProgress(0, mContext.getString(R.string.state_download));
                    mLeftPbBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            manualUpdateTrack(EventConstant.NormalClick.ACTION_DOWNLOAD);
                            downloadThisSkin();
                        }
                    });
                }

                mRightPbBtn.setVisibility(View.GONE);
            } else {
                // 未购买
                mLeftPbBtn.setVisibility(View.VISIBLE);
                mLeftPbBtn.setEnabled(true);
                mLeftPbBtn.updateStateAndProgress(0, mContext.getString(R.string.state_buy));
                mLeftPbBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        manualUpdateTrack(EventConstant.NormalClick.ACTION_BUY);
                        payForSkins();
                    }
                });

                if (mSkins.getCanTry() >= 0) {
                    // 可试用
                    mRightPbBtn.setEnabled(true);
                    if (mSkinDownload.isDownloadSuccess(mSkins)) {
                        mRightPbBtn.updateStateAndProgress(0, mContext.getString(R.string.state_trial));
                        mRightPbBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                manualUpdateTrack(EventConstant.NormalClick.ACTION_TRIAL);
                                SkinUsing.trialSkin(ThemeDetailsFragment.this, mSkins);
                            }
                        });
                    } else {
                        mRightPbBtn.updateStateAndProgress(0, mContext.getString(R.string.state_download));
                        mRightPbBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                manualUpdateTrack(EventConstant.NormalClick.ACTION_DOWNLOAD);
                                downloadThisSkin();
                            }
                        });
                    }
                } else {
                    // 不能试用
                    mRightPbBtn.setVisibility(View.GONE);
                }
            }
        }
    }

    private void handlePrice() {
        setSkinType(mSkins);
        switch (mSkins.getPay()) {
            case ShopContract.Pay
                    .DEFAULT:
                mCoinLayout.setVisibility(View.GONE);
                mOriginalRMBTV.setVisibility(View.GONE);
                mLatestRMBTV.setText(R.string.state_free);
                break;
            case ShopContract.Pay
                    .COIN:
                mRmbLayout.setVisibility(View.GONE);
                mCoinLayout.setVisibility(View.VISIBLE);

                setCoinPrice();
                break;
            case ShopContract.Pay
                    .RMB:
                mCoinLayout.setVisibility(View.GONE);
                mRmbLayout.setVisibility(View.VISIBLE);

                setPrice();
                break;
            case ShopContract.Pay
                    .COIN_AND_RMB:
                mCoinLayout.setVisibility(View.VISIBLE);
                mRmbLayout.setVisibility(View.VISIBLE);

                setCoinPrice();
                setPrice();
                break;
        }
    }

    private static void setSkinType(SkinVersionsBean bean) {
        int pay = ShopContract.Pay.DEFAULT;
        if (!PriceUtils.isFree(bean.getScorePrice(), bean.getDiscountScorePrice(), false)) {
            pay += ShopContract.Pay.COIN;
        }
        if (!PriceUtils.isFree(bean.getPrice(), bean.getDiscountPrice(), false)) {
            pay += ShopContract.Pay.RMB;
        }
        bean.setPay(pay);
    }

    private void setPrice() {
        if (mSkins.getDiscountPrice() > 0) {
            mOriginalRMBTV.setVisibility(View.VISIBLE);
            mLatestRMBTV.setText(getString(R.string.rmb_price, String.valueOf(mSkins.getDiscountPrice())));
            mOriginalRMBTV.setText(String.valueOf(mSkins.getPrice()));
        } else {
            mOriginalRMBTV.setVisibility(View.GONE);
            mLatestRMBTV.setText(getString(R.string.rmb_price, String.valueOf(mSkins.getPrice())));
        }
    }

    private void setCoinPrice() {
        if (mSkins.getDiscountScorePrice() > 0) {
            mCoinOriginal.setVisibility(View.VISIBLE);
            mYellowCoinTV.setText(String.valueOf(mSkins.getDiscountScorePrice()));
            mCoinOriginal.setText(String.valueOf(mSkins.getScorePrice()));
        } else {
            mYellowCoinTV.setText(String.valueOf(mSkins.getScorePrice()));
            mCoinOriginal.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    private void payForSkins() {
        if (mSkins.getPay() == ShopContract.Pay.DEFAULT) {
            manualUpdateTrack(EventConstant.NormalClick.ACTION_USE);
            useThisSkin();
        } else {
            ShopTrackManager.newSingleton().setBaseInfo(ResourceType.SKIN, mSkins.toTrackString(), this.getClass().getName());
            manualUpdateTrack(EventConstant.NormalClick.ACTION_BUY);
            switch (mSkins.getPay()) {
                case ShopContract.Pay
                        .COIN:
                    PayHandler.getInstance().carCoinPayWindow(
                            getActivity(),
                            ResourceType.SKIN,
                            mSkins.getId(),
                            mSkins.getAppName(),
                            mSkins.getScorePrice(),
                            false,
                            getPayResultCallback());
                    break;
                case ShopContract.Pay
                        .COIN_AND_RMB:
                    PayHandler.getInstance().scanCodePayWindow(
                            getActivity(),
                            ResourceType.SKIN,
                            mSkins.getId(),
                            mSkins.getAppName(),
                            String.valueOf(mSkins.getLatestPrice()),
                            mSkins.getLatestScorePrice(),
                            false,
                            getPayResultCallback());
                    break;
                case ShopContract.Pay
                        .RMB:
                    PayHandler.getInstance().scanCodePayWindow(
                            getActivity(),
                            ResourceType.SKIN,
                            mSkins.getId(),
                            mSkins.getAppName(),
                            String.valueOf(mSkins.getLatestPrice()),
                            0,
                            true,
                            getPayResultCallback());
                    break;
            }
        }
    }

    private PaySuccessResultCallback getPayResultCallback() {
        return new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                manualUpdateTrack(EventConstant.NormalClick.ACTION_BUY_CONFIRM);
                dealAfterSkinBuyed();
            }

            @Override
            public void cancel() {
                manualUpdateTrack(EventConstant.NormalClick.ACTION_BUY_CANCEL);
            }
        };
    }

    private void uploadBuyCount() {
        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.BUYTHEME.getType());
    }

    private void dealAfterSkinBuyed() {
        mSkins.setIsBuy(true);
        uploadBuyCount();
        mSkins.setUsedNum(mSkins.getUsedNum() + 1);
        mUseCountTV.setText(mContext.getString(R.string.str_buy_count, UnitConverUtils.moreThanToConvert(mSkins.getUsedNum() + mSkins.getDefaultShowNum())));
        PayHandler.getInstance().buySuccess(getActivity(), new PaySuccessResultCallback() {
            @Override
            public void confirm() {
                DownloadStatus downloadStatus = mSkinDownload.getDownloadStatus(mSkins);
                if (downloadStatus != null
                        && DownloadManager.STATUS_SUCCESSFUL == downloadStatus.status) {
                    useThisSkin();
                } else {
                    downloadThisSkin();
                }
                ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        mSkins.setIsBuy(true);
                        handleBtnState();
                    }
                });
            }

            @Override
            public void cancel() {
            }
        });
    }

    private void useThisSkin() {
        SkinUsing.useSkin(this, mSkins);
    }

    private void downloadThisSkin() {
        if (!NetworkUtils.isConnected(getContext())) {
            showToastException(R.string.no_network);
            return;
        }
        RequestManager.addSkuToBuyList(mSkins.getId(), ResourceType.SKIN, new ResultCallback<XMResult<Object>>() {
            @Override
            public void onSuccess(XMResult<Object> result) {
                if (isDestroy())
                    return;
                if (result != null && result.isSuccess()) {
                    mSkinDownload.start(mSkins);
                } else {
                    showToastException(R.string.hint_download_error);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (isDestroy())
                    return;
                showToastException(R.string.hint_download_error);
            }
        });
    }

    private void manualUpdateTrack(String eventAction) {
        XmAutoTracker.getInstance().onEvent(eventAction, mSkins.toTrackString(), this.getClass().getName(), EventConstant.PageDesc.FRAGMENT_SKIN_DETAILS);
    }

    private void showGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.SHOP_SHOWED, GuideConstants.SHOP_GUIDE_FIRST, false))
            return;
        thumbnailsRV.postDelayed(new Runnable() {
            @Override
            public void run() {
                final View view = thumbnailsRV.getLayoutManager().findViewByPosition(1);
                if (view == null) return;
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        newGuide = NewGuide.with(getActivity())
                                .setLebal(GuideConstants.SHOP_SHOWED)
                                .setGuideLayoutId(R.layout.guide_view_skin_details)
                                .setTargetViewAndRect(view)
                                .setNeedHande(true)
                                .setNeedShake(true)
                                .setHandLocation(NewGuide.RIGHT_AND_BOTTOM_BOTTOM)
                                .setViewHandId(R.id.iv_gesture)
                                .setViewWaveIdOne(R.id.iv_wave_one)
                                .setViewWaveIdTwo(R.id.iv_wave_two)
                                .setViewWaveIdThree(R.id.iv_wave_three)
                                .setViewSkipId(R.id.tv_guide_skip)
                                .build();
                        newGuide.showGuide();
                    }
                });
            }
        }, 200);
    }

    private void shouldShowSecondGuide() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.SHOP_SHOWED, GuideConstants.SHOP_GUIDE_FIRST, false))
            return;
        final RecyclerView bigPicRv = mShowBigPopView.getBigPicRv();
        mShowBigPopView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                dismissGuideWindow();
                showThirdGuideWindow();
            }
        });
        initHandlerAndRunnable(bigPicRv);
        bigPicRv.postDelayed(new Runnable() {
            @Override
            public void run() {
                justifyTargetViewEmpty(bigPicRv);
            }
        }, 500);
    }

    private void initHandlerAndRunnable(final RecyclerView bigPicRv) {
        runnable = new Runnable() {
            @Override
            public void run() {
                justifyTargetViewEmpty(bigPicRv);
            }
        };
        handler = new Handler();
    }

    private void justifyTargetViewEmpty(RecyclerView bigPicRv) {
        final View view = bigPicRv.getLayoutManager().findViewByPosition(1);
        if (view == null) {
            handler.postDelayed(runnable, 200);
            return;
        }
        showSecondGuide(view);
    }

    private void showSecondGuide(final View view) {
        view.post(new Runnable() {
            @Override
            public void run() {
                View targetView = view.findViewById(R.id.ivClose);
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.SHOP_SHOWED)
                        .setGuideLayoutId(R.layout.guide_view_skin_details_two)
                        .setTargetView(targetView)
                        .setHighLightRect(NewGuide.getViewRect(targetView))
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setHandLocation(NewGuide.RIGHT_AND_BOTTOM_BOTTOM)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .setTargetViewTranslationX(0.05f)
                        .setCallBack(new GuideCallBack() {
                            @Override
                            public void onHighLightClicked() {
                                if (mShowBigPopView != null && mShowBigPopView.isShowing())
                                    mShowBigPopView.dismiss();
                            }
                        })
                        .build();
                newGuide.showGuide();
            }
        });
    }

    private void showThirdGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.SHOP_SHOWED, GuideConstants.SHOP_GUIDE_FIRST, false))
            return;
        try {
//            View targetView = ((ViewGroup) ((ViewGroup) getNaviBar().getChildAt(0)).getChildAt(0)).getChildAt(0);
            View targetView = getNaviBar().getBackView();
            Rect viewRect = NewGuide.getViewRect(targetView);
            Rect targetRect = new Rect(viewRect.left, viewRect.top + (viewRect.height() / 2 - 92), viewRect.right, viewRect.top + (viewRect.height() / 2 + 92));
            newGuide = NewGuide.with(getActivity())
                    .setLebal(GuideConstants.SHOP_SHOWED)
                    .setGuideLayoutId(R.layout.guide_view_skin_details_three)
                    .setTargetView(targetView)
                    .setHighLightRect(targetRect)
                    .setNeedHande(true)
                    .setNeedShake(true)
                    .needMoveUpALittle(true)
                    .setViewHandId(R.id.iv_gesture)
                    .setViewWaveIdOne(R.id.iv_wave_one)
                    .setViewWaveIdTwo(R.id.iv_wave_two)
                    .setViewWaveIdThree(R.id.iv_wave_three)
                    .setViewSkipId(R.id.tv_guide_skip)
                    .setTargetViewTranslationX(0.03f)
                    .setCallBack(new GuideCallBack() {
                        @Override
                        public void onHighLightClicked() {
                            try {
                                if (getActivity() == null)
                                    return;
                                dismissGuideWindow();
                                MainActivity activity = (MainActivity) getActivity();
                                activity.onBackPressed();
                                activity.showLastGuide();
                            } catch (Exception ignored) {
                            }
                        }
                    })
                    .build();
            newGuide.showGuide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }
}

