package com.xiaoma.xting;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.logintype.callback.AbsClearDataListener;
import com.xiaoma.utils.logintype.receiver.CleanDataBroadcastReceiver;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.dispatch.annotation.Command;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingNodeHelper;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.local.model.AMChannelBean;
import com.xiaoma.xting.local.model.BaseChannelBean;
import com.xiaoma.xting.local.model.FMChannelBean;
import com.xiaoma.xting.local.ui.ManualFragment;
import com.xiaoma.xting.player.ui.FMPlayerActivity;
import com.xiaoma.xting.welcome.consract.FirstInAppStatus;
import com.xiaoma.xting.welcome.ui.SplashActivity;
import com.xiaoma.xting.welcome.vm.PreferenceVM;

/**
 * Created by youthyj on 2018/9/5.
 */
@PageDescComponent(EventConstants.PageDescribe.ACTIVITY_MAIN)
public class MainActivity extends BaseActivity {
    private static final String FRAGMENT_TAG_MANUAL = "FRAGMENT_TAG_MANUAL";
    private static final String FRAGMENT_TAG_HOME = "FRAGMENT_TAG_HOME";
    private AbsClearDataListener mAbsClearDataListener;
    private CleanDataBroadcastReceiver mCleanDataBroadcastReceiver;

    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().showBackAndHomeNavi();
        replaceFragment(HomeFragment.newInstance(), FRAGMENT_TAG_HOME);
        checkInAppPage();
//        registerClearDataListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
//        WheelCarControlHelper.newSingleton().register();
    }

    @Override
    protected void onDestroy() {

//        unRegisterClearDataListener();
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (BackHandlerHelper.handleBackPress(this)) {
            getNaviBar().showBackAndHomeNavi();
            return;
        }
        if (PlayerSourceFacade.newSingleton().getSourceType() == PlayerSourceType.DEFAULT
                || !PlayerSourceFacade.newSingleton().getPlayerControl().isPlaying()) {
            moveTaskToBack(true);
            return;
        }
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.view_content);
        if (fragment instanceof HomeFragment) {
            ConfirmDialog dialog = new ConfirmDialog(this);
            dialog.setContent(getString(R.string.dialog_back_msg))
                    .setPositiveButton(getString(R.string.dialog_home), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            moveTaskToBack(true);
                        }
                    })
                    .setNegativeButton(getString(R.string.dialog_back), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            PlayerSourceFacade.newSingleton().getPlayerControl().pause();
                            moveTaskToBack(true);
                        }
                    })
                    .show();
        }
    }

    public void launchManualTune() {
        addBackStackFragment(ManualFragment.newInstance(), FRAGMENT_TAG_MANUAL);
        getNaviBar().showBackNavi();
    }

    private void addBackStackFragment(BaseFragment fragment, String tag) {
        FragmentUtils.add(
                getSupportFragmentManager(),
                fragment,
                R.id.view_content,
                tag,
                true,
                0, 0, 0, 0
//                R.anim.transitions_enter_from_left,
//                R.anim.transitions_out_to_left,
//                R.anim.transitions_enter_from_left,
//                R.anim.transitions_out_to_left
        );
    }

    private void replaceFragment(BaseFragment fragment, String tag) {
        FragmentUtils.replace(
                getSupportFragmentManager(),
                fragment,
                R.id.view_content,
                tag,
                false
        );
    }

    private void checkInAppPage() {
        if (XtingConstants.SUPPORT_ONLINE_FM) {
            Integer firstInAppStatus = TPUtils.get(this,
                    XtingConstants.TP_FIRST_START_APP,
                    FirstInAppStatus.PREFERENCE_PAGE_FIRST);
            if (firstInAppStatus == FirstInAppStatus.FM_PAGE_FIRST) {
                // do nothing?
                Intent intent = getIntent();
                if (intent != null) {
                    Bundle bundle = intent.getBundleExtra("bundle");
                    if (bundle != null) {
                        String info = bundle.getString("info");
                        if (!TextUtils.isEmpty(info)) {
                            String typeStr = info.substring(0, 1);
                            BaseChannelBean baseChannelBean = null;
                            if (Integer.valueOf(typeStr) == 0) {
                                baseChannelBean = GsonHelper.fromJson(info.substring(1), FMChannelBean.class);
                            } else {
                                baseChannelBean = GsonHelper.fromJson(info.substring(1), AMChannelBean.class);
                            }
                            PlayerInfoImpl.newSingleton().onPlayerInfoChanged(BeanConverter.toPlayerInfo(baseChannelBean));
                            FMPlayerActivity.launch(this);
                        }
                    }
                }
            } else if (firstInAppStatus == FirstInAppStatus.AUTO_UPDATE_PREFERENCE) {
                PreferenceVM preferenceVM = ViewModelProviders.of(this)
                        .get(PreferenceVM.class);
                preferenceVM.sendSelectedPreferences(preferenceVM.getSelectedTags());
            } else {
                SplashActivity.launch(this);
            }
        }
    }


    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        switch (nextNode) {
            case NodeConst.Xting.FGT_MANUAL:
                launchManualTune();
                return true;
            case NodeConst.Xting.FGT_HOME:
                replaceFragment(HomeFragment.newInstance(), FRAGMENT_TAG_HOME);
                return true;
            default:
                return false;
        }
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.ACT_MAIN;
    }

    @Command("(打开(我的)?|我的)收藏")
    public void showMyCollect(String voiceCmd) {
        Log.d("VoiceTest", "{showMyCollect}-[voiceCmd] : " + voiceCmd);
        XtingNodeHelper.jump2MyCollect(this);
    }

    @Command("(打开)?(节目分类)(界面)?")
    public void showCategory(String voiceCmd) {
        Log.d("VoiceTest", "{showCategory}-[voiceCmd] : " + voiceCmd);
        XtingNodeHelper.jump2Category(this);
    }

    @Command("打开播放列表")
    public void popoutPlayList() {
        XtingNodeHelper.popoutPlayList(this);
    }

    @Command("(（这是|现在放的是|正在播放）什么歌)|(这歌叫什么(名字)?)|(这是谁(唱的)|(的歌))|(打开听歌识曲)")
    public void listenToRecognize() {
        XtingNodeHelper.openListenToRecognize(this);
    }

    @Command("关闭播放列表")
    public void closePlayerList() {
        XtingNodeHelper.closePlayList(this);
    }

   /* private void registerClearDataListener() {
        mCleanDataBroadcastReceiver = new CleanDataBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginTypeConstant.BROADCAST_ACTION_SWITCH_USER_CLEAR);
        registerReceiver(mCleanDataBroadcastReceiver, intentFilter);
        LoginTypeConstant.isInitiativeClose = false;
        LoginTypeManager.getInstance().registerClearDataListener(mAbsClearDataListener = new AbsClearDataListener() {
            @Override
            public void onSwitchUserClear() {
                Xting.onSwitchUserClear(getApplication());
            }

            @Override
            public void clearAllData(String userId) {
                XtingUtils.clearHistory();
                Xting.clearAllData(getApplication(), userId);
            }

            @Override
            public void closeProcess() {
                XtingUtils.clearHistory();
                LoginTypeConstant.isInitiativeClose = true;
                Intent intent = new Intent(MainActivity.this, LauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.this.startActivity(intent);
            }
        }, getApplication(), true, true);
    }

   private void unRegisterClearDataListener() {
        if (LoginTypeConstant.isInitiativeClose) {
            OnlineFMPlayerFactory.getPlayer().pause();
            OnlineFMPlayerFactory.getPlayer().resetPlayList();
            LocalFMFactory.getSDK().closeRadio();
            KoalaPlayer.newSingleton().pause();
            KoalaPlayer.newSingleton().clearPlayerList();
            Xting.clearAllData(getApplicationContext(), LoginManager.getInstance().getLoginUserId());
        }
        LoginTypeManager.getInstance().unRegisterClearDataListener(mAbsClearDataListener);
        unregisterReceiver(mCleanDataBroadcastReceiver);
    }*/

}

