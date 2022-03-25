package com.xiaoma.app.ui.fragment;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.app.R;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.common.constant.EventConstants;
import com.xiaoma.app.listener.ISwitchFragmentListener;
import com.xiaoma.app.model.AppInfo;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.app.ui.activity.AppDetailsActivity;
import com.xiaoma.app.util.ApkUtils;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;

/**
 * @author taojin
 * @date 2018/10/19
 */
@PageDescComponent(EventConstants.PageDescribe.appUpdateLogFragmentFragmentPagePathDesc)
public class AppUpdateLogFragment extends BaseFragment {
    public static final String DOWNLOAD_APPINFO = "download_appinfo";
    private TextView tvVersioInfo;
    private LinearLayout llUpdatelog;
    private TextView mTvAppDetail;
    //Log最大显示条数
    private static final int MAX_LOG_SIZE = 5;
    //fragment切换监听
    private ISwitchFragmentListener mSwitchFragmentListener;

    public static AppUpdateLogFragment newInstance(DownLoadAppInfo downLoadAppInfo) {
        AppUpdateLogFragment appUpdateLogFragment = new AppUpdateLogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(DOWNLOAD_APPINFO, downLoadAppInfo);
        appUpdateLogFragment.setArguments(bundle);

        return appUpdateLogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_update_log_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        bindData();
    }

    private void initView(View view) {
        tvVersioInfo = view.findViewById(R.id.tv_version_info);
        llUpdatelog = view.findViewById(R.id.ll_update_log);
        mTvAppDetail = view.findViewById(R.id.tv_app_detail);
        mTvAppDetail.setOnClickListener(new View.OnClickListener() {

            @Override
            @NormalOnClick({EventConstants.NormalClick.updateLog2Detail})
            @ResId({R.id.tv_app_detail})
            public void onClick(View v) {
                if (mSwitchFragmentListener != null) {
                    mSwitchFragmentListener.switchFragment(AppDetailsActivity.APPDETAILFRAGMENT);
                }
            }
        });
    }

    private void bindData() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        DownLoadAppInfo downLoadAppInfo = (DownLoadAppInfo) bundle.getSerializable(DOWNLOAD_APPINFO);
        if (downLoadAppInfo == null || downLoadAppInfo.getAppInfo() == null) {
            return;
        }
        AppInfo appInfo = downLoadAppInfo.getAppInfo();
        String version;
        //应用为待更新状态或者已安装最新版时，获取本地已安装的versionName
        if (downLoadAppInfo.getInstallState() == AppStoreConstants.INSTALL_STATE_OLD ||
                downLoadAppInfo.getInstallState() == AppStoreConstants.INSTALL_STATE_NEW) {
            PackageInfo packageInfo = ApkUtils.getPackageInfo(appInfo.getPackageName());
            version = packageInfo.versionName;

        } else {
            version = appInfo.getVersionName();
        }
        tvVersioInfo.setText(getString(R.string.version_name, version));
        if (appInfo.getUpdateContentList() == null) {
            return;
        }
        int logListSize = appInfo.getUpdateContentList().size();
        int size = logListSize > MAX_LOG_SIZE ? MAX_LOG_SIZE : logListSize;
        for (int i = 0; i < size; i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_update_log, null);
            TextView logTv = view.findViewById(R.id.tv_log);
            logTv.setText(getString(R.string.update_log_item, appInfo.getUpdateContentList().get(i)));
            llUpdatelog.addView(view);
        }
    }

    public void setSwitchFragmentListener(ISwitchFragmentListener switchFragmentListener) {
        this.mSwitchFragmentListener = switchFragmentListener;
    }
}
