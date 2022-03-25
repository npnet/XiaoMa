package com.xiaoma.music.player.view.player;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.xiaoma.music.R;
import com.xiaoma.ui.view.ReflectionImageView;

/**
 * @author zs
 * @date 2018/10/11 0011.
 */
public class ScrollImageView extends ReflectionImageView {

    private static final String TAG = "ScrollImageView";

    private Context mContext;

    public ScrollImageView(Context context) {
        this(context, null);
    }

    public ScrollImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    private void initView() {
        inflate(mContext, R.layout.view_scorll_image, null);
    }

}
