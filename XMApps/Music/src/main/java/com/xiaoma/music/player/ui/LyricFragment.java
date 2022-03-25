package com.xiaoma.music.player.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.kuwo.listener.OnPlayControlListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.player.model.MusicQualityModel;
import com.xiaoma.music.player.view.lyric.LrcView;
import com.xiaoma.music.player.view.lyric.LyricParser;
import com.xiaoma.music.player.view.player.QualityView;
import com.xiaoma.music.player.vm.LyricVM;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * @author zs
 * @date 2018/10/11 0011.
 */
public class LyricFragment extends BaseFragment {

    private LrcView lrcView;
    private LyricVM lyricVM;
    private TextView musicName;
    private QualityView qualityView;
    private PopupWindow window;
    private View vipView;
    private boolean emptyLyric = false;


    public static LyricFragment newInstance() {
        return new LyricFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lyric, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initEvent();
        initVM();
        initVipView();
    }

    private void initVM() {
        lyricVM = ViewModelProviders.of(this).get(LyricVM.class);
        lyricVM.getLyric().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                lrcView.loadLrc(LyricParser.parseLyrics(s));
                emptyLyric = TextUtils.isEmpty(s);
            }
        });
        fetchLyric();
        lyricVM.getCurrentQualityModels().observe(this, new Observer<MusicQualityModel>() {
            @Override
            public void onChanged(@Nullable MusicQualityModel model) {
                if (model != null && model.getQualityText() != 0) {
                    boolean isNeedBuyVip = model.isNeedVip() && !OnlineMusicFactory.getKWLogin().isCarVipUser();
                    setQualityViewData(model, isNeedBuyVip);
                }
            }
        });
        lyricVM.fetchCurrentQuality(OnlineMusicFactory.getKWPlayer().getDownloadWhenPlayQuality());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden && emptyLyric) {
            fetchLyric();
        }
        if (vipView != null) {
            vipView.setVisibility(hidden ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private void fetchLyric() {
        XMMusic music = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
        if (music != null) {
            lyricVM.getCurrLyric(music);
            lrcView.updateTime(OnlineMusicFactory.getKWPlayer().getCurrentPos());
            musicName.setText(music.getName());
        }
    }

    private void initEvent() {
        lrcView.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                OnlineMusicFactory.getKWPlayer().seek((int) time);
                return true;
            }
        });
        OnlineMusicFactory.getKWMessage().addPlayStateListener(listener);
    }


    private void initView(View view) {
        lrcView = view.findViewById(R.id.lrc_view);
        musicName = view.findViewById(R.id.music_name);
        qualityView = view.findViewById(R.id.online_music_quality_view_lrc);
        qualityView.setOnTextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onQualityClick();
                }
            }
        });
    }

    private OnPlayControlListener listener = new OnPlayControlListener() {

        @Override
        public void onPreStart(final XMMusic music) {
            if (XMMusic.isEmpty(music)) {
                return;
            }
            lyricVM.getCurrLyric(music);
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    musicName.setText(music.getName());
                }
            });
        }

        @Override
        public void onReadyPlay(XMMusic music) {

        }

        @Override
        public void onBufferStart() {

        }

        @Override
        public void onBufferFinish() {

        }

        @Override
        public void onPlay(XMMusic music) {
            if (XMMusic.isEmpty(music)) {
                return;
            }
            lyricVM.getCurrLyric(music);
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    musicName.setText(music.getName());
                }
            });
        }

        @Override
        public void onSeekSuccess(int position) {
            if (!OnlineMusicFactory.getKWPlayer().isPlaying()) {
                OnlineMusicFactory.getKWPlayer().continuePlay();
            }
        }

        @Override
        public void onPause() {

        }

        @Override
        public void onProgressChange(long progressInMs, long totalInMs) {
            lrcView.updateTime(progressInMs);
        }

        @Override
        public void onPlayFailed(int errorCode, String errorMsg) {

        }

        @Override
        public void onPlayStop() {

        }

        @Override
        public void onPlayModeChanged(int playMode) {

        }

        @Override
        public void onCurrentPlayListChanged() {

        }
    };

    public void setQualityViewData(MusicQualityModel model, boolean isNeedBuyVip) {
        if (qualityView != null) {
            qualityView.setEnable(!model.isRadio());
            qualityView.setData(getString(model.getQualityText()), isNeedBuyVip);
            showBuyVipView(isNeedBuyVip);
        }
    }

    public void showBuyVipView(boolean isShow) {
        try {
            if (window == null || vipView == null) {
                return;
            }
            if (isShow) {
                if (vipView.isAttachedToWindow()) {
                    return;
                }
                int offset = getContext().getResources().getDimensionPixelOffset(R.dimen.thumb_vip_tag_height);
                int x = qualityView.getLocationOnScreen()[0];
                int y = qualityView.getLocationOnScreen()[1] - offset * 4;
                window.showAtLocation(vipView, Gravity.START | Gravity.TOP, x, y);
            } else {
                if (vipView.isAttachedToWindow()) {
                    window.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVipView() {
        window = new PopupWindow(getContext());
        vipView = LayoutInflater.from(getContext()).inflate(R.layout.view_buy_vip_guide, null);
        window.setContentView(vipView);
        window.setBackgroundDrawable(new BitmapDrawable());
        vipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.OnBuyVipViewClick();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        OnlineMusicFactory.getKWMessage().removePlayStateListener(listener);
        try {
            window.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OnChildClickListener clickListener;

    public void setOnChildClickListener(OnChildClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnChildClickListener {
        void onQualityClick();

        void OnBuyVipViewClick();

    }
}
