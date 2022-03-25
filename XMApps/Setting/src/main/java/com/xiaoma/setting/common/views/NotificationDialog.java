package com.xiaoma.setting.common.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spanned;
import android.widget.TextView;

import com.xiaoma.setting.R;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/7/9
 * @Desc: 标题加内容形式对话框
 */
public class NotificationDialog extends Dialog {

    private TextView tvTitle;
    private TextView tvContent;

    public NotificationDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notification);
        setCanceledOnTouchOutside(true);
        initView();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }

    public void setContent(String content){
        tvContent.setText(content);
    }

    public void setContent(Spanned content){
        tvContent.setText(content);
    }
}
