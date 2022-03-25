package com.xiaoma.music.online.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.music.R;
import com.xiaoma.music.common.manager.KwPlayInfoManager;
import com.xiaoma.music.common.model.UploadMusicModel;
import com.xiaoma.music.common.util.MusicUtils;
import com.xiaoma.music.common.util.Transformations;
import com.xiaoma.music.kuwo.model.XMBillboardInfo;
import com.xiaoma.music.online.model.BillboardBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.view.AutoScrollTextView;
import com.xiaoma.utils.GsonHelper;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author zs
 * @date 2018/10/10 0010.
 */
public class BillboardAdapter extends XMBaseAbstractBQAdapter<BillboardBean, BaseViewHolder> {

    private WeakReference<Fragment> fragment;

    public BillboardAdapter(Fragment fragment, @Nullable List<BillboardBean> data) {
        super(R.layout.item_recom_or_ranking, data);
        this.fragment = new WeakReference<>(fragment);
    }

    @Override
    protected void convert(BaseViewHolder helper, BillboardBean item) {
        AutoScrollTextView marqueeTextView = helper.getView(R.id.item_recommend_tv);
        marqueeTextView.setText(item.getTitleText());
        setMarquee(marqueeTextView, item);
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
    }

    private void setMarquee(AutoScrollTextView textView, BillboardBean item) {
        textView.stopMarquee();
        String id = getAlbumId(item);
        //正在播放且parentId相等时，才开始跑马灯
        boolean marqueeStart = KwPlayInfoManager.getInstance().isCurrentPlayInfo(id, KwPlayInfoManager.AlbumType.BILLBOARD);
        if (marqueeStart) {
            textView.startMarquee();
        } else {
            textView.stopMarquee();
        }
    }

    private String getAlbumId(BillboardBean item) {
        XMBillboardInfo xmBillboardInfo = item.getXmBillboardInfo();
        return xmBillboardInfo.getSDKBean().getId() + xmBillboardInfo.getSDKBean().getName();
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        XMBillboardInfo info = getData().get(position).getXmBillboardInfo();
        if (info == null || info.getSDKBean() == null) {
            return new ItemEvent(getData().get(position).getTitleText(), String.valueOf(position));
        } else {
            UploadMusicModel musicModel = new UploadMusicModel();
            musicModel.setId(info.getSDKBean().getId());
            musicModel.setMusicName(info.getSDKBean().getName());
            musicModel.setType("kuwo");
            return new ItemEvent("排行榜item", GsonHelper.toJson(musicModel));
        }
    }
}
