package com.xiaoma.launcher.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.app.model.LauncherApp;
import com.xiaoma.launcher.common.manager.LanguageUtils;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.business.ui.ChooseUserActivity;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.User;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.vrpractice.ui.MainActivity;

import java.util.List;

/**
 * @author taojin
 * @date 2019/1/15
 */

public class LauncherAppAdapter extends XMBaseAbstractBQAdapter<LauncherApp, BaseViewHolder> {
    private static final String ASSISTANT_PACKAGE = "com.xiaoma.assistant";
    private static final String PRACTICE_PACKAGE = "com.xiaoma.launcher";
    private static final String PANORAMIC_IMAGE_PACKAGE = "com.android.settings";
    private RequestManager requestManager;

    public LauncherAppAdapter(int layoutResId, @Nullable List<LauncherApp> data, RequestManager mgr) {
        super(layoutResId, data);
        this.requestManager = mgr;
    }

    @Override
    protected void convert(BaseViewHolder helper, LauncherApp item) {
        String packageName = item.getPackageName();
        final Context appContext = mContext.getApplicationContext();//不要手贱乱改，会出大bug
        final Drawable errorDrawable = appContext.getDrawable(R.drawable.app_icon_default);
        if (packageName != null) {
            Drawable drawable = null;
            try {
                int index = packageName.lastIndexOf(".");
                int resId = appContext.getResources().getIdentifier("icon_" + packageName.substring(index + 1),
                        "drawable", mContext.getPackageName());
                KLog.d("XM_LOG_" + "convert: " + "icon_" + packageName.substring(index + 1));
                drawable = appContext.getDrawable(resId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if ("com.xiaoma.launcher".equals(packageName)) {
                final String appName = item.getAppName();
                if ("语音训练".equals(appName)) {
                    requestManager.load(appContext.getDrawable(R.drawable.icon_practice))
                            .error(errorDrawable)
                            .into((ImageView) helper.getView(R.id.iv_app));
                } else {
                    requestManager.load(item.getIconUrl())
                            .error(errorDrawable)
                            .into((ImageView) helper.getView(R.id.iv_app));
                }
            } else {
                if (drawable != null) {
                    requestManager.load(drawable)
                            .error(errorDrawable)
                            .into((ImageView) helper.getView(R.id.iv_app));
                } else {
                    requestManager.load(item.getIconUrl())
                            .error(errorDrawable)
                            .into((ImageView) helper.getView(R.id.iv_app));
                }
            }
        }
        String appName;
        if (LanguageUtils.isChinese(AppHolder.getInstance().getAppContext())) {
            appName = item.getAppName();
        } else {
            appName = item.getAppNameEn();
        }
        helper.setText(R.id.tv_app_name, appName);
        helper.setOnTouchListener(R.id.fl_content, new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.setAlpha(1.0f);
                        break;
                }
                return false;
            }
        });
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canUse(packageName)) {
                    startApp(packageName);
                }
            }
        });
    }


    private void startAssistant() {
        Intent intent = new Intent();
        intent.setAction(VrConstants.Actions.SHOW_ASSSISTANT_DIALOG);
        mContext.sendBroadcast(intent);
    }

    private void startApp(String packageName) {
        if (AppUtils.isAppInstalled(mContext, packageName)) {
            if (ASSISTANT_PACKAGE.equals(packageName)) {
                startAssistant();
                return;
            }
            if (PRACTICE_PACKAGE.equals(packageName)) {
                if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.VRPRACTICE,
                        new OnBlockCallback() {
                            @Override
                            public boolean onShowToast(LoginType loginType) {
                                XMToast.showToast(mContext, loginType.getPrompt(mContext));
                                return true;
                            }
                        })) return;
                //跳转到语音训练
                mContext.startActivity(new Intent(mContext, MainActivity.class));
                return;
            }
            if (CenterConstants.BLUETOOTH_PHONE.equals(packageName)/* || CenterConstants.XKAN.equals(packageName)*/) {
                LaunchUtils.openApp(mContext, packageName);
                return;
            }
            if (PANORAMIC_IMAGE_PACKAGE.equals(packageName)) {
                //打开360环视
                XmCarVendorExtensionManager.getInstance().onAvsSwitch();
                return;
            }
            LaunchUtils.launchApp(mContext, packageName);
        } else {
            XMToast.toastException(mContext, R.string.app_no_install_tip);
        }
    }

    private boolean canUse(final String packageName) {
        return LoginTypeManager.getInstance().judgeUse(packageName,
                new OnBlockCallback() {
                    @Override
                    public boolean onKeyVerification(LoginType loginType) {
                        if (TextUtils.isEmpty(packageName)) return true;
                        User currentUser = UserManager.getInstance().getCurrentUser();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(LoginConstants.KeyBind.BundleKey.USER, currentUser);
                        LoginTypeManager.getInstance().keyVerificationAndStartApp(mContext, bundle, packageName);
                        return true;
                    }

                    @Override
                    public boolean onChooseAccount(LoginType loginType) {
                        ChooseUserActivity.start(mContext);
                        return true;
                    }

                    @Override
                    public boolean onShowToast(LoginType loginType) {
                        XMToast.showToast(mContext, loginType.getPrompt(mContext));
                        return true;
                    }
                });
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getAppName(), getData().get(position).getPackageName());
    }
}
