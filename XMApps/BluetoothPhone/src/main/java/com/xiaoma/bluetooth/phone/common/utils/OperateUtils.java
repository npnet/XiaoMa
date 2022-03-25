package com.xiaoma.bluetooth.phone.common.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.constants.BluetoothPhoneConstants;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.bluetooth.phone.common.views.CircleCharAvatarView;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.ContactList;

import java.util.List;

/**
 * Created by qiuboxiang on 2018/12/6 15:53
 */
public class OperateUtils {


    public static boolean dial(String phoneNum) {
        Context context = BlueToothPhone.getContext();
        if (PhoneStateManager.getInstance(context).isBothCallBusy()) {
            XMToast.showToast(context, context.getString(R.string.excess_call_count));
            return false;
        }
        return BlueToothPhoneManagerFactory.getInstance().dial(phoneNum);
    }

    public static void setHeadImage(CircleCharAvatarView mIvHead, ContactBean bean) {
        Context context = BlueToothPhone.getContext();
        mIvHead.setText(null);
        if (bean != null) {
            if (bean.getIcon() != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bean.getIcon(), 0, bean.getIcon().length);
                mIvHead.setImageBitmap(bitmap);
            } else if (bean.getName().equals(context.getString(R.string.unknown_contact)) || !isLetterOrChinese(bean.getName().substring(0, 1))) {
                mIvHead.setImageDrawable(context.getDrawable(R.drawable.head));
            } else {
                mIvHead.setImageDrawable(context.getDrawable(R.drawable.bg_head));
                mIvHead.setText(bean.getName());
            }
        }
    }

    private static boolean isLetterOrChinese(String str) {
        String regex = "^[a-zA-Z\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }

    public static void upLoadContact(List<ContactBean> contactBeans) {
        if (ListUtils.isEmpty(contactBeans)) return;
        ContactList contactList = new ContactList();
        for (ContactBean bean : contactBeans) {
            contactList.dictcontant.add(new ContactList.Contact(bean.getName()));
        }
        RemoteIatManager.getInstance().upLoadContact(true, GsonHelper.toJson(contactList));
    }

}
