package com.xiaoma.music.player.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.music.R;
import com.xiaoma.music.UsbMusicFactory;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.manager.UsbPlayerProxy;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.player.view.player.ProgressView;
import com.xiaoma.ui.view.AutoScrollTextView;

/**
 * @author zs
 * @date 2018/11/23 0023.
 */
public class UsbContentView extends RelativeLayout {

    private AutoScrollTextView mSongNameTv;
    private ProgressView mProgressView;
    @PlayStatus
    private int mPlayStatus = PlayStatus.STOP;
    private TextView mArtistTv;

    public UsbContentView(Context context) {
        this(context, null);
    }

    public UsbContentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UsbContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_content_usb, this);
        mSongNameTv = findViewById(R.id.usb_content_song_tv);
        mProgressView = findViewById(R.id.usb_content_progress_view);
        mArtistTv = findViewById(R.id.play_content_album_name);
        mProgressView.setSeekBarProgressChangeListener(new ProgressView.OnSeekBarProgressChangeListener() {
            @Override
            public void onSeekBarToProgress(int progress) {
                UsbPlayerProxy.getInstance().seekToPos(progress);
            }
        });
    }

    public void refreshView(UsbMusic usbMusic) {
        try {
            if (usbMusic == null) {
                return;
            }
            mSongNameTv.setText(usbMusic.getName());
            mProgressView.setMax(UsbMusicFactory.getUsbPlayerProxy().getDuration());
            mProgressView.setProgress(UsbMusicFactory.getUsbPlayerProxy().getCurPosition());
            mArtistTv.setText(usbMusic.getArtist());
        } catch (Exception e) {
            e.printStackTrace();
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
            mSongNameTv.startMarquee();
        } else {
            mSongNameTv.stopMarquee();
        }
    }

}
