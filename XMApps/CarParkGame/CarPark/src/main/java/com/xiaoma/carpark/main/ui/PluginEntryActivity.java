package com.xiaoma.carpark.main.ui;

import android.os.Process;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;
import com.xiaoma.carpark.R;
import com.xiaoma.carpark.common.constant.CarParkConstants;
import com.xiaoma.carpark.main.model.XMPluginInfo;
import com.xiaoma.carpark.plugin.listener.IPluginStateListener;
import com.xiaoma.carpark.plugin.listener.PluginDownloadListener;
import com.xiaoma.carpark.plugin.manager.XMPluginManager;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.LoginManager;
import com.xiaoma.network.engine.OkGo;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import java.io.File;

/**
 * 插件入口页面
 */
public class PluginEntryActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PluginEntryActivity";

    //下载
    private static final int PLUGIN_STATE_DOWNLOAD = 1;
    //打开
    private static final int PLUGIN_STATE_OPEN = 2;
    //升级
    private static final int PLUGIN_STATE_UPDATE = 3;
    //插件状态
    private int pluginState = PLUGIN_STATE_DOWNLOAD;

    public static final String PLUGIN_INFO = "plugin_info";

    private XMPluginInfo xmPluginInfo;
    private XMPluginManager mXmPluginManager;
    private ImageView ivClose;
    private TextView tvPluginTitle;
    private TextView tvPluginSubTitle;
    private Button btnPlugin;
    private View progressView;
    private TextView tvProgressLog;
    private TextView tvPluginSize;
    private ProgressBar pluginProgress;
    private ProgressBar shadowProgress;

    private TextView tvSort;
    private DownloadTask mTask;

    public static void startActivity(Context context, XMPluginInfo pluginInfo) {
        Intent intent = new Intent(context, PluginEntryActivity.class);
        intent.putExtra(PLUGIN_INFO, pluginInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin_entry);
        initView();
        initData();
    }

    private void initView() {
        xmPluginInfo = (XMPluginInfo) getIntent().getSerializableExtra(PLUGIN_INFO);

        ivClose = findViewById(R.id.iv_close);
        tvPluginTitle = findViewById(R.id.tv_plugin_title);
        tvPluginSubTitle = findViewById(R.id.tv_plugin_sub_title);
        btnPlugin = findViewById(R.id.btn_plugin);
        progressView = findViewById(R.id.progress_layout);
        tvProgressLog = findViewById(R.id.tv_plugin_log);
        tvPluginSize = findViewById(R.id.tv_plugin_size);
        pluginProgress = findViewById(R.id.plugin_progress);
        pluginProgress.setMax(10000);
        shadowProgress = findViewById(R.id.plugin_shadow);
        shadowProgress.setMax(10000);
        tvSort = findViewById(R.id.tv_sort);

        ivClose.setOnClickListener(this);
        btnPlugin.setOnClickListener(this);
        tvSort.setOnClickListener(this);
    }

    private void initData() {
        if (xmPluginInfo == null) {
            return;
        }
        mXmPluginManager = XMPluginManager.getInstance();

        tvPluginTitle.setText(xmPluginInfo.mainTitle);
        tvPluginSubTitle.setText(xmPluginInfo.subTitle);
        String pluginName = xmPluginInfo.pluginPackageName;
        String size = xmPluginInfo.sizeFomat;
        //如果是H5游戏
        if (xmPluginInfo.reflectType == CarParkConstants.WEB) {
            pluginState = PLUGIN_STATE_OPEN;
            //打开
            btnPlugin.setText(getString(R.string.plugin_start));
            tvSort.setVisibility(View.VISIBLE);
            return;
        }

        //已经安装
        if (mXmPluginManager.isPluginInstalled(pluginName)) {
            PluginInfo pluginInfo = mXmPluginManager.getPluginInfo(pluginName);
            //更新
            int versionCode = 0;
            try {
                versionCode = Integer.parseInt(xmPluginInfo.pluginVersionCode);

            } catch (Exception e) {
                KLog.e("e:" + e);
            }
            if (versionCode > pluginInfo.getVersion()) {
                pluginState = PLUGIN_STATE_UPDATE;
                btnPlugin.setText(getString(R.string.plugin_update, size));
                tvSort.setVisibility(View.GONE);

            } else {
                pluginState = PLUGIN_STATE_OPEN;
                //打开
                btnPlugin.setText(getString(R.string.plugin_start));
                tvSort.setVisibility(View.VISIBLE);
            }

        } else {
            pluginState = PLUGIN_STATE_DOWNLOAD;
            //未安装去下载
            btnPlugin.setText(getString(R.string.plugin_download, size));
            tvSort.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if (mTask != null) {
                    mTask.remove(true);
                }
                finish();
                break;

            //插件下载,升级,打开
            case R.id.btn_plugin:
                if (!NetworkUtils.isConnected(this)) {
                    showToastException(getString(R.string.toast_network_exception));
                    return;
                }
                handlePluginPre();
                break;

            //打开插件指定页面
            case R.id.tv_sort:
                if (!NetworkUtils.isConnected(this)) {
                    showToastException(getString(R.string.toast_network_exception));
                    return;
                }
                openPluginSortPage();
                break;
        }
    }

    private void handlePluginPre() {
        switch (pluginState) {
            //下载
            case PLUGIN_STATE_DOWNLOAD:
                downloadPlugin(false);
                break;

            //打开插件主页
            case PLUGIN_STATE_OPEN:
                if (xmPluginInfo.reflectType == CarParkConstants.WEB) {
                    //跳转H5页面
                    CarPakWebActivity.start(this, xmPluginInfo.path);
                    return;
                }
                //猜方言
                if (CarParkConstants.DIALECT_PACKAGENAME.equals(xmPluginInfo.pluginPackageName)) {
                    xmPluginInfo.pluginClassName = CarParkConstants.DIALECT_MAIN_ACTIVITY;

                } else if (CarParkConstants.SONG_NAME_PACKAGENAME.equals(xmPluginInfo.pluginPackageName)) {
                    xmPluginInfo.pluginClassName = CarParkConstants.SONG_NAME_MAIN_ACTIVITY;
                }

                if (!StringUtil.isEmpty(xmPluginInfo.pluginPackageName) && !StringUtil.isEmpty(xmPluginInfo.pluginClassName)) {
                    mXmPluginManager.startRePluginActivity(this, xmPluginInfo.pluginPackageName,
                            xmPluginInfo.pluginClassName, LoginManager.getInstance().getLoginUserId());
                }
                break;

            //升级
            case PLUGIN_STATE_UPDATE:
                downloadPlugin(true);
                break;
        }
    }

    /**
     * 下载插件
     *
     * @param isUpdate 是否升级下载
     */
    private void downloadPlugin(boolean isUpdate) {
        mTask = OkDownload.request(xmPluginInfo.pluginPackageName, OkGo.<File>get(xmPluginInfo.pluginUrl))
                .extra1(xmPluginInfo)
                .extra2(isUpdate)
                .save()
                .register(new XMPluginDownloadListener(xmPluginInfo.pluginPackageName));
        mTask.start();
    }

    private void openPluginSortPage() {
        if (xmPluginInfo.reflectType == CarParkConstants.WEB) {
            CarPakWebActivity.start(this, xmPluginInfo.path);

        } else {
            //猜歌名排行榜页面
            if (CarParkConstants.SONG_NAME_PACKAGENAME.equals(xmPluginInfo.pluginPackageName)) {
                xmPluginInfo.pluginClassName = CarParkConstants.SONG_NAME_SORT_ACTIVITY;
                mXmPluginManager.startRePluginActivity(this, xmPluginInfo.pluginPackageName,
                        xmPluginInfo.pluginClassName, LoginManager.getInstance().getLoginUserId());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTask != null) {
            mTask.remove();
        }
    }

    /**
     * 插件下载监听
     */
    public class XMPluginDownloadListener extends PluginDownloadListener {
        XMPluginDownloadListener(Object tag) {
            super(tag);
        }

        @Override
        public void onStart(final Progress progress) {
            super.onStart(progress);
            Log.d(TAG, "Download onStart");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnPlugin.setVisibility(View.GONE);
                    progressView.setVisibility(View.VISIBLE);
                    tvPluginSize.setText(xmPluginInfo.sizeFomat);
                    if (progress.extra2 != null && (boolean) progress.extra2) {
                        tvProgressLog.setText(getString(R.string.plugin_updating));

                    } else {
                        tvProgressLog.setText(getString(R.string.plugin_downloading));
                    }
                }
            });
        }

        @Override
        public void onProgress(final Progress progress) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pluginProgress.setProgress((int) (progress.fraction * 10000));
                    shadowProgress.setProgress((int) (progress.fraction * 10000));
                }
            });
        }

        @Override
        public void onFinish(File file, Progress progress) {
            Log.d(TAG, "Download onFinish");
            //下载完成自动安装插件
            mXmPluginManager.installPlugin(progress.filePath, new XMPluginLoadListener((boolean) progress.extra2));
        }

        @Override
        public void onError(Progress progress) {
            super.onError(progress);
            btnPlugin.setText(getString(R.string.plugin_re_download));
            btnPlugin.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.GONE);
            showToastException(getString(R.string.toast_network_exception));
        }
    }

    /**
     * 插件安装监听
     */
    public class XMPluginLoadListener implements IPluginStateListener {
        private boolean mIsUpdate;

        private XMPluginLoadListener(boolean isUpdate) {
            this.mIsUpdate = isUpdate;
        }

        @Override
        public void onStart(PluginInfo pluginInfo) {
            Log.d(TAG, "Plugin onStart");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvProgressLog.setText(getString(R.string.plugin_installing));
                }
            });
        }

        @Override
        public void onSuccess(final PluginInfo pluginInfo) {
            Log.d(TAG, "Plugin onSuccess");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //更新插件状态
                    pluginState = PLUGIN_STATE_OPEN;
                    //更新视图
                    btnPlugin.setVisibility(View.VISIBLE);
                    btnPlugin.setText(getString(R.string.plugin_start));
                    tvSort.setVisibility(View.VISIBLE);
                    progressView.setVisibility(View.GONE);
                    //如果插件正在运行,需要重启
                    if (mIsUpdate && RePlugin.isPluginRunning(pluginInfo.getName())) {
                        showRestartDialog();
                    }
                }
            });

        }

        @Override
        public void onFail(PluginInfo pluginInfo) {
            Log.d(TAG, "Plugin onFail");
        }

        private void showRestartDialog() {
            View view = View.inflate(PluginEntryActivity.this, R.layout.dialog_update_restart, null);
            final XmDialog builder = new XmDialog.Builder(getSupportFragmentManager())
                    .setView(view).setWidth(560)
                    .setHeight(300)
                    .setCancelableOutside(false)
                    .create();
            builder.setCancelable(false);
            view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Process.killProcess(Process.myPid());
                    builder.dismiss();
                }
            });
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }
}
