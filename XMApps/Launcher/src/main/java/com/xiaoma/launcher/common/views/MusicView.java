package com.xiaoma.launcher.common.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xiaoma.launcher.R;
import com.xiaoma.ui.view.MarqueeTextView;

/**
 * @author taojin
 * @date 2019/1/2
 */
public class MusicView extends FrameLayout {

    private ImageView ivMusic;
    private ImageView ivPrevious;
    private ImageView ivNext;
    private ImageView ivPlayPause;
    private MarqueeTextView marqueeTextView;

    public MusicView(@NonNull Context context) {
        super(context);
        initView();
    }

    public MusicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.view_music, this);
        ivMusic = findViewById(R.id.iv_music);
        ivPrevious = findViewById(R.id.iv_music_previous);
        ivNext = findViewById(R.id.iv_music_next);
        ivPlayPause = findViewById(R.id.iv_music_play_pause);
        marqueeTextView = findViewById(R.id.tv_music_tip);


    }
}
