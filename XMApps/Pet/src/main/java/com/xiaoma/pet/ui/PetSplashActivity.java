package com.xiaoma.pet.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.pet.R;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.common.manager.PetAssetManager;
import com.xiaoma.pet.ui.view.PetToast;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * Created by Gillben on 2018/12/24 0024
 * <p>
 * desc: 宠物欢迎页
 */
public class PetSplashActivity extends PetBaseActivity {


    private static final String TAG = PetSplashActivity.class.getSimpleName();
    private static final String FIRST_ENTER = "FIRST_ENTER";
    private ImageView mPetIcon;
    private Button mTripBt;
    private TextView mPromptText;
    private NewGuide newGuide;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        PetAssetManager.init(this);

        boolean isFirst = TPUtils.get(this, FIRST_ENTER, true);
        if (!isFirst) {
            startActivity(PetHomeActivity.class);
            modifyGuideLabel();
            finish();
            return;
        }
        shouldShowGuideWindow();
    }

    private void modifyGuideLabel() {
        boolean shouldShowGuide = GuideDataHelper.shouldShowGuide(this, GuideConstants.PET_SHOWED, GuideConstants.PET_GUIDE_FIRST, true);
        if (!shouldShowGuide) return;
        GuideDataHelper.setFirstGuideFalse(GuideConstants.PET_SHOWED);
    }

    @Override
    protected View bindView() {
        View view = View.inflate(this, R.layout.activity_pet_splash, null);
        initView(view);
        return view;
    }


    private void initView(View view) {
        mPetIcon = view.findViewById(R.id.iv_prompt_pet_icon);
        mPromptText = view.findViewById(R.id.tv_prompt_title);
        mTripBt = view.findViewById(R.id.prompt_bt);

        mTripBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissGuideWindow();
                startTrip();
            }
        });
    }


    private void startTrip() {
        if (!checkNetWork()) {
            networkWarn();
            return;
        }

        mPromptText.setText(R.string.ready_start_new_travel);
        mTripBt.setText(R.string.start_travel);
        RequestManager.initPet(getString(R.string.pet_app), "Hi,guys!", new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                handle();
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.w(TAG, "code: " + code + "msg: " + msg);
                if (code == 0) {
                    //已经有宠物，直接跳过初始化阶段
                    handle();
                } else {
                    PetToast.showException(PetSplashActivity.this, R.string.pet_init_failed);
                }
            }
        });
    }


    private void handle() {
        TPUtils.put(this, FIRST_ENTER, false);
        startActivity(PetHomeActivity.class);
        finish();
    }

    private void shouldShowGuideWindow() {
        boolean shouldShowGuide = GuideDataHelper.shouldShowGuide(this, GuideConstants.PET_SHOWED, GuideConstants.PET_GUIDE_FIRST, true);
        if (!shouldShowGuide) return;
        showGuideWindow();
        GuideDataHelper.setFirstGuideFalse(GuideConstants.PET_SHOWED);
    }

    private void showGuideWindow() {
        mTripBt.post(new Runnable() {
            @Override
            public void run() {
                newGuide = NewGuide.with(PetSplashActivity.this)
                        .setLebal(GuideConstants.PET_SHOWED)
                        .setTargetViewAndRect(mTripBt)
                        .setGuideLayoutId(R.layout.guide_view_start_trip)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setHandLocation(NewGuide.RIGHT_AND_TOP)
                        .build();
                newGuide.showGuide();
            }
        });
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

}