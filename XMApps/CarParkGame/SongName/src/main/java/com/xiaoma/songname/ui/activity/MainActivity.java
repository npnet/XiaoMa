package com.xiaoma.songname.ui.activity;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.songname.R;
import com.xiaoma.songname.common.constant.SongNameConstants;
import com.xiaoma.songname.ui.fragment.AnswerFragment;
import com.xiaoma.songname.ui.fragment.PlayMusicFragment;
import com.xiaoma.utils.log.KLog;


/**
 * 猜歌名主页
 * Created by Thomas on 2018/11/6 0006
 */
public class MainActivity extends BaseActivity {

    private AnswerFragment mAnswerFragment;
    private PlayMusicFragment mPlayMusicFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        String uid = getIntent().getStringExtra(SongNameConstants.UID);
        KLog.d("initView uid:" + uid);
//        uid = "1125591760727453696";
        mAnswerFragment = AnswerFragment.newInstanceFragment(uid);
        getSupportFragmentManager().beginTransaction().replace(R.id.answer_fragment, mAnswerFragment).commit();
        mPlayMusicFragment = (PlayMusicFragment) getSupportFragmentManager().findFragmentById(R.id.play_fragment);
        mPlayMusicFragment.setAnswerPageListener(new PlayMusicFragment.NotifyAnswerPageListener() {
            @Override
            public void playStart() {
                mAnswerFragment.startPlayMusic();
            }

            @Override
            public void playStartAgain() {
                mAnswerFragment.playMusicAgain();
            }

            @Override
            public void playPause() {

            }

            @Override
            public void playEnd() {
                mAnswerFragment.onPlayMusicEnd();
            }
        });
        mAnswerFragment.setIFlushPlayView(new AnswerFragment.IFlushPlayView() {
            @Override
            public void notifyPlayState(int state) {
                mPlayMusicFragment.notifyPlayState(state, PlayMusicFragment.ENTER_ANSWER);
            }

            @Override
            public void setPlayTitle(String title) {
                mPlayMusicFragment.setTitleText(title);
            }

            @Override
            public void setPlaySource(String playSource) {
                mPlayMusicFragment.setPlaySource(playSource);
            }

            @Override
            public void setPlayBtnEnable(boolean enable) {
                mPlayMusicFragment.setPlayBtnEnable(enable);
            }

            @Override
            public void setAnswerPageState(int state) {
                mPlayMusicFragment.setAnswerPageState(state);
            }
        });
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayer mediaPlayer = mPlayMusicFragment.getMediaPlayer();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
