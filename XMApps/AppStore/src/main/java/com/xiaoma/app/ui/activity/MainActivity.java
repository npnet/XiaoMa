package com.xiaoma.app.ui.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.app.R;
import com.xiaoma.app.common.constant.EventConstants;
import com.xiaoma.app.ui.fragment.AppListFragment;
import com.xiaoma.app.ui.fragment.AppManagerFragment;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.apptool.AppObserver;

import java.util.List;

/**
 * Created by Thomas on 2018/10/12 0012
 */
@PageDescComponent(EventConstants.PageDescribe.mainActivityPagePathDesc)
public class MainActivity extends BaseActivity implements AppManagerFragment.WaitUpdateCountListener, View.OnClickListener {

    private AppManagerFragment mAppManagerFragment;
    private AppListFragment mAppListFragment;
    private TextView tvWaitUpdate;
    private TextView mAppMarket;
    private TextView mAppManager;
    //未安装数目
    int unInstallSize = 0;
    private NewGuide newGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        showGuideWindow();
//        initAppNotificationService();
    }

    /*private void initAppNotificationService() {
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity.this.startService(new Intent(MainActivity.this, AppNotificationService.class));
            }
        }, 3000);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
    }

    private void initView() {
        mAppMarket = findViewById(R.id.app_market);
        mAppManager = findViewById(R.id.app_manager);
        mAppMarket.setOnClickListener(this);
        mAppManager.setOnClickListener(this);
        mAppManagerFragment = AppManagerFragment.newInstance();
        mAppListFragment = AppListFragment.newInstance();
        tvWaitUpdate = findViewById(R.id.tv_wait_update_count);
        switchFragment(mAppListFragment, mAppListFragment.getTAG());
        mAppManagerFragment.setWaitUpdateCountListener(this);
        mAppListFragment.setWaitUpdateCountListener(this);
        appMarketSelected(true);
    }

    /**
     * 初始化下载数据
     */
    private void initData() {
        //下载过程中退出导致的状态异常问题
        List<Progress> taskList = DownloadManager.getInstance().getAll();
        for (Progress info : taskList) {

            //等待中-->去下载
            if (info.status == Progress.WAITING) {
                if (info.fraction > 0) {
                    info.status = Progress.PAUSE;
                } else {
                    info.status = Progress.NONE;
                }
            }
            //下载中-->继续下载
            if (info.status == Progress.LOADING) {
                info.status = Progress.PAUSE;
            }
            //安装中/安装失败-->安装失败
            if (info.status == Progress.INSTALLING || info.status == Progress.INSTALL_FAILED) {
                unInstallSize++;
                info.status = Progress.INSTALL_FAILED;
            }
        }
        //未安装提醒
        if (unInstallSize > 0) {
            XMToast.showToast(this, getString(R.string.app_uninstall_tip, unInstallSize));
        }

        DownloadManager.getInstance().replace(taskList);

        //从数据库中恢复数据
        OkDownload.restore(DownloadManager.getInstance().getAll());
    }

    /**
     * 切换fragment
     *
     * @param fragment
     * @param tag
     */
    public void switchFragment(BaseFragment fragment, String tag) {
        List<Fragment> fragments = FragmentUtils.getFragments(getSupportFragmentManager());
        Fragment fragmentByTag = FragmentUtils.findFragment(getSupportFragmentManager(), tag);
        if (fragmentByTag != null) {
            fragments.remove(fragmentByTag);
            FragmentUtils.showHide(fragmentByTag, fragments);
        } else {
            for (Fragment fg : fragments) {
                FragmentUtils.hide(fg);
            }
            FragmentUtils.add(getSupportFragmentManager(), fragment, R.id.fl_container, tag);
        }
    }

    /**
     * 切换到应用市场Fragment
     */
    public void appMarket() {
        if (mAppMarket.isSelected()) {
            return;
        }
        switchFragment(mAppListFragment, mAppListFragment.getTAG());
        if (mAppListFragment != null) {
            mAppListFragment.onTabClick();
        }
        appMarketSelected(true);
    }

    /**
     * 切换到应用管理Fragment
     */
    public void appManager() {
        if (mAppManager.isSelected()) {
            return;
        }
        switchFragment(mAppManagerFragment, mAppManagerFragment.getTAG());
        if (mAppManagerFragment != null) {
            mAppManagerFragment.onTabClick();
        }
        appMarketSelected(false);
    }

    private void appMarketSelected(boolean marketSelected) {
        if (marketSelected) {
            mAppMarket.setSelected(true);
            mAppManager.setSelected(false);
            mAppMarket.setBackgroundResource(R.drawable.bg_item_select);
            mAppManager.setBackgroundResource(0);

        } else {
            mAppMarket.setSelected(false);
            mAppManager.setSelected(true);
            mAppMarket.setBackgroundResource(0);
            mAppManager.setBackgroundResource(R.drawable.bg_item_select);
        }
    }

    /**
     * 设置待更新数目
     *
     * @param count
     */
    @Override
    public void setWaitUpdateCount(int count) {
        if (count > 0) {
            tvWaitUpdate.setVisibility(View.VISIBLE);
            tvWaitUpdate.setText(count + "");

        } else {
            tvWaitUpdate.setVisibility(View.GONE);
        }
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.appMarket, EventConstants.NormalClick.appManager})
    @ResId({R.id.app_market, R.id.app_manager})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.app_market:
                appMarket();
                break;
            case R.id.app_manager:
                appManager();
                dismissGuideWindow();
                break;
        }
    }

    private void showGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.APPSTORE_SHOWED, GuideConstants.APPSTORE_GUIDE_FIRST, true))
            return;
        mAppManager.post(new Runnable() {
            @Override
            public void run() {
                Rect viewRect = NewGuide.getViewRect(mAppManager);
                Rect targetRect = new Rect(viewRect.left + 30, viewRect.top - 40, viewRect.right - 30, viewRect.bottom - 40);
                newGuide = NewGuide.with(MainActivity.this)
                        .setLebal(GuideConstants.APPSTORE_SHOWED)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .needMoveUpALittle(true)
                        .setGuideLayoutId(R.layout.guide_view_main)
                        .setTargetView(mAppManager)
                        .setTargetRect(targetRect)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setViewHandId(R.id.iv_gesture)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .build();
                newGuide.showGuide();
                GuideDataHelper.setFirstGuideFalse(GuideConstants.APPSTORE_SHOWED);
            }
        });
    }

    private void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        List<Progress> downloadingProgress = DownloadManager.getInstance().getDownloading();
        if(downloadingProgress == null ||downloadingProgress.size() == 0){
//            AppObserver.getInstance().closeAllActivitiesAndExit();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppObserver.getInstance().closeAllActivitiesAndExit();
    }
}
