package com.xiaoma.club.common.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xiaoma.club.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: loren
 * Date: 2019/1/4 0004
 */

public class ActiveStarsView extends FrameLayout {

    private List<ImageView> stars;

    public ActiveStarsView(@NonNull Context context) {
        this(context, null);
    }

    public ActiveStarsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActiveStarsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ActiveStarsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.active_star_view, this);
        stars = new ArrayList<>();
        stars.add((ImageView) view.findViewById(R.id.active_star_image_1));
        stars.add((ImageView) view.findViewById(R.id.active_star_image_2));
        stars.add((ImageView) view.findViewById(R.id.active_star_image_3));
    }

    public void setGroupHot(int level) {
        if (level <= 0) {
            for (ImageView v : stars) {
                v.setVisibility(INVISIBLE);
            }
            return;
        }
        if (level > 6) {
            for (ImageView v : stars) {
                v.setImageResource(R.drawable.group_hot);
                v.setVisibility(VISIBLE);
            }
            return;
        }
        int whole = level / 2;
        int half = level % 2;
        int max = 0;
        for (int i = 0; i < stars.size(); i++) {
            if (i < whole) {
                stars.get(i).setImageResource(R.drawable.group_hot);
                stars.get(i).setVisibility(VISIBLE);
                max = i;
            } else {
                stars.get(i).setVisibility(INVISIBLE);
            }
        }
        if (half == 1) {
            stars.get(max + 1).setImageResource(R.drawable.group_hot_half);
            stars.get(max + 1).setVisibility(VISIBLE);
        }
    }
}
