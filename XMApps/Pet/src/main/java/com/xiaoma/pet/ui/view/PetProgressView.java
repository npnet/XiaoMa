package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Gillben on 2018/12/27 0027
 * <p>
 * desc:    宠物进度（升级、进食）
 */
public class PetProgressView extends FrameLayout {


    private ImageView mPetAvatar;
    private TextView mCounterText;
    private PetStatusProgress mPetStatusProgress;


    public PetProgressView(@NonNull Context context) {
        this(context, null);
    }

    public PetProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PetProgressView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 3) {
            throw new IllegalStateException("Can only have up to three children.");
        }

        mPetAvatar = (ImageView) getChildAt(0);
        mCounterText = (TextView) getChildAt(1);
        mPetStatusProgress = (PetStatusProgress) getChildAt(2);
    }

    /**
     * 宠物普通状态处理，显示2D头像，经验值
     *
     * @param progressPercent 经验值、喂食进度百分比
     */
    public void handlePetNormal(float progressPercent) {
        changeStatus(VISIBLE, GONE, VISIBLE);
        mPetStatusProgress.update(false, progressPercent);
    }


    private void handlePetEatFood(String curTime) {
        //TODO 宠物正在进食的处理，显示喂食进度，倒计时
        changeStatus(GONE, VISIBLE, GONE);
        curTime = String.format(Locale.CHINA, "进食中/\n%s", curTime);
        mCounterText.setText(curTime);
    }


    private void changeStatus(int avatar, int counter, int progress) {
        mPetAvatar.setVisibility(avatar);
        mCounterText.setVisibility(counter);
        mPetStatusProgress.setVisibility(progress);
    }


}
