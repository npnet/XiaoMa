package com.xiaoma.login.business.ui.verify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.login.R;
import com.xiaoma.login.business.ui.password.PasswdVerifyActivity;
import com.xiaoma.login.business.ui.verify.view.FaceScanView;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.sdk.FaceFactory;
import com.xiaoma.login.sdk.IdentifyListenerImpl;
import com.xiaoma.model.User;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.SoundPoolUtils;

public class FaceVerifyActivity extends BaseActivity {

    public static final int REQUEST_CODE = 10076;

    private TextView mTitle;
    private FaceScanView mIconImg;
    private TextView mTips;
    private Button mBtn;
    private ConstraintLayout mRootLayout;
    private IdentifyListenerImpl mRecordListenernew;
    private DrawerLayout mBackground;
    private User mUser;
    private DrawerLayout.SimpleDrawerListener mDrawerListener = new DrawerLayout.SimpleDrawerListener() {

        @Override
        public void onDrawerOpened(View drawerView) {
            super.onDrawerOpened(drawerView);
            mBackground.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

        @Override
        public void onDrawerClosed(View drawerView) {
            super.onDrawerClosed(drawerView);
            getRootLayout().setAlpha(0f);
            finish();
        }
    };

    public static void newInstance(Activity activity, User user, int requestCode) {
        if (user != null) {
            if (user.getFaceId() > 0) {
                Intent intent = new Intent(activity, FaceVerifyActivity.class);
                intent.putExtra(LoginConstants.IntentKey.USER, (Parcelable) user);
                activity.startActivityForResult(intent, requestCode);
            } else {
                PasswdVerifyActivity.startForResult(activity, user, requestCode);
            }
        } else {
            XMToast.toastException(activity, R.string.user_invalid);
        }
    }


    public static void newInstance(Fragment fragment, User user, int requestCode) {
        if (user != null) {
            if (user.getFaceId() > 0) {
                Intent intent = new Intent(fragment.getActivity(), FaceVerifyActivity.class);
                intent.putExtra(LoginConstants.IntentKey.USER, (Parcelable) user);
                fragment.startActivityForResult(intent, requestCode);
            } else {
                PasswdVerifyActivity.startForResult(fragment, user, requestCode);
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_verify);
        getNaviBar().setBackgroundColor(getColor(R.color.nav_bar_solid));
        getNaviBar().showBackNavi();
        statusBarDividerGone();
        mUser = getIntent().getParcelableExtra(LoginConstants.IntentKey.USER);
        if (mUser == null) {
            XMToast.toastException(this, R.string.user_invalid);
            finish();
        }
        initWindow();
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBackground.openDrawer(Gravity.START);
    }

    private void initWindow() {
        getWindow().setGravity(Gravity.START);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    private void initData() {
        FaceFactory.getSDK().addIdentifyListener(mRecordListenernew = new IdentifyListenerImpl() {

            @Override
            public void onStart() {
                mIconImg.startAnim();
                mTitle.setText(R.string.look_front);
                mBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }

            @Override
            public void onSuccess(int faceId) {
                if (mUser.getFaceId() == faceId) {
                    mIconImg.cancelAnim();
                    XMToast.toastSuccess(FaceVerifyActivity.this, R.string.face_recognize_success);
                    SoundPoolUtils.getInstance(FaceVerifyActivity.this).playDelay(R.raw.record_success);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    onFailure(-1, "no match");
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                showRecordFail();
                XMToast.toastException(FaceVerifyActivity.this, R.string.face_recognize_fail);
                SoundPoolUtils.getInstance(FaceVerifyActivity.this).playDelay(R.raw.record_fail);
                XMToast.toastException(FaceVerifyActivity.this, R.string.recorgnize_fail_go_passwd);
                PasswdVerifyActivity.startForResult(FaceVerifyActivity.this, mUser, REQUEST_CODE);
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceFactory.getSDK().mockFace();
            }
        });
        FaceFactory.getSDK().startIdentify();
    }

    private void initView() {
        mBackground = findViewById(R.id.face_bg);
        mRootLayout = findViewById(R.id.root);
        mTitle = findViewById(R.id.title);
        mIconImg = findViewById(R.id.icon_img);
        mTips = findViewById(R.id.tips);
        mBtn = findViewById(R.id.btn);

        mBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackground.closeDrawer(Gravity.START);
            }
        });
        mBackground.addDrawerListener(mDrawerListener);
    }

    private void showRecordFail() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mRootLayout);
        constraintSet.setVisibility(R.id.icon_img, View.VISIBLE);
        constraintSet.setVisibility(R.id.btn, View.VISIBLE);
        constraintSet.setVisibility(R.id.tips, View.INVISIBLE);
        // constraintSet.setMargin(R.id.btn, ConstraintSet.TOP, 44);
        mTitle.setText(R.string.recognize_fail);
        mBtn.setText(R.string.re_recognize);
        mIconImg.cancelAnim();

        constraintSet.applyTo(mRootLayout);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTips.setVisibility(View.VISIBLE);
                mTips.setText(R.string.recognizing);
                mTitle.setText(R.string.face_recognize);
                mBtn.setText(R.string.cancel_face_recognize);
                FaceFactory.getSDK().startIdentify();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecordListenernew != null) {
            FaceFactory.getSDK().removeIdentifyListener(mRecordListenernew);
        }
        FaceFactory.getSDK().cancelRecord();
        mIconImg.cancelAnim();
        mBackground.removeDrawerListener(mDrawerListener);
    }
}