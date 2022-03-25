package com.xiaoma.setting.other.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarUpdateManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

/**
 * Created by Administrator on 2018/10/10 0010.
 */

@PageDescComponent(EventConstants.PageDescribe.versionSettingFragmentPagePathDesc)
public class VersionFragment extends BaseFragment implements XmCarUpdateManager.OnConferStateListener, XmCarUpdateManager.OnVersionListener {

    private final String TAG = VersionFragment.class.getSimpleName();
    private XmDialog mUpdatingDialog;
    private String mPackageName;
    private TextView mTvUpdateContent;
    private TextView versionView;
    private String osVersion;
    private String mcuVersion;
    private ConfirmDialog installSDialog;
    private ConfirmDialog installResultDialog;
    private boolean ispdatingDialogShow = false;
    private TextView icciTv;
    private TextView imeiTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_version, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //XmCarUpdateManager.getInstance().init(getContext());
        //XmCarUpdateManager.getInstance().setOnConferStateListener(this);
        initView(view);
        XmCarUpdateManager.getInstance().setVersionListener(this);
        initData();
    }

    private void initData() {
        XmCarUpdateManager.getInstance().getVersionInfo();
        String iccid = ConfigManager.DeviceConfig.getICCID(getContext());
//        String imei = ConfigManager.DeviceConfig.getIMEI(getContext());
        String imei = FileUtils.read(ConfigManager.FileConfig.getLocalTboxConfigFile());
        if (!TextUtils.isEmpty(iccid)) {
            icciTv.setText(String.format(getString(R.string.iccid), iccid));
        }
        if (!TextUtils.isEmpty(imei)) {
            imeiTv.setText(String.format(getString(R.string.imei), imei));
        }
        // 先设置本地保存的版本信息
        String localOsVersion = TPUtils.get(getContext(), SettingConstants.OS_VERSION, "");
        String localMcuVersion = TPUtils.get(getContext(), SettingConstants.MCU_VERSION, "");
        versionView.setText(String.format(getString(R.string.sound_all_version), localOsVersion, localMcuVersion));
    }

    private void initView(View view) {
        versionView = view.findViewById(R.id.version);
        icciTv = view.findViewById(R.id.iccid_tv);
        imeiTv = view.findViewById(R.id.imei_tv);
    }

    private void showAskInstallDialog(String content) {
        installSDialog = new ConfirmDialog(getActivity());
        installSDialog.setCancelable(false);
        installSDialog.setTitle("安装提示")
                .setContent(content)
                .setPositiveButton("安装", new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.confirmVersionSearch})
                    @ResId({R.id.tv_left_button})
                    public void onClick(View v) {
                        XmCarUpdateManager.getInstance().confirmUpdate();
                        installSDialog.dismiss();
                    }
                })
                .setNegativeButton("下一次", new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.cancelVersionUpdate})
                    @ResId({R.id.tv_right_button})
                    public void onClick(View v) {
                        XmCarUpdateManager.getInstance().cancelUpdate();
                        installSDialog.dismiss();
                    }
                })
                .show();
    }

    private void showInstallSuccessDialog(String content) {
        if (installSDialog.isShow()) {
            installSDialog.dismiss();
        }
        installResultDialog = new ConfirmDialog(getActivity());
        installSDialog.setCancelable(false);
        installResultDialog.setTitle("安装结果提示")
                .setContent(content)
                .setPositiveButton("关闭", new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.confirmVersionSearch})
                    @ResId({R.id.tv_button})
                    public void onClick(View v) {
                        installResultDialog.dismiss();
                    }
                })
                .setNegativeButtonVisibility(false)
                .show();
    }

    private void showInstallErrorDialog(String content, String tip, int buttonVisible) {
        View view = View.inflate(getActivity(), R.layout.dialog_result_small, null);
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText("安装结果提示");
        TextView tv_content = view.findViewById(R.id.tv_content);
        tv_content.setText(content);
        TextView tv_tip = view.findViewById(R.id.tv_tip);
        tv_tip.setText(tip);
        tv_tip.setVisibility(View.VISIBLE);
        final XmDialog builder = new XmDialog.Builder(getActivity())
                .setView(view).setWidth(730).setHeight(457)
                .create();
        TextView tv_button = view.findViewById(R.id.tv_button);
        tv_button.setText("关闭");
        tv_button.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.confirmVersionSearch})
            @ResId({R.id.tv_button})
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        tv_button.setVisibility(buttonVisible);
        if (buttonVisible == View.GONE) {
            builder.setCancelable(false);
        } else {
            builder.setCancelable(true);
        }
        builder.show();
    }

    private void showUpdatingDialog(String ecuName) {
        if (mUpdatingDialog == null) {
            View view = View.inflate(getActivity(), R.layout.dialog_updating, null);
            mTvUpdateContent = view.findViewById(R.id.tv_update_content);
            mUpdatingDialog = new XmDialog.Builder(getActivity())
                    .setView(view).setWidth(788).setHeight(548)
                    .create();
            mUpdatingDialog.setCancelable(false);
        }
        mTvUpdateContent.setText("正在升级" + ecuName + "控制器");
        if (!ispdatingDialogShow) {
            mUpdatingDialog.show();
        }
    }

    @Override
    public void onUpdateRequest(String packageName, int updateTime) {
        showAskInstallDialog("控制器新软件包" + packageName + "已下载完成,预计安装时间" + updateTime + "分钟");
        this.mPackageName = packageName;
    }

    @Override
    public void onInstallSuccess(String ecuName) {
        if (mUpdatingDialog != null) {
            mUpdatingDialog.dismiss();
        }
        ispdatingDialogShow = false;
        showInstallSuccessDialog(ecuName + "控制器升级升级成功,软件包" + mPackageName + "更新完成");
    }

    @Override
    public void onInstallErrorCanUseSystem(String ecuName) {
        if (mUpdatingDialog != null) {
            mUpdatingDialog.dismiss();
        }
        ispdatingDialogShow = false;
        showInstallErrorDialog(ecuName + "控制器升级失败,软件包" + mPackageName + "更新失败", "请联系客服xxxxxxx或前往附近的4S店", View.VISIBLE);
    }

    @Override
    public void onInstallErrorNotUseSystem(String ecuName) {
        if (mUpdatingDialog != null) {
            mUpdatingDialog.dismiss();
        }
        ispdatingDialogShow = false;
        showInstallErrorDialog(ecuName + "控制器升级失败,软件包" + mPackageName + "更新失败", "请联系客服XXXXXXX或呼叫道路救援至附近4S店", View.VISIBLE);
    }

    @Override
    public void onInstallFailed(String content) {
        if (mUpdatingDialog != null) {
            mUpdatingDialog.dismiss();
        }
        ispdatingDialogShow = false;
        showInstallErrorDialog("您有一个升级任务由于" + content + "未能启动升级流程,导致没有升级成功。请在下次安装提示确认后，确认已经满足升级条件。", "如遇到问题，可联系客服xxxxxxx或前往附近的4S店", View.VISIBLE);
    }

    @Override
    public void onInstalling(String ecuName) {
        if (mUpdatingDialog != null) {
            mUpdatingDialog.dismiss();
        }
        showUpdatingDialog(ecuName);
    }

    @Override
    public void onVersionInfo(int type, String version) {
        KLog.d("hzx", "Thread: " + Thread.currentThread().getName());
        if (type == SDKConstants.OS_LOCAL_VERSION) {
            osVersion = version;
        } else if (type == SDKConstants.MCU_LOCAL_VERSION) {
            mcuVersion = version;
        }
        if (!TextUtils.isEmpty(osVersion) && !TextUtils.isEmpty(mcuVersion)) {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    handleOsVersionAndMcuVersion();
                }
            });
        }
    }

    /**
     * 处理osVersion 和 mcuVersion的保存和显示
     * 为了避免出现版本号闪一下的问题，做本地数据保存
     */
    private void handleOsVersionAndMcuVersion() {
        String localOsVersion = TPUtils.get(getContext(), SettingConstants.OS_VERSION, "");
        String localMcuVersion = TPUtils.get(getContext(), SettingConstants.MCU_VERSION, "");
        if (osVersion.equals(localOsVersion) && mcuVersion.equals(localMcuVersion)){
            return;
        }
        TPUtils.put(getContext(),SettingConstants.OS_VERSION,osVersion);
        TPUtils.put(getContext(),SettingConstants.MCU_VERSION,mcuVersion);
        versionView.setText(String.format(getString(R.string.sound_all_version), osVersion, mcuVersion));
    }

    @Override
    public void onDestroy() {
        XmCarUpdateManager.getInstance().setVersionListener(null);
        super.onDestroy();
    }
}
