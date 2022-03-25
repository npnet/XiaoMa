package com.xiaoma.setting.other.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.manager.XmCarSensorManager;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.views.MagicTextView;
import com.xiaoma.setting.other.model.ThemeSettingVM;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Administrator on 2018/10/30 0030.
 */

@PageDescComponent(EventConstants.PageDescribe.themeSettingFragmentPagePathDesc)
public class ThemeFragment extends BaseFragment implements View.OnClickListener, ICarEvent {

    private static String TAG = ThemeFragment.class.getSimpleName();
    private MagicTextView mThemeOne, mThemeTwo, mThemeThree;
    private MagicTextView[] array = new MagicTextView[3];
    private ImageView mCoverOne, mCoverTwo, mCoverThree;
    private ImageView[] covers = new ImageView[3];
    private int mCurIndex = 0;
    private ThemeSettingVM mThemeSettingVM;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_theme, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mThemeSettingVM = ViewModelProviders.of(this).get(ThemeSettingVM.class);
        bindView(view);
        initData();
    }

    private void initData() {

    }

    private void bindView(View view) {
        mThemeOne = view.findViewById(R.id.tv_theme_one);
        mThemeTwo = view.findViewById(R.id.tv_theme_two);
        mThemeThree = view.findViewById(R.id.tv_theme_three);
        mThemeOne.setOnClickListener(this);
        mThemeTwo.setOnClickListener(this);
        mThemeThree.setOnClickListener(this);
        mCoverOne = view.findViewById(R.id.bg_cover_one);
        mCoverTwo = view.findViewById(R.id.bg_cover_two);
        mCoverThree = view.findViewById(R.id.bg_cover_three);
        array[0] = mThemeOne;
        array[1] = mThemeTwo;
        array[2] = mThemeThree;
        covers[0] = mCoverOne;
        covers[1] = mCoverTwo;
        covers[2] = mCoverThree;
        initCurIndex();
        changeSelected();
        XmCarEventDispatcher.getInstance().registerEvent(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        XmCarEventDispatcher.getInstance().unregisterEvent(this);

    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.themeOne, EventConstants.NormalClick.themeTwo, EventConstants.NormalClick.themeThree})
    @ResId({R.id.tv_theme_one, R.id.tv_theme_two, R.id.tv_theme_three})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_theme_one:
                showSkinDialog(0);
                break;
            case R.id.tv_theme_two:
                showSkinDialog(1);
                break;
            case R.id.tv_theme_three:
                showSkinDialog(2);
                break;
        }
    }


    public void showSkinDialog(final int index) {
        final ConfirmDialog dialog = new ConfirmDialog(getActivity());
        String contentOne = getString(R.string.skin_using_condition_one);
        String contentTwo = getString(R.string.skin_using_condition_two);
        String content = contentOne + contentTwo;
        SpannableStringBuilder ssb = new SpannableStringBuilder(content);
        AbsoluteSizeSpan smallAss = new AbsoluteSizeSpan(22);
        AbsoluteSizeSpan largeAss = new AbsoluteSizeSpan(28);
        ssb.setSpan(largeAss, 0, contentOne.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        ssb.setSpan(smallAss, content.length() - contentTwo.length(), content.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        dialog.setContent(ssb)
                .setPositiveButton(getString(R.string.state_use), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 换肤达成条件: 处于P档,电子驻车拉起
                        Context context = v.getContext();
                        if (XmCarSensorManager.getInstance().isConditionMeet() || ConfigManager.ApkConfig.isDebug()) {
                            switchSkin(index);
                        } else {
                            XMToast.toastException(context, R.string.condition_unsatisfied_check_retry);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        try {
            TextView tvContent = dialog.getView().findViewById(R.id.confirm_dialog_content);
            if (tvContent != null) {
                tvContent.setGravity(Gravity.START);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.show();
    }

    private void switchSkin(int index) {
        mCurIndex = index;
        if (index == 0) {
            XmSkinManager.getInstance().restoreDefault(getContext());
        } else if (index == 1) {
            XmSkinManager.getInstance().loadSkinByName(getContext(), "skinId", SkinConstants.SKIN_NAME_QINGSHE);
        } else if (index == 2) {
            XmSkinManager.getInstance().loadSkinByName(getContext(), "skinId", SkinConstants.SKIN_NAME_DAOMENG);
        }
        changeSelected();
    }


    private void changeSelected() {
        refreshSkin();
        switch (mCurIndex) {
            case 0:
                mThemeSettingVM.setTheme(SkinConstants.THEME_ZHIHUI);
                break;
            case 1:
                mThemeSettingVM.setTheme(SkinConstants.THEME_QINGSHE);
                break;
            case 2:
                mThemeSettingVM.setTheme(SkinConstants.THEME_DAOMENG);
                break;
        }
    }

    private void initCurIndex() {
        String curSkinName = XmSkinManager.getInstance().getCurSkinName();
        if (TextUtils.isEmpty(curSkinName) || SkinConstants.SKIN_NAME_RENWEN.equalsIgnoreCase(curSkinName)) {
            mCurIndex = 0;
        } else if (SkinConstants.SKIN_NAME_QINGSHE.equalsIgnoreCase(curSkinName)) {
            mCurIndex = 1;
        } else if (SkinConstants.SKIN_NAME_DAOMENG.equalsIgnoreCase(curSkinName)) {
            mCurIndex = 2;
        }
    }

    @Override
    public void onCarEvent(CarEvent event) {
        if (event.id == SDKConstants.ID_THEME) {
            int index = (int) event.value;
            if (index != mCurIndex) {
                mCurIndex = index;
                refreshSkin();
            }
        }
    }

    private void refreshSkin() {
        for (int i = 0; i < array.length; i++) {
            if (i == mCurIndex) {
                array[i].setShine(true);
                array[i].setTextColor(getResources().getColor(R.color.setting_tab_select));
                covers[i].setVisibility(View.VISIBLE);
            } else {
                array[i].setShine(false);
                array[i].setTextColor(getResources().getColor(R.color.setting_tab_unselect));
                covers[i].setVisibility(View.GONE);
            }
        }
    }
}
