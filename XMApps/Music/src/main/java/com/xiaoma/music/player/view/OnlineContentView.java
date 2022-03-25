package com.xiaoma.music.player.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.music.player.model.MusicQualityModel;
import com.xiaoma.music.player.view.player.ProgressView;
import com.xiaoma.music.player.view.player.QualityView;
import com.xiaoma.ui.view.AutoScrollTextView;

import cn.kuwo.mod.playcontrol.PlayMode;

import static com.xiaoma.music.common.constant.EventConstants.NormalClick.seekBar;
import static com.xiaoma.music.common.constant.EventConstants.PageDescribe.onlinePlayFragment;

/**
 * @author zs
 * @date 2018/11/23 0023.
 */
public class OnlineContentView extends RelativeLayout {

    private static String TAG="[OnlineContentView]";
    private AutoScrollTextView mSongName;
    private TextView mAlbumName;
    private ProgressView mProgressView;
    private QualityView qualityView;
    private PopupWindow window;
    String musicName;
    private View vipView;
    @PlayStatus
    private int mPlayStatus = PlayStatus.STOP;

    public OnlineContentView(Context context) {
        this(context, null);
    }

    public OnlineContentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OnlineContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_content_online, this);
        mSongName = findViewById(R.id.play_content_song_name);
        mAlbumName = findViewById(R.id.play_content_album_name);
        mProgressView = findViewById(R.id.fragment_content_view);
        qualityView = findViewById(R.id.online_music_quality_view);
        mProgressView.setSeekBarProgressChangeListener(new ProgressView.OnSeekBarProgressChangeListener() {
            @Override
            public void onSeekBarToProgress(int progress) {
                int playMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
                XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
                int size = nowPlayingList.toList().size();
                int nowPlayMusicIndex = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
                int duration = OnlineMusicFactory.getKWPlayer().getDuration();
                if (PlayMode.MODE_ALL_ORDER == playMode && nowPlayMusicIndex == size - 1 && progress == duration) {
                    OnlineMusicFactory.getKWPlayer().seek(progress - 800);
                    return;
                }
                OnlineMusicFactory.getKWPlayer().seek(progress);
                XmAutoTracker.getInstance().onEvent(seekBar, musicName, "OnlinePlayFragment", onlinePlayFragment);
            }
        });
        initVipView();
    }

    public void refreshView(XMMusic xmMusic) {
        if (xmMusic == null) {
            return;
        }
        mSongName.stopMarquee();
        mSongName.setText(xmMusic.getName());
        mAlbumName.setText(xmMusic.getArtist());
        long duration=OnlineMusicFactory.getKWPlayer().getDuration();
        mProgressView.setMax(duration);
        mProgressView.setProgress(OnlineMusicFactory.getKWPlayer().getCurrentPos());
        musicName = xmMusic.getName();
        if (PlayStatus.PLAYING == mPlayStatus && duration>0) {
            mSongName.startMarquee();
        }
    }

    public void updateProgress(long progressInMs) {
        mProgressView.setProgress(progressInMs);
    }

    public void updatePlayStatus(@PlayStatus int status) {
        if (mPlayStatus == status) {
            return;
        }
        mPlayStatus = status;
        if (PlayStatus.PLAYING == status) {
            mSongName.startMarquee();
        } else {
            mSongName.stopMarquee();
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (vipView != null) {
            //用GONE有问题
            vipView.setVisibility(visibility != VISIBLE ? View.INVISIBLE : View.VISIBLE);
        }
    }

    public void setQualityViewData(MusicQualityModel model, boolean isNeedBuyVip) {
        if (qualityView != null) {
            qualityView.setEnable(!model.isRadio());
            qualityView.setData(getContext().getString(model.getQualityText()), isNeedBuyVip);
            showBuyVipView(isNeedBuyVip);
        }
    }

    public void setOnQualityClickListener(OnClickListener listener) {
        if (qualityView != null) {
            qualityView.setOnTextClickListener(listener);
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
                int y = qualityView.getLocationOnScreen()[1] + offset * 4;
                window.showAtLocation(vipView, Gravity.START | Gravity.BOTTOM, x, y);
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
        //防止Aspect会响应两次点击，以这种方式将点击事件传递出去
        vipView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.OnVipViewClick();
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            window.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OnVipViewClickListener clickListener;

    public void setOnVipViewClickListener(OnVipViewClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnVipViewClickListener {
        void OnVipViewClick();
    }

}
