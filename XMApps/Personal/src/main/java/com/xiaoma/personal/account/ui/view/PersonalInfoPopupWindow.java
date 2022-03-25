package com.xiaoma.personal.account.ui.view;

import android.content.Context;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.InfoType;
import com.xiaoma.ui.navi.NavigationBar;

/**
 * @author Gillben
 * date: 2018/12/03
 */
public class PersonalInfoPopupWindow extends PopupWindow implements View.OnClickListener, PopupWindow.OnDismissListener {

    private Context mContext;
    private NavigationBar mNavigationBar;
    private TextView mPersonalNameText;
    private TextView mPersonalSexText;
    private TextView mPersonalAgeText;

    private RelativeLayout nameRelative;
    private RelativeLayout sexRelative;
    private RelativeLayout ageRelative;

    private OnModifyPersonalInfoCallback onModifyPersonalInfoCallback;

    public PersonalInfoPopupWindow(Context context, int with, int height) {
        this.mContext = context;
        View mContentView = View.inflate(context, R.layout.pop_window_personal_info, null);
        setContentView(mContentView);

        initView(mContentView);
        setSize(with, height);
        setAnimationStyle(R.style.PersonalInfoAnimation);
    }

    private void setSize(int with, int height) {
        setWidth(with);
        setHeight(height);
    }

    private void initView(View view) {
        setFocusable(true);
        setTouchable(true);
        setClippingEnabled(false);
        setOnDismissListener(this);

        mNavigationBar = view.findViewById(R.id.personal_popup_nav);
        mNavigationBar.showBackNavi();

        mPersonalNameText = view.findViewById(R.id.personal_name_text);
        mPersonalSexText = view.findViewById(R.id.personal_sex_text);
        mPersonalAgeText = view.findViewById(R.id.personal_age_text);

        nameRelative = view.findViewById(R.id.personal_name_relative);
        nameRelative.setOnClickListener(this);
        sexRelative = view.findViewById(R.id.personal_sex_relative);
        sexRelative.setOnClickListener(this);
        ageRelative = view.findViewById(R.id.personal_age_relative);
        ageRelative.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.personal_name_relative:
                modifiedPersonalInfo(InfoType.INFO_NAME);
                break;

            case R.id.personal_sex_relative:
                modifiedPersonalInfo(InfoType.INFO_SEX);
                break;

            case R.id.personal_age_relative:
                modifiedPersonalInfo(InfoType.INFO_AGE);
                break;
        }
    }


    private void modifiedPersonalInfo(InfoType type) {
        if (onModifyPersonalInfoCallback != null) {
            onModifyPersonalInfoCallback.modifyPersonalInfo(type);
//            dismiss();
        }
    }


    public void setOnModifyPersonalInfoCallback(OnModifyPersonalInfoCallback callback) {
        this.onModifyPersonalInfoCallback = callback;
    }


    public interface OnModifyPersonalInfoCallback {
        void modifyPersonalInfo(InfoType type);

        void open();

        void close();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (onModifyPersonalInfoCallback != null) {
            onModifyPersonalInfoCallback.open();
        }
    }

    @Override
    public void onDismiss() {
        if (onModifyPersonalInfoCallback != null) {
            onModifyPersonalInfoCallback.close();
        }
    }


    public void setPersonalName(String name) {
        if (mPersonalNameText != null) {
            mPersonalNameText.setText(name);
        }
    }

    public void setPersonalSex(String sex) {
        if (mPersonalSexText != null) {
            mPersonalSexText.setText(sex);
        }
    }

    public void setPersonalAge(String age) {
        if (mPersonalAgeText != null) {
            mPersonalAgeText.setText(age);
        }
    }

}
