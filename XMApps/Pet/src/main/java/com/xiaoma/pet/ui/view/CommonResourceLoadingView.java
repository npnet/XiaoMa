package com.xiaoma.pet.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoma.pet.R;

public class CommonResourceLoadingView extends FrameLayout {
    /**
     * 加载type
     * 1:有进度
     * 2:无进度
     */
    private int mLoadingType;
    /**
     * 场景type
     * 1：宠物资源加载 细分
     * 2：地图资源加载 细分
     * 3：其他资源加载
     */
    private int mSceneType;

    private ImageView mIvBg;
    private TextView mTvDesc;
    private ProgressBar mPbProgress;
    private int mPageBg; // 页面背景
    private String mDescText; // 描述文字
    private int mIndeterminateDrawable;
    private int mProgressDrawable;

    public CommonResourceLoadingView(Context context) {
        this(context, null);
    }

    public CommonResourceLoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonResourceLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonResourceLoadingView);
        mLoadingType = typedArray.getInt(R.styleable.CommonResourceLoadingView_loading_type, 1);
        mSceneType = typedArray.getInt(R.styleable.CommonResourceLoadingView_scene_type, 1);
        mPageBg = typedArray.getResourceId(R.styleable.CommonResourceLoadingView_page_bg, R.drawable.bg_common);
        mIndeterminateDrawable = typedArray.getResourceId(R.styleable.CommonResourceLoadingView_pb_indeterminate_drawable,android.R.drawable.progress_indeterminate_horizontal);
        mProgressDrawable = typedArray.getResourceId(R.styleable.CommonResourceLoadingView_pb_progress_drawable,R.drawable.horizontal_progress_drawable);
        String desc = typedArray.getString(R.styleable.CommonResourceLoadingView_desc_text);
        mDescText = TextUtils.isEmpty(desc) ? context.getString(R.string.resource_loading) : desc;
        typedArray.recycle();
    }


    private void init() {
        inflate(getContext(), R.layout.common_resource_loading_view, this);
        mIvBg = findViewById(R.id.iv_bg);
        mTvDesc = findViewById(R.id.tv_desc);
        mPbProgress = findViewById(R.id.pb_current_progress);
        mPbProgress.setIndeterminate(mLoadingType == 2); // 有无进度
        setIndeterminateDrawable(mIndeterminateDrawable); // 默认
        setProgressDrawable(R.drawable.horizontal_progress_drawable); // 默认
        mTvDesc.setText(mDescText);
        mIvBg.setImageResource(mPageBg);
        switch (mSceneType) { // 加载场景
            case 1:
                break;
            case 2:
                break;
            default:
        }
    }

    public void setIndeterminateDrawable(@DrawableRes int drawableRes){
        mPbProgress.setIndeterminateDrawable(getResources().getDrawable(drawableRes));
    }

    public void setProgressDrawable(@DrawableRes int drawableRes){
        mPbProgress.setProgressDrawable(getResources().getDrawable(drawableRes));
    }

    public void setCurrentProgress(int progress) {
        mPbProgress.setProgress(progress);
    }

    /**
     * 进度条有无进度
     *
     * @param mLoadingType
     */
    public void setLoadingType(int mLoadingType) {
        this.mLoadingType = mLoadingType;
        mPbProgress.setIndeterminate(mLoadingType == 2);
    }

    /**
     * 加载场景
     *
     * @param mSceneType
     */
    public void setSceneType(int mSceneType) {
        this.mSceneType = mSceneType;
        switch (mSceneType){

        }
    }

    /**
     * 页面背景
     *
     * @param mPageBg
     */
    public void setPageBg(int mPageBg) {
        this.mPageBg = mPageBg;
        mIvBg.setImageResource(mPageBg);
    }

    public void setPageBg(Bitmap bp){
        mIvBg.setImageBitmap(bp);
    }

    public void setPageBg(Drawable drawable){
        mIvBg.setImageDrawable(drawable);
    }

    /**
     * 页面描述文字：正在加载游戏...
     *
     * @param mDescText
     */
    public void setDescText(String mDescText) {
        this.mDescText = mDescText;
        mTvDesc.setText(mDescText);
    }
}
