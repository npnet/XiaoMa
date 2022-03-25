package com.xiaoma.music.online.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.music.R;
import com.xiaoma.music.common.adapter.IGalleryData;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.model.UploadMusicModel;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.kuwo.model.XMBaseQukuItem;
import com.xiaoma.music.kuwo.model.XMRadioInfo;
import com.xiaoma.music.online.model.RecommendModel;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.view.AutoScrollTextView;
import com.xiaoma.utils.GsonHelper;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.kuwo.base.bean.quku.AlbumInfo;
import cn.kuwo.base.bean.quku.BaseQukuItem;
import cn.kuwo.base.bean.quku.SongListInfo;

/**
 * @author zs
 * @date 2018/10/10 0010.
 */
public class RecommendAdapter<T extends IGalleryData> extends XMBaseAbstractBQAdapter<T, BaseViewHolder> {

    private WeakReference<Fragment> fragment;

    public RecommendAdapter(Fragment fragment, @Nullable List<T> data) {
        super(R.layout.item_recom_or_ranking, data);
        this.fragment = new WeakReference<>(fragment);
    }

    @Override
    protected void convert(BaseViewHolder helper, T item) {
        try {
            if (fragment.get() != null && !fragment.get().isDetached()) {
                ImageLoader.with(fragment.get())
                        .load(item.getCoverUrl())
                        .placeholder(R.drawable.iv_default_cover)
                        .transform(Transformations.getRoundedCorners())
                        .into((ImageView) helper.getView(R.id.item_recommend_iv));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AutoScrollTextView marqueeTextView = helper.getView(R.id.item_recommend_tv);
        marqueeTextView.setText(item.getTitleText());
        //默认暂停
        marqueeTextView.stopMarquee();
        switchMarquee(item, marqueeTextView);
    }

    private void switchMarquee(T item, AutoScrollTextView marqueeTextView) {
        boolean marqueeStart = KwPlayInfoManager.getInstance().isCurrentPlayInfo(getAlbumId(item), getModelType(item));
        if (marqueeStart) {
            marqueeTextView.startMarquee();
        } else {
            marqueeTextView.stopMarquee();
        }
    }

    private int getModelType(T item) {
        @KwPlayInfoManager.AlbumType int type = KwPlayInfoManager.AlbumType.NONE;
        XMBaseQukuItem info = ((RecommendModel) item).getBaseQukuInfo();
        if (info == null || info.getSDKBean() == null) {
            return type;
        }
        String qukuItemType = info.getQukuItemType();
        switch (qukuItemType) {
            case BaseQukuItem.TYPE_ALBUM:
                type = KwPlayInfoManager.AlbumType.ALBUM;
                break;
            case BaseQukuItem.TYPE_RADIO:
                type = KwPlayInfoManager.AlbumType.RADIO;
                break;
            case BaseQukuItem.TYPE_SONGLIST:
                type = KwPlayInfoManager.AlbumType.SONG_LIST;
                break;
        }
        return type;
    }

    private String getAlbumId(T item) {
        XMBaseQukuItem baseQukuInfo = ((RecommendModel) item).getBaseQukuInfo();
        String albumId = "";
        String qukuItemType = baseQukuInfo.getQukuItemType();
        switch (qukuItemType) {
            case BaseQukuItem.TYPE_ALBUM:
                BaseQukuItem sdkBean = baseQukuInfo.getSDKBean();
                AlbumInfo albumInfo = (AlbumInfo) sdkBean;
                albumId = albumInfo.getId() + albumInfo.getName();
                break;
            case BaseQukuItem.TYPE_RADIO:
                XMRadioInfo xmRadioInfo = XMBaseQukuItem.convertRadioInfo(baseQukuInfo);
                albumId = xmRadioInfo.getCid() + xmRadioInfo.getName();
                break;
            case BaseQukuItem.TYPE_SONGLIST:
                BaseQukuItem baseQukuItem = baseQukuInfo.getSDKBean();
                SongListInfo songListInfo = (SongListInfo) baseQukuItem;
                albumId = songListInfo.getId() + songListInfo.getName();
                break;
            default:
                break;
        }
        return String.valueOf(albumId);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        RecommendModel model = (RecommendModel) getData().get(position);
        XMBaseQukuItem info = model.getBaseQukuInfo();
        if (info == null) {
            return new ItemEvent(model.getTitleText(), String.valueOf(position));
        } else {
            UploadMusicModel musicModel = new UploadMusicModel();
            musicModel.setId(info.getSDKBean().getId());
            musicModel.setMusicName(info.getSDKBean().getInfo());
            musicModel.setType("kuwo");
            return new ItemEvent("个性推荐item", GsonHelper.toJson(musicModel));
        }
    }
}
