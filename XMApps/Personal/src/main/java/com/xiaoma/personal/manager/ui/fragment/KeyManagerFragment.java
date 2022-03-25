package com.xiaoma.personal.manager.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.PasswordPageConstants;
import com.xiaoma.login.sdk.CarKey;
import com.xiaoma.login.sdk.CarKeyFactory;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.manager.ui.BindKeyVerifyActivity;
import com.xiaoma.personal.manager.ui.UnbindKeyVerifyActivity;
import com.xiaoma.ui.navi.NavigationBar;
import com.xiaoma.ui.toast.XMToast;

/**
 * Created by Gillben on 2019/1/8 0008
 * <p>
 * desc: 钥匙管理
 */
@PageDescComponent(EventConstants.PageDescribe.AccountManagerKey)
public class KeyManagerFragment extends BaseFragment implements View.OnClickListener {

    private Button mBluetoothKeyBt;
    private Button mNormalKeyBt;
    private User mUser;
    private CarKey mCarKey;

    public static KeyManagerFragment newInstance() {
        return new KeyManagerFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_key_manager, container, false);
        return super.onCreateWrapView(contentView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        NavigationBar naviBar = getNaviBar();
        if (naviBar != null) {
            naviBar.showBackNavi();
        }
        initView(view);
        UserManager.getInstance().addOnUserUpdateListener(new UserManager.OnUserUpdateListener() {
            @Override
            public void onUpdate(@Nullable User user) {
                refreshBindStatus();
            }
        });
    }

    private void initView(View view) {
        mBluetoothKeyBt = view.findViewById(R.id.unbind_bluetooth_key_bt);
        mNormalKeyBt = view.findViewById(R.id.unbind_normal_key_bt);

        mBluetoothKeyBt.setOnClickListener(this);
        mNormalKeyBt.setOnClickListener(this);

        refreshBindStatus();
    }

    private void refreshBindStatus() {
        User currentUser = UserManager.getInstance().getCurrentUser();
        if (TextUtils.isEmpty(currentUser.getBluetoothKeyId())) {
            mBluetoothKeyBt.setText(R.string.bind);
            mBluetoothKeyBt.setSelected(true);
        } else {
            mBluetoothKeyBt.setText(R.string.unbind);
            mBluetoothKeyBt.setSelected(false);
        }
        if (TextUtils.isEmpty(currentUser.getNormalKeyId())) {
            mNormalKeyBt.setText(R.string.bind);
            mNormalKeyBt.setSelected(true);
        } else {
            mNormalKeyBt.setText(R.string.unbind);
            mNormalKeyBt.setSelected(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unbind_bluetooth_key_bt:
                unOrBind(R.id.unbind_bluetooth_key_bt, v.isSelected());
                break;

            case R.id.unbind_normal_key_bt:
                unOrBind(R.id.unbind_normal_key_bt, v.isSelected());
                break;
        }
    }

    private void unOrBind(int resId, boolean bind) {
        mUser = UserManager.getInstance().getCurrentUser();
        mCarKey = CarKeyFactory.getSDK().getCarKey();
        User keyBoundUser = UserBindManager.getInstance().getKeyBoundUser();
        if (mUser == null) {
            XMToast.showToast(getContext(), R.string.user_invalid);
            return;
        } else if (bind) {
            if (mCarKey == null) {
                XMToast.showToast(getContext(), R.string.no_that_key);
                return;
            }
            if ((mCarKey.isBle() && resId == R.id.unbind_normal_key_bt)
                    || (!mCarKey.isBle() && resId == R.id.unbind_bluetooth_key_bt)) {
                XMToast.showToast(getContext(), R.string.no_that_key);
                return;
            } else if (keyBoundUser != null) {
                if (keyBoundUser.getId() == mUser.getId()) {
                    XMToast.showToast(getContext(), R.string.already_bind_key);
                } else {
                    XMToast.toastException(getContext(), R.string.other_user_bound);
                }
                return;
            }
        }

        CarKey bindCarKey = null;
        if (resId == R.id.unbind_bluetooth_key_bt) {
            bindCarKey = CarKey.strOf(mUser.getBluetoothKeyId());
        } else if (resId == R.id.unbind_normal_key_bt) {
            bindCarKey = CarKey.strOf(mUser.getNormalKeyId());
        }

        if (bind) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(LoginConstants.IntentKey.USER, mUser);
            bundle.putSerializable(LoginConstants.IntentKey.CAR_KEY, mCarKey);
            BindKeyVerifyActivity.startForResult(this, bundle,
                    PasswordPageConstants.BIND_VERIFY_PAGE_REQUEST_CODE);
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(LoginConstants.IntentKey.USER, mUser);
            bundle.putSerializable(LoginConstants.IntentKey.CAR_KEY, bindCarKey);
            UnbindKeyVerifyActivity.startForResult(this, bundle,
                    PasswordPageConstants.UNBIND_VERIFY_PAGE_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PasswordPageConstants.UNBIND_VERIFY_PAGE_REQUEST_CODE
                || requestCode == PasswordPageConstants.BIND_VERIFY_PAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                refreshBindStatus();
            }
        }
    }
}
