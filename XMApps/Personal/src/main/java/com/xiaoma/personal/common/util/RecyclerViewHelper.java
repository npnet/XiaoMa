package com.xiaoma.personal.common.util;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/6
 */
public class RecyclerViewHelper {

    public static void addVerticalDivider(RecyclerView recyclerView, @DrawableRes int drawableId) {
        Context context = recyclerView.getContext();
        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ContextCompat.getDrawable(context, drawableId));
        recyclerView.addItemDecoration(decoration);
    }

    public static void addHorizontalDivider(RecyclerView recyclerView, @DrawableRes int drawableId) {
        Context context = recyclerView.getContext();
        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
        decoration.setDrawable(ContextCompat.getDrawable(context, drawableId));
        recyclerView.addItemDecoration(decoration);
    }
}
