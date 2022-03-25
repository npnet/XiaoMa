package com.xiaoma.guide.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.guide.R;
import com.xiaoma.guide.listener.GuideCallBack;
import com.xiaoma.guide.listener.GuideFinishCallBack;
import com.xiaoma.login.LoginManager;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ScreenUtils;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.lang.ref.WeakReference;

public class NewGuide {
    private static String TAG = "NewGuide";
    // targetView操作的动画
    private TranslateAnimation translateAnimation;
    // 不同app 不同的标识
    private String label;
    // 要显示的activity
    private WeakReference<Activity> activityWeakReference;
    // 目标view 抖动
    private WeakReference<View> targetView;
    // 目标viewid view在guidelayout中 不在activity/fragment中 用于覆盖原始页面中的targeView
    private int targetViewId;
    // 新手引导显示的载体
    private WindowManager wm;
    // 新手引导跳过的回调
    private GuideCallBack callBack;
    // 引导view的父布局
    private FrameLayout contentView;
    // 需要穿透点击事件的区域
    private Rect targetRect;
    // 跳过按钮的位置信息 固定 屏幕右边底部固定的位置
    private Rect skipRect;
    // 是不是最后一张引导，自动存储当前label引导结束
    private boolean isLastGuide = false;
    // 判断已经addview 才移除
    private boolean hasAddView;
    // 不能透传事件 需要回调通知的区域
    private Rect highLightRect;
    // 引导布局
    private int guideLayoutId;
    // 是否需要抖动 （有抖动就有波纹）
    private boolean needShake;
    // 是否需要手势
    private boolean needHand;
    // 手势viewid
    private int ivHandId;
    // 波纹viewid1
    private int ivWaveIdOne;
    // 波纹viewid2
    private int ivWaveIdTwo;
    // 波纹viewid3
    private int ivWaveIdThree;
    // 跳过viewid
    private int skipId;
    // 波纹一
    private View waveViewOne;
    // 波纹二
    private View waveViewTwo;
    // 波纹三
    private View waveViewThree;
    // 1:右下（底部基线以下） 2:右上 3:左下 4:右下（底部基线以上）
    private int handLocation = RIGHT_AND_BOTTOM_BOTTOM;
    public static final int RIGHT_AND_BOTTOM_BOTTOM = 1;
    public static final int RIGHT_AND_TOP = 2;
    public static final int LEFT_AND_BOTTOM = 3;
    public static final int RIGHT_AND_BOTTOM_TOP = 4;
    // 手势view
    private View handView;
    // targetView平移的距离
    private float targetViewTransX = 0.02f;
    // 波纹动画集合
    private AnimatorSet waveAnimatorSet;
    // 手势动画 x,y平移 + 透明度改变
    private AnimatorSet handAnimatorSet;
    // 主线程执行动画
    private static Handler handler = new Handler(Looper.getMainLooper());
    // 手势x平移
    private ObjectAnimator handTranslationX;
    // 手势y平移
    private ObjectAnimator handTranslationY;
    // 新手引导描述(xml中不能写死的部分)
    private Spanned guideDesc;
    // 新手引导描述textView
    private int tvGuideTextId;
    // 是否需要在 事件穿透区域 穿透滑动事件
    private boolean canScroll;
    private boolean targetViewAnimationCancled;
    // 仅展示引导图片时 图片view
    private int guidePicViewId;
    private int nextStepViewId;
    // 引导图片数组
    private int[] guidePicsArr;
    // 新手引导结束回调
    private static GuideFinishCallBack guideFinishCallBack;
    // 新手引导是否正在显示
    private static boolean isGuideShowNow = false;
    // 有些新手引导手势位置 还需要上移 targetView的内容居上 导致手势没有滑动到位
    private boolean needMoveUpALittle;
    // 避免 多次回调onHighLightClicked
    private boolean isHighLightClick;

    private Runnable textRunnable = new Runnable() {
        @Override
        public void run() {
            if (targetView != null && targetView.get() != null) {
                targetView.get().requestFocus();
                targetView.get().startAnimation(translateAnimation);
            }
        }
    };
    private Runnable handRunnable = new Runnable() {
        @Override
        public void run() {
            if (handAnimatorSet != null) {
                handAnimatorSet.start();
            }
        }
    };
    private Runnable waveRunnable = new Runnable() {
        @Override
        public void run() {
            if (waveAnimatorSet != null) {
                waveAnimatorSet.start();
            }
        }
    };
    private Runnable stopHandRunnable = new Runnable() {
        @Override
        public void run() {
            waveViewOne.setAlpha(0);
            waveViewTwo.setAlpha(0);
            waveViewThree.setAlpha(0);
            if (handAnimatorSet != null) {
                handAnimatorSet.cancel();
            }
            startAnimation();
        }
    };


    public NewGuide(Activity activity) {
        if (activity == null) {
            Log.d(TAG, "NewGuide: activity is null !!!");
            return;
        }
        activityWeakReference = new WeakReference<>(activity);
        initAutoTracker();
        initContentView();
    }

    private void initContentView() {
        contentView = new FrameLayout(activityWeakReference.get()) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_MOVE && !canScroll) {
                    return super.dispatchTouchEvent(ev);
                }
                if (targetRect == null && highLightRect == null)
                    return super.dispatchTouchEvent(ev);
                int x = Math.round(ev.getX());
                int y = Math.round(ev.getY());
                if (highLightRect != null && highLightRect.contains(x, y) && callBack != null) {
                    if (isHighLightClick) return super.dispatchTouchEvent(ev);
                    isHighLightClick = true;
                    callBack.onHighLightClicked();
                } else if (targetRect != null && targetRect.contains(x, y) && activityWeakReference.get() != null) { // 穿透
                    return activityWeakReference.get().dispatchTouchEvent(ev);
                }
                return super.dispatchTouchEvent(ev);
            }
        };
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(lp);
        //==================start=====================
        // TODO: 2019/7/8 0008 验证无法点击的问题是否是新手引导导致的 添加一个小红点
        View colorView = new View(activityWeakReference.get());
        colorView.setBackgroundColor(Color.RED);
        contentView.addView(colorView, 2, 2);
        //===================end=====================
    }

    private void initAutoTracker() {
        if (activityWeakReference.get() == null)
            return;
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(activityWeakReference.get().getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    private void initAnimation() {
        if (needHand) {
            handAnimatorSet = new AnimatorSet();
            ObjectAnimator alpha = ObjectAnimator.ofFloat(handView, "alpha", 0, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1);
            handTranslationX = ObjectAnimator.ofFloat(handView, "translationX", 0, -5, -10, -15, -20, -25f);
            handTranslationY = ObjectAnimator.ofFloat(handView, "translationY", 0, -10, -20, -30, -40, -50, -60);
            alpha.setDuration(200);
            handTranslationX.setDuration(1000);
            handTranslationY.setDuration(1000);
            handTranslationX.setRepeatCount(3);
            handTranslationY.setRepeatCount(3);
            handTranslationX.setRepeatMode(ValueAnimator.REVERSE);
            handTranslationY.setRepeatMode(ValueAnimator.REVERSE);
            handAnimatorSet.playTogether(alpha, handTranslationX, handTranslationY);
            handAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    handView.setAlpha(0); // 完全透明
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    handView.setAlpha(0); // 完全透明
                }
            });
        }
        if (needShake && waveViewOne != null && waveViewTwo != null && waveViewThree != null) {
            ObjectAnimator waveAnimatorOne = ObjectAnimator.ofFloat(waveViewOne, "alpha", 0, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1);
            ObjectAnimator waveAnimatorTwo = ObjectAnimator.ofFloat(waveViewTwo, "alpha", 0, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1);
            ObjectAnimator waveAnimatorThree = ObjectAnimator.ofFloat(waveViewThree, "alpha", 0, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1);
            waveAnimatorOne.setDuration(200);
            waveAnimatorTwo.setDuration(200);
            waveAnimatorThree.setDuration(200);
            waveAnimatorSet = new AnimatorSet();
            waveAnimatorSet.playSequentially(waveAnimatorOne, waveAnimatorTwo, waveAnimatorThree);
            waveAnimatorSet.addListener(new AnimatorListenerAdapter() {
                int i = 0;
                boolean isAnimationCancle;

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isAnimationCancle) {
                        Log.d(TAG, "动画停止: waveAnimationSet is cancled");
                        return;
                    }
                    if (i >= 4) {
                        handler.postDelayed(stopHandRunnable, 200);
                        i = 0;
                    } else {
                        // 避免cancle后 再次调用start
                        if (waveAnimatorSet != null) {
                            waveAnimatorSet.start();
                            i++;
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);
                    isAnimationCancle = true;
                }
            });
            translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -targetViewTransX, Animation.RELATIVE_TO_SELF, targetViewTransX, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
            translateAnimation.setDuration(100);
            translateAnimation.setRepeatCount(10);
            translateAnimation.setRepeatMode(Animation.REVERSE);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (targetViewAnimationCancled) {
                        Log.d(TAG, "动画停止: targetViewAnimationCancled = " + targetViewAnimationCancled);
                        return;
                    }
                    handler.post(handRunnable);
                    handler.post(waveRunnable);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    private void initView() {
        View guideView = LayoutInflater.from(activityWeakReference.get()).inflate(guideLayoutId, contentView);
        if ((targetView == null || targetView.get() == null) && targetViewId != 0) {
            View view = guideView.findViewById(targetViewId);
            if (view == null) return;
            targetView = new WeakReference<>(view);
            resetTargetViewLocation();
        }
        if (needShake && ivWaveIdOne != 0 && ivWaveIdTwo != 0 && ivWaveIdThree != 0) {
            waveViewOne = guideView.findViewById(ivWaveIdOne);
            waveViewTwo = guideView.findViewById(ivWaveIdTwo);
            waveViewThree = guideView.findViewById(ivWaveIdThree);
            resetWaveViewLocation();
        }
        if (needHand && ivHandId != 0) {
            handView = guideView.findViewById(ivHandId);
            resetHandViewLocation();
        }
        if (tvGuideTextId != 0 && guideDesc != null) {
            TextView tvGuideDesc = guideView.findViewById(tvGuideTextId);
            tvGuideDesc.setText(guideDesc);
        }
        if (skipId != 0)
            guideView.findViewById(skipId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    skipOrFinishGuide();
                    dismissGuideWindow();
                    if (GuideConstants.PET_SHOWED.equals(label)) {
                        skipGuide(R.layout.guide_last_toast_view_pet/*, R.id.tv_ok*/);
                    } else {
                        skipGuide(0);
                    }
//                    XmAutoTracker.getInstance().onEvent(EventConstants.skipEvent, EventConstants.mainActivity, EventConstants.main);
                }
            });
    }

    private void initViewPics() {
        View guideView = LayoutInflater.from(activityWeakReference.get()).inflate(guideLayoutId, contentView);
        final View nextStepView = guideView.findViewById(nextStepViewId);
        final View guideSkip = guideView.findViewById(skipId);
        guideSkip.setEnabled(false);
        final ImageView guidePicView = guideView.findViewById(guidePicViewId);
        nextStepView.setOnClickListener(new View.OnClickListener() {
            private int index;

            @Override
            public void onClick(View v) {
                if (index >= guidePicsArr.length - 1) return;
                guidePicView.setImageResource(guidePicsArr[++index]);
                if (index == guidePicsArr.length - 1) {
                    nextStepView.setEnabled(false);
                    guideSkip.setEnabled(true);
                }
//                XmAutoTracker.getInstance().onEvent(EventConstants.nextStepEvent, EventConstants.mainActivity, EventConstants.main);
                uploadGuideEvent(EventConstants.nextStepEvent);
            }
        });
        guideSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                skipOrFinishGuide();
                dismissGuideWindow();
                skipGuide(0);
//                XmAutoTracker.getInstance().onEvent(EventConstants.skipEvent, EventConstants.mainActivity, EventConstants.main);
            }
        });
    }

    private void resetTargetViewLocation() {
        if (targetView == null || targetView.get() == null) return;
        if (highLightRect == null) return;
        ViewGroup.LayoutParams params = targetView.get().getLayoutParams();
        if (params instanceof ConstraintLayout.LayoutParams) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) params;
            layoutParams.leftMargin = highLightRect.left;
            layoutParams.topMargin = highLightRect.top;
            layoutParams.width = highLightRect.right - highLightRect.left;
            layoutParams.height = highLightRect.bottom - highLightRect.top;
        } else if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
            layoutParams.leftMargin = highLightRect.left;
            layoutParams.topMargin = highLightRect.top;
            layoutParams.width = highLightRect.right - highLightRect.left;
            layoutParams.height = highLightRect.bottom - highLightRect.top;
        } else {
            throw new RuntimeException("guide view root view must be ConstraintLayout or RelativeLayout");
        }
    }

    private void resetWaveViewLocation() {
        if (targetRect == null && highLightRect == null) return;
        final Rect rect = targetRect == null ? highLightRect : targetRect;
        if (rect == null) return;
        waveViewOne.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = waveViewOne.getLayoutParams();
                if (params instanceof ConstraintLayout.LayoutParams) {
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) params;
                    layoutParams.leftMargin = rect.left - (waveViewOne.getWidth() - (rect.right - rect.left)) / 2;
                    layoutParams.topMargin = rect.top - (waveViewOne.getHeight() - (rect.bottom - rect.top)) / 2;
                } else if (params instanceof RelativeLayout.LayoutParams) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
                    layoutParams.leftMargin = rect.left - (waveViewOne.getWidth() - (rect.right - rect.left)) / 2;
                    layoutParams.topMargin = rect.top - (waveViewOne.getHeight() - (rect.bottom - rect.top)) / 2;
                } else {
                    throw new RuntimeException("guide view root view must be ConstraintLayout or RelativeLayout");
                }
                waveViewTwo.setLayoutParams(params);
                waveViewThree.setLayoutParams(params);
            }
        });
    }

    private void resetHandViewLocation() {
        if (targetRect == null && highLightRect == null) return;
        Rect rect = targetRect == null ? highLightRect : targetRect;
        if (rect == null) return;
        ViewGroup.LayoutParams params = handView.getLayoutParams();
        if (params instanceof ConstraintLayout.LayoutParams) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) params;
            if (handLocation == RIGHT_AND_BOTTOM_BOTTOM) {
                if (needMoveUpALittle) {
                    layoutParams.topMargin = rect.bottom + 20;
                    layoutParams.leftMargin = rect.right - 60;
                } else {
                    layoutParams.topMargin = rect.bottom + 60;
                    layoutParams.leftMargin = rect.right;
                }
            } else if (handLocation == RIGHT_AND_TOP) {
                layoutParams.leftMargin = rect.right;
                layoutParams.topMargin = rect.top + 60;
            } else if (handLocation == LEFT_AND_BOTTOM) {
                layoutParams.leftMargin = rect.left + 60;
                layoutParams.topMargin = rect.bottom + 60;
            } else if (handLocation == RIGHT_AND_BOTTOM_TOP) {
                layoutParams.leftMargin = rect.right - 25;
                layoutParams.topMargin = rect.bottom - 40;
            }
        } else if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
            if (handLocation == RIGHT_AND_BOTTOM_BOTTOM) {
                if (needMoveUpALittle) {
                    layoutParams.leftMargin = rect.right - 60;
                    layoutParams.topMargin = rect.bottom + 20;
                } else {
                    layoutParams.leftMargin = rect.right;
                    layoutParams.topMargin = rect.bottom + 60;
                }
            } else if (handLocation == RIGHT_AND_TOP) {
                layoutParams.leftMargin = rect.right;
                layoutParams.topMargin = rect.top + 60;
            } else if (handLocation == LEFT_AND_BOTTOM) {
                layoutParams.leftMargin = rect.left;
                layoutParams.topMargin = rect.bottom + 60;
            } else if (handLocation == RIGHT_AND_BOTTOM_TOP) {
                layoutParams.leftMargin = rect.right - 25;
                layoutParams.topMargin = rect.bottom - 40;
            }
        } else {
            throw new RuntimeException("guide view root view must be ConstraintLayout or RelativeLayout");
        }
    }


    public void showGuide() {
        if (activityWeakReference.get() != null) {
            if (!NetworkUtils.isConnected(activityWeakReference.get())) {
                finishGuideDueToNoNetwork();
                return;
            }
            if (guideLayoutId == 0) {
                throw new IllegalArgumentException("guide layout id cannot be null");
            }
            initView();
            initAnimation();
            addViewToWindow();
            startAnimation();
        }
    }

    /**
     * 先展示引导文案 不显示动画
     * 等targetView显示之后 再显示动画
     * 避免 最后一次数据刷新 等太久 用户点击进入其他页面 导致操作异常
     */
    public void addGuideWindow() {
        if (guideLayoutId == 0) {
            throw new IllegalArgumentException("guide layout id cannot be null");
        }
        if (activityWeakReference.get() != null) {
            if (!NetworkUtils.isConnected(activityWeakReference.get())) {
                showGuideFinishWindowDueToNetError();
                return;
            }
            addViewToWindow();
        }
    }

    private void addViewToWindow() {
        WindowManager.LayoutParams attrs = setWindowAttributes();
        if (attrs != null) {
            wm.addView(contentView, attrs);
            hasAddView = true;
            setIsShowGuide(true);
        }
    }

    public boolean isShowing() {
        return hasAddView;
    }

    /**
     * 最终的targetView 加载完毕 开启引导
     */
    public void startGuide() {
        initView();
        initAnimation();
        startAnimation();
    }

    /**
     * 展示一系列引导图片 不需要穿透点击事件
     */
    public void showGuideWithPics() {
        if (activityWeakReference.get() != null) {
            if (!NetworkUtils.isConnected(activityWeakReference.get())) {
                finishGuideDueToNoNetwork();
                return;
            }
            if (guideLayoutId == 0) {
                throw new IllegalArgumentException("guide layout id cannot be null");
            }
            if (guidePicsArr == null || guidePicsArr.length == 0) return;
            initViewPics();
            addViewToWindow();
        }
    }

    /**
     * 新手引导正常结束 倒计时0.5s 再toast
     */
    public void showLastGuide() {
        isLastGuide = true;
        showLastGuideNew(0, false);
//        if (activityWeakReference.get() != null) {
//            setTartgetOrHighLightRectNull();
//            View guideView = LayoutInflater.from(activityWeakReference.get()).inflate(R.layout.guide_last_page, contentView);
//            guideView.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    skipOrFinishGuide();
//                    handleGuideUpload();
//                }
//            });
//            addViewToWindow();
//        }
    }

    public void showLastGuide(@LayoutRes int layoutId/*, @IdRes int okViewId*/) {
        isLastGuide = true;
        showLastGuideNew(layoutId, false);
//        if (activityWeakReference.get() != null) {
//            setTartgetOrHighLightRectNull();
//            View guideView = LayoutInflater.from(activityWeakReference.get()).inflate(layoutId, contentView);
//            if (guideView == null) return;
//            guideView.findViewById(okViewId).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    skipOrFinishGuide();
//                    handleGuideUpload();
//                }
//            });
//            addViewToWindow();
//        }
    }

    /**
     * 跳过新手引导
     *
     * @param layoutId
     */
    private void skipGuide(@LayoutRes int layoutId) {
        isLastGuide = true;
        showLastGuideNew(layoutId, true);
    }

    /**
     * 正常结束的最后一张引导（需要delay） 或者 点击跳过 （不需要delay）
     *
     * @param layoutId
     */
    public void showLastGuideNew(@LayoutRes final int layoutId, final boolean isSkip) {
        isLastGuide = true;
        if (activityWeakReference.get() != null) {
            setTartgetOrHighLightRectNull();
            addViewToWindow(); // add empty contentview to window to forbidden click
            long delayTime = isSkip ? 0 : 500;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    skipOrFinishGuide();
                    // 事件上报
                    String event = isSkip ? EventConstants.skipEvent : EventConstants.completeEvent;
                    uploadGuideEvent(event);
                    // toast 提示引导结束
                    int toastLayoutId = layoutId == 0 ? R.layout.guide_last_toast_view : layoutId;
                    GuideToast.showToastInternal(activityWeakReference.get(), true, toastLayoutId);
                }
            }, delayTime);
        }
    }

    /**
     * 上报 新手引导完成 / 跳过 /
     */
    private void uploadGuideEvent(String event) {
        switch (event) {
            case EventConstants.completeEvent:
                handleGuideUpload();
                break;
        }
        XmAutoTracker.getInstance().onEvent(event, EventConstants.mainActivity, EventConstants.main);
    }

    private void finishGuideDueToNoNetwork() {
        if (GuideConstants.PET_SHOWED.equals(label)) {
            showGuideFinishWindowDueToNetError(R.layout.guide_finish_page_pet, R.id.tv_ok);
        } else {
            showGuideFinishWindowDueToNetError();
        }
    }

    /**
     * 网络出现异常时 直接显示新手引导结束
     */
    public void showGuideFinishWindowDueToNetError() {
        isLastGuide = true;
        if (activityWeakReference.get() != null) {
            setTartgetOrHighLightRectNull();
            View guideView = LayoutInflater.from(activityWeakReference.get()).inflate(R.layout.guide_finish_page, contentView);
            guideView.findViewById(R.id.tv_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipOrFinishGuide();
                }
            });
            addViewToWindow();
        }
    }

    /**
     * 网络出现异常时 直接显示新手引导结束
     */
    public void showGuideFinishWindowDueToNetError(@LayoutRes int layoutId, @IdRes int okViewId) {
        isLastGuide = true;
        if (activityWeakReference.get() != null) {
            setTartgetOrHighLightRectNull();
            View guideView = LayoutInflater.from(activityWeakReference.get()).inflate(layoutId, contentView);
            if (guideView == null) return;
            guideView.findViewById(okViewId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipOrFinishGuide();
//                    handleGuideUpload();
                }
            });
            addViewToWindow();
        }
    }

    /**
     * 开启动画
     * 判断当前动画类型：
     * 抖动 + 波纹 + 手势
     * 手势
     * 没有动画
     */
    private void startAnimation() {
        if (isLastGuide) return; // 最后一次没有动画
        if (needShake) { // 抖动 + 波纹 + 手势
            handler.post(textRunnable);
        } else if (needHand) { // 手势
            handTranslationX.setRepeatCount(1000);
            handTranslationY.setRepeatCount(1000);
            handler.post(handRunnable);
        } else { // 没有动画
        }
    }

    /**
     * 跳过之后也会显示最后一张新手引导
     * 所以要将两个rect置空 避免点击事件透传
     */
    private void setTartgetOrHighLightRectNull() {
        targetRect = highLightRect = null;
    }

    /**
     * 上传新手引导完成 获取对应金币奖励
     */
    private void handleGuideUpload() {
        if (activityWeakReference.get() == null || label.isEmpty())
            return;
        switch (label) {
            case GuideConstants.MUSIC_SHOWED:
                XmProperties.build().put("MUSIC_HAS_GUIDED", true);
                break;
            case GuideConstants.XTING_SHOWED:
                XmProperties.build().put("FM_HAS_GUIDED", true);
                break;
            case GuideConstants.PERSONAL_SHOWED:
                XmProperties.build().put("PERSONAL_HAS_GUIDED", true);
                break;
            case GuideConstants.SERVICE_SHOWED:
                XmProperties.build().put("SERVICE_HAS_GUIDED", true);
                break;
            case GuideConstants.APPSTORE_SHOWED:
                XmProperties.build().put("APPSTORE_HAS_GUIDED", true);
                break;
            case GuideConstants.CLUB_SHOWED:
                XmProperties.build().put("CLUB_HAS_GUIDED", true);
                break;
            case GuideConstants.CAR_PARK_SHOWED:
                XmProperties.build().put("CAR_HAS_GUIDED", true);
                break;
            case GuideConstants.SHOP_SHOWED:
                XmProperties.build().put("SHOP_HAS_GUIDED", true);
                break;
            default:
                break;
        }
        // 所有的向导已完成，上报
        if (getValue("MUSIC_HAS_GUIDED") && getValue("FM_HAS_GUIDED") &&
                getValue("PERSONAL_HAS_GUIDED") && getValue("SERVICE_HAS_GUIDED") &&
                getValue("APPSTORE_HAS_GUIDED") && getValue("CLUB_HAS_GUIDED") &&
                getValue("CAR_HAS_GUIDED") && getValue("SHOP_HAS_GUIDED") &&
                getValue("LAUNCHER_HAS_GUIDED") && NetworkUtils.isConnected(activityWeakReference.get())) {
            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.GUIDETASK.getType());
            KLog.d(TAG, "guide event upload");
        }
    }

    private boolean getValue(String key) {
        boolean hasGuided = XmProperties.build().get(key, false);
        return hasGuided;
    }


    /**
     * 设置window属性
     *
     * @return
     */
    @NonNull
    private WindowManager.LayoutParams setWindowAttributes() {
        if (activityWeakReference.get() == null) return null;
        wm = (WindowManager) activityWeakReference.get().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams attrs = new WindowManager.LayoutParams();
        attrs.gravity = Gravity.NO_GRAVITY;
        attrs.width = ScreenUtils.getScreenWidth(activityWeakReference.get());
        attrs.height = ScreenUtils.getScreenHeight(activityWeakReference.get());
        attrs.format = PixelFormat.RGBA_8888;
        attrs.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        attrs.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;
        return attrs;
    }


    /**
     * 跳过新手引导
     */
    private void skipOrFinishGuide() {
        dismissGuideWindow();
        setGuideFinish();
    }

    /**
     * 获取view在屏幕中的位置
     *
     * @param view
     * @return
     */
    public static Rect getViewRect(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        return new Rect(left, top, left + view.getWidth(), top + view.getHeight());
    }


    private void stopAnimator() {
        if (targetView != null && targetView.get() != null)
            targetView.get().clearAnimation();
        if (waveViewOne != null)
            waveViewOne.clearAnimation();
        if (waveViewTwo != null)
            waveViewTwo.clearAnimation();
        if (waveViewThree != null)
            waveViewThree.clearAnimation();
        if (handView != null)
            handView.clearAnimation();
        if (translateAnimation != null) {
            targetViewAnimationCancled = true;
            translateAnimation.cancel();
            translateAnimation = null;
        }
        if (waveAnimatorSet != null) {
            waveAnimatorSet.cancel();
            waveAnimatorSet = null;
        }
        if (handAnimatorSet != null) {
            handAnimatorSet.cancel();
            handAnimatorSet = null;
        }
        if (handTranslationX != null) {
            handTranslationX.cancel();
            handTranslationX = null;
        }
        if (handTranslationY != null) {
            handTranslationY.cancel();
            handTranslationY = null;
        }
    }


    public void dismissGuideWindow() {
        if (contentView == null || wm == null || !hasAddView) return;
        if (!isLastGuide) {
            stopAnimator();
            removeHandlerCallBacks();
        }
        contentView.removeAllViews();
        wm.removeView(contentView);
        hasAddView = false;
    }

    private void removeHandlerCallBacks() {
        handler.removeCallbacks(textRunnable);
        handler.removeCallbacks(handRunnable);
        handler.removeCallbacks(waveRunnable);
        handler.removeCallbacks(stopHandRunnable);
    }

    public void setGuideFinish() {
        if (activityWeakReference.get() != null)
            TPUtils.put(activityWeakReference.get(), label, true);
        // 二次引导
        GuideDataHelper.finishGuideData(label);
        setIsShowGuide(false);
        if (guideFinishCallBack != null) {
            guideFinishCallBack.onGuideFinish();
        }
    }

    /**
     * 当前新手引导是否正在显示
     *
     * @return true 正在显示
     */
    public static boolean isGuideShowNow() {
        return isGuideShowNow;
    }

    /**
     * 设置新手引导结束回调
     *
     * @param callBack
     */
    public static void setGuideFinishCallBack(GuideFinishCallBack callBack) {
        guideFinishCallBack = callBack;
    }

    public void setIsShowGuide(boolean isShow) {
        isGuideShowNow = isShow;
    }

    public void setTargetView(View view) {
        if (view == null) return;
        targetView = new WeakReference<>(view);
    }

    public void setTargetRect(Rect rect) {
        targetRect = rect;
    }

    public void setTargetViewAndRect(View view) {
        if (view == null) return;
        targetView = new WeakReference<>(view);
        targetRect = getViewRect(view);
    }

    public void setHighLightRect(Rect rect) {
        highLightRect = rect;
    }

    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        private NewGuide guide;

        public Builder(Activity activity) {
            guide = new NewGuide(activity);
        }

        public Builder setLebal(@NonNull String lebal) {
            guide.label = lebal;
            return this;
        }

        public Builder setTargetView(View view) {
            guide.targetView = new WeakReference<>(view);
            return this;
        }

        public Builder setTargetViewAndRect(View view) {
            if (view == null) return this;
            guide.targetView = new WeakReference<>(view);
            guide.targetRect = getViewRect(view);
            return this;
        }

        public Builder setTargetViewId(@IdRes int targetViewId) {
            guide.targetViewId = targetViewId;
            return this;
        }

        public Builder setCallBack(GuideCallBack callBack) {
            guide.callBack = callBack;
            return this;
        }

        public Builder setTargetRect(Rect targetRect) {
            guide.targetRect = targetRect;
            return this;
        }

        public Builder setHighLightRect(Rect backRect) {
            guide.highLightRect = backRect;
            return this;
        }

        public Builder setGuideLayoutId(@LayoutRes int layoutId) {
            guide.guideLayoutId = layoutId;
            return this;
        }

        public Builder setNeedShake(boolean needShake) {
            guide.needShake = needShake;
            return this;
        }

        public Builder setNeedHande(boolean needHande) {
            guide.needHand = needHande;
            return this;
        }

        public Builder setViewHandId(@IdRes int handId) {
            guide.ivHandId = handId;
            return this;
        }

        public Builder setViewWaveIdOne(@IdRes int waveId) {
            guide.ivWaveIdOne = waveId;
            return this;
        }

        public Builder setViewWaveIdTwo(@IdRes int waveId) {
            guide.ivWaveIdTwo = waveId;
            return this;
        }

        public Builder setViewWaveIdThree(@IdRes int waveId) {
            guide.ivWaveIdThree = waveId;
            return this;
        }

        public Builder setViewSkipId(@IdRes int skipId) {
            guide.skipId = skipId;
            return this;
        }

        public Builder setTargetViewTranslationX(float x) {
            guide.targetViewTransX = x;
            return this;
        }

        public Builder setHandLocation(int handLocation) {
            guide.handLocation = handLocation;
            return this;
        }

        public Builder setGuideTextDesc(Spanned textDesc) {
            guide.guideDesc = textDesc;
            return this;
        }

        public Builder setGuideTextId(@IdRes int textId) {
            guide.tvGuideTextId = textId;
            return this;
        }

        public Builder setSupportScroll(boolean canScroll) {
            guide.canScroll = canScroll;
            return this;
        }

        public Builder setNextStepViewId(@IdRes int nextStepViewId) {
            guide.nextStepViewId = nextStepViewId;
            return this;
        }

        public Builder setGuidePics(@DrawableRes int[] guidePicsArr) {
            guide.guidePicsArr = guidePicsArr;
            return this;
        }

        public Builder setGuidePicViewId(@IdRes int guidePicViewId) {
            guide.guidePicViewId = guidePicViewId;
            return this;
        }

        public Builder needMoveUpALittle(boolean need) {
            guide.needMoveUpALittle = need;
            return this;
        }

        public NewGuide build() {
            return guide;
        }
    }
}
