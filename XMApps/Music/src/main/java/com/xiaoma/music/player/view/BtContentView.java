package com.xiaoma.music.player.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.music.R;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.player.view.player.ProgressView;
import com.xiaoma.ui.view.AutoScrollTextView;

/**
 * @author zs
 * @date 2018/11/23 0023.
 */
public class BtContentView extends RelativeLayout {

    private ProgressView mProgressView;
    private AutoScrollTextView songTitle;
    private TextView artist;
    @PlayStatus
    private int mPlayStatus = PlayStatus.STOP;

    public BtContentView(Context context) {
        this(context, null);
    }

    public BtContentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BtContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_content_bt, this);
        mProgressView = findViewById(R.id.view_bt_progress_view);
        songTitle = findViewById(R.id.bt_content_song_tv);
        artist = findViewById(R.id.bt_content_artist_name);
        mProgressView.seekEnable(false);
        mProgressView.setVisibility(INVISIBLE);
    }

    public void refreshView(BTMusic btMusic) {
        mProgressView.setMax(btMusic.getDuration());
        if (btMusic.getCurrentProgress() != 0) {
            mProgressView.setProgress(btMusic.getCurrentProgress());
        }
        songTitle.setText(btMusic.getTitle());
        if (!TextUtils.isEmpty(btMusic.getArtist())) {
            artist.setText(btMusic.getArtist());
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
            songTitle.startMarquee();
        } else {
            songTitle.stopMarquee();
        }
    }

}
