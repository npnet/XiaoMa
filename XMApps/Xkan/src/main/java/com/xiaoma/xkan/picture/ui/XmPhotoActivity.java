package com.xiaoma.xkan.picture.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ByteUtils;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.annotation.Command;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.constant.EventConstants;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.common.util.ImageUtils;
import com.xiaoma.xkan.common.view.VerticalSeekBar;
import com.xiaoma.xkan.common.view.VerticalSeekBarWrapper;
import com.xiaoma.xkan.photoview.OnPhotoViewTouchListener;
import com.xiaoma.xkan.photoview.OnSingleFlingListener;
import com.xiaoma.xkan.photoview.PhotoView;
import com.xiaoma.xkan.picture.vm.XmPhotoVM;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;
import java.util.Random;

import static com.xiaoma.xkan.common.constant.EventConstants.SlideEvent.PICTURE_TRACK;

/**
 * @author taojin
 * @date 2018/11/12
 * @desc 图片展示Activity
 */
@PageDescComponent(EventConstants.PageDescribe.XMPHOTOACTIVITYPAGEPATHDESC)
public class XmPhotoActivity extends BaseActivity implements View.OnTouchListener {
    private static final String TAG = "XmPhotoActivity";
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
    private static final long COUNTDOWNINTERVAL = 1000;
    /**
     * 倒计时默认时间为5s
     */
    private static final long MILLISINFUTURE = 2000;
    private View errorView;
    private VerticalSeekBarWrapper mSeekbarSeting;
    private static final float DEFAULTSCALE = 1f;
    private float mScale = 1f;
    private float imageShowWidth;
    private float imageShowHeight;
    private View mContentView;
    private TextView mTvSize;


    private boolean isErrorImg;

    private List<UsbMediaInfo> datas;

    private static final int FLING_DISTANCE = 50;

    private static final int SCALE_PROGRESS_MIN = 0;
    private static final int SCALE_PROGRESS_FIRST = 30;
    private static final int SCALE_PROGRESS_SC0ND = 60;
    private static final int SCALE_PROGRESS_MAX = 100;

    private GestureDetector mGestureDetector;

    //随机动作id
    public static int[] mAction = new int[]{14
            , 16
            , 27};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        getNaviBar().hideNavi();
        setContentView(R.layout.activity_xm_photo);
        statusBarDividerGone();
        EventBus.getDefault().register(this);
        initView();
        bindView();
        initData();
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
        mContentView = findViewById(R.id.pic_constraint);
        View llLeft = findViewById(R.id.ll_left);
        View llRight = findViewById(R.id.ll_right);

        errorView = findViewById(R.id.view_empty);
        ImageView ivError = findViewById(R.id.iv_empty);
        ivError.setImageResource(R.drawable.img_file_error);
        //走马灯
        mTvImage.setSelected(true);

        setOperationVisible(true);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return moveByFling(e1, e2);
            }
        });

        llLeft.setOnTouchListener(this);
        llRight.setOnTouchListener(this);
        errorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGestureDetector.onTouchEvent(motionEvent);
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                    setOperationVisible(!operationVisible);
                }
                return true;
            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    private void bindView() {
        /*
          触摸到Photoview重置操作按钮计时时间
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
                return moveByFling(e1, e2);
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
                float scaleFactor = mScale + (float) progress / seekBar.getMax();
                KLog.d("XmPhotoActivity", "progress: " + progress + " scaleFactor: " + scaleFactor);
                mIvPhoto.getAttacher().onScale(scaleFactor, mIvPhoto.getWidth() / 2, mIvPhoto.getHeight() / 2);
                XmAutoTracker.getInstance().onEvent(PICTURE_TRACK, String.valueOf(progress),
                        "XmPhotoActivity", EventConstants.PageDescribe.XMPHOTOACTIVITYPAGEPATHDESC);
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

    private boolean moveByFling(MotionEvent e1, MotionEvent e2) {
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

    private void prePic() {
        if (ListUtils.isEmpty(datas)) {
            return;
        }
        rotationIndex = 0;
        resetStatus();
        setOperationVisible(true);
        mIvPhoto.getAttacher().resetSupMatrix();
        mSeekbarSeting.setVisibility(View.INVISIBLE);
        imageIndex--;
        if (imageIndex < 0) {
            imageIndex = datas.size() - 1;
        }

        try {
            setImage(datas.get(imageIndex));
        } catch (Exception e) {
            KLog.e(TAG, e.getMessage());
        } catch (Throwable t) {
            KLog.e(TAG, t.getMessage());
        }
    }

    private void nextPic() {
        if (ListUtils.isEmpty(datas)) {
            return;
        }
        rotationIndex = 0;
        resetStatus();
        setOperationVisible(true);
        mIvPhoto.getAttacher().resetSupMatrix();
        mSeekbarSeting.setVisibility(View.INVISIBLE);
        imageIndex++;
        if (imageIndex >= datas.size()) {
            imageIndex = 0;
        }

        try {
            setImage(datas.get(imageIndex));
        } catch (Exception e) {
            KLog.e(TAG, e.getMessage());
        } catch (Throwable t) {
            KLog.e(TAG, t.getMessage());
        }
    }


    private void initData() {

        mScale = 1f;

        imageIndex = getIntent().getIntExtra(XkanConstants.PHOTO_INDEX, 0);
        imageType = getIntent().getStringExtra(XkanConstants.FROM_TYPE);

        xmPhotoVM = ViewModelProviders.of(this).get(XmPhotoVM.class);
        xmPhotoVM.getmImages().observe(this, new Observer<XmResource<List<UsbMediaInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<UsbMediaInfo>> listXmResource) {
                listXmResource.handle(new OnCallback<List<UsbMediaInfo>>() {
                    @Override
                    public void onSuccess(List<UsbMediaInfo> data) {
                        datas = data;
                        if (ListUtils.isEmpty(data)) {
                            return;
                        }
                        try {
                            setImage(datas.get(imageIndex));
                        } catch (Exception e) {
                            KLog.e(TAG, e.getMessage());
                        } catch (Throwable t) {
                            KLog.e(TAG, t.getMessage());
                        }
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
            mDismissOperationCountDownTimer = new CountDownTimer(MILLISINFUTURE, COUNTDOWNINTERVAL) {
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


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
//            setOperationVisible(!operationVisible);
//        }
//        return super.onTouchEvent(event);
//    }

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
            if (!isDestroyed()) {
                showProgressDialogNoMsg();
                ImageLoader.with(this)
                        .asDrawable()
                        .load(mediaBean.getPath())
                        .listener(glideListener)
                        .into(new ImageViewTarget<GifDrawable>(mIvPhoto) {
                            @Override
                            protected void setResource(@Nullable GifDrawable resource) {
                                mIvPhoto.setVisibility(View.VISIBLE);
                                mIvPhoto.setImageDrawable(resource);
                                dismissProgress();
                            }
                        });
            }
        } else {
            if (!isDestroyed()) {
                showProgressDialogNoMsg();
                ImageLoader.with(this)
                        .load(mediaBean.getPath())
                        .dontAnimate()
                        .listener(glideListener)
                        .into(mIvPhoto);
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
                dismissProgress();
                isErrorImg = true;
            }
            KLog.d("xkan GlideException: " + (e != null ? e.getMessage() : ""));
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            if (mIvIncrease != null && mIvRotate != null && mContentView != null && errorView != null) {
//                mIvRotate.setVisibility(View.VISIBLE);
//                mIvIncrease.setVisibility(View.VISIBLE);
                mIvPhoto.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                dismissProgress();
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
        try {
            if (mIvPhoto != null) {
                Glide.with(getApplicationContext()).clear(mIvPhoto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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
        mSeekbarSeting.setVisibility(View.GONE);
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
            mScale = DEFAULTSCALE;
            mIvPhoto.getAttacher().onScale(mScale, mIvPhoto.getWidth() / 2, mIvPhoto.getHeight() / 2);

        }
        mIvPhoto.setRotationBy(isShun ? ROTATE_BY_ANGEL : -ROTATE_BY_ANGEL);

    }

    @Command("顺时针旋转")
    public void shunAngelPic() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.XKan3DAction.ROTATE_IMG);
                handleRotate(true);
            }
        });
    }

    @Command("逆时针旋转")
    public void niAngelPic() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.XKan3DAction.ROTATE_IMG);
                handleRotate(false);
            }
        });
    }


    @Command("(上|前)一张(图片|照片)?")
    public void prePhoto() {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(getAction());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.XKan3DAction.PRE_IMG);
                prePic();
            }
        });
    }

    @Command("(下|后)一张(图片|照片)?")
    public void nextPhoto() {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(getAction());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.XKan3DAction.NEXT_IMG);
                nextPic();
            }
        });
    }

    @Command("放大(图片)?")
    public void zoomPic() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.XKan3DAction.ZOOM_IMG);
                zoomPhoto();
            }
        });
    }

    @Command("缩小(图片)?")
    public void reducePic() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.XKan3DAction.REDUCE_IMG);
                reducePhoto();
            }
        });
    }

    @Command("图片放大")
    public void zoomPic2() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.XKan3DAction.ZOOM_IMG);
                zoomPhoto();
            }
        });
    }

    @Command("图片缩小")
    public void reducePic2() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.XKan3DAction.REDUCE_IMG);
                reducePhoto();
            }
        });
    }

    /**
     * 随机动作
     */
    public static int getAction() {
        Random random = new Random();
        int i = random.nextInt(mAction.length);
        return mAction[i];
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
            setOperationVisible(!operationVisible);
        }
        return true;
    }
}
