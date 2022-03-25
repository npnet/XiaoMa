package com.xiaoma.club.common.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaoma.club.R;
import com.xiaoma.image.transformation.CircleTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/12/29 0029
 */

public class MemberHeadsView extends FrameLayout {

    private List<ImageView> heads;

    public MemberHeadsView(@NonNull Context context) {
        this(context, null);
    }

    public MemberHeadsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MemberHeadsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MemberHeadsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.member_head_view, this);
        heads = new ArrayList<>();
        heads.add((ImageView) view.findViewById(R.id.member_image_1));
        heads.add((ImageView) view.findViewById(R.id.member_image_2));
        heads.add((ImageView) view.findViewById(R.id.member_image_3));
        heads.add((ImageView) view.findViewById(R.id.member_image_4));
        heads.add((ImageView) view.findViewById(R.id.member_image_5));
    }

    public void setHeadListWithUrl(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return;
        }
        for (int i = 0; i < urls.size(); i++) {
            try {
                if (i > 4) {
                    return;
                }
                heads.get(i).setVisibility(VISIBLE);
                Glide.with(getContext())
                        .load(urls.get(i))
                        .transform(new CircleTransform(getContext()))
                        .placeholder(R.drawable.default_head_icon)
                        .error(R.drawable.default_head_icon)
                        .into(heads.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //剩下的都隐藏
        hideOthers(urls.size());
    }

    private void hideOthers(int size) {
        if (size < heads.size()) {
            heads.get(size).setVisibility(GONE);
            size++;
            hideOthers(size);
        }
    }
}
