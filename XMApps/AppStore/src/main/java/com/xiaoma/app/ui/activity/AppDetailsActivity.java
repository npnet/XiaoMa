package com.xiaoma.app.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.xiaoma.app.R;
import com.xiaoma.app.adapter.AppDetailPageAdapter;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.common.constant.EventConstants;
import com.xiaoma.app.listener.ISwitchFragmentListener;
import com.xiaoma.app.model.AppInfo;
import com.xiaoma.app.model.AppStateEvent;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.app.ui.fragment.AppUpdateLogFragment;
import com.xiaoma.app.ui.fragment.DetailFragment;
import com.xiaoma.app.views.UpAndDownViewPager;
import com.xiaoma.app.vm.AppInfoVM;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.config.ConfigConstants;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import static android.view.View.OVER_SCROLL_NEVER;

/**
 * 应用详情页面
 * Created by zhushi.
 * Date: 2018/10/17
 */
@PageDescComponent(EventConstants.PageDescribe.appDetailActivityPagePathDesc)
public class AppDetailsActivity extends BaseActivity implements ISwitchFragmentListener {

    public static final String APPDETAILFRAGMENT = "AppDetailFragment";
    public static final String APPUPDATELOGFRAGMENT = "AppUpdateLogFragment";
    public static final String FROM_TYPE = "FromType";
    private UpAndDownViewPager verticalViewPager;
    private AppDetailPageAdapter pageAdapter;
    private AppInfoVM appInfoVM;
    private DownLoadAppInfo mDownLoadAppInfo;
    private PackageRemoveListener mPackageRemoveListener;
    private String packageName;
    private String currentFragmentTag = APPDETAILFRAGMENT;

    /**
     * 车应用内部打开详情
     *
     * @param context
     * @param appInfo
     */
    public static void startActivityInApp(Context context, DownLoadAppInfo appInfo, int from) {
        Intent intent = new Intent(context, AppDetailsActivity.class);
        intent.putExtra(ConfigConstants.APP_STORE_TYPE_KEY, ConfigConstants.APP_STORE_TYPE_XIAOMA_APP);
        intent.putExtra(ConfigConstants.APP_STORE_PACKAGENAME_KEY, appInfo.getAppInfo().getPackageName());
        intent.putExtra(FROM_TYPE, from);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        initView();
    }

    private void initView() {
        packageName = getIntent().getStringExtra(ConfigConstants.APP_STORE_PACKAGENAME_KEY);
        EventBus.getDefault().register(this);
        verticalViewPager = findViewById(R.id.vertical_view_pager);
        verticalViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentFragmentTag = position == 0 ? APPDETAILFRAGMENT : APPUPDATELOGFRAGMENT;
                if (position == 1) {
                    int from = getIntent().getIntExtra(FROM_TYPE, AppStoreConstants.APP_MANAGER);
                    String event = from == AppStoreConstants.APP_MANAGER ?
                            EventConstants.SlideEvent.appManagerLog : EventConstants.SlideEvent.appMarketLog;
                    XmAutoTracker.getInstance().onEvent(event, "AppDetailsActivity",
                            EventConstants.PageDescribe.appDetailActivityPagePathDesc);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pageAdapter = new AppDetailPageAdapter(getSupportFragmentManager());
        appInfoVM = ViewModelProviders.of(this).get(AppInfoVM.class);
        appInfoVM.getAppInfo().observe(this, new Observer<XmResource<DownLoadAppInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<DownLoadAppInfo> appInfoXmResource) {
                if (appInfoXmResource == null) {
                    return;
                }
                appInfoXmResource.handle(new OnCallback<DownLoadAppInfo>() {
                    @Override
                    public void onSuccess(DownLoadAppInfo downLoadAppInfo) {
                        mDownLoadAppInfo = downLoadAppInfo;
                        setAdapter(downLoadAppInfo);
                        showContentView();
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        showNoNetView();
                    }
                });
            }
        });

        appInfoVM.fetchAppInfo(packageName);
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        appInfoVM.fetchAppInfo(packageName);
    }

    private void setAdapter(DownLoadAppInfo downLoadAppInfo) {
        //详情fragment
        DetailFragment appDetailFragment = DetailFragment.newInstance(downLoadAppInfo);
        appDetailFragment.setSwitchFragmentListener(this);
        pageAdapter.clear();
        pageAdapter.add(appDetailFragment);
        AppInfo appInfo = downLoadAppInfo.getAppInfo();
        //最新应用不显示更新日志
        if (downLoadAppInfo.getInstallState() != AppStoreConstants.INSTALL_STATE_NEW && appInfo.getUpdateContent() != null) {
            //更新日志fragment
            AppUpdateLogFragment appUpdateLogFragment = AppUpdateLogFragment.newInstance(downLoadAppInfo);
            appUpdateLogFragment.setSwitchFragmentListener(this);
            pageAdapter.add(appUpdateLogFragment);
        }
        verticalViewPager.setAdapter(pageAdapter);
        verticalViewPager.setOverScrollMode(OVER_SCROLL_NEVER);
        verticalViewPager.setCurrentItem(0);
    }

    /**
     * 安装应用后的回调
     *
     * @param appStateEvent
     */
    @Subscriber(tag = AppStoreConstants.APP_INSTALL_RECEIVER)
    public void appStateChange(AppStateEvent appStateEvent) {
        KLog.d("AppDetailsActivity", "AppDetailsActivity:" + appStateEvent.toString());
        String packageName = appStateEvent.getPackageName();
        switch (appStateEvent.getAppState()) {
            //安装
            //替换
            case AppStateEvent.STATE_PACKAGE_ADDED:
            case AppStateEvent.STATE_PACKAGE_REPLACED:
                //安装失败
                if (!appStateEvent.isResult()) {
                    //1.标记任务失败
                    DownloadTask task = OkDownload.getInstance().getTask(packageName);
                    if (task == null) {
                        return;
                    }
                    task.progress.status = Progress.INSTALL_FAILED;
                    task.updateDatabase(task.progress);
                    //2.刷新视图
                    if (packageName.equals(mDownLoadAppInfo.getAppInfo().getPackageName())) {
                        mDownLoadAppInfo.setInstallState(AppStoreConstants.INSTALL_STATE_NOTHING);
                        setAdapter(mDownLoadAppInfo);
                    }
                    //3.弹框
                    showInstallErrorView();

                    return;
                }

                //安装成功
                //1.移除下载任务
                DownloadTask task = OkDownload.getInstance().getTask(packageName);
                if (task != null) {
                    task.remove(true);
                }
                //2.刷新视图
                if (packageName.equals(mDownLoadAppInfo.getAppInfo().getPackageName())) {
                    mDownLoadAppInfo.setInstallState(AppStoreConstants.INSTALL_STATE_NEW);
                    setAdapter(mDownLoadAppInfo);
                }
                break;

            //卸载
            case AppStateEvent.STATE_PACKAGE_REMOVED:
                if (mPackageRemoveListener != null) {
                    mPackageRemoveListener.packageRemove(appStateEvent.isResult());
                }
                //下载失败
                if (!appStateEvent.isResult()) {
                    XMToast.toastException(this, getString(R.string.uninstall_failed), false);
                    return;
                }
                //卸载成功
                //刷新视图
                if (packageName.equals(mDownLoadAppInfo.getAppInfo().getPackageName())) {
                    mDownLoadAppInfo.setInstallState(AppStoreConstants.INSTALL_STATE_NOTHING);
                    setAdapter(mDownLoadAppInfo);
                    XMToast.toastSuccess(this, getString(R.string.uninstall_success), false);
                }
                break;
        }
    }

    /**
     * 安装异常弹框
     */
    private void showInstallErrorView() {
        final ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(R.string.tips_install_error))
                .setPositiveButton(getString(R.string.i_down), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.installError})
                    @ResId({R.id.sure})
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButtonVisibility(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void setPackageRemoveListener(PackageRemoveListener mPackageRemoveListener) {
        this.mPackageRemoveListener = mPackageRemoveListener;
    }

    @Override
    public void onBackPressed() {
        if (APPUPDATELOGFRAGMENT.equals(currentFragmentTag)) {
            verticalViewPager.setCurrentItem(0);

        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void switchFragment(String tag) {
        currentFragmentTag = tag;
        if (APPDETAILFRAGMENT.equals(tag)) {
            verticalViewPager.setCurrentItem(0);

        } else if (APPUPDATELOGFRAGMENT.equals(tag)) {
            verticalViewPager.setCurrentItem(1);
        }
    }

    //卸载监听
    public interface PackageRemoveListener {
        void packageRemove(boolean result);
    }
}
