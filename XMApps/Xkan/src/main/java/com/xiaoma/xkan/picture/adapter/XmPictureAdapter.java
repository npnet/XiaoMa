package com.xiaoma.xkan.picture.adapter;

import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.base.XMBaseFilterAdapter;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.common.util.ImageUtils;

import java.util.List;

/**
 * @author taojin
 * @date 2018/11/15
 */
public class XmPictureAdapter extends XMBaseFilterAdapter {

    public XmPictureAdapter(List<UsbMediaInfo> dataList, RequestManager imgReq) {
        super(dataList, R.layout.item_picture, imgReq);
    }

    @Override
    protected void convert(BaseViewHolder holder, UsbMediaInfo usbMediaInfo) {
        try {
            holder.setText(R.id.tv_name, FileUtils.getFileNameNoEx(usbMediaInfo.getMediaName()));
            if (ImageUtils.isGif(usbMediaInfo.getPath())) {
                holder.setBackgroundRes(R.id.icon_bg, R.drawable.img_gif_play);
            } else {
                holder.setBackgroundRes(R.id.icon_bg, R.drawable.highlight);
            }
            getImgRequest()
                    .load(usbMediaInfo.getPath())
                    .error(R.drawable.icon_img_error)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .dontAnimate()
                    .transform(getTransformation())
                    .override(160)
                    .into((ImageView) holder.getView(R.id.iv_cover));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
