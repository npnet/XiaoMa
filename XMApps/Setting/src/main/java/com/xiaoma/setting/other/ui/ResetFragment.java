package com.xiaoma.setting.other.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.setting.R;
import com.xiaoma.setting.Setting;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.views.MagicTextView;
import com.xiaoma.setting.other.manager.CleanAppDataManager;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Administrator on 2018/10/10 0010.
 */

@PageDescComponent(EventConstants.PageDescribe.resetSettingFragmentPagePathDesc)
public class ResetFragment extends BaseFragment implements View.OnClickListener, CleanAppDataManager.OnDeleteCallback {
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            XmCarVendorExtensionManager.getInstance().setRestoreCmd(0);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reset, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
    }

    private void bindView(View view) {
        view.findViewById(R.id.btn_reset).setOnClickListener(this);
        ((MagicTextView) view.findViewById(R.id.tv_reset)).setShine(true);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.resetSetting})
    @ResId({R.id.btn_reset})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:
                final ConfirmDialog dialog = new ConfirmDialog(getActivity());
                dialog.setContent(getString(R.string.content_ask_reset))
                        .setPositiveButton(getString(R.string.confirm), new View.OnClickListener() {
                            @Override
                            @NormalOnClick({EventConstants.NormalClick.confirmReset})
                            @ResId({R.id.tv_left_button})
                            public void onClick(View v) {
                                //SettingToast.build(mContext).setMessage(getString(R.string.reset_error)).setImage(R.drawable.icon_error).show(2000);
//                                XmCarVendorExtensionManager.getInstance().setRestoreCmd(0);
                                deleteAppData();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                            @Override
                            @NormalOnClick({EventConstants.NormalClick.cancelReset})
                            @ResId({R.id.tv_right_button})
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        })
                        .show();

//                View view = View.inflate(getActivity(), R.layout.dialog_confirm_ui, null);
//                TextView tv_content = view.findViewById(R.id.tv_content);
//                tv_content.setText(getString(R.string.content_ask_reset));
//                final XmDialog builder = new XmDialog.Builder(getActivity())
//                        .setView(view).setWidth(720).setHeight(443)
//                        .create();
//                view.findViewById(R.id.tv_right_button).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    @NormalOnClick({EventConstants.NormalClick.cancelReset})
//                    @ResId({R.id.tv_right_button})
//                    public void onClick(View v) {
//                        builder.dismiss();
//                    }
//                });
//                view.findViewById(R.id.tv_left_button).setOnClickListener(new View.OnClickListener(){
//                    @Override
//                    @NormalOnClick({EventConstants.NormalClick.confirmReset})
//                    @ResId({R.id.tv_left_button})
//                    public void onClick(View v) {
//                        SettingToast.build(mContext).setMessage(getString(R.string.reset_error)).setImage(R.drawable.icon_error).show(2000);
//                        builder.dismiss();
//                    }
//                });
//                builder.show();
                break;
        }
    }

    private void deleteAppData() {
        //关闭仪表展示
        XmCarFactory.getCarVendorExtensionManager().setInteractMode(SDKConstants.VALUE.HuInteractReq_INACTIVE_REQ);
        XmCarFactory.getCarVendorExtensionManager().setSimpleMenuDisplay(SDKConstants.VALUE.CanCommon_OFF);
        XmCarFactory.getCarVendorExtensionManager().setNaviDisplay(SDKConstants.VALUE.CanCommon_OFF);

        String[] packageList = getResources().getStringArray(R.array.app_package);
        CleanAppDataManager instance = CleanAppDataManager.getInstance();
        instance.setOnDeleteCallback(this);
        instance.deleteData(Setting.getContext(), packageList);
    }

    @Override
    public void onDeleteFinish() {
        // App缓存文件删除完成,删除系统配置信息
        KLog.d("hzx", "删除完成");
        boolean delete = FileUtils.delete(getContext().getCacheDir());
        KLog.d("hzx","小马设置删除cacheDir结果: " + delete);
        XmCarVendorExtensionManager.getInstance().setRestoreCmd(0);
//        handler.postDelayed(runnable, 3000);
    }
}
