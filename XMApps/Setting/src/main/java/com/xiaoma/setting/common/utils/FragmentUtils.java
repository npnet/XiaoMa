package com.xiaoma.setting.common.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

/**
 * @Author ZiXu Huang
 * @Data 2019/2/12
 */
public class FragmentUtils {
    public static Fragment newFragmentInstance(FragmentManager fm, String fragmentNamePath) {
        if (fm == null || TextUtils.isEmpty(fragmentNamePath)) {
            return null;
        }
        Fragment fragmentByTag = fm.findFragmentByTag(fragmentNamePath);
        if (fragmentByTag == null) {
            try {
                fragmentByTag = (Fragment) Class.forName(fragmentNamePath).newInstance();
            } catch (Exception e) {

            }
        } else {
            fm.beginTransaction().hide(fragmentByTag).commit();
        }
        return fragmentByTag;
    }
}
