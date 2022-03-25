package com.xiaoma.launcher.common.views;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.ui
 *  @file_name:      EditMsgDialog
 *  @author:         Rookie
 *  @create_time:    2019/1/16 11:23
 *  @description：   TODO             */

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;

public class EditMsgDialog extends Dialog {

    public EditText mEtName;


    public OnClickSureListener mOnClickSureListener;

    public interface OnClickSureListener {
        void onClickSure(String name);
    }


    public void setOnClickSureListener(OnClickSureListener mOnClickSureListener) {
        this.mOnClickSureListener = mOnClickSureListener;
    }

    public EditMsgDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    public EditMsgDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_user_room);
        mEtName = findViewById(R.id.et_name);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.EDITMSG_DIALOG_SURE)
            @ResId(R.id.tv_cancel)//按钮对应的R文件id
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.btn_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick(EventConstants.NormalClick.EDITMSG_DIALOG_CANCEL)
            @ResId(R.id.btn_sure)//按钮对应的R文件id
            public void onClick(View v) {
                if (mOnClickSureListener != null) {
                    mOnClickSureListener.onClickSure(mEtName.getText().toString().trim());
                }
            }
        });
    }

    private void setDialogWidthAndHeight() {
        // 动态设置自定义Dialog的显示内容的宽和高
        Window mWindow = getWindow();
        WindowManager.LayoutParams params = mWindow.getAttributes();
        params.gravity = Gravity.TOP | Gravity.START;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        mWindow.setAttributes(params);

    }

    public EditText getEditText() {
        return mEtName;
    }

    @Override
    public void show() {
        super.show();
        setDialogWidthAndHeight();
    }
}
