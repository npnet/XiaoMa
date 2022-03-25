package com.xiaoma.pet.ui.mall.store;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.XmResource;
import com.xiaoma.pet.R;
import com.xiaoma.pet.adapter.StoreGoodsAdapter;
import com.xiaoma.pet.common.annotation.GoodsType;
import com.xiaoma.pet.common.callback.ILoadDataOfMallListener;
import com.xiaoma.pet.common.callback.XmPetResourceHandleCallback;
import com.xiaoma.pet.common.utils.PetConstant;
import com.xiaoma.pet.model.StoreGoodsInfo;
import com.xiaoma.pet.ui.mall.MallHomeFragment;
import com.xiaoma.pet.ui.mall.PetMallActivity;
import com.xiaoma.pet.ui.mall.PetSuppliesFragment;
import com.xiaoma.pet.ui.mall.pay.PayFragment;
import com.xiaoma.pet.ui.view.PetToast;
import com.xiaoma.pet.vm.GoodsVM;

import java.util.List;

/**
 * Created by Gillben on 2018/12/25 0025
 * <p>
 * desc:  商店食物页
 */
public class StoreGoodsFragment extends PetSuppliesFragment<StoreGoodsAdapter> {

    private static final String STORE_GOODS_TYPE = "STORE_GOODS_TYPE";
    @GoodsType
    private String goodsType;
    private Handler handler;
    private Runnable runnable;
    private NewGuide newGuide;

    private ILoadDataOfMallListener loadDataFailedOfMallListener;

    public static StoreGoodsFragment newInstance(@GoodsType String goodsType) {
        Bundle bundle = new Bundle();
        bundle.putString(STORE_GOODS_TYPE, goodsType);
        StoreGoodsFragment storeGoodsFragment = new StoreGoodsFragment();
        storeGoodsFragment.setArguments(bundle);
        return storeGoodsFragment;
    }

    @Override
    protected StoreGoodsAdapter createRVAdapter() {
        return new StoreGoodsAdapter();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDataFailedOfMallListener = (ILoadDataOfMallListener) getActivity();
        if (getArguments() != null) {
            goodsType = getArguments().getString(STORE_GOODS_TYPE);
        }
    }

    @Override
    protected void initData() {
        GoodsVM mGoodsVM = ViewModelProviders.of(this).get(GoodsVM.class);
        PetToast.showLoading(mContext, R.string.pet_loading_text);
        mGoodsVM.getStoreFoodList(goodsType).observe(this, new Observer<XmResource<List<StoreGoodsInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<StoreGoodsInfo>> listXmResource) {
                PetToast.dismissLoading();
                if (listXmResource == null) {
                    return;
                }

                listXmResource.handle(new XmPetResourceHandleCallback<List<StoreGoodsInfo>>() {
                    @Override
                    public void onSuccess(List<StoreGoodsInfo> data) {
                        if (loadDataFailedOfMallListener != null) {
                            loadDataFailedOfMallListener.loadSuccessOfMall();
                        }
                        if (data == null || data.size() == 0) {
                            showEmptyView();
                            return;
                        }
                        hideEmptyView();
                        getAdapter().setNewData(data);
                    }

                    @Override
                    public void onFailure(String message) {
                        if (loadDataFailedOfMallListener != null) {
                            loadDataFailedOfMallListener.loadFailedOfMall(PetConstant.STORE_FRAGMENT_TAG);
                        }
                    }
                });
            }
        });

        shouldShowGuideWindow();
    }

    @Override
    protected void onRVItemClick(BaseQuickAdapter adapter, View view, int position) {
        StoreGoodsInfo storeGoodsInfo = getAdapter().getData().get(position);
        if (getParentFragment() != null && getParentFragment().getParentFragment() != null) {
            int carCoin = ((MallHomeFragment) (getParentFragment().getParentFragment())).getTempTotalCarCoin();
            updateLayout(PayFragment.newInstance(storeGoodsInfo, carCoin), PetConstant.PAY_FRAGMENT_TAG, true);
        }
    }

    private boolean isFoodPage() {
        return GoodsType.FOOD.equals(goodsType);
    }

    private void shouldShowGuideWindow() {
        if (!isFoodPage()) {
            return;
        }
        if (((PetMallActivity) getActivity()).isStoreFoodGuideShowed()) {
            return;
        }
        boolean shouldShowGuide = GuideDataHelper.shouldShowGuide(getActivity(), GuideConstants.PET_SHOWED, GuideConstants.PET_GUIDE_FIRST, false);
        if (!shouldShowGuide) return;
        ((PetMallActivity) getActivity()).setStoreFoodGuideShowed(true);
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                justifyTargetViewEmpty();
            }
        }, 500);
    }

    private void justifyTargetViewEmpty() {
        View view = mRecyclerView.getLayoutManager().findViewByPosition(0);
        if (view == null) {
            initHandlerAndRunnable();
            handler.postDelayed(runnable, 100);
            return;
        }
        showGuideWindow(view);
    }

    private void showGuideWindow(View view) {
        final View targetView = view.findViewById(R.id.ll_top);
        targetView.post(new Runnable() {
            @Override
            public void run() {
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.PET_SHOWED)
                        .setTargetViewAndRect(targetView)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setGuideLayoutId(R.layout.guide_store_food)
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

    private void initHandlerAndRunnable() {
        if (handler == null)
            handler = new Handler();
        if (runnable == null)
            runnable = new Runnable() {
                @Override
                public void run() {
                    justifyTargetViewEmpty();
                }
            };
    }

    @Override
    protected void dismissGuideWindow() {
        if (newGuide == null) return;
        newGuide.dismissGuideWindow();
        newGuide = null;
    }


}
