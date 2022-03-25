package com.xiaoma.personal.account.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.guide.listener.ViewLayoutCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.personal.R;
import com.xiaoma.personal.account.adapter.PersonalCategoryAdapter;
import com.xiaoma.personal.account.model.CurrentDate;
import com.xiaoma.personal.account.ui.view.BasePersonalInfoDialog;
import com.xiaoma.personal.account.ui.view.ChangeAvatarDialog;
import com.xiaoma.personal.account.ui.view.PersonalAgeDialog;
import com.xiaoma.personal.account.ui.view.PersonalInfoPopupWindow;
import com.xiaoma.personal.account.ui.view.PersonalNameDialog;
import com.xiaoma.personal.account.ui.view.PersonalSexDialog;
import com.xiaoma.personal.account.vm.CategoryInfoVM;
import com.xiaoma.personal.account.vm.ItemFlag;
import com.xiaoma.personal.carinfo.ui.CarInfoActivity;
import com.xiaoma.personal.coin.ui.CarCoinActivity;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.common.util.InfoType;
import com.xiaoma.personal.common.util.Utils;
import com.xiaoma.personal.feedback.ui.FeedbackActivity;
import com.xiaoma.personal.manager.ui.SetupActivity;
import com.xiaoma.personal.newguide.ui.NewGuideReopenActivity;
import com.xiaoma.personal.order.ui.MineOrderActivity;
import com.xiaoma.personal.qrcode.ui.QRCodeManageActivity;
import com.xiaoma.personal.taskcenter.ui.TaskCenterActivity;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.DoubleClickUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import static com.xiaoma.network.ErrorCodeConstants.USER_NAME_EXISTED_ERROR;


/**
 * @author Gillben
 * date: 2018-11-19
 * 个人中心首页
 */
@PageDescComponent(EventConstants.PageDescribe.personalCenterMainActivity)
public class PersonalCenterActivity extends BaseActivity implements View.OnClickListener {


    private static final int LOGIN_RESULT_CODE = 1;
    private View mPersonalCenterBackground;
    private ImageView mPersonalIconImg;
    private TextView mPhoneNumberText;
    private Button mPersonalInfoBt;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private PersonalInfoPopupWindow mInfoPopupWindow;
    private PersonalCategoryAdapter mPersonalCategoryAdapter;
    private CategoryInfoVM mCategoryInfoVM;
    private TextView mTvName;
    private XmScrollBar mScrollBar;

    private String[] ageRecord = new String[1];
    private NewGuide newGuide;
    private boolean hasChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setBackgroundDrawableResource(R.drawable.bg_common);
        setContentView(R.layout.activity_personal_center);
        mContext = this;
        initView();
        initListener();
        // 在setData之前
        showGuideWindow();
        initData();
        checkNet();
        if (!LoginManager.getInstance().isUserLogin()) {
//            ChooseUserActivity.start(this, true);
//            finish();
            LoginManager.getInstance().touristLogin();
            return;
        } else {
        }
        setupUserInfo(UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId()));
        fetchUserInfo();
    }

    private void fetchUserInfo() {
        String userId = LoginManager.getInstance().getLoginUserId();
        User cachedUser = UserManager.getInstance().getUser(userId);
        if (!TextUtils.isEmpty(userId)) {
            RequestManager.getUserById(Long.valueOf(userId), new ResultCallback<XMResult<User>>() {
                @Override
                public void onSuccess(XMResult<User> result) {
                    User user = result.getData();
                    if (result.isSuccess() && user != null) {
                        UserManager.getInstance().notifyUserUpdate(user);
                        setupUserInfo(user);
                    }
                }

                @Override
                public void onFailure(int code, String msg) {
                    //do nothing
                }
            });
        }
    }

    private void initView() {
        mPersonalCenterBackground = findViewById(R.id.personal_center_background);
        mPersonalIconImg = findViewById(R.id.personal_icon);
        mPhoneNumberText = findViewById(R.id.phone_number_text);
        mTvName = findViewById(R.id.tv_name);
        mPersonalInfoBt = findViewById(R.id.personal_info_bt);
        mRecyclerView = findViewById(R.id.personal_center_recycler_view);
        mScrollBar = findViewById(R.id.xsb_personal_center);

        mPersonalCategoryAdapter = new PersonalCategoryAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mPersonalCategoryAdapter);
        mScrollBar.setRecyclerView(mRecyclerView);

        mInfoPopupWindow = new PersonalInfoPopupWindow(this, 715, WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void initListener() {
        mPersonalInfoBt.setOnClickListener(this);
        mPersonalIconImg.setOnClickListener(this);
        //为个人信息窗口添加点击事件
        mInfoPopupWindow.setOnModifyPersonalInfoCallback(new PersonalInfoPopupWindow.OnModifyPersonalInfoCallback() {
            @Override
            public void modifyPersonalInfo(InfoType type) {
                switch (type) {
                    case INFO_SEX:
                        showSexDialog();
                        break;
                    case INFO_NAME:
                        showNameDialog();
                        break;
                    case INFO_AGE:
                        handleAgeSetup();
                        break;
                }
            }

            private void showSexDialog() {
                PersonalSexDialog personalSexDialog = new PersonalSexDialog();
                personalSexDialog.setOnDialogCallback(new BasePersonalInfoDialog.OnDialogCallback() {
                    @Override
                    public void onLoading() {
                        showProgressDialog(R.string.modifying);
                    }

                    @Override
                    public void success(String content) {
                        mInfoPopupWindow.setPersonalSex(content);
                        hasChanged = true;
                    }

                    @Override
                    public void fail(int code, String msg) {
                        XMToast.toastException(mContext, R.string.modify_failed);
                    }

                    @Override
                    public void onComplete() {
                        dismissProgress();
                    }
                });
                personalSexDialog.show(getSupportFragmentManager(), null);
            }

            private void showNameDialog() {
                PersonalNameDialog personalNameDialog = new PersonalNameDialog();
                personalNameDialog.setOnDialogCallback(new BasePersonalInfoDialog.OnDialogCallback() {
                    @Override
                    public void onLoading() {
                        showProgressDialog(R.string.modifying);
                    }

                    @Override
                    public void success(String content) {
                        mTvName.setText(content);
                        mInfoPopupWindow.setPersonalName(content);
                        hasChanged = true;
                    }

                    @Override
                    public void fail(int code, String msg) {
                        if (code == USER_NAME_EXISTED_ERROR) {
                            XMToast.toastException(mContext, R.string.user_name_exist);
                        } else {
                            XMToast.toastException(mContext, R.string.modify_failed);
                        }
                    }

                    @Override
                    public void onComplete() {
                        dismissProgress();
                    }
                });
                personalNameDialog.show(getSupportFragmentManager(), null);
            }


            private void handleAgeSetup() {
                if (!TextUtils.isEmpty(ageRecord[0])) {
                    showAgeDialog(ageRecord[0]);
                } else {
                    XMProgress.showProgressDialog(PersonalCenterActivity.this, mContext.getString(R.string.state_loading));
                    RequestManager.pullServerNewDate(new ResultCallback<XMResult<CurrentDate>>() {
                        @Override
                        public void onSuccess(XMResult<CurrentDate> result) {
                            XMProgress.dismissProgressDialog(PersonalCenterActivity.this);
                            String date = result.getData().getDate();
                            if (!TextUtils.isEmpty(date)) {
                                showAgeDialog(date);
                                ageRecord[0] = date;
                            }
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            XMProgress.dismissProgressDialog(PersonalCenterActivity.this);
                            XMToast.toastException(PersonalCenterActivity.this, mContext.getString(R.string.state_loading_fail));
                        }

                    });
                }
            }


            private void showAgeDialog(String date) {
                User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
                String birthDay = null;
                if (user != null) {
                    birthDay = user.getBirthDay();
                }

                PersonalAgeDialog personalAgeDialog = new PersonalAgeDialog();
                if (!TextUtils.isEmpty(birthDay)) {
                    personalAgeDialog.setInitDate(date, birthDay);
                }
                personalAgeDialog.setOnDialogCallback(new BasePersonalInfoDialog.OnDialogCallback() {
                    @Override
                    public void onLoading() {
                        showProgressDialog(R.string.modifying);
                    }

                    @Override
                    public void success(String content) {
                        mInfoPopupWindow.setPersonalAge(content);
                        XMToast.toastSuccess(mContext, R.string.modify_success);
                        hasChanged = true;
                    }

                    @Override
                    public void fail(int code, String msg) {
                        XMToast.toastException(mContext, R.string.modify_failed);
                    }

                    @Override
                    public void onComplete() {
                        dismissProgress();
                    }
                });
                personalAgeDialog.show(getSupportFragmentManager(), null);
            }

            @Override
            public void open() {
                mPersonalCenterBackground.setVisibility(View.VISIBLE);
            }

            @Override
            public void close() {
                mPersonalCenterBackground.setVisibility(View.GONE);
                uploadChangeInfo();
            }
        });

        mPersonalCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (DoubleClickUtils.isFastDoubleClick(2000)) {
                    return;
                }
                jumpNewPage(position);
                dismissGuideWindow();
            }
        });

        UserManager.getInstance().addOnUserUpdateListener(new UserManager.OnUserUpdateListener() {
            @Override
            public void onUpdate(@Nullable User user) {
                setupUserInfo(user);
            }
        });
    }

    private void initData() {
        mCategoryInfoVM = ViewModelProviders.of(this).get(CategoryInfoVM.class);
        mPersonalCategoryAdapter.setNewData(mCategoryInfoVM.getHomeList(this));
    }

    // 第一次修改姓名或性别或年纪，完成任务
    private void uploadChangeInfo() {
        if (hasChanged && NetworkUtils.isConnected(this)) {
            XmTracker.getInstance().uploadEvent(-1, TrackerCountType.CHANGE_INFO_TASK.getType());
        }
    }

    private void setupUserInfo(User user) {
        if (isDestroy()) {
            return;
        }
        if (user == null) {
            XMToast.showToast(this, R.string.user_not_exist);
            return;
        }
        String name = user.getName();
        String sex = user.isMan() ? getString(R.string.man) : getString(R.string.woman);
        String age = user.getAge();
        String avatarUrl = user.getPicPath();
        String phone = user.getPhone();

        if (mInfoPopupWindow != null) {
            mInfoPopupWindow.setPersonalName(name);
            mInfoPopupWindow.setPersonalSex(sex);
            mInfoPopupWindow.setPersonalAge(age);
        }
        Glide.with(this).load(avatarUrl)
                .transform(new CircleCrop())
                .placeholder(R.drawable.personal_avator_icon)
                .into(mPersonalIconImg);
        mPhoneNumberText.setText(Utils.convertSimplePhoneNumber(phone));
        mTvName.setText(name);
    }

    /**
     * 页面跳转
     *
     * @param position 具体页在列表中的下标位置
     */
    private void jumpNewPage(int position) {
        // 在游客模式下屏蔽新手模式 不能用position去判断 所以添加flag
        int itemFlag = mPersonalCategoryAdapter.getData().get(position).getItemFlag();
        switch (itemFlag) {
            //账号管理
            case ItemFlag.ACCOUNT:
                if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.PERSONAL_ACCOUNT_MANAGEMENT, new OnBlockCallback() {
                    @Override
                    public boolean onShowToast(LoginType loginType) {
                        XMToast.showToast(mContext, loginType.getPrompt(mContext));
                        return true;
                    }
                })) return;
                startActivity(SetupActivity.class);
                break;

            //我的订单
            case ItemFlag.ORDER:
                startActivity(MineOrderActivity.class);
                break;

            //我的车币
            case ItemFlag.CAR_COIN:
                startActivity(CarCoinActivity.class);
                break;

            //车辆信息
            case ItemFlag.CAR_INFO:
                CarInfoActivity.startActivity(PersonalCenterActivity.this);
                break;

            //任务中心
            case ItemFlag.TASK_CENTER:
                TaskCenterActivity.launch(this);
                break;

            //反馈
            case ItemFlag.FEED_BACK:
                startActivity(FeedbackActivity.class);
                break;

            // 新手模式
            case ItemFlag.NEW_GUIDE:
                startActivity(NewGuideReopenActivity.class);
                break;

            //二维码管理
            case ItemFlag.CODE_MANAGER:
                startActivity(QRCodeManageActivity.class);
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == LOGIN_RESULT_CODE) {
            setupUserInfo(UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
        fetchUserInfo();
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.personalInfoEdit, EventConstants.NormalClick.personalIcon})
    @ResId({R.id.personal_info_bt, R.id.personal_icon})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_info_bt:
                if (mInfoPopupWindow != null) {
                    mInfoPopupWindow.showAtLocation(getRootLayout(), Gravity.START | Gravity.TOP,
                            0, 0);
                }
                break;
            case R.id.personal_icon:
                new ChangeAvatarDialog().onCancelAction(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // 用户手动取消dialog后，手动刷新一次用户信息
                        UserManager.getInstance().fetchUserByUserId(LoginManager.getInstance().getLoginUserId(), new UserManager.UserCallback() {
                            @Override
                            public void onSuccess(User user) {
                                //TODO:由于直接更新后台返回的user信息，会覆盖掉主账号信息以及要是绑定信息，所以这里先只更新头像信息
                                long userId = Long.parseLong(LoginManager.getInstance().getLoginUserId());
                                User currentUser = UserBindManager.getInstance().getCachedUserById(userId);
                                if (currentUser != null) {
                                    currentUser.setPicPath(user.getPicPath());
                                    UserManager.getInstance().notifyUserUpdate(currentUser);
                                }
                            }

                            @Override
                            public void onFailure(int errCode, String errMsg) {

                            }
                        });
                    }
                }).showDialog(getSupportFragmentManager());
                break;
        }
    }

    public void showGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(this, GuideConstants.PERSONAL_SHOWED, GuideConstants.PERSONAL_GUIDE_FIRST, true))
            return;
        if (!NetworkUtils.isConnected(this)) {
            NewGuide.with(this)
                    .setLebal(GuideConstants.PERSONAL_SHOWED)
                    .build()
                    .showGuideFinishWindowDueToNetError();
            return;
        }
        mPersonalCategoryAdapter.setCallBack(new ViewLayoutCallBack() {
            @Override
            public void onViewLayouted(View itemView) {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
//                        View view = mRecyclerView.getLayoutManager().findViewByPosition(2);
                        if (itemView == null) return;
                        View targetView = itemView.findViewById(R.id.iv_category_icon);
                        newGuide = NewGuide.with(PersonalCenterActivity.this)
                                .setLebal(GuideConstants.PERSONAL_SHOWED)
                                .setTargetView(targetView)
                                .setTargetRect(NewGuide.getViewRect(targetView))
                                .setGuideLayoutId(R.layout.guide_view_personal)
                                .setNeedHande(true)
                                .setNeedShake(true)
                                .setViewWaveIdOne(R.id.iv_wave_one)
                                .setViewWaveIdTwo(R.id.iv_wave_two)
                                .setViewWaveIdThree(R.id.iv_wave_three)
                                .setViewHandId(R.id.iv_gesture)
                                .setTargetViewTranslationX(0.03f)
                                .setViewSkipId(R.id.tv_guide_skip)
                                .build();
                        newGuide.showGuide();
                    }
                });
            }
        });
        GuideDataHelper.setFirstGuideFalse(GuideConstants.PERSONAL_SHOWED);
    }

    public void dismissGuideWindow() {
        if (newGuide != null)
            newGuide.dismissGuideWindow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        AppObserver.getInstance().closeAllActivitiesAndExit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
//        AppObserver.getInstance().closeAllActivitiesAndExit();
    }
}
