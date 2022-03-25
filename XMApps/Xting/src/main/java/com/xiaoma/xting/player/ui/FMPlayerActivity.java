package com.xiaoma.xting.player.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.vr.dispatch.annotation.Command;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.XtingNodeHelper;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.player.ui.fragment.PlayerDetailsFragment;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/9
 */
public class FMPlayerActivity extends BaseActivity {
    private NewGuide newGuide;
    public static final String ARG_ACTION = "FROM_XTING";
    private boolean mLaunchFromOutSideF = false;

    public static void launch(Context context) {
        Intent intent = new Intent(context, FMPlayerActivity.class);
        intent.putExtra(ARG_ACTION, true);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        handIntent(getIntent());
        showLastGuide();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handIntent(intent);
    }

    private void replaceFragment(BaseFragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.view_content, fragment)
                .commitAllowingStateLoss();
    }

    public void addFragment(BaseFragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .hide(getSupportFragmentManager().findFragmentById(R.id.view_content))
                .add(R.id.view_content, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    public void goBackToMainActivity() {
        if (mLaunchFromOutSideF) {
            PrintInfo.print("FM_ACTIVITY", "JUMPING");
            //跳转桌面太慢,所以加点状态
//            showProgressDialog(R.string.back_to_launcher);
            ComponentName componentName = new ComponentName("com.xiaoma.launcher", "com.xiaoma.launcher.main.ui.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(componentName);
            startActivity(intent);
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    dismissProgress();
                    PrintInfo.print("FM_ACTIVITY", "FINISH");
                    finish();
                    return false;
                }
            });
        } else {
            finish();
        }
    }

    private void handIntent(Intent intent) {
        if (intent != null) {
            mLaunchFromOutSideF = !(intent.getBooleanExtra(ARG_ACTION, false));
        }
        replaceFragment(PlayerDetailsFragment.newInstance());
    }

    private void showLastGuide() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.XTING_SHOWED, GuideConstants.XTING_GUIDE_FIRST, false))
            return;
        newGuide = NewGuide.with(this)
                .setLebal(GuideConstants.XTING_SHOWED)
                .build();
        newGuide.showLastGuide();
    }

    @Override
    public String getThisNode() {
        return NodeConst.Xting.ACT_PLAYER;
    }

    @Override
    public boolean handleJump(String nextNode) {
        super.handleJump(nextNode);
        Log.d("VoiceTest", "handleJump: " + nextNode);
        switch (nextNode) {
            case NodeConst.Xting.FGT_PLAYER_ONLINE_DETAILS:
//                replaceFragment(PlayerDetailsFragment.newInstance());
                mLaunchFromOutSideF = false;
                return true;
            default:

                return false;
        }
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
    public void popPlayList() {
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

}
