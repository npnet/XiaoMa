package com.xiaoma.shop.business.ui.theme;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iflytek.speech.util.NetworkUtil;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.EventMsg;
import com.xiaoma.shop.business.model.PayInfo;
import com.xiaoma.shop.business.ui.ShopBaseFragment;
import com.xiaoma.shop.common.callback.OnPayFromPersonalCallback;
import com.xiaoma.shop.common.constant.PaySourceType;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/24
 *     desc   :
 * </pre>
 */
@PageDescComponent(EventConstant.PageDesc.FRAGMENT_PERSONAL_THEME)
public class MainThemeFragment extends ShopBaseFragment implements View.OnClickListener, OnPayFromPersonalCallback {

    private TextView mTvTheme;
    private TextView mTvSound;
    private Fragment[] mFragments = {PersonalityThemeFragment.newInstance(), VoiceToneFragment.newInstance()};

    private String mCurrentTag;
    private boolean mFirstLoad = true;//是否是第一次进入

    public static MainThemeFragment newInstance() {
        return new MainThemeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_theme, container, false);
        initView(view);
        return super.onCreateWrapView(view);
    }

    private void initView(View view) {
        mTvTheme = view.findViewById(R.id.tv_theme);
        mTvTheme.setOnClickListener(this);
        mTvSound = view.findViewById(R.id.tv_sound);
        mTvSound.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkNetView();
        EventBus.getDefault().register(this);
        EventBus.getDefault().registerSticky(this);
    }

    @Subscriber
    public void getAction(EventMsg eventMsg) {
        if (eventMsg == null || TextUtils.isEmpty(eventMsg.getAction())) return;
        switch (eventMsg.getAction()) {
            case EventMsg.ACTION.ACTION_SKIN:
                switchSelect(true);
                break;
            case EventMsg.ACTION.ACTION_VOICE:
                switchSelect(false);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        for (final Fragment fragment : mFragments) {
            fragment.onHiddenChanged(hidden);
        }
    }

    private boolean checkNetView() {
        if (NetworkUtils.isConnected(getActivity())) {
            if (mFirstLoad) {
                FragmentUtils.add(getChildFragmentManager(), mFragments, R.id.fl_contain, 0);
                switchSelect(true);
                mFirstLoad = false;
            } else {
                EventBus.getDefault().post(mCurrentTag, mCurrentTag);
            }
            //            showContentView();

            return true;
        } else {
            showNoNetView();
            showGuideFinishWindow();
            return false;
        }
    }

    private void switchSelect(boolean selectSystemThemeT) {
        mTvTheme.setSelected(selectSystemThemeT);
        mTvSound.setSelected(!selectSystemThemeT);
        mTvTheme.setTextAppearance(selectSystemThemeT ? R.style.text_view_light_blue : R.style.text_view_normal);
        mTvSound.setTextAppearance(!selectSystemThemeT ? R.style.text_view_light_blue : R.style.text_view_normal);

        FragmentUtils.showHide(selectSystemThemeT ? 0 : 1, mFragments);
    }

    @Override
    protected void noNetworkOnRetry() {
        checkNetView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_theme:
                switchSelect(true);
                break;
            case R.id.tv_sound:
                switchSelect(false);
                break;
        }
    }

    private void showGuideFinishWindow() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.SHOP_SHOWED, GuideConstants.SHOP_GUIDE_FIRST, true))
            return;
        NewGuide.with(getActivity())
                .setLebal(GuideConstants.SHOP_SHOWED)
                .build()
                .showGuideFinishWindowDueToNetError();
    }

    public void parentShowNoNetView(String tag) {
        mCurrentTag = tag;
        showNoNetView();
    }

    public void parentShowContentView() {
        showContentView();
    }

    @Override
    public void handlePay(PayInfo payInfo, boolean immediateExecute) {
        //判断哪个界面的
        if (PaySourceType.SKIN.equals(payInfo.getProductType())) {
            PersonalityThemeFragment themeFragment = (PersonalityThemeFragment) mFragments[0];
            if (themeFragment instanceof OnPayFromPersonalCallback) {
                EventBus.getDefault().postSticky(new EventMsg(EventMsg.ACTION.ACTION_SKIN));
                EventBus.getDefault().post(new EventMsg(EventMsg.ACTION.ACTION_SKIN));
                ((OnPayFromPersonalCallback) themeFragment).handlePay(payInfo, false);
            }
        } else {
            VoiceToneFragment voiceToneFragment = (VoiceToneFragment) mFragments[1];
            if (voiceToneFragment instanceof OnPayFromPersonalCallback) {
                EventBus.getDefault().postSticky(new EventMsg(EventMsg.ACTION.ACTION_VOICE));
                EventBus.getDefault().post(new EventMsg(EventMsg.ACTION.ACTION_VOICE));
                ((OnPayFromPersonalCallback) voiceToneFragment).handlePay(payInfo, false);
            }
        }
    }
}
