package com.xiaoma.systemui.topbar.controller;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.systemui.R;
import com.xiaoma.systemui.common.controller.BaseController;
import com.xiaoma.systemui.common.util.DateTimeUtil;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.common.util.SysUIToast;
import com.xiaoma.systemui.navigationbar.NavigationBarController;
import com.xiaoma.systemui.topbar.model.NotificationModelHelper;
import com.xiaoma.systemui.topbar.view.DraggableParent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static com.xiaoma.systemui.topbar.model.NotificationModelHelper.getNotificationTime;

/**
 * Created by LKF on 2018/11/14 0014.
 */
public class PanelController extends BaseController {
    @SuppressLint("StaticFieldLeak")
    private static PanelController sInstance;

    public static PanelController getInstance() {
        if (sInstance == null) {
            synchronized (TopBarController.class) {
                if (sInstance == null) {
                    sInstance = new PanelController();
                }
            }
        }
        return sInstance;
    }

    private PanelController() {
    }

    private DraggableParent mPanelParent;
    private View mPanelView;
    private View mEmptyView;
    private RecyclerView mRvNotifications;

    private ObjectAnimator mNotificationAnim;
    private NotificationListAdapter mNotificationListAdapter;
    private boolean mIsCollapsingPanel;
    private boolean mIsExpandingPanel;
    private int mPanelHeight;

    private final NotificationModelHelper mNotificationModelHelper = new NotificationModelHelper();

    @SuppressLint("ClickableViewAccessibility")
    public void init(final Context context) {
        super.init(context);
        mPanelParent = (DraggableParent) View.inflate(context, R.layout.panel_view_parent, null);

        // 进入应急模式的暗门
        Button btnLauncher = mPanelParent.findViewById(R.id.btn_launcher);
        btnLauncher.setVisibility(View.VISIBLE);
        if (ConfigManager.ApkConfig.isDebug()) {
            btnLauncher.setAlpha(1.0f);
            btnLauncher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PackageManager pm = context.getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
                    if (intent != null) {
                        context.startActivity(intent);
                    }
                    dismiss();
                }
            });
        } else {
            btnLauncher.setAlpha(0f);
            btnLauncher.setOnClickListener(new View.OnClickListener() {
                private int clickTime = 0;
                private final Runnable resetClickTimeTask = new Runnable() {
                    @Override
                    public void run() {
                        clickTime = 0;
                    }
                };

                @Override
                public void onClick(View v) {
                    v.removeCallbacks(resetClickTimeTask);
                    if (++clickTime >= 10) {
                        PackageManager pm = context.getPackageManager();
                        Intent intent = pm.getLaunchIntentForPackage(context.getPackageName());
                        if (intent != null) {
                            context.startActivity(intent);
                        }
                        dismiss();
                        clickTime = 0;
                    } else {
                        v.postDelayed(resetClickTimeTask, 2000);
                    }
                }
            });

        }

        mPanelParent.setDragCallback(new DraggableParent.DragCallback() {
            @Override
            public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
                if (R.id.dim_overlay == capturedChild.getId()) {
                    logI("dim_overlay onViewCaptured");
                }
                if (mCallback != null)
                    mCallback.onViewCaptured(capturedChild, activePointerId);
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                if (R.id.dim_overlay == child.getId()) {
                    logI("dim_overlay clampViewPositionVertical( top: %s, dy: %s )", top, dy);
                }
                if (mCallback != null) {
                    return mCallback.clampViewPositionVertical(child, top, dy);
                }
                return 0;
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xVel, float yVel) {
                boolean doCallback = true;
                if (R.id.dim_overlay == releasedChild.getId()) {
                    // 没有滑动速度时,认为是点击事件
                    if (xVel == 0 && yVel == 0) {
                        animateCollapsePanel();
                        doCallback = false;
                    }
                    logI("dim_overlay onViewReleased( xVel: %s, yVel: %s )", xVel, yVel);
                }
                if (mCallback != null && doCallback) {
                    mCallback.onViewReleased(releasedChild, xVel, yVel);
                }
                mRvNotifications.setLayoutFrozen(false);
                //mRvNotifications.smoothCloseMenu();
            }

            @Override
            public boolean shouldConsumeNestedScroll(@NonNull View target, int dx, int dy) {
                if (mCallback != null) {
                    return mCallback.shouldConsumeNestedScroll(target, dx, dy);
                }
                return false;
            }
        });

        mPanelView = mPanelParent.findViewById(R.id.panel_view);
        final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mPanelView.getLayoutParams();
        lp.topMargin = -getPanelHeight();

        mEmptyView = mPanelParent.findViewById(R.id.empty_container);

        mRvNotifications = mPanelView.findViewById(R.id.rv_notifications);
        mRvNotifications.setHasFixedSize(false);
        mRvNotifications.setLayoutManager(new LinearLayoutManager(context));
        final DefaultItemAnimator itemAnimator = (DefaultItemAnimator) mRvNotifications.getItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);// 屏蔽Item闪烁效果
        // 绘制时间轴
        mRvNotifications.addItemDecoration(new RecyclerView.ItemDecoration() {
            private final String TAG = "PanelItemDecoration";
            private final Resources RES = context.getResources();
            private final int LEFT_PADDING = RES.getDimensionPixelSize(R.dimen.notification_stick_left_padding);
            private final int TOP_PADDING = RES.getDimensionPixelSize(R.dimen.notification_stick_top_padding);
            private final int WIDTH = RES.getDimensionPixelSize(R.dimen.notification_stick_width);

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                final RecyclerView.LayoutManager lm = parent.getLayoutManager();
                final int verOffset = lm.computeVerticalScrollOffset(state);
                final int verRange = lm.computeVerticalScrollRange(state);
                LogUtil.logI(TAG, "onDraw() { verOffset: %s, verRange: %s }", verOffset, verRange);
                final Drawable stickDr = context.getDrawable(R.drawable.notification_timeline_stick_bg);
                final int top = Math.max(TOP_PADDING - verOffset, 0);
                final int bottom = getPanelHeight();
                stickDr.setBounds(LEFT_PADDING, top, LEFT_PADDING + WIDTH, bottom);
                stickDr.draw(c);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }

            /* @Override
            public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
                Drawable dr = parent.getResources().getDrawable(R.drawable.notification_timeline_stick_bg);
                //dr.setBounds(8, 108, 8, parent.getMeasuredHeight());
                dr.setBounds(0, 0, 8, parent.getMeasuredHeight());
                dr.draw(c);
            }*/
        });
        mRvNotifications.setAdapter(mNotificationListAdapter = new NotificationListAdapter(mNotificationModelHelper, new NotificationListAdapter.Callback() {
            @Override
            public void onNotificationClick(StatusBarNotification sbn) {
                //触发通知点击Intent
                sendNotificationIntent(sbn);
                animateCollapsePanel();
            }

            @Override
            public void onCancelNotification(StatusBarNotification sbn) {
                if (sbn.isClearable()) {
                    cancelNotification(sbn);
                } else {
                    SysUIToast.makeTextAndShow(context, R.string.notification_cannot_remove_tips, Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onCancelNotifications(List<StatusBarNotification> sbnList) {
                final Set<String> keys = new ArraySet<>(sbnList.size());
                for (final StatusBarNotification sbn : sbnList) {
                    keys.add(sbn.getKey());
                }
                cancelNotifications(keys);
            }

            @Override
            public void onCancelNotificationsByDate(Date date) {
                final Set<String> keys = new ArraySet<>();
                final List<StatusBarNotification> notifications = mNotificationModelHelper.getDisplayingNotifications();
                for (final StatusBarNotification sbn : notifications) {
                    final Date sbnDate = new Date(getNotificationTime(sbn));
                    if (DateTimeUtil.isSameDay(sbnDate, date)) {
                        keys.add(sbn.getKey());
                    }
                }
                cancelNotifications(keys);
            }

            @Override
            public void onGroupExpand(final StatusBarNotification sbn) {
                updateNotificationsByDiff(new Runnable() {
                    @Override
                    public void run() {
                        mNotificationModelHelper.setGroupCollapse(sbn, false);
                    }
                });
            }

            @Override
            public void onGroupCollapse(final StatusBarNotification sbn) {
                updateNotificationsByDiff(new Runnable() {
                    @Override
                    public void run() {
                        mNotificationModelHelper.setGroupCollapse(sbn, true);
                    }
                });
            }
        }));
    }

    @Override
    public void show() {
        final NavigationBarController controller = NavigationBarController.getInstance();
        int startMargin = getContext().getResources().getDimensionPixelSize(R.dimen.notification_panel_start_margin);
        if (controller.isShowing()) {
            startMargin -= controller.getNavigationBarWidth();
        }
        final FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mPanelView.getLayoutParams();
        lp.setMarginStart(startMargin);
        logI("show() controller.getNavigationBarWidth() = %s", controller.getNavigationBarWidth());
        controller.setKeyInterceptor(new NavigationBarController.KeyInterceptor() {
            @Override
            public boolean onKeyEvent(int keyCode) {
                boolean handled = true;
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_HOME:
                        animateCollapsePanel();
                        break;
                    default:
                        handled = false;
                }
                return handled;
            }
        });
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        NavigationBarController.getInstance().setKeyInterceptor(null);
        // 回到顶部
        final LinearLayoutManager lm = (LinearLayoutManager) mRvNotifications.getLayoutManager();
        lm.scrollToPositionWithOffset(0, 0);
    }

    private void sendNotificationIntent(StatusBarNotification sbn) {
        //触发通知点击Intent
        final Notification notification = sbn.getNotification();
        if (notification != null && notification.contentIntent != null) {
            try {
                notification.contentIntent.send();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final int flags = notification.flags;
            if ((flags & Notification.FLAG_AUTO_CANCEL) != 0) {
                cancelNotification(sbn);
            }
        }
    }

    /**
     * 收起通知栏
     */
    void animateCollapsePanel() {
        if (mIsCollapsingPanel) {
            logI("animateCollapsePanel() Is doing");
            return;
        }
        logI("animateCollapsePanel()");
        mIsCollapsingPanel = true;
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                final Animator animator = makeAnim(getTranslationY(), 0);
                animator.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dismiss();
                        mIsCollapsingPanel = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mIsCollapsingPanel = false;
                    }
                });
                animator.start();
            }
        });
    }


    /**
     * 展开通知栏
     */
    void animateExpandPanel() {
        if (mIsExpandingPanel) {
            logI("animateExpandPanel() Is doing");
            return;
        }
        logI("animateExpandPanel()");
        mIsExpandingPanel = true;
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                show();
                final Animator animator = makeAnim(getTranslationY(), getPanelHeight());
                animator.addListener(new SimpleAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mIsExpandingPanel = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mIsExpandingPanel = false;
                    }
                });
                animator.start();
            }
        });
    }

    private Animator makeAnim(float fromY, float toY) {
        if (mNotificationAnim != null) {
            mNotificationAnim.cancel();
        }
        mNotificationAnim = ObjectAnimator.ofFloat(mPanelView, "translationY", fromY, toY);
        mNotificationAnim.setInterpolator(new LinearInterpolator());
        mNotificationAnim.setDuration(150);
        return mNotificationAnim;
    }


    int getPanelHeight() {
        if (mPanelHeight <= 0) {
            mPanelHeight = getContext().getResources().getDimensionPixelSize(R.dimen.notification_panel_height);
        }
        return mPanelHeight;
    }

    float getTranslationY() {
        return mPanelView.getTranslationY();
    }

    void setTranslationY(float translationY) {
        mPanelView.setTranslationY(translationY);
    }

    @Override
    protected WindowManager.LayoutParams makeLp() {
        final Context context = getContext();
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_STATUS_BAR_PANEL,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                PixelFormat.TRANSLUCENT);
        lp.token = new Binder();
        lp.gravity = Gravity.TOP;
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        lp.setTitle("Panel");
        lp.packageName = context.getPackageName();
        return lp;
    }

    @Override
    protected View getSystemUIView() {
        return mPanelParent;
    }

    public void updateNotifications() {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                updateNotificationsByDiff(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            StatusBarNotification[] notificationArr = mNotificationMgrDelegate.getNotifications();
                            mNotificationModelHelper.setDataSource(notificationArr);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
                mEmptyView.setVisibility(mNotificationModelHelper.getDisplayingNotifications().isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void updateNotificationsByDiff(Runnable action) {
        final List<StatusBarNotification> oldList = new ArrayList<>(mNotificationModelHelper.getDisplayingNotifications());
        action.run();
        final List<StatusBarNotification> newList = mNotificationModelHelper.getDisplayingNotifications();
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new NotificationDiffCallback(
                        oldList,
                        newList),
                true);
        final long t0 = System.currentTimeMillis();
        diffResult.dispatchUpdatesTo(mNotificationListAdapter);
        final long t1 = System.currentTimeMillis();
        logI("updateNotificationsByDiff() Cost time: %s ms", t1 - t0);
    }

    // 播放通知音效
    private void playSoundEffect(@NonNull Notification n) {
        Uri soundUri = n.sound;
        if (soundUri == null) {
            soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            logI("playSoundEffect() soundUri is null, fetch system default: %s", soundUri);
        }
        if (soundUri == null) {
            logI("playSoundEffect() system default is null");
            return;
        }
        final Ringtone ringtone = RingtoneManager.getRingtone(getContext(), soundUri);
        if (ringtone == null) {
            logI("playSoundEffect() ringtone is null");
            return;
        }
        logI("playSoundEffect() do play!");
        ringtone.play();
    }

    private DraggableParent mHeadsUpParent;
    private boolean mHeadsUpDragging;
    private final Handler mDismissHeadsUpHandler = new Handler(Looper.getMainLooper());
    private final Runnable mDismissHeadsUpTask = new Runnable() {
        @Override
        public void run() {
            if (mHeadsUpDragging)
                return;
            removeCurHeadsUpView(true);
        }
    };

    // 显示通知浮层
    public void showHeadsUpView(final StatusBarNotification sbn) {
        if (!NotificationModelHelper.canNotificationShow(sbn)) {
            logI(String.format("showHeadsUpView() Notification cannot show: %s", sbn != null ? sbn.getPackageName() : "null"));
            return;
        }
        if (isShowing()) {
            logI("showHeadsUpView() Panel is showing");
            return;
        }
        final Notification n = sbn.getNotification();
        final RemoteViews remoteViews = n.headsUpContentView;
        if (remoteViews == null) {
            logI("showHeadsUpView() headsUpContentView is null");
            return;
        }
        removeCurHeadsUpView(false);
        final DraggableParent parent = (DraggableParent) View.inflate(getContext(), R.layout.notification_heads_up_parent, null);
        if (n.contentIntent != null) {
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendNotificationIntent(sbn);
                }
            });
        }

        final View headsUpView = remoteViews.apply(getContext(), parent);
        headsUpView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dock_top_enter));
        parent.addView(headsUpView);
        parent.setDragCallback(new DraggableParent.DragCallback() {
            private boolean mInDragMode;// 用于区分点击和拖拽事件

            @Override
            public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
                logI("HeadsUp onViewCaptured()");
                mInDragMode = false;
                mHeadsUpDragging = true;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                logI("HeadsUp clampViewPositionVertical( top: %s, dy: %s )", top, dy);
                if (!mInDragMode) {
                    if (Math.abs(top) >= Math.sqrt(ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
                        mInDragMode = true;
                    }
                }
                float y = child.getTranslationY() + dy;
                if (y > 0) {
                    y = 0;
                } else if (y < -child.getMeasuredHeight()) {
                    y = -child.getMeasuredHeight();
                }
                child.setTranslationY(y);
                return 0;
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xVel, float yVel) {
                logI("HeadsUp onViewReleased( xVel: %s, yVel: %s ) y: %s", xVel, yVel, releasedChild.getTranslationY());
                if (mInDragMode) {
                    if (yVel < 0) {
                        removeCurHeadsUpView(true);
                    } else if (yVel > 0) {
                        animateExpandHeadsUpView(headsUpView);
                    } else {
                        if (releasedChild.getTop() < -releasedChild.getMeasuredHeight() / 2) {
                            removeCurHeadsUpView(true);
                        } else {
                            animateExpandHeadsUpView(headsUpView);
                        }
                    }
                } else {
                    try {
                        n.contentIntent.send();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    removeCurHeadsUpView(false);
                }
                mInDragMode = false;
                mHeadsUpDragging = false;
            }

            @Override
            public boolean shouldConsumeNestedScroll(@NonNull View target, int dx, int dy) {
                logI("HeadsUp shouldConsumeNestedScroll( dx: %s, dy: %s )", dx, dy);
                return true;
            }
        });
        getWindowManager().addView(parent, makeHeadsUpLp());
        mHeadsUpParent = parent;
        // 自动消失
        scheduleRemoveHeadsUpView();
    }

    private void removeCurHeadsUpView(boolean animate) {
        if (mHeadsUpParent != null) {
            if (animate) {
                final Animation exitAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dock_top_exit);
                exitAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        removeCurHeadsUpView(false);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                final View headsUpView = mHeadsUpParent.getChildAt(0);
                headsUpView.startAnimation(exitAnim);
            } else {
                mHeadsUpParent.clearAnimation();
                try {
                    getWindowManager().removeViewImmediate(mHeadsUpParent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    mHeadsUpParent = null;
                }
            }
        }
    }

    private void animateExpandHeadsUpView(@NonNull View v) {
        v.clearAnimation();
        final ObjectAnimator anim = ObjectAnimator.ofFloat(v, "translationY", v.getTranslationY(), 0)
                .setDuration(getContext().getResources().getInteger(R.integer.notification_heads_up_anim_time));
        anim.setInterpolator(new LinearInterpolator());
        anim.setAutoCancel(true);
        anim.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                scheduleRemoveHeadsUpView();
            }
        });
        anim.start();
    }

    private void scheduleRemoveHeadsUpView() {
        mDismissHeadsUpHandler.removeCallbacks(mDismissHeadsUpTask);
        mDismissHeadsUpHandler.postDelayed(mDismissHeadsUpTask, getContext().getResources().getInteger(R.integer.notification_heads_up_lasting_time));
    }

    private WindowManager.LayoutParams makeHeadsUpLp() {
        final Context context = getContext();
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_DISPLAY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT);
        lp.token = new Binder();
        lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        lp.setTitle("HeadsUpContentView");
        lp.packageName = context.getPackageName();
        return lp;
    }

    /**
     * 移除多个通知
     */
    private void cancelNotifications(Set<String> keys) {
        logI("cancelNotifications( keys: %s )", keys);
        if (keys == null || keys.isEmpty())
            return;
        if (mNotificationMgrDelegate != null) {
            mNotificationMgrDelegate.cancelNotifications(keys);
        }
    }

    // 移除某个通知
    private void cancelNotification(StatusBarNotification sbn) {
        logI("cancelNotification( sbn: %s )", sbn);
        if (sbn == null)
            return;
        if (mNotificationMgrDelegate != null) {
            mNotificationMgrDelegate.cancelNotification(sbn.getKey());
        }
    }

    // 移除所有通知
    /*private void cancelAllNotifications() {
        logI("cancelAllNotifications()");
        if (mNotificationMgrDelegate != null) {
            mNotificationMgrDelegate.cancelAllNotifications();
        }
    }*/

    private DraggableParent.DragCallback mCallback;

    void setCallback(final DraggableParent.DragCallback callback) {
        mCallback = callback;
    }

    private NotificationMgrDelegate mNotificationMgrDelegate;

    public void setNotificationMgrDelegate(NotificationMgrDelegate notificationMgrDelegate) {
        mNotificationMgrDelegate = notificationMgrDelegate;
    }

    public interface NotificationMgrDelegate {
        void cancelNotification(String key);

        void cancelNotifications(Set<String> keys);

        void cancelAllNotifications();

        StatusBarNotification[] getNotifications();
    }
}