package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;
import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.R;
import com.xiaoma.music.common.manager.RequestManager;
import com.xiaoma.music.export.client.AssistantClient;
import com.xiaoma.music.export.manager.IDPageManager;
import com.xiaoma.music.export.model.XMSimpleMusic;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.listener.OnMusicChargeListener;
import com.xiaoma.music.kuwo.listener.PlayAfterSuccessFetchListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.player.AbsAudioClient;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class ResultRequestHandler extends BaseRequestInterceptHandler {
    public static final int PAGE_SIZE = 20;
    private int mPage = 0;
    private List<XMMusic> mMusicList = new ArrayList<>();

    public ResultRequestHandler(Context mContext, ClientCallback clientCallback) {
        super(mContext, clientCallback);
    }

    @Override
    public void handler(AbsAudioClient client, Bundle data) {
        int categoryId = data.getInt(AudioConstants.BundleKey.SEARCH_CATEGORY_ID, -1);
        if (!NetworkUtils.isConnected(mContext)) {
            XMToast.showToast(mContext, mContext.getString(R.string.net_error));
            return;
        }
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                client.shareAudioDataSourceChanged(AudioConstants.OnlineInfoSource.LAUNCHER);
                RequestManager.getInstance().getLauncherMusicData(String.valueOf(categoryId), 0, new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        XMSimpleMusic result = GsonHelper.fromJson(response.body(), new TypeToken<XMSimpleMusic>() {
                        }.getType());
                        if (result == null || !result.isSuccess()) {
                            client.shareAudioList(AudioConstants.AudioResponseCode.ERROR, null, null, 0, clientCallback);
                            XMToast.toastException(mContext, mContext.getString(R.string.net_error));
                            return;
                        }
                        XMSimpleMusic.DataBean data = result.getData();
                        List<XMSimpleMusic.DataBean.ContentBean> content = data.getContent();
                        List<Long> ids = new ArrayList<>();
                        List<Long> playIdList = new ArrayList<>();
                        for (int i = 0; i < content.size(); i++) {
                            XMSimpleMusic.DataBean.ContentBean contentBean = content.get(i);
                            if (contentBean == null) {
                                continue;
                            }
                            if (i < PAGE_SIZE) {
                                playIdList.add((long) contentBean.getSongId());
                            } else {
                                ids.add((long) contentBean.getSongId());
                            }
                        }
                        mMusicList.clear();
                        IDPageManager.getInstance().paging(PAGE_SIZE, ids);
                        OnlineMusicFactory.getKWAudioFetch().fetchMusicByIds(playIdList, new PlayAfterSuccessFetchListener<List<XMMusic>>() {
                            @Override
                            public void onFetchSuccess(List<XMMusic> xmMusics) {
                                chargeMusic(xmMusics, client);
                            }

                            @Override
                            public void onFetchFailed(String msg) {
                                ThreadDispatcher.getDispatcher().remove(pageRequestRunnable);
                                ThreadDispatcher.getDispatcher().post(pageRequestRunnable);
                            }
                        });
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        client.shareAudioList(AudioConstants.AudioResponseCode.ERROR, null, null, 0, clientCallback);
                        XMToast.toastException(mContext, mContext.getString(R.string.net_error));
                    }
                });
            }
        });
    }

    private void chargeMusic(List<XMMusic> xmMusics, AbsAudioClient client) {
        OnlineMusicFactory.getKWPlayer().chargeMusics(xmMusics, new OnMusicChargeListener() {
            @Override
            public void onChargeSuccess(List<XMMusic> musics, List<Integer> chargeResults) {
                List<XMMusic> temp = new ArrayList<>();
                if (!ListUtils.isEmpty(chargeResults)) {
                    for (int i = 0; i < chargeResults.size(); i++) {
                        if (chargeResults.get(i) == IKuwoConstant.XMMusicChargeType.FREE) {
                            final XMMusic xmMusic = musics.get(i);
                            if (!XMMusic.isEmpty(xmMusic)) {
                                temp.add(xmMusic);
                            }
                        }
                    }
                }
                OnlineMusicFactory.getKWPlayer().play(temp, ThreadLocalRandom.current().nextInt(0, temp.size()));
                client.shareAudioList(AudioConstants.AudioResponseCode.SUCCESS, null, null, 0, clientCallback);
                ThreadDispatcher.getDispatcher().remove(pageRequestRunnable);
                ThreadDispatcher.getDispatcher().post(pageRequestRunnable);
            }

            @Override
            public void onChargeFailed(String msg) {
                OnlineMusicFactory.getKWPlayer().play(xmMusics, ThreadLocalRandom.current().nextInt(0, xmMusics.size()));
                client.shareAudioList(AudioConstants.AudioResponseCode.SUCCESS, null, null, 0, clientCallback);
                ThreadDispatcher.getDispatcher().remove(pageRequestRunnable);
                ThreadDispatcher.getDispatcher().post(pageRequestRunnable);
            }
        });
    }

    @Override
    public void searchInKw(AssistantClient client, int action, Bundle data) {

    }

    @Override
    public void searchInUsb(AssistantClient client, int action, Bundle data) {

    }

    private Runnable pageRequestRunnable = new Runnable() {
        @Override
        public void run() {
            int totalPage = IDPageManager.getInstance().getTotalPage();
            if (mPage < totalPage) {
                List<Long> ids = IDPageManager.getInstance().searchListByPage(mPage);
                Runnable pageRunnable = this;
                OnlineMusicFactory.getKWAudioFetch().fetchMusicByIds(ids, new OnAudioFetchListener<List<XMMusic>>() {
                    @Override
                    public void onFetchSuccess(List<XMMusic> xmMusics) {
                        mMusicList.addAll(xmMusics);
                        mPage++;
                        ThreadDispatcher.getDispatcher().remove(pageRunnable);
                        ThreadDispatcher.getDispatcher().post(pageRequestRunnable);
                    }

                    @Override
                    public void onFetchFailed(String msg) {
                        mPage++;
                        ThreadDispatcher.getDispatcher().remove(pageRunnable);
                        ThreadDispatcher.getDispatcher().post(pageRequestRunnable);
                    }
                });
            } else {
                mPage = 0;
                ThreadDispatcher.getDispatcher().remove(this);
                OnlineMusicFactory.getKWPlayer().chargeMusics(mMusicList, new OnMusicChargeListener() {
                    @Override
                    public void onChargeSuccess(List<XMMusic> musics, List<Integer> chargeResults) {
                        List<XMMusic> temp = new ArrayList<>();
                        if (!ListUtils.isEmpty(chargeResults)) {
                            for (int i = 0; i < chargeResults.size(); i++) {
                                if (chargeResults.get(i) == IKuwoConstant.XMMusicChargeType.FREE) {
                                    final XMMusic xmMusic = musics.get(i);
                                    if (!XMMusic.isEmpty(xmMusic)) {
                                        temp.add(xmMusic);
                                    }
                                }
                            }
                        }
                        OnlineMusicFactory.getMusicListControl().insertMusic("list.temporary", temp);
                    }

                    @Override
                    public void onChargeFailed(String msg) {
                        OnlineMusicFactory.getMusicListControl().insertMusic("list.temporary", mMusicList);
                    }
                });
            }
        }
    };
}
