package com.xiaoma.personal.manager.ui.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.image.transformation.CircleTransform;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.UserIconManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.business.ui.ChooseUserActivity;
import com.xiaoma.login.business.ui.password.PasswdTexts;
import com.xiaoma.login.business.ui.password.PasswdVerifyActivity;
import com.xiaoma.login.business.ui.verify.FaceRecordActivity;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.sdk.FaceFactory;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.manager.ui.ModifyPasswordActivity;
import com.xiaoma.personal.manager.ui.SetupActivity;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import static com.xiaoma.login.common.PasswordPageConstants.MODIFY_PAGE_REQUEST_CODE;
import static com.xiaoma.personal.common.util.FragmentTagConstants.KEY_MANAGER_FRAGMENT_TAG;

/**
 * Created by Gillben on 2019/1/8 0008
 * <p>
 */
@PageDescComponent(EventConstants.PageDescribe.AccountManagerHome)
public class SetupFragment extends VisibleFragment implements View.OnClickListener {

    private static final int CHOOSE_OTHER_USER_REQUEST_CODE = 10000;
    private static final int DELETD_USER_REQUEST_CODE = 10001;
    private static final int FACE_REQUEST_CODE = 10002;
    private Button passwordManagerBt;
    private Button keyManagerBt;
    private Button accountManagerBt;
    private View faceManagerLayout;
    private View faceManagerBt;
    private View deleteAccountBt;
    private TextView tvUserName;
    private ImageView ivUserAvatar;
    private ImageView ivUserGender;


    public static SetupFragment newInstance() {
        return new SetupFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_setup, container, false);
        return super.onCreateWrapView(contentView);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNaviBar().showBackAndHomeNavi();
        initView(view);
        initData();
    }

    private void initView(View view) {
        ivUserAvatar = view.findViewById(R.id.iv_user_avatar);
        ivUserGender = view.findViewById(R.id.iv_user_gender);
        tvUserName = view.findViewById(R.id.tv_user_name);
        accountManagerBt = view.findViewById(R.id.enter_account_manager_bt);
        deleteAccountBt = view.findViewById(R.id.bt_delete_user);
        passwordManagerBt = view.findViewById(R.id.modify_password_bt);
        keyManagerBt = view.findViewById(R.id.enter_key_manger_bt);
        faceManagerLayout = view.findViewById(R.id.face_manager_layout);
        faceManagerBt = view.findViewById(R.id.enter_face_manager_bt);

        accountManagerBt.setOnClickListener(this);
        deleteAccountBt.setOnClickListener(this);
        passwordManagerBt.setOnClickListener(this);
        keyManagerBt.setOnClickListener(this);
        faceManagerBt.setOnClickListener(this);
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible) {
            // 如果是工厂模式或者游客模式，头像使用默认值
            User currentUser = UserManager.getInstance().getCurrentUser();
            Glide.with(this).load(currentUser.getPicPath())
                    .transform(new CircleTransform(getContext()))
                    .placeholder(R.drawable.personal_avator_icon)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(ivUserAvatar);

            ivUserGender.setImageResource(currentUser.getGender() == 0 ? R.drawable.gender_woman : R.drawable.gender_man);
            tvUserName.setText(currentUser.getName());
        }
    }

    private void initData() {
        if (!XmCarConfigManager.hasFaceRecognition()) {
            faceManagerLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enter_account_manager_bt:
                User currentUser1 = UserManager.getInstance().getCurrentUser();
                PasswdVerifyActivity.startForResult(this, currentUser1,
                        CHOOSE_OTHER_USER_REQUEST_CODE, PasswdTexts.MANAGER);
                break;
            case R.id.bt_delete_user:
                User currentUser2 = UserManager.getInstance().getCurrentUser();
                PasswdVerifyActivity.startForResult(this, currentUser2,
                        DELETD_USER_REQUEST_CODE, PasswdTexts.NORMAL);
                break;
            case R.id.modify_password_bt:
                enterPasswordVerifyPage(ModifyPasswordActivity.class, MODIFY_PAGE_REQUEST_CODE);
                break;
            case R.id.enter_key_manger_bt:
                enterManagerPage(KeyManagerFragment.newInstance(), KEY_MANAGER_FRAGMENT_TAG);
                break;
            case R.id.enter_face_manager_bt:
                FaceRecordActivity.newInstance(this, FACE_REQUEST_CODE, null);
                break;
        }
    }

    private void enterManagerPage(BaseFragment fragment, String tag) {
        ((SetupActivity) getActivity()).replaceFragment(fragment, tag);
    }


    private void enterPasswordVerifyPage(Class clazz, int requestCode) {
        Intent intent = new Intent(getContext(), clazz);
        startActivityForResult(intent, requestCode);
    }

    private void showDeleteDialog() {
        ConfirmDialog mDeleteDialog = new ConfirmDialog(getActivity());
        mDeleteDialog.setTitle(mContext.getString(R.string.query_delete_account))
                .setContent(mContext.getString(R.string.hint_delete_account_result))
                .setPositiveButton(mContext.getString(R.string.confirm), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteUser(UserManager.getInstance().getCurrentUser());
                        mDeleteDialog.dismiss();
                    }
                })
                .setNegativeButton(mContext.getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDeleteDialog.dismiss();
                    }
                }).show();
    }

    private void deleteUser(final User user) {
        if (user == null) {
            KLog.w("User is null");
            return;
        }
        getProgressDialog().setCancelable(false);
        showProgressDialog(R.string.base_loading);
        RequestManager.deleteChildAccount(user.getId(), new ResultCallback<XMResult<OnlyCode>>() {
            @Override
            public void onSuccess(XMResult<OnlyCode> result) {
                boolean b = UserBindManager.getInstance().removeCacheUser(user.getId());
                if (b) {
                    XMToast.toastSuccess(getContext(), R.string.deleted_account);
                    //调用接口通知人脸识别删除faceid
                    FaceFactory.getSDK().deleteRecord(user.getFaceId());
                    UserIconManager.getInstance().removeIcon();
                    LoginManager.getInstance().logout();
                    goLauncherSplash();
                } else {
                    XMToast.toastException(getContext(), R.string.delete_fail_pls_retry);
                    dismissProgress();
                    getProgressDialog().setCancelable(true);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                KLog.e("删除用户失败");
                if (TextUtils.isEmpty(msg) || code == -1 || !NetworkUtils.isConnected(getContext())) {
                    msg = getString(com.xiaoma.login.R.string.no_network);
                }
                XMToast.toastException(getContext(), msg);
                dismissProgress();
                getProgressDialog().setCancelable(true);
            }
        });
    }

    private void goLauncherSplash() {
        getProgressDialog().setCancelable(true);
        Intent intent = new Intent(LoginConstants.XIAOMA_LAUNCHER_SPLASH_ACTION,
                Uri.parse(LoginConstants.XIAOMA_LAUNCHER_SPLASH_URI));
        intent.putExtra(LoginConstants.IntentKey.CANCELLATION, true);
        dismissProgress();
        try {
            ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                @Override
                public void run() {
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }, 1000);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            XMToast.toastException(getContext(), com.xiaoma.login.R.string.launcher_not_install);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FACE_REQUEST_CODE) {
            if (getNaviBar() != null) {
                getNaviBar().showBackAndHomeNavi();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == DELETD_USER_REQUEST_CODE) {
                showDeleteDialog();
            } else if (requestCode == CHOOSE_OTHER_USER_REQUEST_CODE) {
                ChooseUserActivity.start(getContext());
            }
        }
    }

}
