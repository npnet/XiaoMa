package com.xiaoma.ui.dialog.impl;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/21
 */
public class XMCompatDialog {

    public static XMMiddleTextDialog createMiddleTextDialog() {
        return XMMiddleTextDialog.newInstance();
    }

    public static XMSmallTextDialog createSmallDialog() {
        return XMSmallTextDialog.newInstance();
    }
}
