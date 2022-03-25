package com.xiaoma.pet.ui.mall;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.model.XmResource;
import com.xiaoma.network.model.Progress;
import com.xiaoma.pet.R;
import com.xiaoma.pet.common.annotation.GoodsType;
import com.xiaoma.pet.common.annotation.NodeAttr;
import com.xiaoma.pet.common.annotation.ResourceType;
import com.xiaoma.pet.common.annotation.UnityAction;
import com.xiaoma.pet.common.annotation.XmlNode;
import com.xiaoma.pet.common.callback.ILoadDataOfMallListener;
import com.xiaoma.pet.common.callback.IResourceConfigResult;
import com.xiaoma.pet.common.callback.OnDownLoadCallback;
import com.xiaoma.pet.common.callback.OnRefreshTimerCallback;
import com.xiaoma.pet.common.callback.OnUpdateLayoutCallback;
import com.xiaoma.pet.common.callback.OnUsedGoodsCallback;
import com.xiaoma.pet.common.callback.XmPetResourceHandleCallback;
import com.xiaoma.pet.common.manager.DownloadPetResource;
import com.xiaoma.pet.common.manager.PetAssetManager;
import com.xiaoma.pet.common.utils.AUBridgeDispatcher;
import com.xiaoma.pet.common.utils.EatFoodTimer;
import com.xiaoma.pet.common.utils.PetConstant;
import com.xiaoma.pet.common.utils.ResourceUsedRegister;
import com.xiaoma.pet.common.utils.SavePetInfoUtils;
import com.xiaoma.pet.common.utils.UpgradeEnergyHandler;
import com.xiaoma.pet.model.AssetInfo;
import com.xiaoma.pet.model.PetInfo;
import com.xiaoma.pet.model.StoreGoodsInfo;
import com.xiaoma.pet.model.UpgradeRewardInfo;
import com.xiaoma.pet.ui.PetBaseActivity;
import com.xiaoma.pet.ui.mall.pay.PayFragment;
import com.xiaoma.pet.ui.view.PaySuccessView;
import com.xiaoma.pet.ui.view.PetExperienceView;
import com.xiaoma.pet.ui.view.PetToast;
import com.xiaoma.pet.ui.view.PetUpgradeView;
import com.xiaoma.pet.vm.PetVM;
import com.xiaoma.ui.dialog.OnViewClickListener;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.util.Locale;

/**
 * Created by Gillben on 2018/12/24 0024
 * <p>
 * desc: 宠物商城
 */
public class PetMallActivity extends PetBaseActivity
        implements PayFragment.OnPayCallback, OnUpdateLayoutCallback, OnUsedGoodsCallback, ILoadDataOfMallListener {

    private static final String TAG = PetMallActivity.class.getSimpleName();
    private TextView mTimingText;
    private PetExperienceView mPetExperienceView;
    private TextView mLevelText;
    private TextView mPercentText;
    private TextView mPetPromptText;
    private MallHomeFragment mallHomeFragment = MallHomeFragment.newInstance();
    private CountDownTimer mCountDownTimer;
    private int storeOrRepositoryFlag;
    private String refreshFragmentTag;
    private PetVM petVM;
    private boolean isStoreFoodGuideShowed;


    @Override
    protected View bindView() {
        showNavigationBar();
        View view = View.inflate(this, R.layout.activity_pet_mall, null);
        initView(view);
        ResourceUsedRegister.getInstance().register(this);
        return view;
    }

    private void initView(View view) {
        mPetExperienceView = view.findViewById(R.id.pet_experience_view);
        mTimingText = view.findViewById(R.id.tv_mall_timing_text);
        mLevelText = view.findViewById(R.id.tv_pet_level_text);
        mPercentText = view.findViewById(R.id.tv_pet_experience_number);
        mPetPromptText = view.findViewById(R.id.tv_pet_prompt);
    }

    @Override
    protected void initData() {
        Intent tempIntent = getIntent();
        if (tempIntent != null) {
            storeOrRepositoryFlag = tempIntent.getIntExtra(PetConstant.STORE_AND_REPOSITORY_FLAG, 0);
        }
        petVM = ViewModelProviders.of(this).get(PetVM.class);
        updateLayout(mallHomeFragment, PetConstant.MALL_HOME_FRAGMENT_TAG, true);
        fetchPetInfo();
    }


    @Override
    public void updateLayoutOnFragment(Fragment fragment, String tag, boolean isStack) {
        updateLayout(fragment, tag, isStack);
    }


    public void updateLayout(Fragment fragment, String tag, boolean isAddStack) {
        FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.mall_layout, tag, false, isAddStack);
    }


    public int getStoreAndRepository() {
        return storeOrRepositoryFlag;
    }


    @Override
    public void loadSuccessOfMall() {
        loadSuccess();
    }

    @Override
    public void loadFailedOfMall(String fragmentTag) {
        this.refreshFragmentTag = fragmentTag;
        loadFailed();
    }


    @Override
    protected void refresh() {
        if (TextUtils.isEmpty(refreshFragmentTag)) {
            initData();
        } else if (PetConstant.PAY_FRAGMENT_TAG.equals(refreshFragmentTag)) {
            loadSuccess();
        } else {
            mallHomeFragment.refreshDataOfChildFragment(refreshFragmentTag);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void finish() {
        //刷新首页宠物信息
        setResult(PetConstant.REFRESH_HOME_PET_INFO);
        super.finish();
        //增加退出处理，预防闪屏
        overridePendingTransition(-1, R.anim.mall_activity_exit);
    }


    @Override
    public void usedFood(boolean eating) {
        if (eating) {
            mPetPromptText.setText(R.string.pet_eating_food);
        } else {
            fetchPetInfo();
        }
    }


    @Override
    public void usedDecoratorNeedDownload(String goodsType, String url) {
        handleResourceUsedLogin(goodsType, url);
    }


    @Override
    public void paySuccess(StoreGoodsInfo storeGoodsInfo) {
        boughtSuccess(storeGoodsInfo);
    }


    private void fetchPetInfo() {
        petVM.getPetInfo().observe(this, new Observer<XmResource<PetInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<PetInfo> petInfoXmResource) {
                if (petInfoXmResource == null) {
                    KLog.w(TAG, "petInfoXmResource is null.");
                    return;
                }

                petInfoXmResource.handle(new XmPetResourceHandleCallback<PetInfo>() {
                    @Override
                    public void onSuccess(PetInfo petInfo) {
                        loadSuccess();
                        boolean isUpgrade = UpgradeEnergyHandler.getInstance().checkUpgrade(petInfo.getGrade(), petInfo.getExperienceValue());
                        KLog.d(TAG, "update pet info.   isUpgrade=" + isUpgrade);
                        if (isUpgrade) {
                            handleUpgrade(petInfo);
                        } else {
                            refreshPetInfo(petInfo);
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        loadFailed();
                        KLog.w(TAG, "message: " + message);
                    }
                });
            }
        });
    }


    private void handleUpgrade(PetInfo petInfo) {
        petVM.checkUpgrade(petInfo, new PetVM.OnHandleUpgradeAction() {
            @Override
            public void upgradeAction(int newLevel, UpgradeRewardInfo upgradeRewardInfo) {
                upgradeReward(newLevel, upgradeRewardInfo);
                String content = UpgradeEnergyHandler.getInstance().getPetEvolutionStatus(newLevel);
                if (TextUtils.isEmpty(content)) {
                    mPetPromptText.setText(R.string.pet_upgrade);
                } else {
                    mPetPromptText.setText(R.string.pet_evolution);
                }
                fetchPetInfo();
            }

            @Override
            public void upgradeFailed() {
                KLog.w(TAG, "upgrade failed.");
            }
        });
    }


    private void refreshPetInfo(PetInfo petInfo) {
        long nextLevelEnergy = UpgradeEnergyHandler.getInstance().calculationNextLevelExperience(petInfo.getGrade());
        mPetExperienceView.updateExperience(nextLevelEnergy, petInfo.getExperienceValue());
        mLevelText.setText(getString(R.string.pet_level_text, petInfo.getGrade()));

        float percent = UpgradeEnergyHandler.getInstance().calculationPercent(petInfo.getGrade(), petInfo.getExperienceValue());
        String temp = (int) (percent * 100) + "%";
        mPercentText.setText(temp);

        //宠物正在进食，开启计时
        if (petInfo.isEating()) {
            mPetPromptText.setText(R.string.delicious_of_food);
            handleTimer(petInfo.getSurplusTime());
        } else {
            mPetPromptText.setText(R.string.pet_very_hungry);
            mTimingText.setVisibility(View.GONE);
        }

        SavePetInfoUtils.save(petInfo);
    }


    private void handleTimer(long surplusTime) {
        KLog.d(TAG, "pet eating prompt.");
        EatFoodTimer.getInstance().startTimer(surplusTime, 60000, new OnRefreshTimerCallback() {
            @Override
            public void refresh(int hour, int min) {
                mTimingText.setVisibility(View.VISIBLE);
                String hourText = String.format(Locale.CHINA, "%02d", hour);
                String minText = String.format(Locale.CHINA, "%02d", min);
                mTimingText.setText(PetMallActivity.this.getString(R.string.eat_food_timer_text, hourText, minText));
            }

            @Override
            public void finish() {
                mTimingText.setVisibility(View.GONE);
                //进食完成校验是否可以升级
                KLog.d(TAG, "Upgrade finish renew get pet info.");
                fetchPetInfo();
            }
        });
    }


    private void upgradeReward(int petNewLevel, UpgradeRewardInfo upgradeRewardInfo) {
        PetUpgradeView petUpgradeView = new PetUpgradeView(this, petNewLevel, upgradeRewardInfo);
        petUpgradeView.showAtLocation(getRootLayout(), Gravity.START | Gravity.TOP, 0, 0);
    }


    private void boughtSuccess(StoreGoodsInfo storeGoodsInfo) {
        getSupportFragmentManager().popBackStack();
        if (mallHomeFragment != null) {
            mallHomeFragment.forceRefreshData(storeGoodsInfo.getGoodsType());
        }

        if (GoodsType.FOOD.equals(storeGoodsInfo.getGoodsType())) {
            PaySuccessView paySuccessView = PaySuccessView.newInstance(storeGoodsInfo);
            paySuccessView.show(getSupportFragmentManager(), null);
            paySuccessView.setOnCancelListener(new PaySuccessView.OnCancelListener() {
                @Override
                public void cancel() {
                    if (mallHomeFragment != null) {
                        mallHomeFragment.newProductNotify();
                    }
                }
            });
        } else {
            promptUseGoods(storeGoodsInfo);
        }
    }


    private void promptUseGoods(final StoreGoodsInfo storeGoodsInfo) {
        View view = View.inflate(this, R.layout.prompt_use_goods_view, null);
        TextView descText = view.findViewById(R.id.tv_pet_dialog_desc);
        final TextView cancelText = view.findViewById(R.id.tv_pet_dialog_cancel);
        String type = storeGoodsInfo.getGoodsType().equals(GoodsType.DECORATOR) ? getString(R.string.pet_decoration) : getString(R.string.pet_cosplay);
        descText.setText(getString(R.string.use_current_buy_goods, type));

        final XmDialog xmDialog = new XmDialog.Builder(this)
                .setView(view)
                .setWidth(getResources().getDimensionPixelSize(R.dimen.width_pet_prompt_dialog))
                .setHeight(getResources().getDimensionPixelSize(R.dimen.height_pet_prompt_dialog))
                .addOnClickListener(R.id.tv_pet_dialog_confirm, R.id.tv_pet_dialog_cancel)
                .setOnViewClickListener(new OnViewClickListener() {
                    @Override
                    public void onViewClick(View view, XmDialog tDialog) {
                        tDialog.dismiss();
                        if (view.getId() == R.id.tv_pet_dialog_confirm) {
                            handleResourceUsedLogin(storeGoodsInfo.getGoodsType(), storeGoodsInfo.getGoodsFilePath());
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (mCountDownTimer != null) {
                            mCountDownTimer.cancel();
                        }
                        if (mallHomeFragment != null) {
                            mallHomeFragment.newProductNotify();
                        }
                    }
                })
                .setCancelableOutside(false)
                .create();
        xmDialog.show();

        mCountDownTimer = new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String content = PetMallActivity.this.getString(R.string.cancel_use_goods) + "(" + millisUntilFinished / 1000 + ")";
                cancelText.setText(content);
            }

            @Override
            public void onFinish() {
                xmDialog.dismiss();
                if (mCountDownTimer != null) {
                    mCountDownTimer.cancel();
                }
            }
        };
        mCountDownTimer.start();
    }


    private void handleResourceUsedLogin(String goodsType, String url) {
        if (TextUtils.isEmpty(url)) {
            PetToast.showException(this, R.string.pet_download_failed);
            KLog.w(TAG, "url is null.");
            return;
        }
        
        int type = -1;
        switch (goodsType) {
            case GoodsType.DECORATOR:
                type = ResourceType.DECORATOR;
                break;

            case GoodsType.COS_PLAY:
                type = ResourceType.PET;
                break;

        }
        String fileName = DownloadPetResource.getInstance().createFileName(url);
        boolean exists = PetAssetManager.getInstance().checkFileExists(type, fileName);
        if (exists) {
            String resourcePath = PetAssetManager.getInstance().getResourcePath(type, fileName);
            AssetInfo assetInfo = createAssetInfo(type, NodeAttr.path, resourcePath);
            PetAssetManager.getInstance().directSaveXMLNode(assetInfo);
            AUBridgeDispatcher.getInstance().callUnity(System.currentTimeMillis(), UnityAction.REFRESH);
        } else {
            startDownloadPetResource(type, url);
        }
    }


    private void startDownloadPetResource(final int type, String url) {
        PetToast.showLoading(PetMallActivity.this, R.string.pet_downloading_text);
        DownloadPetResource.getInstance().startDownLoad(url, new OnDownLoadCallback(TAG) {
            @Override
            public void onStart(Progress progress) {
                //TODO 下载任务开始，处理用户交互
                KLog.d(TAG, "Start download resource.");
            }

            @Override
            public void onProgress(Progress progress) {
                //TODO 下载过程进度展示
                KLog.d(TAG, "total: " + progress.totalSize + "    current: " + progress.currentSize);
            }

            @Override
            public void onFinish(File file, Progress progress) {
                PetToast.dismissLoading();
                saveResourceFile(type, file);
            }

            @Override
            public void onError(Progress progress) {
                super.onError(progress);
                PetToast.dismissLoading();
                PetToast.showException(PetMallActivity.this, R.string.pet_download_failed);
            }
        });
    }


    private void saveResourceFile(int type, File file) {
        AssetInfo assetInfo = createAssetInfo(type, NodeAttr.path, file.getAbsolutePath());
        PetAssetManager.getInstance()
                .pullAssetBundleSaveToLocal(assetInfo, file.getName(), file, new IResourceConfigResult() {
                    @Override
                    public void start() {
                        KLog.d(TAG, "Start copy raw file to xml directory.");
                    }

                    @Override
                    public void result(boolean success) {
                        if (success) {
                            AUBridgeDispatcher.getInstance().callUnity(System.currentTimeMillis(), UnityAction.REFRESH);
                        } else {
                            KLog.w(TAG, "Resource file save failed.");
                        }
                    }
                });
    }


    private AssetInfo createAssetInfo(int type, String attr, String value) {
        String node = null;
        switch (type) {
            case ResourceType.LEVEL:
                node = XmlNode.level;
                break;
            case ResourceType.DECORATOR:
                node = XmlNode.decorator;
                break;
            case ResourceType.GIFT:
                node = XmlNode.gift;
                break;
            case ResourceType.PET:
                node = XmlNode.pet;
                break;
            case ResourceType.SCENE:
                node = XmlNode.scene;
                break;
        }

        AssetInfo assetInfo = new AssetInfo();
        assetInfo.setAssetType(type);
        //TODO 默认使用版本号为1
        assetInfo.setAppVersion(String.valueOf(1));
        assetInfo.setLevelVersion(String.valueOf(1));
        assetInfo.setTagNode(node);
        assetInfo.setAttr(attr);
        assetInfo.setValue(value);

        return assetInfo;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ResourceUsedRegister.getInstance().unregister();
        EatFoodTimer.getInstance().stopTimer();
    }

    public boolean isStoreFoodGuideShowed() {
        return isStoreFoodGuideShowed;
    }

    public void setStoreFoodGuideShowed(boolean isStoreFoodGuideShowed) {
        this.isStoreFoodGuideShowed = isStoreFoodGuideShowed;
    }

    public void showRepositoryGuide() {
        if (mallHomeFragment == null) return;
        mallHomeFragment.showGuideWindow();
    }

    private boolean shouldShowGuide() {
        return GuideDataHelper.shouldShowGuide(this, GuideConstants.PET_SHOWED, GuideConstants.PET_GUIDE_FIRST, false);
    }


}
