package com.qiming.fawcard.synthesize.base.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.qiming.fawcard.synthesize.R;
import com.qiming.fawcard.synthesize.base.widget.BackButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class LoadingNormalDialog extends LoadingDialog {
    private static final String TAG = "LoadingNormalDialog";
    @BindView(R.id.iv_loading1)
    ImageView ivLoading1;
    @BindView(R.id.ibBackButton)
    BackButton ibBackButton;
    private RotateAnimation mRotateAnimation;

    @Inject
    public LoadingNormalDialog(@NonNull Context context) {
        super(context, R.layout.dialog_normal_loading);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initView() {
        //加载旋转图片
        mRotateAnimation = new RotateAnimation(0, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        //设置图片转一圈用时
        mRotateAnimation.setDuration(4000);
        //设置动画持续时间(无限大)
        mRotateAnimation.setRepeatCount(-1);
        ivLoading1.startAnimation(mRotateAnimation);
    }

    @Override
    public void dismiss() {
        mRotateAnimation.cancel();
        super.dismiss();
    }
    @OnClick(R.id.ibBackButton)
    public void onViewClicked() {
        dismiss();
    }

    @Override
    public void show() {
        super.show();
        initView();
    }
}
