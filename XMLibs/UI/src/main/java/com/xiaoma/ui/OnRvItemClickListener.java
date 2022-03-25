package com.xiaoma.ui;

/**
 * Created by ZYao.
 * Date ：2018/10/13 0013
 */
public interface OnRvItemClickListener<T> {
    void onItemClick(int position, T t);

    void onItemDelete();
}
