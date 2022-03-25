package com.xiaoma.login.business.ui;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.login.R;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.business.ui.verify.view.FaceScanView;
import com.xiaoma.login.common.LoginMethod;
import com.xiaoma.login.sdk.FaceFactory;
import com.xiaoma.login.sdk.FaceId;
import com.xiaoma.login.sdk.IdentifyListenerImpl;
import com.xiaoma.model.User;
import com.xiaoma.skin.views.XmViewSwitcher;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;

/**
 * Created by kaka
 * on 19-5-10 下午4:54
 * <p>
 * desc: 车机初始化的Activity，会在这里初始化主账号数据，以及一些其他需要在真正进入桌面之前初始化的东西
 * </p>
 */
public class CarInitProActivity extends AbsInitActivity implements View.OnClickListener {
    private static final String TAG = CarInitProActivity.class.getSimpleName() + "_LOG";
    private Button mCancel;
    private XmViewSwitcher mFaceSwitch;
    private ImageView mIvLeftPic;
    private ImageView mLeftGender;
    private TextView mLeftUserName;
    private ImageView mIvRightPic;
    private ImageView mRightGender;
    private TextView mRightUserName;
    private View mLeft;
    private View mRight;
    private User leftUser;
    private User rightUser;
    private FaceScanView mIcon;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_init_pro);
        mCancel = findViewById(R.id.cancel);
        mFaceSwitch = findViewById(R.id.face_switch);
        mIcon = findViewById(R.id.icon);
        mLeft = findViewById(R.id.left);
        mRight = findViewById(R.id.right);
        mFaceSwitch = findViewById(R.id.face_switch);
        mIvLeftPic = findViewById(R.id.iv_left_pic);
        mLeftGender = findViewById(R.id.left_gender);
        mLeftUserName = findViewById(R.id.left_user_name);
        mIvRightPic = findViewById(R.id.iv_right_pic);
        mRightGender = findViewById(R.id.right_gender);
        mRightUserName = findViewById(R.id.right_user_name);

        mCancel.setOnClickListener(this);
        mIcon.setOnClickListener(this);
    }

    @Override
    public void fetchBindUser() {
        FaceFactory.getSDK().addIdentifyListener(new IdentifyListenerImpl() {
            @Override
            public void onStart() {
                onRecognizing();
                ThreadDispatcher.getDispatcher().postDelayed(mTimeOut = new Runnable() {
                    @Override
                    public void run() {
                        onEnd();
                        onSuccess(0);
                    }
                }, 1000 * 10);
            }

            @Override
            public void onSuccess(int faceId) {
                ThreadDispatcher.getDispatcher().remove(mTimeOut);
                User faceBoundUser = UserBindManager.getInstance().getFaceBoundUser(FaceId.valueOf(faceId));
                if (faceBoundUser == null && keyBoundUser == null) {
                    XMToast.toastException(CarInitProActivity.this, R.string.face_recognize_fail);
                    onFetchResult(null, LoginMethod.TOURISTS);
                } else if (faceBoundUser == null) {
                    XMToast.toastException(CarInitProActivity.this, R.string.face_recognize_fail);
                    LoginMethod loginMethod = mCarKey.isBle() ? LoginMethod.KEY_BLE : LoginMethod.KEY_NORMAL;
                    onFetchResult(keyBoundUser, loginMethod);
                } else if (keyBoundUser == null) {
                    XMToast.toastSuccess(CarInitProActivity.this, R.string.face_recognize_success);
                    onFetchResult(faceBoundUser, LoginMethod.FACE);
                } else {
                    XMToast.toastSuccess(CarInitProActivity.this, R.string.face_recognize_success);
                    if (faceBoundUser.getId() == keyBoundUser.getId()) {
                        onFetchResult(faceBoundUser, LoginMethod.FACE);
                    } else {
                        onChooseUser(faceBoundUser, keyBoundUser);
                    }
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                ThreadDispatcher.getDispatcher().remove(mTimeOut);
                XMToast.toastException(CarInitProActivity.this, R.string.face_recognize_fail);
                if (keyBoundUser != null) {
                    LoginMethod loginMethod = mCarKey.isBle() ? LoginMethod.KEY_BLE : LoginMethod.KEY_NORMAL;
                    onFetchResult(keyBoundUser, loginMethod);
                } else {
                    onFetchResult(null, LoginMethod.TOURISTS);
                }
            }

            @Override
            public void onEnd() {
                onRecognizeEnd();
                ThreadDispatcher.getDispatcher().remove(mTimeOut);
                FaceFactory.getSDK().removeIdentifyListener(this);
            }
        });
        FaceFactory.getSDK().startIdentify();
    }

    @Override
    protected void onRecognizing() {
        super.onRecognizing();
        mIcon.startAnim();
    }

    @Override
    protected void onRecognizeEnd() {
        super.onRecognizeEnd();
        mIcon.cancelAnim();
    }

    @Override
    protected void onChooseUser(User faceUser, User keyUser) {
        super.onChooseUser(faceUser, keyUser);
        leftUser = faceUser;
        rightUser = keyUser;
        //face
        int genderFace = R.drawable.gender_man;
        if (!faceUser.isMan()) {
            genderFace = R.drawable.gender_woman;
        }
        ImageLoader.with(this)
                .load(faceUser.getPicPath())
                .placeholder(R.drawable.icon_default_user)
                .error(R.drawable.icon_default_user)
                .circleCrop()
                .into(mIvLeftPic);
        mLeftGender.setImageResource(genderFace);
        mLeftUserName.setText(faceUser.getName());

        //key
        int genderKey = R.drawable.gender_man;
        if (!keyUser.isMan()) {
            genderKey = R.drawable.gender_woman;
        }
        ImageLoader.with(this)
                .load(keyUser.getPicPath())
                .placeholder(R.drawable.icon_default_user)
                .error(R.drawable.icon_default_user)
                .circleCrop()
                .into(mIvRightPic);
        mRightGender.setImageResource(genderKey);
        mRightUserName.setText(keyUser.getName());

        mFaceSwitch.setDisplayedChild(1);
        mLeft.setOnClickListener(this);
        mRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.left) {
            onFetchResult(leftUser, LoginMethod.FACE);
        } else if (id == R.id.right) {
            onFetchResult(rightUser, mCarKey.isBle() ? LoginMethod.KEY_BLE : LoginMethod.KEY_NORMAL);
        } else if (id == R.id.icon) {
            FaceFactory.getSDK().mockFace();
        } else if (id == R.id.cancel) {
            mCancel.setOnClickListener(null);
            if (mPrepareWork != null) {
                mPrepareWork.interrupt();
                mPrepareWork.doWork(null);
            }
            FaceFactory.getSDK().cancelIdentify();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mIcon.cancelAnim();
    }
}
