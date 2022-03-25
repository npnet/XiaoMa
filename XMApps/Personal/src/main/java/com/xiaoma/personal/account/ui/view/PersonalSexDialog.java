package com.xiaoma.personal.account.ui.view;

import android.view.View;
import android.widget.TextView;

import com.xiaoma.component.AppHolder;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.OnlyCode;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;

public class PersonalSexDialog extends BasePersonalInfoDialog implements View.OnClickListener {

    private TextView tvMan;
    private TextView tvWomen;
    private String gender;

    @Override
    protected boolean isCancelableOutside() {
        return false;
    }

    @Override
    protected int contentLayoutId() {
        return R.layout.dialog_personal_sex;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.personal_sex);
    }

    @Override
    public void onBindView(View view) {
        tvMan = view.findViewById(R.id.tv_man);
        tvWomen = view.findViewById(R.id.tv_women);

        User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
        if (user != null) {
            if (user.getGender() == 0) {
                selectWoman();
            } else {
                selectMan();
            }
        } else {
            selectMan();
        }
    }

    @Override
    protected int[] getClickViewIds() {
        return new int[]{R.id.tv_man, R.id.tv_women};
    }

    @Override
    protected void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_man:
                selectMan();
                break;
            case R.id.tv_women:
                selectWoman();
                break;
        }
    }

    @Override
    protected void onSure() {
        onLoading();
        final int genderInt = this.gender.equals(getString(R.string.man)) ? 1 : 0;
        RequestManager.changeUserGender(genderInt, new ResultCallback<XMResult<OnlyCode>>() {
            @Override
            public void onSuccess(XMResult<OnlyCode> result) {
                if (result.isSuccess()) {
                    User user1 = UserManager.getInstance().getCurrentUser();
                    user1.setGender(genderInt);
                    UserManager.getInstance().notifyUserUpdate(user1);
                    XMToast.toastSuccess(getContext(), R.string.modify_success);
                    onSuccessResult(gender);
                    dismiss();
                } else {
                    onFail(-1, "fail");
                }
                onComplete();
            }

            @Override
            public void onFailure(int code, String msg) {
                if (!NetworkUtils.isConnected(AppHolder.getInstance().getAppContext())) {
                    XMToast.toastException(getContext(), R.string.no_network);
                } else {
                    onFail(code, msg);
                }
                onComplete();
            }
        });
    }

    private void selectMan() {
        tvMan.setBackgroundResource(R.drawable.bg_popu_sex_checked);
        tvWomen.setBackgroundResource(0);
        tvMan.setTextSize(ResUtils.getDimension(AppHolder.getInstance().getAppContext(), R.dimen.font_size_home));
        tvWomen.setTextSize(ResUtils.getDimension(AppHolder.getInstance().getAppContext(), R.dimen.font_size_home_28));
        gender = getString(R.string.man);
    }

    private void selectWoman() {
        tvMan.setBackgroundResource(0);
        tvWomen.setBackgroundResource(R.drawable.bg_popu_sex_checked);
        tvWomen.setTextSize(ResUtils.getDimension(AppHolder.getInstance().getAppContext(), R.dimen.font_size_home));
        tvMan.setTextSize(ResUtils.getDimension(AppHolder.getInstance().getAppContext(), R.dimen.font_size_home_28));
        gender = getString(R.string.woman);
    }
}
