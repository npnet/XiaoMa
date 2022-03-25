package com.xiaoma.login.business.ui.infoview;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xiaoma.component.AppHolder;
import com.xiaoma.login.R;
import com.xiaoma.utils.ResUtils;

public class PersonalSexDialog extends BasePersonalInfoDialog implements View.OnClickListener {

    private TextView tvMan;
    private TextView tvWomen;
    private String gender;
    private boolean isMan = true;

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
        if (isMan) {
            selectMan();
        } else {
            selectWoman();
        }
    }

    @Override
    protected int[] getClickViewIds() {
        return new int[]{R.id.tv_man, R.id.tv_women};
    }

    @Override
    protected void onViewClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_man) {
            selectMan();
        } else if (i == R.id.tv_women) {
            selectWoman();
        }
    }

    @Override
    protected void onSure() {
        onSuccessResult(gender);
        isMan = !TextUtils.isEmpty(gender) && getString(R.string.man).equals(gender);
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
