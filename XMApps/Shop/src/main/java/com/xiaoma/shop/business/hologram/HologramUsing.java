package com.xiaoma.shop.business.hologram;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fsl.android.uniqueota.UniqueOtaConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.store.HologramRepo;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.model.HoloListModel;
import com.xiaoma.shop.business.model.HologramDress;
import com.xiaoma.shop.common.manager.update.UpdateOtaManager;
import com.xiaoma.ui.toast.XMToast;

/**
 * Created by LKF on 2019-7-1 0001.
 */
public class HologramUsing {
    public static final String ACTION_ROLE_USING = "com.xiaoma.shop.3D_ROLE_USING";
    public static final String ACTION_ROLE_CLOTH_USING = "com.xiaoma.shop.3D_ROLE_CLOTH_USING";
    public static final String EXTRA_ROLE_MODEL = "role_model";
    public static final String EXTRA_ROLE_CLOTH_MODEL = "role_cloth_model";
    private static final int INVALID_ROLE_ID = Integer.MIN_VALUE;
    private static final String TAG = "HologramUsing";
    public static final int DIY_ROLE_ID = 5;// 自定义形象的固定ID

    /**
     * 切换3D角色
     */
    public static void useRole(Context context, HoloListModel role) {
        if (context == null || role == null)
            return;
        if (HoloListModel.TYPE_3D_SELF == role.getType()) {
            // 注意,传给狗尾草那边的形象id是code字段,HoloListModel中的id字段只是后台的id
            XmCarVendorExtensionManager.getInstance().setRobCharacterMode(DIY_ROLE_ID);
            // 切换人物后的动作
            XmCarVendorExtensionManager.getInstance().setRobAction(1);
            // 发送广播通知
            context.sendBroadcast(new Intent(ACTION_ROLE_USING)
                    .putExtra(EXTRA_ROLE_MODEL, role));
            return;
        }
        if (HoloListModel.TYPE_3D_NORMAL == role.getType()) {
            UpdateOtaManager.getInstance().getInfos().remove(UniqueOtaConstants.EcuId.ROBOT);
            int id = role.getId();
            String roleName = role.getName();
            int goldWildId = getRoleGoldWildId(role);
            Log.e(TAG, String.format("useRole(){ name: %s, id: %s, roleId: %s }", roleName, id, goldWildId));
            if (goldWildId != INVALID_ROLE_ID) {
                // 注意,传给狗尾草那边的形象id是code字段,HoloListModel中的id字段只是后台的id
                XmCarVendorExtensionManager.getInstance().setRobCharacterMode(goldWildId);
                // 切换人物后的动作
                XmCarVendorExtensionManager.getInstance().setRobAction(1);
                // 发送广播通知
                context.sendBroadcast(new Intent(ACTION_ROLE_USING)
                        .putExtra(EXTRA_ROLE_MODEL, role));
            } else {
                XMToast.toastException(context, R.string.tips_3d_invalid_role);
            }
            return;
        }
        XMToast.toastException(context, R.string.tips_3d_invalid_role);
    }

    /**
     * 是否使用中的角色
     */
    public static boolean isRoleUsing(Context context, HoloListModel role) {
        if (role == null)
            return false;
        int goldWildId = getRoleGoldWildId(role);
        if (goldWildId == INVALID_ROLE_ID)
            return false;
        return goldWildId == HologramRepo.getUsingRoleId(context);
    }

    public static void useCloth(Context context, HoloListModel role, HologramDress cloth) {
        int roleId = getRoleGoldWildId(role);
        int clothId = getClothGoldWildId(cloth);
        Log.e(TAG, String.format("useCloth: roleId: %s, clothId: %s", roleId, clothId));
        if (roleId != INVALID_ROLE_ID
                && clothId >= HologramRepo.DEFAULT_CLOTH_ID) {
            // 发送服装切换信号
            XmCarVendorExtensionManager.getInstance().setRobClothMode(roleId, clothId);
            // 发送服装切换的动作信号
            XmCarVendorExtensionManager.getInstance().setRobAction(3);
            // 发送人物切换通知(切换衣服的同时,角色ID也会生效)
            context.sendBroadcast(new Intent(ACTION_ROLE_USING)
                    .putExtra(EXTRA_ROLE_MODEL, role));
            // 发送衣服切换通知
            context.sendBroadcast(new Intent(ACTION_ROLE_CLOTH_USING)
                    .putExtra(EXTRA_ROLE_MODEL, role)
                    .putExtra(EXTRA_ROLE_CLOTH_MODEL, cloth));
        } else {
            XMToast.toastException(context, R.string.tips_3d_invalid_cloth);
        }
    }

    /*public static boolean isClothUsing(Context context, HoloListModel role, HologramDress cloth) {
//        if (!isRoleUsing(context, role))
//            return false;
        return getClothGoldWildId(cloth) == HologramRepo.getUsingClothId(context, getRoleGoldWildId(role));
    }*/

    public static int getRoleGoldWildId(HoloListModel role) {
        if (role != null) {
            try {
                return Integer.parseInt(role.getCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return INVALID_ROLE_ID;
    }

    public static int getClothGoldWildId(HologramDress cloth) {
        if (cloth != null) {
            try {
                return Integer.parseInt(cloth.getCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return HologramRepo.DEFAULT_CLOTH_ID - 1;
    }
}
