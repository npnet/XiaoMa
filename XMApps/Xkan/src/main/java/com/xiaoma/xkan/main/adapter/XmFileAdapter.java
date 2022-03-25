package com.xiaoma.xkan.main.adapter;

import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.xkan.R;
import com.xiaoma.xkan.common.base.XMBaseFilterAdapter;
import com.xiaoma.xkan.common.constant.XkanConstants;
import com.xiaoma.xkan.common.model.UsbMediaInfo;
import com.xiaoma.xkan.common.util.ImageUtils;

import java.util.List;

public class XmFileAdapter extends XMBaseFilterAdapter {

    public XmFileAdapter(List<UsbMediaInfo> dataList, RequestManager imgReq) {
        super(dataList, R.layout.item_file, imgReq);
    }

    @Override
    protected void convert(BaseViewHolder holder, UsbMediaInfo usbMediaInfo) {
        try {
            if (usbMediaInfo.getFileType() == XkanConstants.FILE_TYPE_DEC) {
                holder.setText(R.id.tv_name, usbMediaInfo.getMediaName());
            } else {
                holder.setText(R.id.tv_name, FileUtils.getFileNameNoEx(usbMediaInfo.getMediaName()));
            }
            RequestManager imgReq = getImgRequest();
            if (usbMediaInfo.getFileType() == XkanConstants.FILE_TYPE_DEC) {
                imgReq.load(R.drawable.icon_folder)
                        .transform(getTransformation())
                        .format(DecodeFormat.PREFER_RGB_565)
                        .into((ImageView) holder.getView(R.id.iv_cover));
                holder.setBackgroundRes(R.id.icon_bg, R.drawable.highlight);
            } else if (usbMediaInfo.getFileType() == XkanConstants.FILE_TYPE_VIDEO) {
                imgReq.load(usbMediaInfo.getPath())
                        .placeholder(R.drawable.icon_video_default)
                        .error(R.drawable.icon_video_error)
                        .dontAnimate()
                        .transform(getTransformation())
                        .format(DecodeFormat.PREFER_RGB_565)
                        .into((ImageView) holder.getView(R.id.iv_cover));
                holder.setBackgroundRes(R.id.icon_bg, R.drawable.img_video_play);
            } else {
                imgReq.load(usbMediaInfo.getPath())
                        .error(R.drawable.icon_img_error)
                        .dontAnimate()
                        .transform(getTransformation())
                        .format(DecodeFormat.PREFER_RGB_565)
                        .into((ImageView) holder.getView(R.id.iv_cover));
                if (ImageUtils.isGif(usbMediaInfo.getPath())) {
                    holder.setBackgroundRes(R.id.icon_bg, R.drawable.img_gif_play);
                } else {
                    holder.setBackgroundRes(R.id.icon_bg, R.drawable.highlight);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
