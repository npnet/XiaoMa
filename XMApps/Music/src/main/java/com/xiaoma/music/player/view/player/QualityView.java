package com.xiaoma.music.player.view.player;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.music.R;

/**
 * Author: loren
 * Date: 2019/6/29 0029
 */
public class QualityView extends LinearLayout {

    private TextView quality_tv;
    private TextView vip_tv;

    public QualityView(Context context) {
        this(context, null);
    }

    public QualityView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QualityView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public QualityView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.view_quality, this);
        quality_tv = view.findViewById(R.id.quality_view_quality_tv);
        vip_tv = view.findViewById(R.id.quality_view_vip_tv);
    }

    public void setEnable(boolean enable) {
        quality_tv.setTextColor(enable ? getContext().getColor(R.color.search_key_word) : getContext().getColor(R.color.quality_text_bg_disable));
        quality_tv.setBackground(enable ? getContext().getDrawable(R.drawable.music_quality_text_selector) : getContext().getDrawable(R.drawable.music_quality_text_disable));
        quality_tv.setClickable(enable);
    }

    public void setOnTextClickListener(OnClickListener listener) {
        if (quality_tv != null) {
            quality_tv.setOnClickListener(listener);
        }
    }

    public void setData(String quality, boolean isVip) {
        quality_tv.setText(quality);
        vip_tv.setVisibility(isVip ? VISIBLE : INVISIBLE);
    }
}
