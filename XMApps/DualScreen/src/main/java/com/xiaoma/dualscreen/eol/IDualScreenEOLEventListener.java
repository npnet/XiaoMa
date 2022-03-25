package com.xiaoma.dualscreen.eol;

import com.xiaoma.center.logic.remote.ClientCallback;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/16
 */
public interface IDualScreenEOLEventListener {
    //以防以后需要传递图片进行显示,这里先给一个bundle
    void showImage(ClientCallback callback);

    void hideImage(ClientCallback callback);
}
