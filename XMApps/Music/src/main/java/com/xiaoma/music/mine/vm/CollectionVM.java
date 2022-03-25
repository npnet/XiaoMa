package com.xiaoma.music.mine.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.XmResource;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.mine.model.LocalModel;
import com.xiaoma.music.player.model.CollectEventBean;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/11 0011
 */
public class CollectionVM extends AndroidViewModel {

    private static final int MAX_HISTORY_MUSIC = 100;
    private MutableLiveData<XmResource<List<LocalModel>>> collectorAlbum;

    public CollectionVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<LocalModel>>> getCollectionMusic() {
        if (collectorAlbum == null) {
            collectorAlbum = new MutableLiveData<>();
        }
        return collectorAlbum;
    }

    public void initCollectorData() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                getCollectionMusic().setValue(XmResource.<List<LocalModel>>loading());
            }
        });
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                final List<XMMusic> musicHistory = MusicDbManager.getInstance().queryCollectionMusic();
                if (ListUtils.isEmpty(musicHistory)) {
                    List<LocalModel> localModels = new ArrayList<>();
                    getCollectionMusic().postValue(XmResource.response(localModels));
                    return;
                }
                List<LocalModel> localModels = new ArrayList<>();
                for (XMMusic xmMusic : musicHistory) {
                    if (xmMusic == null) {
                        continue;
                    }
                    localModels.add(new LocalModel(xmMusic));
                }
                Collections.reverse(localModels);
                getCollectionMusic().postValue(XmResource.response(localModels));
            }
        });
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        collectorAlbum = null;
    }

    public void addMusicToCollection(CollectEventBean event) {
        if (event != null) {
            XMMusic music = event.getMusic();
            boolean favorite = event.isFavorite();
            XmResource<List<LocalModel>> localModels = getCollectionMusic().getValue();
            if (localModels != null && localModels.data != null) {
                final List<LocalModel> data = localModels.data;
                if (favorite) {
                    removeFavorite(music, data);
                } else {
                    addFavorite(music, data);
                }
                if (data.size() > MAX_HISTORY_MUSIC) {
                    getCollectionMusic().postValue(XmResource.response(data.subList(0, MAX_HISTORY_MUSIC)));
                } else {
                    getCollectionMusic().postValue(XmResource.response(data));
                }
            }
        }
    }

    private void addFavorite(XMMusic music, List<LocalModel> data) {
        boolean contain = false;
        for (LocalModel model : data) {
            if (model == null || model.getXmMusic() == null) {
                continue;
            }
            if (music.getRid() == model.getXmMusic().getRid()) {
                contain = true;
            }
        }
        if (!contain) {
            data.add(0, new LocalModel(music));
        }
    }

    private void removeFavorite(XMMusic music, List<LocalModel> data) {
        Iterator<LocalModel> iterator = data.iterator();
        while (iterator.hasNext()) {
            LocalModel model = iterator.next();
            if (model == null || model.getXmMusic() == null) {
                continue;
            }
            if (music.getRid() == model.getXmMusic().getRid()) {
                iterator.remove();
            }
        }
    }
}
