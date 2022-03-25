package com.xiaoma.instructiondistribute.xkan.picture.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.instructiondistribute.R;
import com.xiaoma.instructiondistribute.xkan.common.base.XMBaseFilterAdapter;
import com.xiaoma.instructiondistribute.xkan.common.model.UsbMediaInfo;
import com.xiaoma.instructiondistribute.xkan.common.util.ImageUtils;
import com.xiaoma.ui.vh.XMViewHolder;
import com.xiaoma.utils.FileUtils;

import java.util.List;

/**
 * @author taojin
 * @date 2018/11/15
 */
public class XmPictureAdapter extends XMBaseFilterAdapter {

    public XmPictureAdapter(Context context, List<UsbMediaInfo> datas) {
        super(context, datas, R.layout.item_picture);
    }

    @Override
    protected void convert(XMViewHolder holder, final UsbMediaInfo usbMediaInfo, int position) {
        holder.setText(R.id.tv_name, FileUtils.getFileNameNoEx(usbMediaInfo.getMediaName()));

        ImageLoader.with(mContext)
                .load(usbMediaInfo.getPath())
                .error(R.drawable.icon_img_error)
                .dontAnimate()
                .into((ImageView) holder.getView(R.id.iv_cover));

        if (ImageUtils.isGif(usbMediaInfo.getPath())) {
            holder.setBackgroundRes(R.id.icon_bg, R.drawable.img_gif_play);
        } else {
            holder.setBackgroundRes(R.id.icon_bg, R.drawable.highlight);
        }

    }

}
