package com.xiaoma.pet.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.unity3d.player.UnityPlayer;
import com.xiaoma.guide.listener.GuideFinishCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.pet.R;
import com.xiaoma.pet.common.RequestManager;
import com.xiaoma.pet.common.annotation.GoodsType;
import com.xiaoma.pet.common.annotation.UnityAction;
import com.xiaoma.pet.common.callback.OnUnityResultCallback;
import com.xiaoma.pet.common.callback.XmPetResourceHandleCallback;
import com.xiaoma.pet.common.manager.DownloadPetResource;
import com.xiaoma.pet.common.utils.AUBridgeDispatcher;
import com.xiaoma.pet.common.utils.ConvertMapTimeCoordinate;
import com.xiaoma.pet.common.utils.PetConstant;
import com.xiaoma.pet.common.utils.SavePetInfoUtils;
import com.xiaoma.pet.common.utils.UpgradeEnergyHandler;
import com.xiaoma.pet.model.OpenTreasureInfo;
import com.xiaoma.pet.model.PetGiftInfo;
import com.xiaoma.pet.model.PetInfo;
import com.xiaoma.pet.model.PetMapInfo;
import com.xiaoma.pet.model.RewardDetails;
import com.xiaoma.pet.model.UpgradeRewardInfo;
import com.xiaoma.pet.ui.mall.PetMallActivity;
import com.xiaoma.pet.ui.map.MapInfoHandler;
import com.xiaoma.pet.ui.map.PassChapterObserver;
import com.xiaoma.pet.ui.map.PetTaskMap;
import com.xiaoma.pet.ui.view.AccelerateView;
import com.xiaoma.pet.ui.view.CarSpeedView;
import com.xiaoma.pet.ui.view.DrawStrokeTextView;
import com.xiaoma.pet.ui.view.InterceptView;
import com.xiaoma.pet.ui.view.PetProgressView;
import com.xiaoma.pet.ui.view.PetToast;
import com.xiaoma.pet.ui.view.PetUpgradeView;
import com.xiaoma.pet.vm.PetVM;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;


/**
 * Created by Gillben on 2018/12/24 0024
 * <p>
 * desc: 宠物首页
 */
public class PetHomeActivity extends PetBaseActivity
        implements View.OnClickListener, OnUnityResultCallback {

    private static final String TAG = PetHomeActivity.class.getSimpleName();
    //弹出框
    private LinearLayout eatFoodLayout;
    private RelativeLayout treasureRelative;
    private Button mGoToEatFood;
    private TextView mPetLevelText;
    private FrameLayout mPetHomeLayout;
    //宝箱
    private ImageView mSmallIcon;
    private TextView mSmallTitle;
    private TextView mSmallDesc;
    private DrawStrokeTextView mTreasureBoxNumber;
    //主operation
    private PetProgressView petProgressView;
    private ImageView mMapText;
    private ImageView mTreasureText;
    private InterceptView mInterceptView;

    private Animation mShowAction;
    private Animation mHiddenAction;
    private static Handler mHandler = new Handler();
    private int saveTreasureNumber;
    private volatile int TREASURE_BOX_MAX_NUMBER;

    private UnityPlayer mUnityPlayer;
    private NewGuide newGuide;
    private boolean hasShowedGuide;
    // 宠物升级弹框是否正在显示 是 则不显示弹框
    private boolean isPetUpgradeViewShow;
    private PetVM petVM;

    private long recordCurrentTime;


    @Override
    protected View bindView() {
        hideNavigationBar();
        View view = View.inflate(this, R.layout.activity_pet_home, null);
        initView(view);
        addListener();
        AUBridgeDispatcher.getInstance().registerResultCallback(PetHomeActivity.class.getSimpleName(), this);
        guideJustifyNetWork();
        petVM = ViewModelProviders.of(this).get(PetVM.class);
        return view;
    }

    private void initView(View view) {
        mPetHomeLayout = view.findViewById(R.id.pet_home_layout);
        eatFoodLayout = view.findViewById(R.id.pet_eat_food_linear);
        mPetLevelText = view.findViewById(R.id.tv_pet_upgrade);
        treasureRelative = view.findViewById(R.id.treasure_relative_layout);
        mGoToEatFood = view.findViewById(R.id.go_to_eat_foot_bt);
        mSmallIcon = view.findViewById(R.id.iv_small_icon);
        mSmallTitle = view.findViewById(R.id.tv_small_title);
        mSmallDesc = view.findViewById(R.id.tv_small_desc);
        mTreasureBoxNumber = view.findViewById(R.id.treasure_box_number);
        petProgressView = view.findViewById(R.id.pet_progress_view);
        mMapText = view.findViewById(R.id.tv_pet_map_text);
        mTreasureText = view.findViewById(R.id.tv_treasure_chest);
        mInterceptView = view.findViewById(R.id.intercept_view);

        mUnityPlayer = new UnityPlayer(this);
        mPetHomeLayout.addView(mUnityPlayer.getView());
        mUnityPlayer.requestFocus();

        controllerAccelerate(view);
        initAnimation();
    }


    private void initAnimation() {
        mShowAction = new ScaleAnimation(
                0.0f, 1f,
                1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(300);

        mHiddenAction = new ScaleAnimation(
                1f, 0.0f,
                1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(300);
    }


    private void controllerAccelerate(View view) {
        final CarSpeedView mCarSpeedView = view.findViewById(R.id.car_speed_view);
        AccelerateView mAccelerateView = view.findViewById(R.id.accelerate_view);

        mAccelerateView.setOnAccelerateLocationCallback(new AccelerateView.OnAccelerateLocationCallback() {
            @Override
            public void updateProgress(int location) {
                mCarSpeedView.update(true, location);
            }

            @Override
            public void currentLocation(float speed) {
                int temp = (int) (0 + (speed / 180) * 50);
                if (temp > 0) {
                    ConvertMapTimeCoordinate.startRunning();
                    ConvertMapTimeCoordinate.genGiftTimer(new ConvertMapTimeCoordinate.OnGenGiftCallback() {
                        @Override
                        public void genGift() {
                            AUBridgeDispatcher.getInstance().callUnity(System.currentTimeMillis(), UnityAction.GEN_GIFT);
                        }
                    });
                } else {
                    ConvertMapTimeCoordinate.postRunningTimePercentToServer();
                    ConvertMapTimeCoordinate.cancelGenGift();
                }
                AUBridgeDispatcher.getInstance().callUnity(System.currentTimeMillis(), UnityAction.SPEED, temp);
            }
        });
    }


    private void addListener() {
        petProgressView.setOnClickListener(this);
        mMapText.setOnClickListener(this);
        mTreasureText.setOnClickListener(this);
        mGoToEatFood.setOnClickListener(this);
    }


    @Override
    protected void initData() {
        if (!shouldShowGuide()) {
            PetToast.showGravityTop(this, R.string.start_trip_prompt);
        }

        NewGuide.setGuideFinishCallBack(new GuideFinishCallBack() {
            @Override
            public void onGuideFinish() {
                ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PetToast.showGravityTop(PetHomeActivity.this, R.string.start_trip_prompt);
                    }
                }, 3500);
            }
        });
        fetchPetInfo();
    }


    @Override
    protected void refresh() {
        fetchPetInfo();
    }

    @Override
    public void onClick(View v) {
        int resId = v.getId();
        //宠物进度
        if (resId == R.id.pet_progress_view) {
            gotoStoreOrRepository(PetConstant.GO_TO_STORE);
            dismissGuideWindow();
        }
        //地图
        else if (resId == R.id.tv_pet_map_text) {
            openMap();
        }
        //宝箱
        else if (resId == R.id.tv_treasure_chest) {
            if (treasureRelative.getVisibility() == View.VISIBLE) {
                closeSmallView(treasureRelative);
            } else {
                openTreasureBox();
            }
        }
        //跳转喂食
        else if (resId == R.id.go_to_eat_foot_bt) {
            gotoStoreOrRepository(PetConstant.GO_TO_REPOSITORY);
            closeSmallView(eatFoodLayout);
        }
    }


    @Override
    public void receiveResultForUnity(String action, String result) {
        switch (action) {
            case UnityAction.HIT_GIFT:
                genTreasureBoxNumber();
                break;

            case UnityAction.CURRENT_ANIMATION:
                //TODO 拿到宠物当前状态
                KLog.d(TAG, "result: " + result);
                break;

            case UnityAction.COMPLETE:
                PetToast.dismissLoading();
                if (!TextUtils.isEmpty(result)) {
                    if (result.equals("[1]")) {
//                        PetToast.showToast(this, R.string.use_resource_success);
                        KLog.w(TAG, "使用成功");
                    } else {
                        KLog.w(TAG, "使用失败");
//                        PetToast.showException(this, R.string.use_resource_failed);
                    }
                }
                break;
        }
    }


    private void gotoStoreOrRepository(int pageId) {
        Intent intent = new Intent(this, PetMallActivity.class);
        intent.putExtra(PetConstant.STORE_AND_REPOSITORY_FLAG, pageId);
        if (shouldShowGuide()) {
            startActivityForResult(intent, PetConstant.SHOW_LAST_GUIDE);
        } else {
            startActivityForResult(intent, PetConstant.HOME_MALL_RESULT_CODE);
        }
    }

    private void openSmallView(ViewGroup viewGroup) {
        viewGroup.setVisibility(View.VISIBLE);
        viewGroup.startAnimation(mShowAction);
    }


    private void closeSmallView(ViewGroup viewGroup) {
        viewGroup.setVisibility(View.GONE);
        viewGroup.startAnimation(mHiddenAction);
        mHandler.removeCallbacksAndMessages(null);
    }


    private void treasureBoxTimingClose() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeSmallView(treasureRelative);
            }
        }, 2000);
    }


    private void openMap() {
        MapInfoHandler.getInstance().request(this, new PassChapterObserver() {
            @Override
            public void passChapter(boolean complete, PetInfo petInfo, PetMapInfo mapInfo) {
                PetTaskMap petTaskMap = new PetTaskMap(PetHomeActivity.this, complete, petInfo, mapInfo);
                petTaskMap.showAtLocation(getRootLayout(), Gravity.TOP | Gravity.START, 0, 0);
                petTaskMap.setPassChapterListener(new PetTaskMap.IPassChapterListener() {
                    @Override
                    public void startNewTrip() {
                        showPassChapterLoadingProgressLayout();
                    }
                });
            }
        });
    }


    private void fetchPetInfo() {
        PetToast.showLoading(this, R.string.pet_loading_text);
        petVM.getPetInfo().observe(this, new Observer<XmResource<PetInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<PetInfo> petInfoXmResource) {
                PetToast.dismissLoading();
                if (petInfoXmResource == null) {
                    KLog.w(TAG, "petInfoXmResource is null.");
                    return;
                }

                petInfoXmResource.handle(new XmPetResourceHandleCallback<PetInfo>() {
                    @Override
                    public void onSuccess(PetInfo petInfo) {
                        loadSuccess();
                        boolean isUpgrade = UpgradeEnergyHandler.getInstance().checkUpgrade(petInfo.getGrade(), petInfo.getExperienceValue());
                        if (isUpgrade) {
                            handleUpgrade(petInfo);
                        } else {
                            refreshPetInfo(petInfo);
                        }
                        showGuideWindow();
                    }

                    @Override
                    public void onFailure(String message) {
                        loadFailed();
                        showGuideWindow();
                        if (String.valueOf(PetConstant.PET_NOT_EXISTS_ERROR_CODE).equals(message)) {
                            KLog.d(TAG, "init new pet.");
                            petVM.initPet(PetHomeActivity.this);
                        }
                    }
                });
            }
        });
    }


    private void handleUpgrade(PetInfo petInfo) {
        petVM.checkUpgrade(petInfo, new PetVM.OnHandleUpgradeAction() {
            @Override
            public void upgradeAction(int newLevel, UpgradeRewardInfo upgradeRewardInfo) {
                PetToast.dismissLoading();
                upgradeReward(newLevel, upgradeRewardInfo);
                fetchPetInfo();
            }

            @Override
            public void upgradeFailed() {
                KLog.w(TAG, "upgrade failed.");
            }
        });
    }


    private void refreshPetInfo(PetInfo petInfo) {
        initChapter(petInfo);
        mPetLevelText.setText(PetHomeActivity.this.getString(R.string.pet_level_text, petInfo.getGrade()));
        float percent = UpgradeEnergyHandler.getInstance().calculationPercent(petInfo.getGrade(), petInfo.getExperienceValue());
        petProgressView.handlePetNormal(percent);
        refreshTreasureBoxText(petInfo.getBoxRecordCount());

        if (!petInfo.isEating()) {
            openSmallView(eatFoodLayout);
        } else {
            closeSmallView(eatFoodLayout);
        }

        SavePetInfoUtils.save(petInfo);
    }


    private void initChapter(final PetInfo petInfo) {
        if (petInfo != null && petInfo.getChapterId() == 0) {
            RequestManager.getGameChapterInfo("V1.0", 1, new ResultCallback<XMResult<PetMapInfo>>() {
                @Override
                public void onSuccess(XMResult<PetMapInfo> result) {
                    final PetMapInfo petMapInfo = result.getData();
                    if (petMapInfo == null) {
                        KLog.w(TAG, "petMapInfo is null.");
                        return;
                    }
                    RequestManager.postMapCompleteTime(0, 1, new ResultCallback<XMResult<String>>() {
                        @Override
                        public void onSuccess(XMResult<String> result) {
                            petInfo.setChapterId(1);
                            SavePetInfoUtils.save(petInfo);
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            KLog.w(TAG, "关卡与时间累积绑定失败");
                        }
                    });
                }

                @Override
                public void onFailure(int code, String msg) {

                }
            });
        }
    }


    private void upgradeReward(int petNewLevel, UpgradeRewardInfo upgradeRewardInfo) {
        PetUpgradeView petUpgradeView = new PetUpgradeView(this, petNewLevel, upgradeRewardInfo);
        petUpgradeView.showAtLocation(getRootLayout(), Gravity.START | Gravity.TOP, 0, 0);
        isPetUpgradeViewShow = true;
        petUpgradeView.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isPetUpgradeViewShow = false;
                showGuideWindow();
            }
        });
    }


    private void genTreasureBoxNumber() {
        if (TREASURE_BOX_MAX_NUMBER >= 99) {
            return;
        }

        RequestManager.genRandomGift(new ResultCallback<XMResult<String>>() {
            @Override
            public void onSuccess(XMResult<String> result) {
                RequestManager.getBoxGiftList(new ResultCallback<XMResult<List<PetGiftInfo>>>() {
                    @Override
                    public void onSuccess(XMResult<List<PetGiftInfo>> result) {
                        List<PetGiftInfo> giftInfoList = result.getData();
                        int treasureNumber = giftInfoList != null && giftInfoList.size() > 0 ? giftInfoList.size() : 0;
                        refreshTreasureBoxText(treasureNumber);
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        KLog.w(TAG, "msg: " + msg);
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.w(TAG, "msg: " + msg);
            }
        });
    }


    private void refreshTreasureBoxText(int boxNumber) {
        TREASURE_BOX_MAX_NUMBER = boxNumber;
        if (boxNumber <= 0) {
            mTreasureBoxNumber.setVisibility(View.GONE);
        } else {
            saveTreasureNumber = boxNumber;
            mTreasureBoxNumber.setVisibility(View.VISIBLE);
            mTreasureBoxNumber.setText(PetHomeActivity.this.getString(R.string.treasure_number_text, boxNumber));
        }
    }


    private void openTreasureBox() {
        RequestManager.openPetGift(new ResultCallback<XMResult<OpenTreasureInfo>>() {
            @Override
            public void onSuccess(XMResult<OpenTreasureInfo> result) {
                --saveTreasureNumber;
                TREASURE_BOX_MAX_NUMBER = saveTreasureNumber;
                if (saveTreasureNumber > 0) {
                    mTreasureBoxNumber.setText(PetHomeActivity.this.getString(R.string.treasure_number_text, saveTreasureNumber));
                } else {
                    mTreasureBoxNumber.setVisibility(View.GONE);
                }

                List<RewardDetails> rewardDetails = result.getData().getRewardDetails();
                if (rewardDetails != null && rewardDetails.size() > 0) {
                    RewardDetails rewardDetail = rewardDetails.get(0);
                    ImageLoader.with(PetHomeActivity.this)
                            .load(rewardDetail.getGoodsIcon())
                            .apply(RequestOptions.centerCropTransform())
                            .into(mSmallIcon);
                    mSmallDesc.setText(rewardDetail.getGoodsName());
                    if (rewardDetail.getGoodsType().equals(GoodsType.FOOD)) {
                        mSmallTitle.setText(getString(R.string.add_food_number, rewardDetail.getGoodsNumber()));
                    } else {
                        //TODO 如果开启的不是食物，显示规则重新处理
                    }

                    treasureBoxTimingClose();
                    openSmallView(treasureRelative);
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                if (code == 0) {
                    //没有宝箱可以开启
                    PetToast.showToast(PetHomeActivity.this, msg);
                } else {
                    PetToast.showException(PetHomeActivity.this, R.string.treasure_box_open_failed);
                }
            }
        });
    }


    // Quit Unity
    @Override
    protected void onDestroy() {
        mUnityPlayer.destroy();
        ConvertMapTimeCoordinate.postRunningTimePercentToServer();
        super.onDestroy();
        DownloadPetResource.getInstance().release();
        AUBridgeDispatcher.getInstance().release(PetHomeActivity.class.getSimpleName());
        ConvertMapTimeCoordinate.startRunning();
        ConvertMapTimeCoordinate.cancelGenGift();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
//        mUnityPlayer.pause();
    }


    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mUnityPlayer.stop();
    }


    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }


    private void showGuideWindow() {
        if (!shouldShowGuide() || hasShowedGuide || isPetUpgradeViewShow) return;
        hasShowedGuide = true;
        petProgressView.post(new Runnable() {
            @Override
            public void run() {
                newGuide = NewGuide.with(PetHomeActivity.this)
                        .setLebal(GuideConstants.PET_SHOWED)
                        .setTargetViewAndRect(petProgressView)
                        .setGuideLayoutId(R.layout.guide_go_to_shop_or_repository)
                        .setNeedHande(true)
                        .setNeedShake(true)
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
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

    public void showLastGuide() {
        if (!shouldShowGuide()) return;
        NewGuide.with(this)
                .setLebal(GuideConstants.PET_SHOWED)
                .build()
                .showLastGuide(R.layout.guide_last_toast_view_pet/*, R.id.tv_ok*/);
    }

    private boolean shouldShowGuide() {
        return GuideDataHelper.shouldShowGuide(this, GuideConstants.PET_SHOWED, GuideConstants.PET_GUIDE_FIRST, false);
    }


    private void guideJustifyNetWork() {
        if (!NetworkUtils.isConnected(this)) {
            if (shouldShowGuide())
                NewGuide.with(this)
                        .setLebal(GuideConstants.PET_SHOWED)
                        .build()
                        .showGuideFinishWindowDueToNetError(R.layout.guide_finish_page_pet, R.id.tv_ok);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PetConstant.HOME_MALL_RESULT_CODE && resultCode == PetConstant.REFRESH_HOME_PET_INFO) {
            PetInfo petInfo = SavePetInfoUtils.readPetInfo();
            refreshPetInfo(petInfo);
            KLog.w(TAG, "update pet info.");
        }

        if (requestCode == PetConstant.SHOW_LAST_GUIDE) {
            showLastGuide();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (System.currentTimeMillis() - recordCurrentTime <= 300) {
                if (mInterceptView.getVisibility() == View.VISIBLE) {
                    mInterceptView.setVisibility(View.GONE);
                } else {
                    mInterceptView.setVisibility(View.VISIBLE);
                }
            } else {
                recordCurrentTime = System.currentTimeMillis();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
