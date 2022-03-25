package com.xiaoma.shop.business.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoma.shop.R;
import com.xiaoma.skin.views.XmSkinLinearLayout;

import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatBackgroundHelper;
import skin.support.widget.SkinCompatHelper;

import static skin.support.widget.SkinCompatHelper.INVALID_ID;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/5
 */
public class ProgressButton extends XmSkinLinearLayout {

    public static final String TAG = "StatefulButton";

    private ProgressBar mPb;
    private TextView mStateTV;
    private int mTextColor;
    //    private Drawable mBgDrawable;
    private LayerDrawable mDrawable;
    private String mText = "N/A";
    private float mTextSizeInPixel;
    private int mTextPaddingBottomPixel;

    private Drawable mProgressDrawable;

    private int mBackground = INVALID_ID;
    private int mProgressBackgroud = INVALID_ID;

    private SkinCompatBackgroundHelper mBackgroundTintHelper;


    public ProgressButton(@NonNull Context context) {
        this(context, null);
    }

    public ProgressButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_stateful_button, this);
        //初始化背景
        mPb = findViewById(R.id.pb);
        mDrawable = (LayerDrawable) mPb.getProgressDrawable();
        initAttrs(context, attrs, defStyleAttr);
        initView();

    }

    private void initAttrs(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);
        mTextColor = typedArray.getColor(R.styleable.ProgressButton_textColor, context.getColor(android.R.color.white));
//        mBgDrawable = typedArray.getDrawable(R.styleable.ProgressButton_progressBg);
//        mProgressDrawable = typedArray.getDrawable(R.styleable.ProgressButton_progressingBg);
        mText = typedArray.getString(R.styleable.ProgressButton_text);
        mTextSizeInPixel = typedArray.getDimension(R.styleable.ProgressButton_textSize, 24.0f);
        mTextPaddingBottomPixel = typedArray.getDimensionPixelSize(R.styleable.ProgressButton_textPaddingBottom, 0);
        mBackground = typedArray.getResourceId(R.styleable.ProgressButton_progressBg, INVALID_ID);
        mProgressBackgroud = typedArray.getResourceId(R.styleable.ProgressButton_progressingBg, INVALID_ID);
        typedArray.recycle();

        applyDropDownBackgroundResource();
        mBackgroundTintHelper = new SkinCompatBackgroundHelper(this);
        mBackgroundTintHelper.loadFromAttributes(attrs, defStyleAttr);

    }

    private void initView() {
        mStateTV = findViewById(R.id.tvState);
        //初始化字体颜色
        updateText(mText);
        updateTextColor(mTextColor);
        updateTextSize(mTextSizeInPixel);
        updateTextPaddingBottom(mTextPaddingBottomPixel);
//        updateProgressBg(mBgDrawable);
//        updateProgressDrawable(mProgressDrawable);
    }

    public void updateTextColor(@ColorInt int color) {
        if (mStateTV != null) {
            mStateTV.setTextColor(color);
        }
    }

    public void updateTextColorSkin(int style){
        if(mStateTV != null){
            mStateTV.setTextAppearance(style);
        }
    }

    public void updateTextPaddingBottom(int padding) {
        if (mStateTV != null) {
            mStateTV.setPadding(0, 0, 0, padding);
        }
    }

    public void updateTextSize(float pxSize) {
        if (mStateTV != null) {
            mStateTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, pxSize);
        }
    }


    public void updateProgressBg(Drawable drawable) {
        if (mDrawable != null) {
            mDrawable.setDrawableByLayerId(android.R.id.background, drawable);
        }
    }

    public void updateProgressDrawable(Drawable drawable) {
        if (mDrawable != null && drawable != null) {
            ClipDrawable clipDrawable = new ClipDrawable(drawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
            mDrawable.setDrawableByLayerId(android.R.id.progress, clipDrawable);
        }
    }

    public void updateStateAndProgress(long progress, long duration) {
        int progressByPercent = (int) (progress * 1.0f / duration * 100);
        updateProgress(progressByPercent);
        updateText(getContext().getString(R.string.str_download_progress, progressByPercent));
    }

    public void updateText(String text) {
        if (mStateTV != null) {
            mStateTV.setText(text);
        } else {
            Log.e(TAG, "TextView is " + mStateTV);
        }
    }

    public void updateStateAndProgress(final int progress, final String state) {
        updateText(state);
        updateProgress(progress);
        changeTheCenterTvColor(state);
    }

    private void changeTheCenterTvColor(String state) {
        int color;
        if (state.equals(getContext().getString(R.string.state_using))) {
//            color = mContext.getColor(R.color.color_using);
            mStateTV.setTextAppearance(R.style.text_using);
        } else {
            color = mTextColor;
            mStateTV.setTextColor(color);
        }


    }

    public String getText() {
        if (mStateTV == null) {
            return "";
        }
        return mStateTV.getText().toString();
    }

    private void updateProgress(int progress) {
        if (mPb != null) {
            if (progress > 100) {
                progress = 100;
            }
            mPb.setProgress(progress);
        } else {
            Log.e(TAG, "Progressbar is " + mPb);
        }
    }

    @Override
    public void applySkin() {
        super.applySkin();
        if (mBackgroundTintHelper != null) {
            mBackgroundTintHelper.applySkin();
        }
        applyDropDownBackgroundResource();
    }

    private void applyDropDownBackgroundResource() {
        mBackground = SkinCompatHelper.checkResourceId(mBackground);
        mProgressBackgroud = SkinCompatHelper.checkResourceId(mProgressBackgroud);
        if (mBackground != INVALID_ID) {
            Drawable drawable = SkinCompatResources.getDrawableCompat(getContext(), mBackground);
            if (drawable != null) {
                updateProgressBg(drawable);
            }
        }
        if(mProgressBackgroud != INVALID_ID){
            Drawable drawable = SkinCompatResources.getDrawableCompat(getContext(), mProgressBackgroud);
            updateProgressDrawable(drawable);
        }

    }


    @Override
    public void setBackgroundResource(int resid) {
        super.setBackgroundResource(resid);
    }
}
