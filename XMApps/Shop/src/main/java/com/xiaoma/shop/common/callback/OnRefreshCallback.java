package com.xiaoma.shop.common.callback;

/**
 * Author: Ljb
 * Time  : 2019/7/10
 * Description:
 */
public interface OnRefreshCallback {

    void onSingleRefresh(long id, String filePath);
    void onRefreshAll();
}
