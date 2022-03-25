package com.xiaoma.launcher.splash.ui.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.manager.LauncherAppManager;
import com.xiaoma.launcher.common.views.CustomVideoView;
import com.xiaoma.launcher.main.ui.MainActivity;
import com.xiaoma.launcher.schedule.manager.ScheduleDataManager;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.launcher.splash.manager.SplashVideoManager;
import com.xiaoma.launcher.splash.model.ListBean;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginMethod;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.logintype.manager.TravellerLoginType;

import java.util.List;

/**
 * Created by Thomas on 2019/2/20 0020
 * 欢迎页
 */

@PageDescComponent(EventConstants.PageDescribe.WelcomeSplashFragmentPagePathDesc)
public class WelcomeSplashFragment extends BaseFragment {

    private ImageView mIvWelCome;
    private TextView mTvWelCome;
    private ImageView mIvWelcomeBg;
    private static final int IV_HEAD_HEIGTH = 300;
    private static final int IV_HEAD_PADDING = 20;
    private TextView mSchedule;
    private CustomVideoView mWelcomeVideo;
    private ImageView mLoadingImg;
    private TextView mLoadingText;
    private String welcomeString = "";
    private static final int TIME_DELAYED = 6_000;
    private User user;

    public static WelcomeSplashFragment newInstance() {
        WelcomeSplashFragment orderFragment = new WelcomeSplashFragment();
        return orderFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);
        mIvWelCome = view.findViewById(R.id.iv_welcome);
        mIvWelcomeBg = view.findViewById(R.id.iv_welcome_bg);
        mTvWelCome = view.findViewById(R.id.tv_welcome);
        mSchedule = view.findViewById(R.id.schedule);
        mWelcomeVideo = view.findViewById(R.id.welcome_video);
        mLoadingImg = view.findViewById(R.id.loading_img);
        mLoadingText = view.findViewById(R.id.loading_text);
        try {
            user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user != null) {
            setUserName(user);
            try {
                CircleCrop circleCrop = new CircleCrop();
                ImageLoader.with(WelcomeSplashFragment.this).load(user.getPicPath())
                        .placeholder(R.drawable.icon_default_user)
                        .error(R.drawable.icon_default_user)
                        .transform(circleCrop)
                        .into(mIvWelCome);
                ImageLoader.with(WelcomeSplashFragment.this)
                        .load(R.drawable.bg_headphoto)
                        .transform(circleCrop)
                        .into(mIvWelcomeBg);
            } catch (Exception e) {
                e.printStackTrace();
            }
            setScheduleText();
            setSplashVideo(user);
            setLoadingView();
        } else {
            enterMain();
        }
        return super.onCreateWrapView(view);
    }

    private void setUserName(User user) {
        try {
            if (getContext() == null)
                return;
            String userName = null;
            if (user != null) {
                userName = user.getName();
            }
            String welcome;
            if (!TextUtils.isEmpty(welcomeString)) {
                welcome = welcomeString;
            } else {
                welcome = DateUtil.getWelcomeStrByTime(getContext());
            }
            mTvWelCome.setText(getString(R.string.welcome_str, welcome, StringUtil.optString(userName)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置开机问候语和视频
     *
     * @param user
     */
    private void setSplashVideo(User user) {
        try {
            String lineFormatDate = DateUtil.getLineFormatDate();
            List<ListBean> localVideo = SplashVideoManager.getLocalVideo();
            if (!ListUtils.isEmpty(localVideo) && StringUtil.isNotEmpty(lineFormatDate)) {
                for (ListBean item : localVideo) {
                    if (StringUtil.isNotEmpty(item.getDate()) && item.getDate().equals(lineFormatDate)) {
                        if (StringUtil.isNotEmpty(item.getGreetings())) {
                            welcomeString = item.getGreetings();
                            mTvWelCome.setText(getString(R.string.welcome_str, item.getGreetings(), user.getName()));
                        }
                        if (StringUtil.isNotEmpty(item.getMd5String())) {
                            String videoExists = SplashVideoManager.getVideoExists(item);
                            if (StringUtil.isNotEmpty(videoExists)) {
                                showVideo(videoExists);
                                return;
                            } else {
                                mWelcomeVideo.setVisibility(View.GONE);
                                enterMain();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWelcomeVideo.setVisibility(View.GONE);
        enterMain();
    }

    /**
     * 设置登录方式
     */
    private void setLoadingView() {
        try {
            String loginMethod = null;
            if (LoginManager.getInstance() != null) {
                if (LoginManager.getInstance().getLoginStatus() != null) {
                    loginMethod = LoginManager.getInstance().getLoginStatus().getLoginMethod();
                }
            }

            if (StringUtil.isNotEmpty(loginMethod)) {
                if (loginMethod.equals(LoginMethod.PASSWD.name())
                        || loginMethod.equals(LoginMethod.FACTORY.name())) {
                    mLoadingImg.setImageResource(R.drawable.icon_clock);
                    mLoadingText.setText(R.string.password_login);
                } else if (loginMethod.equals(LoginMethod.KEY_BLE.name())) {
                    mLoadingImg.setImageResource(R.drawable.icon_bluetooth);
                    mLoadingText.setText(R.string.bluetooth_key_login);
                } else if (loginMethod.equals(LoginMethod.KEY_NORMAL.name())) {
                    mLoadingImg.setImageResource(R.drawable.icon_key);
                    mLoadingText.setText(R.string.ordinary_key_login);
                } else if (loginMethod.equals(LoginMethod.FACE.name())) {
                    mLoadingImg.setImageResource(R.drawable.icon_face);
                    mLoadingText.setText(R.string.face_recognition_login);
                } else if (loginMethod.equals(LoginMethod.TOURISTS.name())) {
                    mLoadingImg.setImageResource(R.drawable.icon_tourists);
                    mLoadingText.setText(R.string.tourist_login);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放背景视频
     *
     * @param path
     */
    private void showVideo(String path) {
        mWelcomeVideo.setVideoPath(path);
        mWelcomeVideo.start();
      /*  mWelcomeVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mWelcomeVideo.stopPlayback();
                mWelcomeVideo.setVisibility(View.GONE);
                return false;
            }
        });*/

        mWelcomeVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                enterMain();
            }
        });
    }

    private void enterMain() {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                try {
                    FragmentActivity activity = getActivity();
                    if (activity == null) {
                        KLog.e("SplashActivity WelcomeSplashFragment startMain activity is null");
                        return;
                    }
                    LauncherAppManager.launcherAudioService(activity);
                    LauncherAppManager.launcherVrService(activity);
                    LauncherAppManager.launcherOtherApp(activity);
                } catch (Exception e) {
                    KLog.e("SplashActivity WelcomeSplashFragment enterMain activity is null，Exception...");
                }
            }
        });
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                startMain();
            }
        }, TIME_DELAYED);
    }

    private void startMain() {
        try {
            FragmentActivity activity = getActivity();
            if (activity == null) {
                KLog.e("SplashActivity WelcomeSplashFragment startMain activity is null");
                return;
            }
            Intent intent = new Intent(activity, MainActivity.class);
            startActivity(intent);
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e("SplashActivity WelcomeSplashFragment startMain activity is null, Exception...");
        }
    }

    /**
     * 获取时间
     */
    private void setScheduleText() {
        try {
            List<ScheduleInfo> scheduleDetailInfos = ScheduleDataManager.getLocalScheduleInfosForDate(DateUtil.getCurrentFormatDate());
            if (!LoginManager.getInstance().isUserLogin()
                    || LoginTypeManager.getInstance().getLoginType() instanceof TravellerLoginType
                    || ListUtils.isEmpty(scheduleDetailInfos)) {
                mSchedule.setVisibility(View.GONE);
            } else {
                mSchedule.setText(StringUtil.format(getString(R.string.schedule_reminder), scheduleDetailInfos.size()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
