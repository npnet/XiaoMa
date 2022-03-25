package com.xiaoma.xkan.video.adapter;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.base.XMBaseFilterAdapter;
import com.xiaoma.xkan.common.model.UsbMediaInfo;

import java.util.List;

/**
 * @author taojin
 * @date 2018/11/15
 */
public class XmVideoAdapter extends XMBaseFilterAdapter {


    public XmVideoAdapter(List<UsbMediaInfo> dataList, @NonNull RequestManager imgReq) {
        super(dataList, R.layout.item_video, imgReq);
    }

    @Override
    protected void convert(BaseViewHolder holder, UsbMediaInfo usbMediaInfo) {
        try {
            holder.setText(R.id.tv_name, FileUtils.getFileNameNoEx(usbMediaInfo.getMediaName()));
            getImgRequest()
                    .load(usbMediaInfo.getPath())
                    .placeholder(R.drawable.icon_video_default)
                    .error(R.drawable.icon_video_error)
                    .dontAnimate()
                    .transform(getTransformation())
                    .override(160)
                    .into((ImageView) holder.getView(R.id.iv_cover));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
