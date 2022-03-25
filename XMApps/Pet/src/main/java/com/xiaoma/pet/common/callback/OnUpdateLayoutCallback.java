package com.xiaoma.pet.common.callback;

import android.support.v4.app.Fragment;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/22 0022 15:32
 *   desc:   在fragment中切换activity页面回调
 * </pre>
 */
public interface OnUpdateLayoutCallback {

    void updateLayoutOnFragment(Fragment fragment, String tag, boolean isStack);
}
