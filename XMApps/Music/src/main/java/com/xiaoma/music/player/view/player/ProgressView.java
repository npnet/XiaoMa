package com.xiaoma.music.player.view.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.music.R;
import com.xiaoma.utils.TimeUtils;

/**
 * @author zs
 * @date 2018/10/11 0011.
 */
public class ProgressView extends RelativeLayout implements XmMusicSeekBar.onSeekListener {

    private Context mContext;
    private XmMusicSeekBar mSeekBar;
    private TextView tvTimeStart;
    private TextView tvTimeEnd;
    private long maxProgress;
    private boolean mIsSeekBarControl;

    public ProgressView(Context context) {
        this(context, null);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        inflate(mContext, R.layout.view_progress, this);
        mSeekBar = findViewById(R.id.view_progress_seekbar);
        tvTimeStart = findViewById(R.id.time_start);
        tvTimeEnd = findViewById(R.id.time_end);

        mSeekBar.setOnSeekListener(this);
    }

    public void seekEnable(boolean enable) {
        if (mSeekBar != null) {
            mSeekBar.setSeekEnable(enable);
        }
    }

    public void setCurrTime(long time) {
        if (!mIsSeekBarControl) {
            tvTimeStart.setText(TimeUtils.timeMsToMMSS(time));
        }
    }

    public void setTotalTime(long time) {
        tvTimeEnd.setText(TimeUtils.timeMsToMMSS(time));
    }

    public void setProgress(long progress) {
        if ((maxProgress - progress) < 1000 && (maxProgress - progress) >= 500) {
            progress += 500;
        }
        mSeekBar.setProgress(Long.valueOf(progress).intValue());
        setCurrTime(progress);
    }

    public void setMax(long progress) {
        mSeekBar.setMax(Long.valueOf(progress).intValue());
        setTotalTime(progress);
        maxProgress = progress;
    }

    private OnSeekBarProgressChangeListener listener;

    public void setSeekBarProgressChangeListener(OnSeekBarProgressChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSyncSeek(int progress, boolean isFromUser) {
        if (isFromUser) {
            tvTimeStart.setText(TimeUtils.timeMsToMMSS(progress));
        }
    }

    @Override
    public void onOneSeekDone(int progress, boolean isFromUser) {
        if (isFromUser && listener != null) {
            listener.onSeekBarToProgress(progress);
            mIsSeekBarControl = false;
        }
    }

    @Override
    public void onControl(boolean seek) {
        mIsSeekBarControl = seek;
    }

    public interface OnSeekBarProgressChangeListener {
        void onSeekBarToProgress(int progress);
    }

}
