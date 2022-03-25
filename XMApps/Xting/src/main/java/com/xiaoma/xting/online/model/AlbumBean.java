package com.xiaoma.xting.online.model;

import android.content.Context;
import android.text.Html;

import com.xiaoma.utils.StringUtil;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.adapter.IGalleryData;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.sdk.bean.XMAlbum;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
public class AlbumBean extends XMAlbum implements Serializable, IGalleryData {

    private AlbumBean(Album album) {
        super(album);
    }

    public static List<AlbumBean> convert2Album(List<XMAlbum> xmAlbums) {
        ArrayList<AlbumBean> albumBeans = new ArrayList<>(xmAlbums.size());
        for (XMAlbum xmAlbum : xmAlbums) {
            //移除付费
//            if (xmAlbum.isPaid()) {
//                continue;
//            }
            albumBeans.add(new AlbumBean(xmAlbum.getSDKBean()));
        }
        return albumBeans;
    }

    @Override
    public String getCoverUrl() {
        return getValidCover();
    }

    @Override
    public CharSequence getTitleText(Context context) {
        return getAlbumTitle();
    }

    @Override
    public CharSequence getFooterText(Context context) {
        return null;
    }

    @Override
    public CharSequence getBottomText(Context context) {
        return Html.fromHtml(context.getString(R.string.str_count_listener, "<font color=\"#fbd3a4\">" + StringUtil.convertBigDecimal(getPlayCount()) + "</font>"));
//        return SpanUtils.with(context)
//                .append(StringUtil.convertBigDecimal(getPlayCount()))
//                .setForegroundColorRes(R.color.gallery_hint_text_color)
//                .appendRes(R.string.channel_listener_num).setForegroundColorRes(R.color.gallery_text_color).create();
    }

    @Override
    public long getUUID() {
        return getId();
    }

    @Override
    public int getSourceType() {
        return PlayerSourceType.HIMALAYAN;
    }
}
