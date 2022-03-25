package com.xiaoma.personal.feedback.callback;

import com.xiaoma.component.base.BaseFragment;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/7/3 0003 14:34
 *   desc:   切换fragment
 * </pre>
 */
public interface OnSwitchFragmentCallback {

    void onSwitchFragment(BaseFragment fragment, String tag, boolean isAddStack);
}
