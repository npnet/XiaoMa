package com.xiaoma.shop.business.ui.bought;

import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.carlib.utils.DeviceUtils;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.download.DownloadListener;
import com.xiaoma.shop.business.download.DownloadStatus;
import com.xiaoma.shop.business.download.impl.SkinDownload;
import com.xiaoma.shop.business.ui.ShopBaseFragment;
import com.xiaoma.shop.business.ui.bought.callback.ChangeFragmentCallback;
import com.xiaoma.shop.business.ui.bought.callback.OneKeyCleanCacheCallback;
import com.xiaoma.shop.business.ui.view.DisableSwipeViewPager;
import com.xiaoma.shop.common.constant.CacheBindStatus;
import com.xiaoma.shop.common.constant.ResourceType;
import com.xiaoma.shop.common.track.EventConstant;
import com.xiaoma.shop.common.track.model.CleanTrackInfo;
import com.xiaoma.shop.common.util.ResourceCounter;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.OnViewClickListener;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.model.Result;

import java.io.File;
import java.util.Objects;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/4/19 0019 17:43
 *   desc:   已购列表主内容
 * </pre>
 */
public class BoughtMainFragment extends ShopBaseFragment implements View.OnClickListener, OneKeyCleanCacheCallback {


    private TabLayout mTabLayout;
    private DisableSwipeViewPager disableSwipeViewPager;
    private RelativeLayout cleanCacheLayout;
    private LinearLayout bindAccountLayout;
    private TextView mCacheTextDesc;
    private TextView mCacheOperation;
    private ImageView mAccountBindImg;
    private TextView mAccountBindText;
    private CheckBox mSelectAll;

    private ImageView cleanImage;
    private ImageView successImage;
    private TextView contentText;
    private LinearLayout knownLinear;
    private AnimationDrawable mAnimationDrawable;
    private ChangeFragmentCallback mChangeFragmentCallback;

    private TextView mTvAudioSound;
    private TextView mTvInstrumentSound;

    private AbsBoughtFragment[] mFragments = {BoughtThemeFragment.newInstance(),
            BoughtVoiceToneFragment.newInstance(),
            BoughtHologramFragment.newInstance(),
            BoughtVehicleSoundFragment.newInstance()};

    private int currentResourceType = ResourceType.SKIN;
    private boolean startCleanFlag = false;
    private XmDialog cleanCacheDialog;
    private String cacheTextTemp;

    private Handler mainHandler = new Handler(Looper.myLooper());
    private int startTime = 5;
    private TextView mTvTiming;
    private final static int TIME_DELAY = 1000;

    public static BoughtMainFragment newInstance() {
        return new BoughtMainFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChangeFragmentCallback = (ChangeFragmentCallback) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_bought_main, container, false);
        return onCreateWrapView(contentView);
    }

    private DownloadListener mDownloadListener;
    private SkinDownload mSkinDownload;

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 监听所有下载内容
        mSkinDownload = SkinDownload.getInstance();
        mSkinDownload.addDownloadListener(mDownloadListener = new DownloadListener() {
            @Override
            public void onDownloadStatus(final DownloadStatus downloadStatus) {
                ViewCompat.postOnAnimation(view, new Runnable() {
                    @Override
                    public void run() {
                        if (isDestroy())
                            return;
                        if (downloadStatus != null && downloadStatus.status == android.app.DownloadManager.STATUS_SUCCESSFUL) {
                            if (mSelectAll.isChecked()) {
                                selectAll(true);
                            } else {
                                refreshCacheSize();
                            }
                        }

                    }
                });
            }
        });
        initView(view);
        initData();
        refreshCacheSize();
        //        getResourceFolderTotalSize();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSkinDownload.removeDownloadListener(mDownloadListener);
    }

    private void initView(View view) {
        mTabLayout = view.findViewById(R.id.bought_tab);
        disableSwipeViewPager = view.findViewById(R.id.bought_view_pager);
        cleanCacheLayout = view.findViewById(R.id.clean_cache_layout);
        bindAccountLayout = view.findViewById(R.id.account_bind_layout);
        mCacheTextDesc = view.findViewById(R.id.tv_cache_text_desc);
        mCacheOperation = view.findViewById(R.id.bt_cache_operation);
        mAccountBindImg = view.findViewById(R.id.iv_account_bind);
        mAccountBindText = view.findViewById(R.id.tv_account_bind);
        mSelectAll = view.findViewById(R.id.ck_select_all);

        mCacheOperation.setOnClickListener(this);
        mAccountBindText.setOnClickListener(this);
        mSelectAll.setOnClickListener(this);


        mTvAudioSound = view.findViewById(R.id.tv_audio_sound);
        mTvInstrumentSound = view.findViewById(R.id.tv_instrument_sound);


        mTvAudioSound.setOnClickListener(this);
        mTvInstrumentSound.setOnClickListener(this);
        disableSwipeViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == mFragments.length - 1) {
                    hideOrShowTv(true);
                } else {
                    hideOrShowTv(false);
                }
            }
        });
        hideOrShowTv(false);

    }

    private void hideOrShowTv(boolean isVisible) {
        mTvAudioSound.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        mTvInstrumentSound.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void changeTheTvStyle(int position) {
        restoreStyle();
        switch (position) {
            case 0:
                mTvAudioSound.setTextAppearance(R.style.text_view_light_white);
                break;
            case 1:
                mTvInstrumentSound.setTextAppearance(R.style.text_view_light_white);
                break;
            default:
                break;
        }
    }

    @Override
    protected void noNetworkOnRetry() {
        initData();
    }

    private void initData() {
        if (!NetworkUtils.isConnected(mContext)) {
            showNoNetView();
            return;
        }
        showContentView();

        final String[] title = {
                getString(R.string.buy_system_theme_title),
                getString(R.string.buy_voice_style_title),
                getString(R.string.buy_hologram_title),
                getString(R.string.buy_vehicle_sound_title)};

        disableSwipeViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return title[position];
            }
        });

        disableSwipeViewPager.setOffscreenPageLimit(1);
        mTabLayout.setupWithViewPager(disableSwipeViewPager);
        mTabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {
            String mTabText;

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabText = tab.getText().toString();
                changeCleanCacheStatus(false);
                changeTheTabVisible(mTabText);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mTabText, "0");
            }
        });

        //changeTabLayoutItem();
    }

    private void changeTheTabVisible(String tabText) {
        if (!TextUtils.isEmpty(tabText) && tabText.equals(getString(R.string.buy_vehicle_sound_title))) {
            cleanCacheLayout.setVisibility(View.GONE);
            bindAccountLayout.setVisibility(View.GONE);
        }
    }


    public void changeCleanCacheStatus(boolean open) {
        if (currentResourceType == ResourceType.SKIN) {
            mFragments[0].cleanCacheOperation(open);
        } else if (currentResourceType == ResourceType.ASSISTANT) {
            mFragments[1].cleanCacheOperation(open);
        }

        startCleanFlag = open;
        if (open) {
            showSelectedFileSize("0M");
            changeButtonText(R.string.start_clean_cache);
            mSelectAll.setVisibility(View.VISIBLE);
            ResourceCounter.getInstance().release();
        } else {
            getResourceFolderTotalSize();
            changeButtonText(R.string.clean_cache);
            mSelectAll.setVisibility(View.GONE);
            mSelectAll.setChecked(false);
        }
    }


    public boolean isExecutingCleanCache() {
        boolean flag = false;
        if (currentResourceType == ResourceType.SKIN) {
            flag = mFragments[0].isExecutingCleanCache();
        } else if (currentResourceType == ResourceType.ASSISTANT) {
            flag = mFragments[1].isExecutingCleanCache();
        }
        return flag;
    }


    @Override
    public void initAction(int status, int type) {
        currentResourceType = type;
        switch (status) {
            case CacheBindStatus.ACCOUNT_BIND:
                cleanCacheLayout.setVisibility(View.GONE);
                bindAccountLayout.setVisibility(View.GONE);
                break;
            case CacheBindStatus.CLEAN_CACHE:
                cleanCacheLayout.setVisibility(View.VISIBLE);
                bindAccountLayout.setVisibility(View.GONE);
                break;
            case CacheBindStatus.NONE:
                cleanCacheLayout.setVisibility(View.GONE);
                bindAccountLayout.setVisibility(View.GONE);
                break;
        }
        //        getResourceFolderTotalSize();
        refreshCacheSize();
    }

    @Override
    public void selectedCacheSize(String cacheText) {
        showSelectedFileSize(cacheText);

        //计算是否已经全选
        int currentSelected = ResourceCounter.getInstance().getCanCleanFileName().size();
        int totalFileNumber = 0;

        //-1 默认有一个资源处于使用中不允许操作
        if (currentResourceType == ResourceType.SKIN) {
            //            totalFileNumber = ResourceCounter.getInstance().getDirectoryLength(ConfigManager.FileConfig.getShopSkinDownloadFolder()) - 1;
            currentSelected = mFragments[0].calcuSelectedNum();
            totalFileNumber = mFragments[0].calcuCanDeleteFileNum();
            KLog.i("filOut| "+"[selectedCacheSize]->currentSelected "+currentSelected);
            KLog.i("filOut| "+"[selectedCacheSize]->totalFileNumber "+totalFileNumber);
        } else if (currentResourceType == ResourceType.ASSISTANT) {
            currentSelected = mFragments[1].calcuSelectedNum();
            totalFileNumber = mFragments[1].calcuCanDeleteFileNum();
            //            totalFileNumber = ResourceCounter.getInstance().getDirectoryLength(ConfigManager.FileConfig.getShopAssistantFolder()) - 1;
        }
        if (totalFileNumber != 0) {
            if (currentSelected >= totalFileNumber) {//有出现后台给的皮肤路径一致，切又是个新产品的情况，可能是测试环境这样，这里避免下
                mSelectAll.setChecked(true);
            } else {
                mSelectAll.setChecked(false);
            }
        } else {
            mSelectAll.setChecked(false);
        }
    }

    @Override
    public void startClean() {
        mCacheOperation.setText(R.string.start_clean_cache);
        showCleanCacheDialog(true, null);
    }

    @Override
    public void completeClean() {
        mCacheOperation.setText(R.string.clean_cache);
        changeCleanCacheStatus(false);
        if (!TextUtils.isEmpty(cacheTextTemp)) {
            showCleanCacheDialog(false, cacheTextTemp);
        }
    }

    @Override
    public void downloadCompleteUpdateCacheSize() {
        getResourceFolderTotalSize();
    }

    @Override
    public void refreshCacheSize() {
        if (isSelectedCleanCacheState()) {
            String cacheSize = "0M";
            if (currentResourceType == ResourceType.SKIN) {
                cacheSize = ResourceCounter.getInstance().convertByteAsUnit(mFragments[0].calcuSelectedCacheSize());
            } else if (currentResourceType == ResourceType.ASSISTANT) {
                cacheSize = ResourceCounter.getInstance().convertByteAsUnit(mFragments[1].calcuSelectedCacheSize());
                //                totalFileNumber = ResourceCounter.getInstance().getDirectoryLength(ConfigManager.FileConfig.getShopAssistantFolder()) - 1;
            }
            selectedCacheSize(cacheSize);
        } else {
            getResourceFolderTotalSize();
        }

    }


    private void changeButtonText(int resId) {
        mCacheOperation.setText(resId);
    }


    private void showSelectedFileSize(String cache) {
        cacheTextTemp = cache;//保存已选择需要清理的缓存数值
        mCacheTextDesc.setText(getString(R.string.selected_cache_size, cache));
    }

    /**
     * 计算已占用内存空间
     */
    private void getResourceFolderTotalSize() {
        String sizeText;
        if (currentResourceType == ResourceType.SKIN) {
            //            sizeText = ResourceCounter.getInstance().convertFolderCacheText(a());
            sizeText = ResourceCounter.getInstance().convertByteAsUnit(mFragments[0].calcuCacheSize());
        } else if (currentResourceType == ResourceType.ASSISTANT) {
            //            sizeText = ResourceCounter.getInstance().convertFolderCacheText(ConfigManager.FileConfig.getShopAssistantFolder());
            sizeText = ResourceCounter.getInstance().convertByteAsUnit(mFragments[1].calcuCacheSize());
        } else {
            sizeText = "0M";
        }
        mCacheTextDesc.setText(getString(R.string.own_cache_size, sizeText));
    }


    private void starterClean() {
        //如果没有选择任何需要清理的资源也不处理
        int selectSize = ResourceCounter.getInstance().getSelectedNeedCleanLength();
        //        manualUpdateTrack(EventConstant.NormalClick.ACTION_CLEAN_CACHE);
        XmAutoTracker.getInstance().onEvent(EventConstant.NormalClick.ACTION_CLEAN_CACHE,
                new CleanTrackInfo(currentResourceType, String.valueOf(selectSize)).toTrackString(),
                this.getClass().getName(), EventConstant.PageDesc.ACTIVITY_MY_BUY);
        if (selectSize <= 0) {
            XMToast.showToast(mContext, R.string.tips_select_res_for_clean);
            return;
        }

        if (currentResourceType == ResourceType.SKIN) {
            mFragments[0].executeCleanCache();
        } else if (currentResourceType == ResourceType.ASSISTANT) {
            mFragments[1].executeCleanCache();
        }
    }


    public void selectAll(boolean select) {
        String cacheSize = "0";
        if (currentResourceType == ResourceType.SKIN) {
            cacheSize = mFragments[0].selectAll(select);
        } else if (currentResourceType == ResourceType.ASSISTANT) {
            cacheSize = mFragments[1].selectAll(select);
        }
        //        selectedCacheSize(cacheSize);
        refreshCacheSize();
    }


    private void handleCache() {
        if (startCleanFlag) {//开始清理
            starterClean();
        } else {//计算选中需要
            manualUpdateTrack(EventConstant.NormalClick.ACTION_CLEAN_CACHE);
            int fileSize = 0;
            if (currentResourceType == ResourceType.SKIN) {
                fileSize = mFragments[0].calcuCanDeleteFileNum();
            } else if (currentResourceType == ResourceType.ASSISTANT) {
                //                fileSize = ResourceCounter.getInstance().getDirectoryLength(ConfigManager.FileConfig.getShopAssistantFolder());
                fileSize = mFragments[1].calcuCanDeleteFileNum();
            }

            //无任何资源文件存在，无需清理
            if (fileSize <= 0) {
                XMToast.showToast(mContext, R.string.tips_no_need_to_clean);
                return;
            }
            changeCleanCacheStatus(true);
        }
    }

    private void showCleanCacheDialog(final boolean start, final String cacheText) {
        if (cleanCacheDialog == null) {
            View view = View.inflate(mContext, R.layout.dialog_clean_cache, null);
            cleanImage = view.findViewById(R.id.iv_clean_cache);
            successImage = view.findViewById(R.id.iv_clean_cache_success);
            contentText = view.findViewById(R.id.tv_clean_cache_text);
            knownLinear = view.findViewById(R.id.linear_layout_confirm);
            mTvTiming = view.findViewById(R.id.tv_timing);
            cleanCacheDialog = new XmDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setView(view)
                    .setWidth(mContext.getResources().getDimensionPixelOffset(R.dimen.width_big_dialog))
                    .setHeight(mContext.getResources().getDimensionPixelOffset(R.dimen.height_clean_cache))
                    .setCancelableOutside(false)
                    .addOnClickListener(R.id.linear_layout_confirm)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            mainHandler.removeCallbacksAndMessages(null);
                            cleanCacheDialog = null;

                        }
                    })
                    .setOnViewClickListener(new OnViewClickListener() {
                        @Override
                        public void onViewClick(View view, XmDialog dialog) {
                            if (view.getId() == R.id.linear_layout_confirm) {
                                dialog.dismiss();
                            }
                        }
                    })
                    .create()
                    .show();

            mAnimationDrawable = (AnimationDrawable) cleanImage.getDrawable();
            mAnimationDrawable.setOneShot(true);
            mAnimationDrawable.start();
        } else {
            if (!start) {
                if (mAnimationDrawable != null) {
                    mAnimationDrawable.stop();
                    mAnimationDrawable = null;
                }
                cleanImage.setVisibility(View.GONE);
                successImage.setVisibility(View.VISIBLE);
                contentText.setText(getString(R.string.clean_cache_success, cacheText));
                knownLinear.setVisibility(View.VISIBLE);
                mTvTiming.setVisibility(View.VISIBLE);
                startTime = 5;
                mTvTiming.setText(String.format(getString(R.string.automatically_close_the_popup), startTime));
                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startTime--;
                        if (startTime <= 0) {
                            mainHandler.removeCallbacksAndMessages(null);
                            if (cleanCacheDialog != null) {
                                cleanCacheDialog.dismiss();
                            }
                            return;
                        }
                        mTvTiming.setText(String.format(getString(R.string.automatically_close_the_popup), startTime));
                        mainHandler.postDelayed(this, TIME_DELAY);
                    }
                }, TIME_DELAY);
            }
        }
    }

    private void restoreStyle() {
        mTvAudioSound.setTextAppearance(R.style.text_view_normal);
        mTvInstrumentSound.setTextAppearance(R.style.text_view_normal);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_cache_operation:
                handleCache();
                break;

            case R.id.ck_select_all:
                manualUpdateTrack(mSelectAll.isChecked()
                        ? EventConstant.NormalClick.ACTION_SELECT_ALL
                        : EventConstant.NormalClick.ACTION_CANCEL_SELECT_ALL);
                selectAll(mSelectAll.isChecked());
                break;

            case R.id.tv_account_bind:
                manualUpdateTrack(EventConstant.NormalClick.ACTION_ACCOUNT_BIND);
                if (currentResourceType == ResourceType.HOLOGRAM) {
                    //TODO 进入全息账户绑定
                    changeFragment(BinderHologramFragment.newInstance());

                }
                break;

            case R.id.tv_audio_sound:
                changeTheTvStyle(0);
                BoughtVehicleSoundFragment boughtVehicleSoundFragments = (BoughtVehicleSoundFragment) mFragments[mFragments.length - 1];
                boughtVehicleSoundFragments.switchFragment(0);
                break;
            case R.id.tv_instrument_sound:
                changeTheTvStyle(1);
                BoughtVehicleSoundFragment boughtVehicleSoundFragments21 = (BoughtVehicleSoundFragment) mFragments[mFragments.length - 1];
                boughtVehicleSoundFragments21.switchFragment(1);
                break;
        }
    }

    public void changeFragment(BaseFragment targetFragment) {
        if (mChangeFragmentCallback != null) {
            mChangeFragmentCallback.changeFragment(targetFragment);
        }
    }

    private void manualUpdateTrack(String eventAction) {
        XmAutoTracker.getInstance().onEvent(eventAction, this.getClass().getName(), EventConstant.PageDesc.FRAGMENT_BUYED_MAIN);
    }

    public boolean isAllSelected() {//是否是全选状态
        return mSelectAll.isChecked();
    }

    public boolean isSelectedCleanCacheState() {//是否是选择清理垃圾状态
        String status = ResUtils.getString(mActivity, R.string.start_clean_cache);
        return status.equals(mCacheOperation.getText().toString());
    }

}
