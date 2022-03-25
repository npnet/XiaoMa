package com.xiaoma.xting.common.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseViewHolder;

/**
 * @author KY
 * @date 2018/11/6
 */
public class MyBaseViewHolder extends BaseViewHolder {
    public MyBaseViewHolder(View view) {
        super(view);
    }

    @Override
    public BaseViewHolder setVisible(int viewId, boolean visible) {
        if (getView(viewId) == null) return null;
        return super.setVisible(viewId, visible);
    }
}
