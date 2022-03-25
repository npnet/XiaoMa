package com.xiaoma.musicrec.manager;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.acrcloud.rec.ACRCloudClient;
import com.acrcloud.rec.ACRCloudConfig;
import com.acrcloud.rec.ACRCloudResult;
import com.acrcloud.rec.IACRCloudListener;
import com.xiaoma.musicrec.constant.Config;
import com.xiaoma.musicrec.model.AcrMusic;
import com.xiaoma.musicrec.model.Music;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/*
 *  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.musicrec.manager
 *  @文件名:   MusicRecManager
 *  @创建者:   Rookie
 *  @创建时间:  2018/12/4 16:55
 *  @描述：    TODO
 */
public class MusicRecManager {

    private static final String TAG = "MusicRecManager111";
    public static final String OUT_FILE_PATH = Environment.getExternalStorageDirectory() + "/msc";
    public static final String PCM_FILE_PATH = Environment.getExternalStorageDirectory() + "/msc/acrIat.pcm";
    private static MusicRecManager sMusicRecManager;

    private Context context;
    private FileOutputStream pcmOutput;
    private ACRCloudClient mClient;
    private boolean initState = false;
    private boolean mProcessing = false;
    private boolean isStop = false;


    private MusicRecManager() {
    }


    public static synchronized MusicRecManager getInstance() {
        if (sMusicRecManager == null) {
            sMusicRecManager = new MusicRecManager();
        }
        return sMusicRecManager;
    }

    public void init(Context context, IACRCloudListener listener) {

        this.context = context.getApplicationContext();
        File outFile = new File(OUT_FILE_PATH);
        if (!outFile.exists()) {
            outFile.mkdirs();
        }

        File file = new File(PCM_FILE_PATH);
        if (file.exists()) {
            file.delete();
            Log.d(TAG, "file delete");
        }
        try {
            file.createNewFile();
            Log.d(TAG, "create file");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "create file failed");
        }
        try {
            pcmOutput = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initMusicRec(listener);
    }

    private void initMusicRec(IACRCloudListener listener) {
        mClient = new ACRCloudClient();

        ACRCloudConfig config = new ACRCloudConfig();
        config.acrcloudListener = listener;

        config.context = this.context;
        config.host = Config.HOST;
        config.accessKey = Config.ACCESS_KEY;
        config.accessSecret = Config.ACCESS_SECRET;
        config.protocol = ACRCloudConfig.NetworkProtocol.HTTP;
        config.recorderConfig.rate = 16000;
        config.recorderConfig.reservedRecordBufferMS = 0;

        initState = mClient.initWithConfig(config);
    }

    public void handleRecognize() {
        if (!initState) {
            XMToast.showToast(context, "初始化失败");
            return;
        }
        isStop = false;
        if (!mProcessing) {
//            FunctionCtrlManager.getInstance().setSongsReco(FunctionCtrl.SongReco.ON);
//            KLog.d("setSongsReco on");
            mProcessing = !(this.mClient == null || !this.mClient.startRecognize());
            Log.d(TAG, "startRecognize");
        }
    }

    public void stopRecognize() {
        isStop = true;
        if (mProcessing && this.mClient != null) {
//            FunctionCtrlManager.getInstance().setSongsReco(FunctionCtrl.SongReco.OFF);
//            KLog.d("setSongsReco off");
            this.mClient.stopRecordToRecognize();
            this.mClient.stopPreRecord();
            this.mClient.cancel();
        }
        mProcessing = false;
//        releaseRecordToIvw();
    }

    public String handlerResult(ACRCloudResult acrCloudResult) {
        if (acrCloudResult == null) {
            if (this.mClient != null) {
                this.mClient.cancel();
                mProcessing = false;

            }
            return "无结果";
        }
        if (acrCloudResult.getRecordDataPCM() != null && acrCloudResult.getRecordDataPCM().length > 0) {
            try {
                pcmOutput.write(acrCloudResult.getRecordDataPCM());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    pcmOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        FunctionCtrlManager.getInstance().setSongsReco(FunctionCtrl.SongReco.OFF);
//        KLog.d("setSongsReco off");
        if (this.mClient != null) {
            //this.mClient.cancel();
            mProcessing = false;
        }
//        releaseRecordToIvw();
        AcrMusic acrMusic = GsonHelper.fromJson(acrCloudResult.getResult(), AcrMusic.class);
        if (acrMusic == null) {
            return "无结果";
        }
        stopRecognize();
        Log.d(TAG, "onResult   stopRecognize" + acrMusic.getStatus().getCode());
        if (acrMusic.getStatus().getCode() == 0) {

            List<Music> musics = acrMusic.getMetadata().getMusic();
//            MusicRecord record = new MusicRecord();
            //默认取第一条数据
            String musicName = musics.get(0).getTitle();
            String album = musics.get(0).getAlbum().getName();
            List<Music.ArtistsBean> artists = musics.get(0).getArtists();
            StringBuilder singer = new StringBuilder();
            for (int i = 0; i < artists.size(); i++) {
                singer.append(artists.get(i).getName());
                if (i < artists.size() - 1) {
                    singer.append("&");
                }
            }

//            record.setMusicName(musicName);
//            record.setSinger(singer.toString());
//            record.setAlbum(album);

            String singers = singer.toString();

            if (TextUtils.isEmpty(musicName) || TextUtils.isEmpty(singers)) {

                return "无结果";
            } else {
                return singers + "的" + musicName;
            }
        } else {
            if (!isStop) {

                return "无结果";
            }
        }
        KLog.json(acrCloudResult.getResult());
        return "无结果";
    }


    public Music handlerMusicResult(ACRCloudResult acrCloudResult) {
        if (acrCloudResult == null) {
            if (this.mClient != null) {
                //this.mClient.cancel();
                mProcessing = false;

            }
            return null;
        }
        if (acrCloudResult.getRecordDataPCM() != null && acrCloudResult.getRecordDataPCM().length > 0) {
            try {
                pcmOutput.write(acrCloudResult.getRecordDataPCM());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    pcmOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        FunctionCtrlManager.getInstance().setSongsReco(FunctionCtrl.SongReco.OFF);
//        KLog.d("setSongsReco off");
        if (this.mClient != null) {
            //this.mClient.cancel();
            mProcessing = false;
        }
//        releaseRecordToIvw();
        AcrMusic acrMusic = GsonHelper.fromJson(acrCloudResult.getResult(), AcrMusic.class);
        if (acrMusic == null) {
            return null;
        }
        stopRecognize();
        Log.d(TAG, "onResult   stopRecognize" + acrMusic.getStatus().getCode());
        if (acrMusic.getStatus().getCode() == 0) {

            List<Music> musics = acrMusic.getMetadata().getMusic();

            return musics.get(0);
        } else {
            if (!isStop) {

                return null;
            }
        }
        KLog.json(acrCloudResult.getResult());
        return null;
    }


    public void releaseMusicClient() {
        if (this.mClient != null) {
            this.mClient.release();
            this.initState = false;
            this.mClient = null;
        }
    }
}
