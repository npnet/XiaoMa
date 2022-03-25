package com.xiaoma.dualscreen.eol;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.views.BaseView;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/16
 */
public class EOLView extends BaseView {

    private Context mContext;
    private ImageView mShowPicIV;

    public EOLView(Context context) {
        this(context, null);
    }

    public EOLView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        mShowPicIV = findViewById(R.id.ivShowPic);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public int contentViewId() {
        return R.layout.view_eol;
    }
}
