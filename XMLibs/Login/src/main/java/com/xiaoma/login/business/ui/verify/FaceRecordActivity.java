package com.xiaoma.login.business.ui.verify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.business.ui.verify.view.FaceScanView;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.sdk.FaceFactory;
import com.xiaoma.login.sdk.FaceId;
import com.xiaoma.login.sdk.IRecordListenerImpl;
import com.xiaoma.login.sdk.RecordGuid;
import com.xiaoma.model.User;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.SoundPoolUtils;

public class FaceRecordActivity extends BaseActivity {
    private TextView mTitle;
    private FaceScanView mIconImg;
    private TextView mTips;
    private Button mBtn;
    private ConstraintLayout mRootLayout;
    private ConstraintSet layoutConstraintSet;
    private Status currentStatus;
    private IRecordListenerImpl mRecordListenernew;
    private boolean isCreateSubAccount;
    private int boundFaceId;
    private DrawerLayout mBackground;
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

    public static void newInstance(Activity activity, int requestCode, Bundle bundle) {
        Intent intent = new Intent(activity, FaceRecordActivity.class);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void newInstance(Fragment fragment, int requestCode, Bundle bundle) {
        Intent intent = new Intent(fragment.getContext(), FaceRecordActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isCreateSubAccount = getIntent().getBooleanExtra(LoginConstants.IntentKey.RECORD_TO_CREATE, false);
        boundFaceId = getIntent().getIntExtra(LoginConstants.IntentKey.RECORD_TO_CREATE_BOUND_FACEID, 0);
        setContentView(R.layout.activity_face_record);
        statusBarDividerGone();
        getNaviBar().setBackgroundColor(getColor(R.color.nav_bar_solid));
        getNaviBar().showBackNavi();
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
        User currentUser = UserManager.getInstance().getCurrentUser();
        if (isCreateSubAccount) {
            if (boundFaceId > 0) {
                showRecording();
                FaceFactory.getSDK().startRecord(boundFaceId);
            } else {
                showNotBind();
            }
        } else if (currentUser != null
                && FaceId.isValid(currentUser.getFaceId())
                && !isCreateSubAccount) {
            showAlreadyBind();
            boundFaceId = currentUser.getFaceId();
        } else {
            showNotBind();
            FaceId availableFaceId = UserBindManager.getInstance().getAvailableFaceId();
            if (availableFaceId != null) {
                boundFaceId = availableFaceId.getValue();
            } else {
                XMToast.toastException(FaceRecordActivity.this, R.string.can_not_bind_more_face);
                finish();
            }
        }

        //=====================================测试代码，用于打开人脸识别功能各级开关====================================
//        FaceSDK.getInstance().openDms();
//        FaceSDK.getInstance().openDistraction();
//        FaceSDK.getInstance().openTired();
//        FaceSDK.getInstance().openBadDrive();
//        FaceSDK.getInstance().openUserId();
//        FaceSDK.getInstance().openUserFeeling();
        //===================================================================================================
    }

    private void initView() {
        mBackground = findViewById(R.id.face_bg);
        mRootLayout = findViewById(R.id.root);
        mTitle = findViewById(R.id.title);
        mIconImg = findViewById(R.id.icon_img);
        mTips = findViewById(R.id.tips);
        mBtn = findViewById(R.id.btn);

        layoutConstraintSet = new ConstraintSet();
        layoutConstraintSet.clone(mRootLayout);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentStatus) {
                    case NotBind:
                        FaceFactory.getSDK().startRecord(boundFaceId);
                        break;
                    case AlreadyBind:
                        FaceFactory.getSDK().startRecord(boundFaceId);
                        break;
                    case Recording:
                        FaceFactory.getSDK().removeRecordListener(mRecordListenernew);
                        FaceFactory.getSDK().cancelRecord();
                        finish();
                        break;
                    case RecordFail:
                        FaceFactory.getSDK().startRecord(boundFaceId);
                        break;
                }
            }
        });

        FaceFactory.getSDK().addRecordListener(mRecordListenernew = new IRecordListenerImpl() {
            @Override
            public void onStart() {
                showRecording();
            }

            @Override
            public void onGuidTip(RecordGuid recordGuid) {
                switch (recordGuid) {
                    case front:
                        mTitle.setText(R.string.look_front);
                        break;
                    case LookUp:
                        mTitle.setText(R.string.look_up);
                        break;
                    case TurnLeft:
                        mTitle.setText(R.string.turn_left);
                        break;
                    case TurnRight:
                        mTitle.setText(R.string.look_right);
                        break;
                    case LookDown:
                        mTitle.setText(R.string.look_down);
                        break;
                }
            }

            @Override
            public void onSuccess(int faceId) {
                if (!isCreateSubAccount) {
                    User currentUser = UserManager.getInstance().getCurrentUser();
                    currentUser.setFaceId(faceId);
                    UserManager.getInstance().notifyUserUpdate(currentUser,true);
                    setResult(RESULT_OK);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(LoginConstants.IntentKey.FACE_ID, faceId);
                    setResult(RESULT_OK, intent);
                }
                XMToast.toastSuccess(FaceRecordActivity.this, R.string.face_record_success);
                SoundPoolUtils.getInstance(FaceRecordActivity.this).playDelay(R.raw.record_success);
                showAlreadyBind();
                // 非创建子账户时，第一次添加人脸识别时上报(创建子账户时还未登录，XmAutoTracker还未初始化，会报错)
            }

            @Override
            public void onFaceAlreadyBind(User user) {
                showRecordFail();
                XMToast.toastException(FaceRecordActivity.this, getString(R.string.face_already_bind, user.getName()));
                SoundPoolUtils.getInstance(FaceRecordActivity.this).playDelay(R.raw.record_fail);
            }

            @Override
            public void onFailure(int code, String msg) {
                showRecordFail();
                XMToast.toastException(FaceRecordActivity.this, R.string.face_record_fail);
                SoundPoolUtils.getInstance(FaceRecordActivity.this).playDelay(R.raw.record_fail);
            }
        });

        mBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackground.closeDrawer(Gravity.START);
            }
        });
        mBackground.addDrawerListener(mDrawerListener);
        //TODO:暂时增加模拟触发
        mIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceFactory.getSDK().mockFace();
            }
        });
    }

    private void showNotBind() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layoutConstraintSet);
        constraintSet.setVisibility(R.id.icon_img, View.GONE);
        constraintSet.setVisibility(R.id.btn, View.VISIBLE);
        constraintSet.setVisibility(R.id.tips, View.VISIBLE);
        constraintSet.setMargin(R.id.btn, ConstraintSet.TOP, 146);
        constraintSet.connect(R.id.tips, ConstraintSet.TOP, R.id.title, ConstraintSet.BOTTOM);
        constraintSet.setMargin(R.id.tips, ConstraintSet.TOP, 200);
        mIconImg.cancelAnim();
        mTitle.setText(R.string.face_recognize);
        mTips.setText(R.string.not_bind_face);
        mBtn.setText(R.string.go_bind);

        constraintSet.applyTo(mRootLayout);
        currentStatus = Status.NotBind;
    }

    private void showAlreadyBind() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layoutConstraintSet);
        constraintSet.setVisibility(R.id.icon_img, View.GONE);
        constraintSet.setVisibility(R.id.btn, View.VISIBLE);
        constraintSet.setVisibility(R.id.tips, View.VISIBLE);
        constraintSet.setMargin(R.id.btn, ConstraintSet.TOP, 146);
        constraintSet.connect(R.id.tips, ConstraintSet.TOP, R.id.title, ConstraintSet.BOTTOM);
        constraintSet.setMargin(R.id.tips, ConstraintSet.TOP, 200);
        mIconImg.cancelAnim();
        mTitle.setText(R.string.face_recognize);
        mTips.setText(R.string.already_bind_face);
        mBtn.setText(R.string.rebinding);

        constraintSet.applyTo(mRootLayout);
        currentStatus = Status.AlreadyBind;
    }

    private void showRecording() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layoutConstraintSet);
        constraintSet.setVisibility(R.id.icon_img, View.VISIBLE);
        constraintSet.setVisibility(R.id.btn, View.GONE);
        constraintSet.setVisibility(R.id.tips, View.VISIBLE);
        constraintSet.setMargin(R.id.btn, ConstraintSet.TOP, 146);
        mIconImg.startAnim();
        mTitle.setText(R.string.look_front);
        mTips.setText(R.string.recognizing);

        constraintSet.applyTo(mRootLayout);
        currentStatus = Status.Recording;
    }

    private void showRecordFail() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layoutConstraintSet);
        constraintSet.setVisibility(R.id.icon_img, View.VISIBLE);
        constraintSet.setVisibility(R.id.btn, View.VISIBLE);
        constraintSet.setVisibility(R.id.tips, View.GONE);
        constraintSet.setMargin(R.id.btn, ConstraintSet.TOP, 44);
        mIconImg.cancelAnim();
        mTitle.setText(R.string.recognize_fail);
        mBtn.setText(R.string.re_recognize);

        constraintSet.applyTo(mRootLayout);
        currentStatus = Status.RecordFail;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecordListenernew != null) {
            FaceFactory.getSDK().removeRecordListener(mRecordListenernew);
        }
        FaceFactory.getSDK().cancelRecord();
        mIconImg.cancelAnim();
        mBackground.removeDrawerListener(mDrawerListener);
    }

    enum Status {
        Recording,
        RecordFail,
        AlreadyBind,
        NotBind
    }
}