package com.xiaoma.xkan.common.base;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.xkan.common
 *  @file_name:      XMBaseFilterAdapter
 *  @author:         Rookie
 *  @create_time:    2018/12/20 17:43
 *  @description：   TODO             */

import android.support.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.xkan.common.model.UsbMediaInfo;

import java.util.List;

public abstract class XMBaseFilterAdapter extends XMBaseAbstractBQAdapter<UsbMediaInfo, BaseViewHolder> {

    private static final int IMG_RADIUS = 8;
    private RequestManager mImgRequest;

    public XMBaseFilterAdapter(List<UsbMediaInfo> dataList, int layoutId, @NonNull RequestManager imgReq) {
        super(layoutId, dataList);
        mImgRequest = imgReq;
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getMediaName(), "");
    }

    private BitmapTransformation mTransformation;

    protected BitmapTransformation getTransformation() {
        if (mTransformation == null) {
            mTransformation = new RoundedCorners(IMG_RADIUS);
        }
        return mTransformation;
    }

    @NonNull
    protected RequestManager getImgRequest() {
        return mImgRequest;
    }
}
