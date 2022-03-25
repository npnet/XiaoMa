package com.xiaoma.login.business.ui.subaccount.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.R;
import com.xiaoma.login.UserBindManager;
import com.xiaoma.login.business.ui.infoview.BasePersonalInfoDialog;
import com.xiaoma.login.business.ui.infoview.PersonalAgeDialog;
import com.xiaoma.login.business.ui.infoview.PersonalNameDialog;
import com.xiaoma.login.business.ui.infoview.PersonalRelationDialog;
import com.xiaoma.login.business.ui.infoview.PersonalSexDialog;
import com.xiaoma.login.business.ui.verify.FaceRecordActivity;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.LoginMethod;
import com.xiaoma.login.common.UserUtil;
import com.xiaoma.login.sdk.CarKey;
import com.xiaoma.login.sdk.CarKeyFactory;
import com.xiaoma.login.sdk.FaceId;
import com.xiaoma.model.User;
import com.xiaoma.network.callback.SimpleCallback;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.progress.loading.XMProgress;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.xiaoma.login.business.ui.subaccount.CreateSubAccountActivity.TAG_CREATE_SUCCESS;
import static com.xiaoma.login.business.ui.subaccount.CreateSubAccountActivity.TAG_FINISH;

/**
 * Created by kaka
 * on 19-5-24 下午5:52
 * <p>
 * desc: #a
 * </p>
 */
public class UserInitFragment extends AbsCreateSubAccountFragment implements View.OnClickListener, BackHandlerHelper.FragmentBackHandler {
    private static final int FACE_RECORD_REQUEST_CODE = 1001;

    private Button mConfirm;

    private TextView mPersonalNameText;
    private TextView mPersonalSexText;
    private TextView mPersonalAgeText;
    private TextView mPersonalRelationText;

    private LinearLayout nameRelative;
    private LinearLayout sexRelative;
    private LinearLayout ageRelative;
    private LinearLayout relationRelative;
    private Button mDoneSetting;
    private TextView mTips;
    private TextView mVerifyTitle;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private PersonalNameDialog personalNameDialog;
    private PersonalSexDialog personalSexDialog;
    private PersonalAgeDialog personalAgeDialog;
    private PersonalRelationDialog personalRelationDialog;

    public static UserInitFragment newInstance() {
        return new UserInitFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_init, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        if (!XmCarConfigManager.hasFaceRecognition()) {
            mVerifyTitle.setText(R.string.key_bind);
            mTips.setText(R.string.key_bind_tips);
            mConfirm.setVisibility(View.GONE);
        }
    }

    private void initView(View view) {

        mPersonalNameText = view.findViewById(R.id.personal_name_text);
        mPersonalSexText = view.findViewById(R.id.personal_sex_text);
        mPersonalAgeText = view.findViewById(R.id.personal_age_text);
        mPersonalRelationText = view.findViewById(R.id.personal_relation_text);
        nameRelative = view.findViewById(R.id.personal_name_relative);
        sexRelative = view.findViewById(R.id.personal_sex_relative);
        ageRelative = view.findViewById(R.id.personal_age_relative);
        relationRelative = view.findViewById(R.id.personal_relation_relative);
        mVerifyTitle = view.findViewById(R.id.verify_title);
        mTips = view.findViewById(R.id.tips);
        mConfirm = view.findViewById(R.id.confirm);
        mDoneSetting = view.findViewById(R.id.done_setting);

        nameRelative.setOnClickListener(this);
        sexRelative.setOnClickListener(this);
        ageRelative.setOnClickListener(this);
        relationRelative.setOnClickListener(this);

        mConfirm.setOnClickListener(this);
        mDoneSetting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.personal_name_relative) {
            showNameDialog();
        } else if (i == R.id.personal_sex_relative) {
            showSexDialog();
        } else if (i == R.id.personal_age_relative) {
            handleAgeSetup();
        } else if (i == R.id.personal_relation_relative) {
            showRelationDialog();
        } else if (i == R.id.confirm) {
            String value = mCreateSubAccountVM.getFaceId().getValue();
            int faceId = 0;
            try {
                faceId = Integer.valueOf(value);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!FaceId.isValid(faceId)) {
                    FaceId id = UserBindManager.getInstance().getAvailableFaceId();
                    if (id != null) {
                        faceId = id.getValue();
                    }
                }
            }
            Bundle bundle = new Bundle();
            bundle.putBoolean(LoginConstants.IntentKey.RECORD_TO_CREATE, true);
            bundle.putInt(LoginConstants.IntentKey.RECORD_TO_CREATE_BOUND_FACEID, faceId);
            FaceRecordActivity.newInstance(this, FACE_RECORD_REQUEST_CODE, bundle);
        } else if (i == R.id.done_setting) {
            if (XmCarConfigManager.hasFaceRecognition()) {
                showBindKeyDialog();
            } else {
                bindCarKey();
                createSubAccount();
            }
        }
    }

    private void bindCarKey() {
        CarKey carKey = CarKeyFactory.getSDK().getCarKey();
        mCreateSubAccountVM.setKeyId(carKey == null ? null : carKey.getStr());
        mCreateSubAccountVM.setIsBlueTooth(carKey != null && carKey.isBle());
    }

    private void showBindKeyDialog() {
        final ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setCancelable(true)
                .setContent(getString(R.string.bind_or_not_bind))
                .setPositiveButton(getString(R.string.bind), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bindCarKey();
                        createSubAccount();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.do_not_bind), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createSubAccount();
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FACE_RECORD_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mTips.setText(R.string.face_bound);
                mConfirm.setText(R.string.rebinding);
                int faceId = data.getIntExtra(LoginConstants.IntentKey.FACE_ID, 0);
                mCreateSubAccountVM.setFaceId(String.valueOf(faceId));
                checkConfirmStatus();
            } else {
                if (!TextUtils.isEmpty(mCreateSubAccountVM.getFaceId().getValue())) {
                    XMToast.toastException(mContext, R.string.face_bind_fail);
                }
            }
        }
    }

    private void createSubAccount() {
        showProgressDialog(R.string.base_loading);
        mCreateSubAccountVM.createSubAccount(new SimpleCallback<User>() {
            @Override
            public void onSuccess(User model) {
                String value = mCreateSubAccountVM.getFaceId().getValue();
                model.setFaceId(!TextUtils.isEmpty(value) ? Integer.valueOf(value) : 0);
                String passwdHash = UserUtil.getPasswdHash(mCreateSubAccountVM.getPasswd().getValue());
                model.setPassword(passwdHash);
                LoginManager.getInstance().manualLogin(model, LoginMethod.PASSWD.name());
                mCreateSubAccountVM.setPageTag(TAG_CREATE_SUCCESS);
                dismissProgress();
            }

            @Override
            public void onError(int code, String msg) {
                dismissProgress();
                if (TextUtils.isEmpty(msg) || code == -1
                        || !NetworkUtils.isConnected(getContext())) {
                    msg = getString(R.string.no_network);
                }
                XMToast.toastException(getContext(), msg);
            }
        });

    }

    private void showSexDialog() {
        if (personalSexDialog == null) {
            personalSexDialog = new PersonalSexDialog();
            personalSexDialog.setOnDialogCallback(new BasePersonalInfoDialog.OnDialogCallback() {

                @Override
                public void success(String content) {
                    mPersonalSexText.setText(content);
                    checkConfirmStatus();
                    mCreateSubAccountVM.setGender(getGenderByStr(content));
                    personalSexDialog.dismiss();
                }
            });
        }
        personalSexDialog.show(getChildFragmentManager(), null);
    }

    private int getGenderByStr(String gender) {
        if (getString(R.string.woman).equals(gender)) {
            return 0;
        } else if (getString(R.string.man).equals(gender)) {
            return 1;
        } else {
            return 1;
        }
    }

    private void showNameDialog() {
        if (personalNameDialog == null) {
            personalNameDialog = new PersonalNameDialog();
            personalNameDialog.setOnExDialogCallback(new PersonalNameDialog.OnExDialogCallback() {
                @Override
                public void success(String content) {
                    mPersonalNameText.setText(content);
                    checkConfirmStatus();
                    mCreateSubAccountVM.setName(content);
                    personalNameDialog.dismiss();
                }

                @Override
                public void onShowLoading() {
                    showProgressDialog(R.string.base_loading);
                }

                @Override
                public void onDismissLoading() {
                    dismissProgress();
                }
            });
        }
        personalNameDialog.show(getChildFragmentManager(), null);
    }


    private void handleAgeSetup() {
        Calendar currentTime = mCreateSubAccountVM.getCurrentTime().getValue();
        if (currentTime != null) {
            showAgeDialog(currentTime);
        } else {
            showProgressDialog(R.string.base_loading);
            mCreateSubAccountVM.fetchCurrentTime(new SimpleCallback<Calendar>() {
                @Override
                public void onSuccess(Calendar model) {
                    XMProgress.dismissProgressDialog(UserInitFragment.this);
                    showAgeDialog(model);
                }

                @Override
                public void onError(int code, String msg) {
                    XMProgress.dismissProgressDialog(UserInitFragment.this);
                    if (TextUtils.isEmpty(msg) || code == -1
                            || !NetworkUtils.isConnected(getContext())) {
                        msg = getString(R.string.no_network);
                    }
                    XMToast.toastException(mContext, msg);
                }
            });
        }
    }


    private void showAgeDialog(final Calendar now) {
        Calendar birthDay = mCreateSubAccountVM.getBirthdayTime().getValue();
        String birthDayStr = null;
        if (birthDay != null) {
            birthDayStr = dateFormat.format(birthDay.getTime());
        }
        if (personalAgeDialog == null) {
            personalAgeDialog = new PersonalAgeDialog();
            personalAgeDialog.setInitDate(dateFormat.format(now.getTime()), birthDayStr);
            personalAgeDialog.setOnDialogCallback(new BasePersonalInfoDialog.OnDialogCallback() {

                @Override
                public void success(String content) {
                    long timeStamp = Long.parseLong(content);
                    Calendar birthday = Calendar.getInstance();
                    birthday.setTimeInMillis(timeStamp);
                    int age = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
                    mPersonalAgeText.setText(String.valueOf(age));
                    checkConfirmStatus();
                    mCreateSubAccountVM.setBirthdayTime(birthday);
                    personalAgeDialog.dismiss();
                }
            });
        } else {
            personalAgeDialog.setInitDate(dateFormat.format(now.getTime()), birthDayStr);
        }
        personalAgeDialog.show(getChildFragmentManager(), null);
    }

    private void showRelationDialog() {
        if (personalRelationDialog == null) {
            personalRelationDialog = new PersonalRelationDialog();
            personalRelationDialog.setOnDialogCallback(new BasePersonalInfoDialog.OnDialogCallback() {

                @Override
                public void success(String content) {
                    mPersonalRelationText.setText(content);
                    checkConfirmStatus();

                    mCreateSubAccountVM.setRelation(getRelationByStr(content));
                    personalRelationDialog.dismiss();
                }
            });
        }
        personalRelationDialog.show(getChildFragmentManager(), null);
    }

    private int getRelationByStr(String relation) {
        if (getString(R.string.family).equals(relation)) {
            return 0;
        } else if (getString(R.string.friends).equals(relation)) {
            return 1;
        } else if (getString(R.string.loan).equals(relation)) {
            return 2;
        } else if (getString(R.string.self).equals(relation)) {
            return 3;
        } else {
            return 3;
        }
    }

    private void checkConfirmStatus() {
        if (!TextUtils.isEmpty(mPersonalNameText.getText().toString())
                && !TextUtils.isEmpty(mPersonalSexText.getText().toString())
                && !TextUtils.isEmpty(mPersonalAgeText.getText().toString())
                && !TextUtils.isEmpty(mPersonalRelationText.getText().toString())) {
            mDoneSetting.setEnabled(true);
        } else {
            mDoneSetting.setEnabled(false);
        }
    }

    @Override
    public boolean onBackPressed() {
        final ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setCancelable(true)
                .setTitle(getString(R.string.exit))
                .setContent(getString(R.string.exit_with_no_data_save))
                .setPositiveButton(getString(R.string.back_to_home), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCreateSubAccountVM.setPageTag(TAG_FINISH);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.back_to_phone_bind), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFragmentManager().popBackStack();
                        dialog.dismiss();
                    }
                }).show();
        return true;
    }
}
