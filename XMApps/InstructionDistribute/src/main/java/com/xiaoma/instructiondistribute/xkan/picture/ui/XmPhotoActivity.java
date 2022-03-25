package com.xiaoma.instructiondistribute.xkan.picture.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.instructiondistribute.R;
import com.xiaoma.instructiondistribute.utils.DistributeConstants;
import com.xiaoma.instructiondistribute.xkan.common.constant.EventConstants;
import com.xiaoma.instructiondistribute.xkan.common.constant.XkanConstants;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbMediaInfo;
import com.xiaoma.instructiondistribute.xkan.common.util.ImageUtils;
import com.xiaoma.instructiondistribute.xkan.common.view.VerticalSeekBar;
import com.xiaoma.instructiondistribute.xkan.common.view.VerticalSeekBarWrapper;
import com.xiaoma.instructiondistribute.xkan.photoview.OnPhotoViewTouchListener;
import com.xiaoma.instructiondistribute.xkan.photoview.OnSingleFlingListener;
import com.xiaoma.instructiondistribute.xkan.photoview.PhotoView;
import com.xiaoma.instructiondistribute.xkan.picture.vm.XmPhotoVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ByteUtils;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;


/**
 * @author taojin
 * @date 2018/11/12
 * @desc 图片展示Activity
 */
public class XmPhotoActivity extends BaseActivity {
    private PhotoView mIvPhoto;
    private ImageView mIvClose;
    private ImageView mIvLeft;
    private ImageView mIvRight;
    private ImageView mIvIncrease;
    private ImageView mIvRotate;
    private TextView mTvImage;
    private VerticalSeekBar mVerticalSeekBar;
    /**
     * 图片旋转角度
     */
    private float roatationAngel;
    private static final float ROTATE_BY_ANGEL = 90;
    private int rotationIndex = 0;

    private XmPhotoVM xmPhotoVM;
    /**
     * 图片索引
     */
    private int imageIndex;
    /**
     * 图片从哪里跳转过来（全部/图片）
     */
    private String imageType;
    private CountDownTimer mDismissOperationCountDownTimer;
    private boolean operationVisible;
    private long countDownInterval = 1000;
    /**
     * 倒计时默认时间为5s
     */
    private static final long MILLISINFUTURE = 2000;
    private ProgressBar mProgressBar;
    private View errorView;
    private VerticalSeekBarWrapper mSeekbarSeting;
    private static final float DEFAULTSCALE = 1f;
    private float mScale = 1f;
    private float imageShowWidth;
    private float imageShowHeight;
    private View mContentView;
    private TextView mTvSize;


    private boolean isErrorImg;
    private GestureDetector mGestureDetector;
    private List<UsbMediaInfo> datas;

    private static final int FLING_DISTANCE = 50;

    private static final int SCALE_PROGRESS_MIN = 0;
    private static final int SCALE_PROGRESS_FIRST = 30;
    private static final int SCALE_PROGRESS_SC0ND = 60;
    private static final int SCALE_PROGRESS_MAX = 100;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        setContentView(R.layout.activity_xm_photo);
        EventBus.getDefault().register(this);
        initView();
        bindView();
        initData();
        new Handler().postDelayed(() -> {
            handlePicOperation();
        },5000);
    }

    private void handlePicOperation() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        int action = extras.getInt("action");
        int type = extras.getInt("type");
        Bundle result = new Bundle();
        switch (action) {
            case DistributeConstants.ACTION_SET_USB_PIC_PREVIOUS_NEXT:
                handleUsbPicPreviousOrNext(type, result);
                break;
            case DistributeConstants.ACTION_SET_USB_PICTURE_OPERATION:
                handleUsbPicOperation(type, result);
                break;
            case DistributeConstants.ACTION_SET_USB_PICTURE_SHOW_TYPE:
                break;
            case DistributeConstants.ACTION_GET_USB_PICTURE_SHOW_TYPE:
                break;

        }
    }

    private void handleUsbPicOperation(int type, Bundle result) {
        if (type == 1) { // 放大
            zoomPhoto();
        } else if (type == 2) { // 缩小
//            zoomPhoto(); // 先放大
//            new Handler().postDelayed(() -> reducePhoto(),2000);
            reducePhoto();
        } else if (type == 4) { // 右旋
            handleRotate(true);
        }
        result.putInt("type", type);
        EventBus.getDefault().post(result, "usb_pic_operation");
    }

    private void handleUsbPicPreviousOrNext(int type, Bundle result) {
        if (type == 1)
            nextPic();
        else
            prePic();
        result.putInt("type", type);
        EventBus.getDefault().post(result, "usb_pic_next_or_previous");
    }

    private void initView() {
        mIvPhoto = findViewById(R.id.iv_photo);
        mIvClose = findViewById(R.id.icon_close);
        mIvLeft = findViewById(R.id.icon_left);
        mIvRight = findViewById(R.id.icon_right);
        mIvIncrease = findViewById(R.id.iv_increase);
        mIvRotate = findViewById(R.id.iv_rotate);
        mTvImage = findViewById(R.id.tv_photo);
        mTvSize = findViewById(R.id.tv_size);
        mSeekbarSeting = findViewById(R.id.seekbar_setting);
        mVerticalSeekBar = findViewById(R.id.mySeekBar);
        mProgressBar = findViewById(R.id.progress);

        mContentView = findViewById(R.id.pic_constraint);
        errorView = findViewById(R.id.view_empty);
        ImageView ivError = findViewById(R.id.iv_empty);
        ivError.setImageResource(R.drawable.img_file_error);
        //增加触摸区域
//        ViewUtils.expandViewTouchDelegate(mIvLeft, touchRange);
//        ViewUtils.expandViewTouchDelegate(mIvRight, touchRange);
        /**
         * 初始化默认操作按钮不可见
         */
        setOperationVisible(false);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > FLING_DISTANCE) {
                    prePic();
                    return true;
                }
                if (e2.getX() - e1.getX() > FLING_DISTANCE) {
                    nextPic();
                    return true;
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void bindView() {
        /**
         * 触摸到Photoview重置操作按钮计时时间
         */
        mIvPhoto.setOnPhotoViewTouchListener(new OnPhotoViewTouchListener() {
            @Override
            public void onPhotoViewTouch() {
                setOperationVisible(!operationVisible);
            }
        });

        //手势滑动
        mIvPhoto.setOnSingleFlingListener(new OnSingleFlingListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > FLING_DISTANCE) {
                    nextPic();
                    return true;
                }
                if (e2.getX() - e1.getX() > FLING_DISTANCE) {
                    prePic();
                    return true;
                }

                return false;
            }
        });

        /**
         * 放大按钮操作
         */
        mIvIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.INCREASE)
            @ResId(R.id.iv_increase)
            public void onClick(View v) {
                if (mIvPhoto.getDrawable() == null) {
                    XMToast.showToast(XmPhotoActivity.this, R.string.image_loading);
                    return;
                }
                startDismissOperatonTimer();
                mSeekbarSeting.setVisibility(View.VISIBLE);

            }
        });

        /**
         * seekbar操作
         */
        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                startDismissOperatonTimer();
                float scaleFactor = mScale + (float) progress / seekBar.getMax() * 2f;
                KLog.d("XmPhotoActivity", "progress: " + progress + " scaleFactor: " + scaleFactor);
                mIvPhoto.getAttacher().onScale(scaleFactor, mIvPhoto.getWidth() / 2, mIvPhoto.getHeight() / 2);
                //如果缩小到最小,就重置，便于左右滑下一张
                if (scaleFactor == DEFAULTSCALE) {
                    mIvPhoto.getAttacher().resetSupMatrix();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * 旋转操作(每次顺时针旋转90度)
         */
        mIvRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.ROTATE)
            @ResId(R.id.iv_rotate)
            public void onClick(View v) {
                handleRotate(true);
            }
        });

        /**
         * 下一张
         */
        mIvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.PICTURENEXT)
            @ResId(R.id.icon_right)
            public void onClick(View v) {
                nextPic();
            }
        });

        /**
         * 上一张
         */
        mIvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.PICTUREPREVIOUS)
            @ResId(R.id.icon_left)
            public void onClick(View v) {
                prePic();
            }
        });

        /**
         * 关闭
         */
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.CLOSE)
            @ResId(R.id.icon_close)
            public void onClick(View v) {
                postEvent();
                finish();
            }
        });
    }

    private void prePic() {
        if (ListUtils.isEmpty(datas)) {
            return;
        }
        rotationIndex = 0;
        resetStatus();
        startDismissOperatonTimer();
        mIvPhoto.getAttacher().resetSupMatrix();
        imageIndex--;
        if (imageIndex < 0) {
            imageIndex = datas.size() - 1;
        }
        setImage(datas.get(imageIndex));
    }

    private void nextPic() {
        if (ListUtils.isEmpty(datas)) {
            return;
        }
        rotationIndex = 0;
        resetStatus();
        startDismissOperatonTimer();
        mIvPhoto.getAttacher().resetSupMatrix();
        imageIndex++;
        if (imageIndex >= datas.size()) {
            imageIndex = 0;
        }
        setImage(datas.get(imageIndex));
    }


    private void initData() {

        mScale = 1f;

        imageIndex = getIntent().getIntExtra(XkanConstants.PHOTO_INDEX, 0);
        imageType = getIntent().getStringExtra(XkanConstants.FROM_TYPE);
        if (StringUtil.isEmpty(imageType)) {
            imageType = XkanConstants.FROM_PHOTO;
        }
        xmPhotoVM = ViewModelProviders.of(this).get(XmPhotoVM.class);
        xmPhotoVM.getmImages().observe(this, new Observer<XmResource<List<UsbMediaInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<UsbMediaInfo>> listXmResource) {
                listXmResource.handle(new OnCallback<List<UsbMediaInfo>>() {
                    @Override
                    public void onSuccess(List<UsbMediaInfo> data) {
                        datas = data;
                        if (ListUtils.isEmpty(data)){
                            return;
                        }
                        setImage(datas.get(imageIndex));
                    }
                });
            }
        });
        xmPhotoVM.fetchImages(imageType);
    }


    /**
     * 控制操作按钮的显示与隐藏
     *
     * @param visible
     */
    private void setOperationVisible(boolean visible) {
        if (isErrorImg) {
            mIvIncrease.setVisibility(View.GONE);
            mIvRotate.setVisibility(View.GONE);
        } else {
            mIvIncrease.setVisibility(visible ? View.VISIBLE : View.GONE);
            mIvRotate.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        mContentView.setVisibility(visible ? View.VISIBLE : View.GONE);
        if (!visible) {
            mSeekbarSeting.setVisibility(View.GONE);
        }
        operationVisible = visible;
        if (visible) {
            startDismissOperatonTimer();
        } else {
            cancelDismissOperationTimer();
        }
    }

    /**
     * 开启操作按钮消失的timer
     */
    private void startDismissOperatonTimer() {
        cancelDismissOperationTimer();
        if (mDismissOperationCountDownTimer == null) {
            mDismissOperationCountDownTimer = new CountDownTimer(MILLISINFUTURE, countDownInterval) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setOperationVisible(false);
                }
            };
        }
        mDismissOperationCountDownTimer.start();
    }

    /**
     * 取消操作按钮消失的timer
     */
    private void cancelDismissOperationTimer() {
        if (mDismissOperationCountDownTimer != null) {
            mDismissOperationCountDownTimer.cancel();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            setOperationVisible(!operationVisible);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置图片
     *
     * @param mediaBean
     */
    @SuppressLint("StringFormatInvalid")
    private void setImage(UsbMediaInfo mediaBean) {
        mTvImage.setText(FileUtils.getFileNameNoEx(mediaBean.getMediaName()));
        mTvSize.setText(String.format(getResources().getString(R.string.file_size), ByteUtils.getFileSize(mediaBean.getSize())));
        mVerticalSeekBar.setProgress(0);
        if (ImageUtils.isGif(mediaBean.getPath())) {
            mIvPhoto.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            if (isDestroyed()) {
                return;
            }
            ImageLoader.with(this)
                    .asGif()
                    .load(mediaBean.getPath())
                    .listener(glideListener)
                    .into(new ImageViewTarget<GifDrawable>(mIvPhoto) {
                        @Override
                        protected void setResource(@Nullable GifDrawable resource) {
                            mIvPhoto.setVisibility(View.VISIBLE);
                            mIvPhoto.setImageDrawable(resource);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            if (!isDestroyed()) {
                ImageLoader.with(this).load(mediaBean.getPath()).dontAnimate().listener(glideListener).into(mIvPhoto);
            }
        }

    }

    private RequestListener glideListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            if (mIvIncrease != null && mIvRotate != null && mContentView != null && errorView != null) {
                mIvRotate.setVisibility(View.GONE);
                mIvIncrease.setVisibility(View.GONE);
                mIvPhoto.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                isErrorImg = true;
            }
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            if (mIvIncrease != null && mIvRotate != null && mContentView != null && errorView != null) {
//                mIvRotate.setVisibility(View.VISIBLE);
//                mIvIncrease.setVisibility(View.VISIBLE);
                mIvPhoto.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                isErrorImg = false;
            }
            imageShowWidth = resource.getIntrinsicWidth();
            imageShowHeight = resource.getIntrinsicHeight();
            return false;
        }
    };


    /**
     * 收到USB移除通知，关闭页面
     *
     * @param event
     */
    @Subscriber(tag = XkanConstants.RELEASE_MEDIAINFO)
    public void finishActivity(String event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void postEvent() {
        if (XkanConstants.FROM_ALL.equals(imageType)) {
            EventBus.getDefault().post(imageIndex, XkanConstants.XKAN_MAIN_PIC_POS);
        } else {
            EventBus.getDefault().post(imageIndex, XkanConstants.XKAN_PIC_POS);
        }
    }

    /**
     * 处理旋转
     */
    private void handleRotate(boolean isShun) {
        if (ListUtils.isEmpty(datas)) {
            return;
        }
        if (ImageUtils.isGif(datas.get(imageIndex).getPath())) {
            XMToast.showToast(XmPhotoActivity.this, R.string.gif_no_rotate);
            return;
        }

        if (mIvPhoto.getDrawable() == null) {
            XMToast.showToast(XmPhotoActivity.this, R.string.image_loading);
            return;
        }
        mVerticalSeekBar.setProgress(0);
        startDismissOperatonTimer();
        if (rotationIndex >= 4 || rotationIndex <= -4) {
            rotationIndex = 0;
            roatationAngel = 0;
        }
        if (isShun) {
            roatationAngel += ROTATE_BY_ANGEL;

            rotationIndex++;
        } else {
            roatationAngel -= ROTATE_BY_ANGEL;

            rotationIndex--;
        }

        //计算缩放比例
        if (Math.abs(roatationAngel) == 90 || Math.abs(roatationAngel) == 270) {
            float scaleFactor = Math.min(mIvPhoto.getHeight() / imageShowWidth, mIvPhoto.getWidth() / imageShowHeight);
            mScale = scaleFactor;
            KLog.d("scale", "scaleFactor" + scaleFactor);
            mIvPhoto.getAttacher().onScale(mScale, mIvPhoto.getWidth() / 2, mIvPhoto.getHeight() / 2);
        }

        if (roatationAngel == 0 || Math.abs(roatationAngel) == 180 || Math.abs(roatationAngel) == 360) {

//            float scaleFactor = Math.min(mIvPhoto.getHeight() / imageShowHeight, mIvPhoto.getWidth() / imageShowWidth);
//            mScale = scaleFactor;
//            KLog.d("scale", "scaleFactor" + scaleFactor);
//            mIvPhoto.getAttacher().onScale(mScale, mIvPhoto.getWidth() / 2, mIvPhoto.getHeight() / 2);

            mScale = DEFAULTSCALE;
            mIvPhoto.getAttacher().onScale(mScale, mIvPhoto.getWidth() / 2, mIvPhoto.getHeight() / 2);

        }
        mIvPhoto.setRotationBy(isShun ? ROTATE_BY_ANGEL : -ROTATE_BY_ANGEL);

    }

    /**
     * 语音放大图片
     */
    private void zoomPhoto() {
        startDismissOperatonTimer();
        if (mVerticalSeekBar.getProgress() < SCALE_PROGRESS_FIRST) {
            mVerticalSeekBar.setProgress(SCALE_PROGRESS_FIRST);
        } else if (mVerticalSeekBar.getProgress() < SCALE_PROGRESS_SC0ND) {
            mVerticalSeekBar.setProgress(SCALE_PROGRESS_SC0ND);
        } else {
            mVerticalSeekBar.setProgress(SCALE_PROGRESS_MAX);
        }
        float scaleFactor = mScale + (float) mVerticalSeekBar.getProgress() / mVerticalSeekBar.getMax() * 2f;
        KLog.d("voicecontrol", "zoomPhoto progress: " + mVerticalSeekBar.getProgress() + " scaleFactor: " + scaleFactor);
        mIvPhoto.getAttacher().onScale(scaleFactor, mIvPhoto.getWidth() / 2, mIvPhoto.getHeight() / 2);
    }

    /**
     * 语音缩小图片
     */
    private void reducePhoto() {
        startDismissOperatonTimer();
        if (mVerticalSeekBar.getProgress() <= SCALE_PROGRESS_FIRST) {
            mVerticalSeekBar.setProgress(SCALE_PROGRESS_MIN);
        } else if (mVerticalSeekBar.getProgress() <= SCALE_PROGRESS_SC0ND) {
            mVerticalSeekBar.setProgress(SCALE_PROGRESS_FIRST);
        } else {
            mVerticalSeekBar.setProgress(SCALE_PROGRESS_SC0ND);
        }
        float scaleFactor = mScale + (float) mVerticalSeekBar.getProgress() / mVerticalSeekBar.getMax() * 2f;
        KLog.d("voicecontrol", "reducePhoto progress: " + mVerticalSeekBar.getProgress() + " scaleFactor: " + scaleFactor);
        mIvPhoto.getAttacher().onScale(scaleFactor, mIvPhoto.getWidth() / 2, mIvPhoto.getHeight() / 2);
    }


    private void resetStatus() {
        imageShowWidth = 0;
        imageShowHeight = 0;
        mScale = DEFAULTSCALE;
    }

    @Override
    public void onBackPressed() {
        postEvent();
        super.onBackPressed();

    }
}
