package com.xiaoma.pet.ui.mall.repository;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.guide.listener.GuideCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.XmResource;
import com.xiaoma.pet.R;
import com.xiaoma.pet.adapter.RepositoryGoodsAdapter;
import com.xiaoma.pet.common.annotation.GoodsType;
import com.xiaoma.pet.common.callback.ILoadDataOfMallListener;
import com.xiaoma.pet.common.callback.XmPetResourceHandleCallback;
import com.xiaoma.pet.common.utils.PetConstant;
import com.xiaoma.pet.model.RepositoryInfo;
import com.xiaoma.pet.ui.mall.PetMallActivity;
import com.xiaoma.pet.ui.mall.PetSuppliesFragment;
import com.xiaoma.pet.ui.view.GoodsDetailView;
import com.xiaoma.pet.ui.view.PetToast;
import com.xiaoma.pet.vm.GoodsVM;

import java.util.List;

/**
 * Created by Gillben on 2018/12/25 0025
 * <p>
 * desc: 仓库食物
 */
public class RepositoryGoodsFragment extends PetSuppliesFragment<RepositoryGoodsAdapter> {

    private static final String REPOSITORY_GOODS_TYPE = "REPOSITORY_GOODS_TYPE";
    @GoodsType
    private String goodsType;
    private NewGuide newGuide;

    private ILoadDataOfMallListener loadDataFailedOfMallListener;

    public static RepositoryGoodsFragment newInstance(@GoodsType String goodsType) {
        Bundle bundle = new Bundle();
        bundle.putString(REPOSITORY_GOODS_TYPE, goodsType);
        RepositoryGoodsFragment repositoryGoodsFragment = new RepositoryGoodsFragment();
        repositoryGoodsFragment.setArguments(bundle);
        return repositoryGoodsFragment;
    }

    @Override
    protected RepositoryGoodsAdapter createRVAdapter() {
        return new RepositoryGoodsAdapter();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getParentFragment() != null && getParentFragment().getParentFragment() != null) {
            loadDataFailedOfMallListener = (ILoadDataOfMallListener) getParentFragment().getParentFragment().getActivity();
        }
        if (getArguments() != null) {
            goodsType = getArguments().getString(REPOSITORY_GOODS_TYPE);
        }
    }


    @Override
    protected void initData() {
        GoodsVM mGoodsVM = ViewModelProviders.of(this).get(GoodsVM.class);
        PetToast.showLoading(mContext, R.string.pet_loading_text);
        mGoodsVM.getRepositoryFoodList(goodsType).observe(this, new Observer<XmResource<List<RepositoryInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<RepositoryInfo>> listXmResource) {
                PetToast.dismissLoading();
                if (listXmResource == null) {
                    return;
                }

                listXmResource.handle(new XmPetResourceHandleCallback<List<RepositoryInfo>>() {
                    @Override
                    public void onSuccess(List<RepositoryInfo> data) {
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
                            loadDataFailedOfMallListener.loadFailedOfMall(PetConstant.REPOSITORY_FRAGMENT_TAG);
                        }
                    }
                });

            }
        });

        shouldShowGuideWindow();
    }


    @Override
    protected void onRVItemClick(BaseQuickAdapter adapter, View view, int position) {
        RepositoryInfo repositoryInfo = getAdapter().getData().get(position);
        if (repositoryInfo != null) {
            handleGoodsType(position, repositoryInfo);
        }

    }

    private void handleGoodsType(final int position, final RepositoryInfo repositoryInfo) {
        GoodsDetailView goodsDetailView = new GoodsDetailView(mContext, repositoryInfo, (ILoadDataOfMallListener) getActivity());
        goodsDetailView.showAtLocation(getView(), Gravity.TOP | Gravity.START, 0, 0);
        goodsDetailView.setOnDetailsResultCallback(new GoodsDetailView.OnDetailsResultCallback() {
            @Override
            public void hasUsed() {
                int goodsNumber = repositoryInfo.getGoodsNumber();
                goodsNumber = goodsNumber - 1;
                if (goodsNumber <= 0) {
                    List<RepositoryInfo> repositoryInfoList = getAdapter().getData();
                    repositoryInfoList.remove(position);
                    if (repositoryInfoList.size() <= 0) {
                        showEmptyView();
                        return;
                    }
                    getAdapter().setNewData(repositoryInfoList);
                } else {
                    repositoryInfo.setGoodsNumber(goodsNumber);
                    getAdapter().notifyItemChanged(position, repositoryInfo);
                }
            }
        });
    }

    private boolean isFoodPage() {
        return GoodsType.FOOD.equals(goodsType);
    }

    private void shouldShowGuideWindow() {
        if (!shouldShowGuide()) return;
        showGuideWindow();
    }

    private void showGuideWindow() {
        final PetMallActivity petMallActivity = (PetMallActivity) getActivity();
        final View targetView = petMallActivity.getNavigationBack();
        if (targetView == null) return;
        targetView.post(new Runnable() {
            @Override
            public void run() {
                Rect viewRect = NewGuide.getViewRect(targetView);
                Rect targetRect = new Rect(viewRect.left, viewRect.top + (viewRect.height() / 2 - 92), viewRect.right, viewRect.top + (viewRect.height() / 2 + 92));
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.PET_SHOWED)
                        .setTargetView(targetView)
                        .setHighLightRect(targetRect)
                        .setGuideLayoutId(R.layout.guide_repository_food)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .setCallBack(new GuideCallBack() {
                            @Override
                            public void onHighLightClicked() {
                                dismissGuideWindow();
                                petMallActivity.onBackPressed();

                            }
                        })
                        .build();
                newGuide.showGuide();
            }
        });
    }

    private boolean shouldShowGuide() {
        if (!isFoodPage()) {
            return false;
        }
        return GuideDataHelper.shouldShowGuide(getActivity(), GuideConstants.PET_SHOWED, GuideConstants.PET_GUIDE_FIRST, false);
    }

    @Override
    protected void dismissGuideWindow() {
        if (newGuide == null) return;
        newGuide.dismissGuideWindow();
        newGuide = null;
    }

}
