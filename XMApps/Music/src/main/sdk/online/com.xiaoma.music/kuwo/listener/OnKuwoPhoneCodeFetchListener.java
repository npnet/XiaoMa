package com.xiaoma.music.kuwo.listener;

import com.xiaoma.music.kuwo.model.XMPhoneCodeResult;

/**
 * @author zs
 * @date 2018/10/16 0016.
 */
public interface OnKuwoPhoneCodeFetchListener {

    void onFetch(XMPhoneCodeResult phoneCodeResult);
}
