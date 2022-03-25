package com.xiaoma.personal.taskcenter.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.guide.listener.GuideCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.personal.R;
import com.xiaoma.personal.coin.model.CoinAndSignInfo;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.common.util.RecyclerViewHelper;
import com.xiaoma.personal.common.view.StepView;
import com.xiaoma.personal.taskcenter.adapter.TaskRecordAdapter;
import com.xiaoma.personal.taskcenter.constract.TaskType;
import com.xiaoma.personal.taskcenter.model.TaskNote;
import com.xiaoma.personal.taskcenter.vm.TaskCenterVM;
import com.xiaoma.ui.StateControl.OnRetryClickListener;
import com.xiaoma.ui.StateControl.StateView;
import com.xiaoma.ui.StateControl.Type;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.xiaoma.guide.utils.NewGuide.LEFT_AND_BOTTOM;
import static com.xiaoma.guide.utils.NewGuide.RIGHT_AND_TOP;


@PageDescComponent(EventConstants.PageDescribe.personalCenterTaskCenter)
public class TaskCenterActivity extends BaseActivity implements View.OnClickListener {

    private ConstraintLayout clTaskLeft;
    private StepView mSignInView;
    private ImageView mIvCenter;
    private TextView mScoreTV;
    private Button mBtSubmit;
    private TextView tvTips;
    private ImageView ivSmallCoin;
    private TaskCenterVM mVM;
    private TextView mTvDaily;
    private TextView mTvGrow;
    private TextView mTvActivity;
    private TextView mTvNotes;
    private List<Fragment> mFragmentList;
    private DrawerLayout mDraw;
    private RecyclerView mDrawRecyclerView;
    private StateView mTaskNoteStateView;
    private XmScrollBar mScrollBar;
    private BaseQuickAdapter<TaskNote, BaseViewHolder> mAdapter;
    private int nextGetCoin;
    private int mTaskRecordPage;
    private SimpleDateFormat mDateFormat;
    private volatile boolean signInFlag = false;
    private OnSignInCallback onSignInCallback;
    private NewGuide newGuide;
    private int clockInDays = 0;

    private DrawerLayout.SimpleDrawerListener mDrawerListener = new DrawerLayout.SimpleDrawerListener() {

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            //侧滑栏打开时,解锁
            mDraw.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            showThirdGuideWindow();
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            //禁止手动滑出侧滑栏造成的状态切换问题
            mDraw.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            showLastGuideWindow();
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            if (newState == DrawerLayout.STATE_SETTLING) {
                if (mDraw.isDrawerOpen(Gravity.START)) {
                    getNaviBar().showBackAndHomeNavi();
                } else {
                    getNaviBar().showBackNavi();
                }
            }
        }
    };

    public static void launch(Context context) {
        context.startActivity(new Intent(context, TaskCenterActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_center);
        initView();
        initDrawRecyclerView();
        initData();
    }

    private void initView() {
        clTaskLeft = findViewById(R.id.cl_task_left);
        mSignInView = findViewById(R.id.signin_view);
        mIvCenter = findViewById(R.id.iv_center);
        mScoreTV = findViewById(R.id.tvScore);
        mBtSubmit = findViewById(R.id.bt_submit);
        tvTips = findViewById(R.id.tv_tips);
        ivSmallCoin = findViewById(R.id.iv_small_coin);
        mBtSubmit.setOnClickListener(this);
        mSignInView = findViewById(R.id.signin_view);
        mSignInView.setOnClickListener(this);
        mIvCenter = findViewById(R.id.iv_center);
        mIvCenter.setOnClickListener(this);
        mScoreTV.setOnClickListener(this);
        mTvDaily = findViewById(R.id.tv_daily);
        mTvGrow = findViewById(R.id.tv_grow);
        mTvActivity = findViewById(R.id.tv_activity);
        mTvNotes = findViewById(R.id.tv_notes);
        mTvDaily = findViewById(R.id.tv_daily);
        mTvDaily.setOnClickListener(this);
        mTvGrow = findViewById(R.id.tv_grow);
        mTvGrow.setOnClickListener(this);
        mTvActivity = findViewById(R.id.tv_activity);
        mTvActivity.setOnClickListener(this);
        mTvNotes = findViewById(R.id.tv_notes);
        mTvNotes.setOnClickListener(this);
        mDraw = findViewById(R.id.draw);
        mDrawRecyclerView = findViewById(R.id.draw_recycler_view);
        mTaskNoteStateView = findViewById(R.id.task_note_state_view);
        mScrollBar = findViewById(R.id.scroll_bar);
    }

    private void initDrawRecyclerView() {
        mDrawRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new TaskRecordAdapter();
        mAdapter.bindToRecyclerView(mDrawRecyclerView);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mTaskRecordPage++;
                mVM.fetchTaskNoteList(mTaskRecordPage);
            }
        }, mDrawRecyclerView);
        RecyclerViewHelper.addVerticalDivider(mDrawRecyclerView, R.drawable.horizontal_divide_line);
        mDraw.addDrawerListener(mDrawerListener);
        mTaskNoteStateView.setOnRetryClickListener(new OnRetryClickListener() {
            @Override
            public void onRetryClick(View view, Type type) {
                switch (type) {
                    case EEMPTY:
                        // do nothing
                        break;
                    case ERROR:
                    case NONETWORK:
                        mTaskRecordPage = 0;
                        mVM.fetchTaskNoteList(mTaskRecordPage);
                        break;
                }
            }
        });

        mScrollBar.setRecyclerView(mDrawRecyclerView);
    }

    private void initData() {
        mVM = ViewModelProviders.of(this).get(TaskCenterVM.class);

        mFragmentList = new ArrayList<>();
        mFragmentList.add(TaskDailyFragment.newInstance());
        mFragmentList.add(TaskGrowFragment.newInstance());
        mFragmentList.add(TaskActivityFragment.newInstance());
        FragmentUtils.add(getSupportFragmentManager(), mFragmentList, R.id.fl_content, TaskType.DAILY);
        switchTaskType(TaskType.DAILY);
        mVM.getTaskNoteListLiveData().observe(this, new Observer<XmResource<List<TaskNote>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<TaskNote>> listXmResource) {
                listXmResource.handle(new OnCallback<List<TaskNote>>() {
                    @Override
                    public void onLoading() {
                        if (mTaskRecordPage == 0) {
                            mTaskNoteStateView.showLoading();
                        }
                    }

                    @Override
                    public void onSuccess(List<TaskNote> data) {
                        mAdapter.loadMoreComplete();
                        if (!ListUtils.isEmpty(data)) {
                            mTaskNoteStateView.showContent();
                            if (mTaskRecordPage == 0) {
                                mAdapter.setNewData(data);
                            } else {
                                mAdapter.addData(data);
                            }
                        } else {
                            if (mTaskRecordPage != 0) {
                                mAdapter.loadMoreEnd();
                            } else {
                                mTaskNoteStateView.showEmpty();
                            }
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        if (mTaskRecordPage > 0) {
                            mTaskRecordPage--;
                        }
                        if (NetworkUtils.isConnected(getApplicationContext())) {
                            mTaskNoteStateView.showError();
                        } else {
                            mTaskNoteStateView.showNoNetwork();
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (mTaskRecordPage > 0) {
                            mTaskRecordPage--;
                        }
                        if (NetworkUtils.isConnected(getApplicationContext())) {
                            mTaskNoteStateView.showError();
                        } else {
                            mTaskNoteStateView.showNoNetwork();
                        }
                    }
                });
            }
        });

        mVM.getSignedInfoLiveData().observe(this, new Observer<XmResource<CoinAndSignInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<CoinAndSignInfo> coinAndSignInfoXmResource) {
                coinAndSignInfoXmResource.handle(new OnCallback<CoinAndSignInfo>() {
                    @Override
                    public void onFailure(String msg) {
                        XMToast.showToast(TaskCenterActivity.this, msg);
//                        if (NetworkUtils.isConnected(TaskCenterActivity.this)) {
//                            showErrorView();
//                        } else {
                        showNoNetView();
//                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        XMToast.showToast(TaskCenterActivity.this, message);
                        if (NetworkUtils.isConnected(TaskCenterActivity.this)) {
                            showErrorView();
                        } else {
                            showNoNetView();
                        }
                    }

                    @Override
                    public void onSuccess(CoinAndSignInfo data) {
                        mBaseStateView.showContent();
                        nextGetCoin = data.getNextSigncoin();
                        clockInDays = data.getSignInProgress();
                        if (data.getIsSigned() == 1) {
                            signSuccess(false);
                            mSignInView.setCurrent(data.getSignInProgress());
                        } else {
                            mBtSubmit.setText(getString(R.string.sign_in_day, data.getSignInProgress(), data.getSignMaxProgress()));
                            mScoreTV.setText(StringUtil.getSignedNumber(data.getNextSigncoin()));
                            mSignInView.setCurrent(data.getSignInProgress());
                        }
                        clTaskLeft.setVisibility(View.VISIBLE);
                        showFirstGuideWindow();
                        XmAutoTracker.getInstance().onPunchEvent("打卡", data.getIsSigned(), clockInDays,
                                "TaskCenterActivity", EventConstants.PageDescribe.personalCenterTaskCenter);
                    }
                });
            }
        });

        mVM.getSignInStateLiveData().observe(this, new Observer<XmResource<XMResult<OnlyCode>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<OnlyCode>> resource) {
                resource.handle(new OnCallback<XMResult<OnlyCode>>() {
                    @Override
                    public void onLoading() {
                        showProgressDialog(R.string.signing);
                    }

                    @Override
                    public void onFailure(String msg) {
                        if (!NetworkUtils.isConnected(TaskCenterActivity.this)
                                || msg == null
                                || msg.isEmpty()
                                || msg.trim().isEmpty()
                        ) {
                            XMToast.toastException(TaskCenterActivity.this, R.string.no_network);
                        } else {
                            XMToast.toastException(TaskCenterActivity.this, msg);
                        }
                    }

                    @Override
                    public void onSuccess(XMResult<OnlyCode> data) {
                        signSuccess(true);
                    }
                });
            }
        });
        if (NetworkUtils.isConnected(getApplication())) {
            mVM.fetchSignedInInfo();
        } else {
            mBaseStateView.showNoNetwork();
        }

    }

    private String getDateString(Date date) {
        if (mDateFormat == null) {
            mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        }
        return mDateFormat.format(date);
    }

    private String getCurrentDate() {
        return getDateString(new Date());
    }

    private void signSuccess(boolean anim) {
        int width = getResources().getDimensionPixelOffset(R.dimen.width_task_center_sign_success_pic);
        int height = getResources().getDimensionPixelOffset(R.dimen.height_task_center_sign_success_pic);
        if (anim) {
            AutoTransition transition = new AutoTransition();
            transition.setDuration(200);
            TransitionManager.beginDelayedTransition(clTaskLeft, transition);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(clTaskLeft);
            constraintSet.setVisibility(R.id.bt_submit, ConstraintWidget.GONE);
            constraintSet.setVisibility(R.id.iv_small_coin, ConstraintSet.VISIBLE);
            constraintSet.constrainWidth(R.id.iv_center, width);
            constraintSet.constrainHeight(R.id.iv_center, height);
            mIvCenter.setImageResource(R.drawable.sign_success);
            mScoreTV.setText(R.string.pls_continue_sign);
            tvTips.setText(getString(R.string.sign_success_tomorrow_get, nextGetCoin));
            constraintSet.applyTo(clTaskLeft);
        } else {
            mBtSubmit.setVisibility(View.GONE);
            ivSmallCoin.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = mIvCenter.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            mIvCenter.setLayoutParams(layoutParams);
            mIvCenter.setImageResource(R.drawable.sign_success);
            mScoreTV.setText(R.string.pls_continue_sign);
            tvTips.setText(getString(R.string.sign_success_tomorrow_get, nextGetCoin));
        }

        if (signInFlag && onSignInCallback != null) {
            onSignInCallback.signIn();
        }
    }

    private void switchTaskType(@TaskType int type) {
        //bold
        mTvDaily.getPaint().setFakeBoldText(type == TaskType.DAILY);
        mTvGrow.getPaint().setFakeBoldText(type == TaskType.GROW_UP);
        mTvActivity.getPaint().setFakeBoldText(type == TaskType.ACTIVITY);
        //外发光
        mTvDaily.setTextAppearance(type == TaskType.DAILY ? R.style.text_view_light_blue : R.style.text_view_normal);
        mTvGrow.setTextAppearance(type == TaskType.GROW_UP ? R.style.text_view_light_blue : R.style.text_view_normal);
        mTvActivity.setTextAppearance(type == TaskType.ACTIVITY ? R.style.text_view_light_blue : R.style.text_view_normal);
        //select
        mTvDaily.setSelected(type == TaskType.DAILY);
        mTvGrow.setSelected(type == TaskType.GROW_UP);
        mTvActivity.setSelected(type == TaskType.ACTIVITY);

        mTvDaily.postInvalidate();
        mTvGrow.postInvalidate();
        mTvActivity.postInvalidate();

        FragmentUtils.showHide(type, mFragmentList);
    }

    @Override
    protected void onDestroy() {
        mDraw.removeDrawerListener(mDrawerListener);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDraw.isDrawerOpen(Gravity.START)) {
            mDraw.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.taskCenterSign, EventConstants.NormalClick.taskCenterEveryDay,
            EventConstants.NormalClick.taskCenterGrowUp, EventConstants.NormalClick.taskCenterActivity,
            EventConstants.NormalClick.taskCenterTaskNote})
    @ResId({R.id.bt_submit, R.id.tv_daily, R.id.tv_grow, R.id.tv_activity, R.id.tv_notes})
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.bt_submit:
                if (NetworkUtils.isConnected(TaskCenterActivity.this)) {
                    signInFlag = true;
                    mVM.signIn();
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.clockInSuccess, String.valueOf(clockInDays + 1),
                            "TaskCenterActivity", EventConstants.PageDescribe.personalCenterTaskCenter);
                } else {
                    XMToast.toastException(TaskCenterActivity.this, R.string.no_network);
                }
                dismissGuideWindow();
                break;
            case R.id.tv_daily:
                switchTaskType(TaskType.DAILY);
                break;
            case R.id.tv_grow:
                switchTaskType(TaskType.GROW_UP);
                break;
            case R.id.tv_activity:
                switchTaskType(TaskType.ACTIVITY);
                break;
            case R.id.tv_notes:
                if (mTaskRecordPage == 0) {
                    mVM.fetchTaskNoteList(mTaskRecordPage);
                }
                dismissGuideWindow();
                mDraw.openDrawer(Gravity.START);
                break;
        }
    }

    @Override
    protected void errorOnRetry() {
        mVM.fetchSignedInInfo();
    }

    @Override
    protected void noNetworkOnRetry() {
        mVM.fetchSignedInInfo();
    }


    public void setOnSignInCallback(OnSignInCallback callback) {
        this.onSignInCallback = callback;
    }

    public interface OnSignInCallback {
        void signIn();
    }

    /**
     * 只引导 没有实际打卡功能
     */
    private void showFirstGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.PERSONAL_SHOWED, GuideConstants.PERSONAL_GUIDE_FIRST, false))
            return;
        View targetView = null;
        if (mBtSubmit.getVisibility() == View.VISIBLE) {
            targetView = mBtSubmit;
        } else {
            targetView = mScoreTV;
        }
        final View finalTargetView = targetView;
        finalTargetView.post(new Runnable() {
            @Override
            public void run() {
                if (newGuide != null) return;
                Rect viewRect = NewGuide.getViewRect(finalTargetView);
                Rect targetRect;
                if (finalTargetView == mBtSubmit) {
                    targetRect = new Rect(viewRect.left, viewRect.top, viewRect.right, viewRect.bottom - 15);
                } else {
                    targetRect = new Rect(viewRect.left - 15, viewRect.top - 5, viewRect.right + 15, viewRect.bottom + 5);
                }
                newGuide = NewGuide.with(TaskCenterActivity.this)
                        .setLebal(GuideConstants.PERSONAL_SHOWED)
                        .setTargetViewId(R.id.tv_target_view)
                        .setGuideLayoutId(R.layout.guide_view_task_center_one)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setViewHandId(R.id.iv_gesture)
                        .setHandLocation(RIGHT_AND_TOP)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .setHighLightRect(targetRect)
                        .setCallBack(new GuideCallBack() {
                            @Override
                            public void onHighLightClicked() {
                                // 此时用户点击了我们的打卡图片 显示下一张引导
                                dismissGuideWindow();
                                showSecondGuideWindow();
                            }
                        })
                        .build();
                newGuide.showGuide();
            }
        });
    }

    /**
     * 有实际打卡功能的方法
     * 用 showFirstGuideWindow 替代
     */
    @Deprecated
    private void showGuideWindow() {
        // 判断是否应该展示
        boolean personalGuideShow = TPUtils.get(this, GuideConstants.PERSONAL_SHOWED, false);
        if (personalGuideShow) return;
        View targetView = null;
        if (mBtSubmit.getVisibility() == View.VISIBLE) {
            targetView = mBtSubmit;
        } else {
            targetView = mScoreTV;
        }
        final View finalTargetView = targetView;
        finalTargetView.post(new Runnable() {
            @Override
            public void run() {
                if (newGuide != null) return;
                Rect viewRect = NewGuide.getViewRect(finalTargetView);
                Rect targetRect = new Rect(viewRect.left, viewRect.top - 10, viewRect.right, viewRect.bottom - 10);
                newGuide = NewGuide.with(TaskCenterActivity.this)
                        .setLebal(GuideConstants.PERSONAL_SHOWED)
                        .setTargetView(finalTargetView)
                        .setTargetRect(targetRect)
                        .setGuideLayoutId(R.layout.guide_view_task_center_one)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setViewHandId(R.id.iv_gesture)
                        .setHandLocation(RIGHT_AND_TOP)
                        .setTargetViewTranslationX(0.01f)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .build();
                newGuide.showGuide();
            }
        });
    }

    private void showSecondGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.PERSONAL_SHOWED, GuideConstants.PERSONAL_GUIDE_FIRST, false))
            return;
        mTvNotes.post(new Runnable() {
            @Override
            public void run() {
                newGuide = NewGuide.with(TaskCenterActivity.this)
                        .setLebal(GuideConstants.PERSONAL_SHOWED)
                        .setTargetView(mTvNotes)
                        .setTargetRect(NewGuide.getViewRect(mTvNotes))
                        .setGuideLayoutId(R.layout.guide_view_task_center_two)
                        .setNeedHande(true)
                        .setNeedShake(true)
                        .setViewWaveIdOne(R.id.iv_wave_one)
                        .setViewWaveIdTwo(R.id.iv_wave_two)
                        .setViewWaveIdThree(R.id.iv_wave_three)
                        .setViewHandId(R.id.iv_gesture)
                        .setHandLocation(LEFT_AND_BOTTOM)
                        .setViewSkipId(R.id.tv_guide_skip)
                        .build();
                newGuide.showGuide();
            }
        });
    }

    private void showThirdGuideWindow() {
        try {
            if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.PERSONAL_SHOWED, GuideConstants.PERSONAL_GUIDE_FIRST, false))
                return;
//            final View view = ((ViewGroup) ((ViewGroup) getNaviBar().getChildAt(0)).getChildAt(1)).getChildAt(0);
            final View view = getNaviBar().getBackPreView();
            if (view == null) return;
            Rect viewRect = NewGuide.getViewRect(view);
            Rect targetRect = new Rect(viewRect.left, viewRect.top + (viewRect.height() / 2 - 92), viewRect.right, viewRect.top + (viewRect.height() / 2 + 92));
            newGuide = NewGuide.with(TaskCenterActivity.this)
                    .setLebal(GuideConstants.PERSONAL_SHOWED)
                    .setTargetView(view)
                    .setGuideLayoutId(R.layout.guide_view_task_center_three)
                    .setNeedHande(true)
                    .setNeedShake(true)
                    .needMoveUpALittle(true)
                    .setViewWaveIdOne(R.id.iv_wave_one)
                    .setViewWaveIdTwo(R.id.iv_wave_two)
                    .setViewWaveIdThree(R.id.iv_wave_three)
                    .setViewHandId(R.id.iv_gesture)
                    .setViewSkipId(R.id.tv_guide_skip)
                    .setHighLightRect(targetRect) // 增加back
                    .setCallBack(new GuideCallBack() {
                        @Override
                        public void onHighLightClicked() {
                            if (mDraw.isDrawerOpen(Gravity.START)) {
                                mDraw.closeDrawer(Gravity.START);
                            }
                        }
                    })
                    .build();
            newGuide.showGuide();
        } catch (Exception e) {
            Log.d(getLocalClassName(), "showThirdGuideWindow: e=" + e.getMessage());
        }
    }

    public void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
    }

    private void showLastGuideWindow() {
        dismissGuideWindow();
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.PERSONAL_SHOWED, GuideConstants.PERSONAL_GUIDE_FIRST, false))
            return;
        NewGuide.with(TaskCenterActivity.this)
                .setLebal(GuideConstants.PERSONAL_SHOWED)
                .build()
                .showLastGuide();
    }
}
