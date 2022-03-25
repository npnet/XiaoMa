package com.xiaoma.club.msg.chat.controller;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.amap.api.services.core.PoiItem;

import java.util.Objects;

/**
 * Created by LKF on 2019-1-9 0009.
 */
public class PoiItemCallback extends DiffUtil.ItemCallback<PoiItem> {
    @Override
    public boolean areItemsTheSame(@NonNull PoiItem oldItem, @NonNull PoiItem newItem) {
        return Objects.equals(oldItem.getPoiId(), newItem.getPoiId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull PoiItem oldItem, @NonNull PoiItem newItem) {
        return false;
    }
}
