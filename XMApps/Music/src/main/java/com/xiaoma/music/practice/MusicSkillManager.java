package com.xiaoma.music.practice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.music.practice
 *  @file_name:      MusicSkillManager
 *  @author:         Rookie
 *  @create_time:    2019/6/20 19:28
 *  @description：   TODO             */

import android.content.Context;

import com.xiaoma.model.pratice.PlayMusicBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.vr.skill.SkillManager;
import com.xiaoma.vr.skill.client.SkillCallback;
import com.xiaoma.vr.skill.client.SkillDispatcher;
import com.xiaoma.vr.skill.client.SkillHandler;
import com.xiaoma.vr.skill.logic.ExecResult;
import com.xiaoma.vr.skill.model.Skill;

public class MusicSkillManager {
    private Context mContext;

    private MusicSkillManager() {

    }

    public static MusicSkillManager getInstance() {
        return MusicSkillManagerHolder.instance;
    }

    private static class MusicSkillManagerHolder {
        static final MusicSkillManager instance = new MusicSkillManager();
    }

    public void init(Context context) {
        mContext = context;
        SkillManager.getInstance().init(context);
        SkillDispatcher.getInstance().addSkillHandler(new SkillHandler() {
            @Override
            public boolean onSkill(String command, Skill skill, SkillCallback callback) {

                if (VrPracticeConstants.SKILL_MUSIC.equals(skill.getSkillDesc())) {
                    PlayMusicBean playMusicBean = GsonHelper.fromJson(skill.getExtra(), PlayMusicBean.class);
                    if (playMusicBean == null) {
                        return true;
                    }
                    playMusic(playMusicBean);
                    ExecResult execResult = new ExecResult("play music", VrPracticeConstants.SKILL_SUCCESS);
                    callback.onExec(execResult);
                    return true;
                }
                return false;
            }
        });
    }

    private void playMusic(PlayMusicBean playMusicBean) {
        ThreadDispatcher.getDispatcher().post(new Runnable() {
            @Override
            public void run() {
                OnlineMusicFactory.getKWAudioFetch().fetchMusicById(playMusicBean.getSongId(),
                        new PlayAfterSuccessFetchListener<XMMusic>() {
                            @Override
                            public void onFetchSuccess(XMMusic xmMusic) {
                                if (xmMusic == null) {
                                    return;
                                }
                                OnlineMusicFactory.getKWPlayer().play(xmMusic);
                                AudioShareManager.getInstance().shareKwAudioDataSourceChanged();
                                KwPlayInfoManager.getInstance().setCurrentPlayInfo(xmMusic.getSDKBean().rid
                                        + xmMusic.getSDKBean().name, KwPlayInfoManager.AlbumType.ASSISTANT);
                            }

                            @Override
                            public void onFetchFailed(String msg) {
                                if (mContext != null) {
                                    XMToast.toastException(mContext, R.string.net_error);
                                }
                            }
                        });
            }
        });
    }
}
