package com.xiaoma.club.common.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.club.R;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.image.transformation.CircleTransform;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.utils.GsonHelper;

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
        genderIv = view.findViewById(R.id.personal_gender_iv);
    }

    public void setUserHeadMsg(User user) {
        if (user != null) {
            levelTv.setText("V" + user.getAge());
            ImageLoader.with(getContext())
                    .load(user.getPicPath())
                    .transform(new CircleTransform(getContext()))
                    .placeholder(R.drawable.default_head_icon)
                    .error(R.drawable.default_head_icon)
                    .into(hendIv);
            if (user.getGender() == 0) {
                genderIv.setBackgroundResource(R.drawable.club_girl);
            } else {
                genderIv.setBackgroundResource(R.drawable.club_boy);
            }

        }
    }

    public void setOnlyHead(String url) {
        if (url != null) {
            ImageLoader.with(getContext())
                    .load(url)
                    .transform(new CircleTransform(getContext()))
                    .placeholder(R.drawable.motorcade_icon)
                    .error(R.drawable.motorcade_icon)
                    .into(hendIv);
        }
    }

    public void setUserHeadByHxid(String hxId) {
        ClubRequestManager.getUserByHxAccount(hxId, new CallbackWrapper<User>() {
            @Override
            public User parse(String data) {
                return GsonHelper.fromJson(data, User.class);
            }

            @Override
            public void onSuccess(final User model) {
                super.onSuccess(model);
                if (model != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            setUserHeadMsg(model);
                        }
                    });
                }
            }
        });
    }
}
