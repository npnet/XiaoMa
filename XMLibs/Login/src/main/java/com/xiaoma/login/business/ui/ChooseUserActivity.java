package com.xiaoma.login.business.ui;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.R;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.business.ui.password.PasswdTexts;
import com.xiaoma.login.business.ui.password.PasswdVerifyActivity;
import com.xiaoma.login.business.ui.subaccount.CreateSubAccountActivity;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.LoginMethod;
import com.xiaoma.login.common.RequestManager;
import com.xiaoma.login.common.UserUtil;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.SingleClick;
import com.xiaoma.network.ErrorCodeConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.DoubleClickUtils;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import java.util.Iterator;
import java.util.List;

/**
 * Created by KY
 * on 2019/1/9 0009
 * <p>
 * desc: 钥匙绑定账户页面
 */
public class ChooseUserActivity extends BaseActivity {

    public static final int MAX_ACCOUNT = 5;
    private static final String TAG = ChooseUserActivity.class.getSimpleName();

    public static final int LOGIN_REQUEST_CODE_USER = 1000;
    public static final int LOGIN_REQUEST_CODE_FACTORY = 1002;
    public static final int LOGIN_REQUEST_CODE_CREATE_SUB = 1003;
    private static final int INVALID_REQUEST_CODE = 1004;
    private RecyclerView rvAccount;
    protected Adapter mAdapter;
    private List<User> mUsers;
    private View factoryEntrance;
    private Handler mHandler = new Handler();
    private int mClickCount;
    private ResetTask mResetTask;
    private String mLoginMethod;
    private View iccidEntrance;

    public static void start(Context context) {
        start(context, null);
    }

    public static void start(Context context, boolean hideNavBar) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(LoginConstants.IntentKey.HIDE_NAV_BAR, hideNavBar);
        start(context, bundle);
    }

    public static void start(Context context, Bundle bundle) {
        if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.USER_CHOOSE, new OnBlockCallback() {
            @Override
            public boolean onShowToast(LoginType loginType) {
                XMToast.showToast(AppHolder.getInstance().getAppContext(),
                        loginType.getPrompt(AppHolder.getInstance().getAppContext()));
                return true;
            }
        })) return;

        Intent intent = new Intent(context, ChooseUserActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean hideNav = getIntent().getBooleanExtra(LoginConstants.IntentKey.HIDE_NAV_BAR, false);
        if (hideNav) {
            getNaviBar().hideNavi();
        } else {
            getNaviBar().showBackNavi();
        }
        getProgressDialog().setCancelable(false);
        setContentView(R.layout.activity_car_face);
        bindView();
        initData();
        UserUtil.fetchUserValid(null);
    }

    private void initData() {
        mUsers = UserBindManager.getInstance().getCachedUser();
        User currentUser = UserManager.getInstance().getCurrentUser();
        mLoginMethod = LoginManager.getInstance().getLoginStatus().getLoginMethod();
        if (!LoginMethod.TOURISTS.name().equals(mLoginMethod)
                && !LoginMethod.FACTORY.name().equals(mLoginMethod)
                && !CollectionUtil.isListEmpty(mUsers)
                && currentUser != null) {
            Iterator<User> iterator = mUsers.iterator();
            while (iterator.hasNext()) {
                User user = iterator.next();
                if (user.getId() == currentUser.getId()) {
                    iterator.remove();
                    break;
                }
            }
        }
        initDataView();
    }

    private void bindView() {
        rvAccount = findViewById(R.id.rv_account);
        factoryEntrance = findViewById(R.id.factory_entrance);
        iccidEntrance = findViewById(R.id.iccid_entrance);
    }

    private void initDataView() {
        initFactoryEntry();
        initIccidEntry();
        initChildView();
    }

    private void initIccidEntry() {
        iccidEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickCount++;
                if (mClickCount == 10) {
                    startActivity(IccidCheckActivity.class);
                }
                if (mResetTask != null) {
                    mHandler.removeCallbacks(mResetTask);
                } else {
                    mResetTask = new ResetTask();
                }
                mHandler.postDelayed(mResetTask, 500);
            }
        });
    }

    private void initFactoryEntry() {
        factoryEntrance.setOnClickListener(new View.OnClickListener() {
            @Override
            @SingleClick(0)
            public void onClick(View v) {
                mClickCount++;
                if (mClickCount == 10) {
                    FactoryActivity.startForResult(ChooseUserActivity.this, LOGIN_REQUEST_CODE_FACTORY);
                }
                if (mResetTask != null) {
                    mHandler.removeCallbacks(mResetTask);
                } else {
                    mResetTask = new ResetTask();
                }
                mHandler.postDelayed(mResetTask, 500);
            }
        });
    }

    private void initChildView() {
        //子账户
        FlexibleFlexboxLayoutManager flexbox = new FlexibleFlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        flexbox.setJustifyContent(JustifyContent.CENTER);
        rvAccount.setLayoutManager(flexbox);
        rvAccount.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvAccount.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = getResources().getDimensionPixelOffset(R.dimen.size_key_bind_item_padding);
                outRect.right = getResources().getDimensionPixelOffset(R.dimen.size_key_bind_item_padding);
            }
        });
        mAdapter = new Adapter(null);
        mAdapter.bindToRecyclerView(rvAccount);
        if (!LoginMethod.TOURISTS.name().equals(mLoginMethod)
                && !LoginMethod.FACTORY.name().equals(mLoginMethod)) {
            mAdapter.setEmptyView(R.layout.layout_empty_user);
        }

        initUserAndCreateItem();
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.imageView) {
                    if (DoubleClickUtils.isFastDoubleClick(1500)) return;
                    User item = (User) adapter.getItem(position);
                    fetchLoginUser(item);
                }
            }
        });
    }

    protected void initUserAndCreateItem() {
        mAdapter.setNewData(mUsers);

        String loginMethod = LoginManager.getInstance().getLoginStatus().getLoginMethod();
        //创建账户只能在游客模式下
        if ((LoginMethod.TOURISTS.name().equals(loginMethod) || loginMethod == null)
                && (mUsers == null || mUsers.size() < MAX_ACCOUNT)) {
            View initFooter = new View(this);
            mAdapter.addFooterView(initFooter, 0, LinearLayout.HORIZONTAL);
            mAdapter.removeFooterView(initFooter);
            View tourist = getLayoutInflater().inflate(R.layout.item_tourist_pro, mAdapter.getFooterLayout(), false);
            tourist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.CREATE_USER, null)) {
                        fetchToCreate();
                    } else {
                        CreateSubAccountActivity.startForResult(ChooseUserActivity.this, LOGIN_REQUEST_CODE_CREATE_SUB);
                    }
                }
            });
            mAdapter.removeAllFooterView();
            mAdapter.addFooterView(tourist, 0, LinearLayout.HORIZONTAL);
        } else {
            mAdapter.removeAllFooterView();
        }
    }

    private void fetchToCreate() {
        showProgressDialog(R.string.base_loading);
        String iccid = ConfigManager.DeviceConfig.getICCID(AppHolder.getInstance().getAppContext());
        RequestManager.getUserByIccid(iccid, new ResultCallback<XMResult<User>>() {
            @Override
            public void onSuccess(XMResult<User> result) {
                dismissProgress();
                if (ChooseUserActivity.this.isDestroy()) return;
                CreateSubAccountActivity.startForResult(ChooseUserActivity.this, LOGIN_REQUEST_CODE_CREATE_SUB);
            }

            @Override
            public void onFailure(int code, String msg) {
                dismissProgress();
                if (ChooseUserActivity.this.isDestroy()) return;
                XMToast.showToast(ChooseUserActivity.this, com.xiaoma.utils.R.string.account_permission_prompt_user_invalid);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE_USER && resultCode == RESULT_OK) {
            User user = data.getParcelableExtra(LoginConstants.IntentKey.USER);
            //合并本地信息
            User mergedUser = UserUtil.merge(user);
            LoginManager.getInstance().manualLogin(mergedUser, LoginMethod.PASSWD.name());
            handleSwitchAccount();
        } else if (requestCode == LOGIN_REQUEST_CODE_FACTORY && resultCode == RESULT_OK) {
            handleSwitchAccount();
        } else if (requestCode == LOGIN_REQUEST_CODE_CREATE_SUB && resultCode == RESULT_OK) {
            handleSwitchAccount();
        }
    }

    protected void handleSwitchAccount() {
        XMToast.toastSuccess(this, R.string.login_success);
        Intent intent = new Intent(LoginConstants.XIAOMA_LAUNCHER_SPLASH_ACTION,
                Uri.parse(LoginConstants.XIAOMA_LAUNCHER_SPLASH_URI));
        intent.putExtra(LoginConstants.IntentKey.SWITCH_ACCOUNT, true);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            XMToast.toastException(this, R.string.launcher_not_install);
        }
        finish();
    }

    protected void fetchLoginUser(final User user) {
        Log.d(TAG, "has bind user, fetch to login");
        showProgressDialog(R.string.base_loading);
        RequestManager.getUserById(user.getId(), new ResultCallback<XMResult<User>>() {
            @Override
            public void onSuccess(XMResult<User> result) {
                dismissProgress();
                User serverUser = result.getData();
                if (serverUser != null) {
                    User mergeUser = UserUtil.merge(serverUser);
                    UserBindManager.getInstance().saveCacheUser(mergeUser);
                    Log.d(TAG, "fetch new user info to login");
                    gotoPasswdVerify(mergeUser);
                } else {
                    Log.d(TAG, "null data, login with cached user");
                    gotoPasswdVerify(user);
                }
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onFailure(int code, String msg) {
                dismissProgress();
                if (code == ErrorCodeConstants.USER_DISABLE_CODE
                        || code == ErrorCodeConstants.USER_NOT_EXIST_CODE
                        || code == ErrorCodeConstants.BIND_KEY_USER_NOT_FOUND) {
                    // 账户已被禁用或不存在
                    Log.d(TAG, "bind user invalid, jump to invalid page");
                    InvalidActivity.startForResult(ChooseUserActivity.this, INVALID_REQUEST_CODE, code);
                } else {
                    //网络异常，直接使用缓存的绑定用户数据
                    Log.d(TAG, "network error, login with cached user");
                    gotoPasswdVerify(user);
                }
                //这里不需要考虑过户逻辑，因为在init中处理过了
            }
        });
    }

    private void gotoPasswdVerify(User user) {
        PasswdVerifyActivity.startForResult(this, user,
                LOGIN_REQUEST_CODE_USER, PasswdTexts.LOGIN);
    }

    class Adapter extends BaseQuickAdapter<User, BaseViewHolder> {

        Adapter(List<User> cachedUser) {
            super(R.layout.item_user_bind_face, cachedUser);
        }

        @Override
        protected void convert(BaseViewHolder helper, User item) {
            helper.setText(R.id.user_name, TextUtils.isEmpty(item.getName()) ? getString(R.string.user) : item.getName())
                    .setImageResource(R.id.gender, item.isMan() ? R.drawable.gender_man : R.drawable.gender_woman);
            ImageLoader.with(ChooseUserActivity.this)
                    .load(item.getPicPath())
                    .placeholder(R.drawable.icon_default_user)
                    .error(R.drawable.icon_default_user)
                    .circleCrop()
                    .into((ImageView) helper.getView(R.id.iv_pic));
            helper.addOnClickListener(R.id.imageView);
        }
    }

    class FlexibleFlexboxLayoutManager extends FlexboxLayoutManager {

        FlexibleFlexboxLayoutManager(Context context, int flexDirection, int flexWrap) {
            super(context, flexDirection, flexWrap);
        }

        public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
            if (lp instanceof RecyclerView.LayoutParams) {
                return new LayoutParams((RecyclerView.LayoutParams) lp);
            } else if (lp instanceof ViewGroup.MarginLayoutParams) {
                return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
            } else {
                return new LayoutParams(lp);
            }
        }
    }

    class ResetTask implements Runnable {

        @Override
        public void run() {
            mClickCount = 0;
        }
    }
}
