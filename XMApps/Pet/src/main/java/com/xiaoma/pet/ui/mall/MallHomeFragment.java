package com.xiaoma.pet.ui.mall;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.pet.R;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.common.callback.ILoadDataOfMallListener;
import com.xiaoma.pet.common.utils.PetConstant;
import com.xiaoma.pet.model.CoinAndSignInfo;
import com.xiaoma.pet.ui.mall.repository.RepositoryFragment;
import com.xiaoma.pet.ui.mall.store.StoreFragment;
import com.xiaoma.pet.ui.view.DrawStrokeTextView;
import com.xiaoma.utils.FragmentUtils;

import java.util.List;

/**
 * Created by Gillben on 2018/12/26 0026
 * <p>
 * desc: 商城首页
 */
public class MallHomeFragment extends BaseFragment {

    private TextView mCoinTotalText;
    private DrawStrokeTextView mRepositoryText;
    private DrawStrokeTextView mStoreText;

    private int defaultPageId;
    private StoreFragment mStoreFragment;
    private RepositoryFragment mRepositoryFragment;
    private int checkedResId = R.id.store_radio_bt;
    private int tempTotalCarCoin;
    private NewGuide newGuide;
    private ILoadDataOfMallListener loadDataFailedOfMallListener;


    public static MallHomeFragment newInstance() {
        return new MallHomeFragment();
    }


    public void newProductNotify() {
        mRepositoryText.showBadgeView(true);
    }

    public void reset() {
        mRepositoryText.showBadgeView(false);
    }


    public int getTempTotalCarCoin() {
        return tempTotalCarCoin;
    }


    public void refreshDataOfChildFragment(String fragmentTag) {
        if (PetConstant.STORE_FRAGMENT_TAG.equals(fragmentTag)) {
            mStoreFragment.refreshData();
        } else if (PetConstant.REPOSITORY_FRAGMENT_TAG.equals(fragmentTag)) {
            mRepositoryFragment.refreshData();
        }
    }


    public void forceRefreshData(String goodsType) {
        if (mStoreFragment != null && mStoreFragment.isAdded()) {
            mStoreFragment.forceRefreshData(goodsType);
        }

        if (mRepositoryFragment != null && mRepositoryFragment.isAdded()) {
            mRepositoryFragment.forceRefreshData(goodsType);
        }
        getUserCoin();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDataFailedOfMallListener = (ILoadDataOfMallListener) getActivity();
        if (getActivity() != null) {
            defaultPageId = ((PetMallActivity) getActivity()).getStoreAndRepository();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mall_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initFragment();
        getUserCoin();
    }

    private void initView(View contentView) {
        mRepositoryText = contentView.findViewById(R.id.repository_radio_bt);
        mStoreText = contentView.findViewById(R.id.store_radio_bt);
        mCoinTotalText = contentView.findViewById(R.id.tv_coin_total_text);

        mStoreText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeEvent();
            }
        });

        mRepositoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repositoryEvent();
                dismissGuideWindow();
            }
        });
    }


    private void initFragment() {
        mStoreFragment = StoreFragment.newFragmentInstance();
        mRepositoryFragment = RepositoryFragment.newFragmentInstance();

        if (defaultPageId == PetConstant.GO_TO_STORE) {
            updateLayoutContent(mStoreFragment, PetConstant.STORE_FRAGMENT_TAG, mStoreText);
        } else {
            updateLayoutContent(mRepositoryFragment, PetConstant.REPOSITORY_FRAGMENT_TAG, mRepositoryText);
        }
    }


    private void storeEvent() {
        if (isChecked(mStoreText.getId())) {
            return;
        }
        defaultPageId = PetConstant.GO_TO_STORE;
        updateLayoutContent(mStoreFragment, PetConstant.STORE_FRAGMENT_TAG, mStoreText);
    }

    private void repositoryEvent() {
        if (isChecked(mRepositoryText.getId())) {
            return;
        }
        reset();
        updateLayoutContent(mRepositoryFragment, PetConstant.REPOSITORY_FRAGMENT_TAG, mRepositoryText);
    }


    private void updateLayoutContent(Fragment fragment, String tag, View view) {
        setCheckedResId(view.getId());
        changeLeftMenuStatus(view.getId());

        List<Fragment> fragments = FragmentUtils.getFragments(getChildFragmentManager());
        Fragment fragmentByTag = FragmentUtils.findFragment(getChildFragmentManager(), tag);
        if (fragmentByTag != null) {
            fragments.remove(fragmentByTag);
            FragmentUtils.showHide(fragmentByTag, fragments);
        } else {
            for (Fragment fg : fragments) {
                FragmentUtils.hide(fg);
            }
            FragmentUtils.add(getChildFragmentManager(), fragment, R.id.mall_content, tag);
        }
    }


    private void changeLeftMenuStatus(int id) {
        if (id == R.id.store_radio_bt) {
            mStoreText.setAlpha(1f);
            mRepositoryText.setAlpha(0.5f);
        } else if (id == R.id.repository_radio_bt) {
            mRepositoryText.setAlpha(1f);
            mStoreText.setAlpha(0.5f);
        }
    }


    private void setCheckedResId(int checkedId) {
        this.checkedResId = checkedId;
    }


    private boolean isChecked(int checkedId) {
        return checkedResId == checkedId;
    }


    private void getUserCoin() {
        RequestManager.getUserCarCoin(new ResultCallback<XMResult<CoinAndSignInfo>>() {
            @Override
            public void onSuccess(XMResult<CoinAndSignInfo> result) {
                if (loadDataFailedOfMallListener != null) {
                    loadDataFailedOfMallListener.loadSuccessOfMall();
                }
                if (result != null && result.getData() != null) {
                    tempTotalCarCoin = result.getData().getCarCoin();
                    mCoinTotalText.setText(mContext.getString(R.string.total_car_coin, tempTotalCarCoin));
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (loadDataFailedOfMallListener != null) {
                    loadDataFailedOfMallListener.loadFailedOfMall(null);
                }
            }
        });
    }

    private boolean shouldShowGuideWindow() {
        return GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.PET_SHOWED, GuideConstants.PET_GUIDE_FIRST, false);
    }

    public void showGuideWindow() {
        if (!shouldShowGuideWindow()) return;
        mRepositoryText.post(new Runnable() {
            @Override
            public void run() {
                newGuide = NewGuide.with(getActivity())
                        .setLebal(GuideConstants.PET_SHOWED)
                        .setTargetViewAndRect(mRepositoryText)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setGuideLayoutId(R.layout.guide_repository)
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

    private void dismissGuideWindow() {
        if (newGuide == null) return;
        newGuide.dismissGuideWindow();
        newGuide = null;
    }

}
