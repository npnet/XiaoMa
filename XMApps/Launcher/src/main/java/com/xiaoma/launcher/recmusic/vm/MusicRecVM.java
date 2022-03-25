package com.xiaoma.launcher.recmusic.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.acrcloud.rec.ACRCloudResult;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.db.DBManager;
import com.xiaoma.launcher.common.manager.RequestManager;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.launcher.recmusic.model.MusicRecord;
import com.xiaoma.player.AudioInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ConvertUtils;

import java.util.List;

/*
 *  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.musicrec.vm
 *  @文件名:   MusicRecVM
 *  @创建者:   Rookie
 *  @创建时间:  2018/12/4 16:41
 *  @描述：    TODO
 */
public class MusicRecVM extends BaseViewModel {

    private MutableLiveData<ACRCloudResult> musicData;
    private MutableLiveData<List<MusicRecord>> musicRecordData;
    private MutableLiveData<XmResource<MusicRecord>> saveMusicData;

    public MusicRecVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ACRCloudResult> getMusicData() {
        if (musicData == null) {
            musicData = new MutableLiveData<>();
        }
        return musicData;
    }

    public MutableLiveData<List<MusicRecord>> getMusicRecordData() {
        if (musicRecordData == null) {
            musicRecordData = new MutableLiveData<>();
        }
        return musicRecordData;
    }

    public void setMusicData(ACRCloudResult acrCloudResult) {
        getMusicData().setValue(acrCloudResult);
    }

    public void fetchMusicRecordData() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                List<MusicRecord> musicRecords = null;
                try {
                    musicRecords = DBManager.getInstance().getDBManager().queryAll(MusicRecord.class);
                    for (MusicRecord item:musicRecords){
                        AudioInfo audioInfo = new AudioInfo();
                        audioInfo.setUniqueId(ConvertUtils.stringToLong(item.getSongId()));
                        item.setAudioInfo(audioInfo);
                    }
                    getMusicRecordData().setValue(musicRecords);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        musicData = null;
        musicRecordData = null;
    }


}
