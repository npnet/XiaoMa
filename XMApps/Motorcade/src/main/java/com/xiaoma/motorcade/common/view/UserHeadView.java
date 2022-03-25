package com.xiaoma.motorcade.common.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.image.transformation.CircleTransform;
import com.xiaoma.motorcade.R;

/**
 * Author: loren
 * Date: 2018/12/26 0026
 */

public class UserHeadView extends FrameLayout {

    private TextView levelTv;
    private ImageView hendIv;
    private ImageView genderIv;

    public UserHeadView(@NonNull Context context) {
        this(context, null);
    }

    public UserHeadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserHeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public UserHeadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_club_user_head, this);
        levelTv = view.findViewById(R.id.personal_level_tv);
        hendIv = view.findViewById(R.id.personal_head_iv);
        hendIv.setBackgroundResource(R.drawable.ic_avater);
        genderIv = view.findViewById(R.id.personal_gender_iv);
        measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int padding = getMeasuredWidth() / 13;
        view.findViewById(R.id.head_rl).setPadding(padding, padding, padding, padding);
    }

    public void setUserHeadMsg(String picPath) {
        ImageLoader.with(getContext())
                .load(picPath)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.ic_member)
                .error(R.drawable.ic_member)
                .error(R.drawable.ic_member)
                .into(hendIv);
    }

    public void setMotorcadeHeadMsg(String picPath) {
        ImageLoader.with(getContext())
                .load(picPath)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.headphoto_pic_n)
                .error(R.drawable.headphoto_pic_n)
                .error(R.drawable.headphoto_pic_n)
                .into(hendIv);
    }

    public void setUserHeadMsg(String picPath, String gender) {
        ImageLoader.with(getContext())
                .load(picPath)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.ic_avater)
                .error(R.drawable.ic_avater)
                .error(R.drawable.ic_avater)
                .into(hendIv);
        if (gender.equals("0")) {
            genderIv.setBackgroundResource(R.drawable.club_girl);
        } else {
            genderIv.setBackgroundResource(R.drawable.club_boy);
        }
    }

}
