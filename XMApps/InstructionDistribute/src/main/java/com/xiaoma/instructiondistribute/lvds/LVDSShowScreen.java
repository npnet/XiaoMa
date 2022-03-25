//package com.xiaoma.instructiondistribute.lvds;
//
//import android.annotation.DrawableRes;
//import android.app.Presentation;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.util.Log;
//import android.view.Display;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.ImageView;
//
//import com.xiaoma.carlib.constant.SDKConstants;
//import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
//import com.xiaoma.instructiondistribute.R;
//
///**
// * <des>
// *
// * @author YangGang
// * @date 2019/7/11
// */
//public class LVDSShowScreen extends Presentation {
//
//    private ImageView mShowLVDSIV;
//    private XmCarVendorExtensionManager mXmCarVendorExtensionManager;
//
//    public LVDSShowScreen(Context outerContext, Display display) {
//        super(outerContext, display);
//    }
//
//    public LVDSShowScreen(Context outerContext, Display display, int theme) {
//        super(outerContext, display, theme);
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mXmCarVendorExtensionManager = XmCarVendorExtensionManager.getInstance();
//        mXmCarVendorExtensionManager.setSimpleMenuDisplay(SDKConstants.VALUE.CanCommon_ON);
//
//        Log.d("LVDSShowScreen", "I am LVD Screen,Welcome you!");
//        setContentView(R.layout.dialog_lvds);
//        mShowLVDSIV = findViewById(R.id.ivShowLVDS);
//
//    }
//
//    public void showLVDS(@DrawableRes int resId) {
//        if (mShowLVDSIV != null) {
//            mShowLVDSIV.setVisibility(View.VISIBLE);
//            mXmCarVendorExtensionManager.setInteractMode(SDKConstants.VALUE.InteractMode_MEDIA_REQ);
//            XmCarVendorExtensionManager.getInstance().setMediaMenuLevel(1);
//            mShowLVDSIV.setImageResource(resId);
//        }
//    }
//
//    public void hideLVDS() {
//        if (mXmCarVendorExtensionManager != null) {
//            mXmCarVendorExtensionManager.setInteractMode(SDKConstants.VALUE.InteractMode_INACTIVE_REQ);
//            mShowLVDSIV.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    @Override
//    public void show() {
//        if (Settings.canDrawOverlays(getContext())) {
//            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//            super.show();
//        } else {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getContext().getPackageName()));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            getContext().startActivity(intent);
//        }
//    }
//}
