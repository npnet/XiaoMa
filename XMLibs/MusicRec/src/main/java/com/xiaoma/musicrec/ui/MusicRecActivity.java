package com.xiaoma.musicrec.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.acrcloud.rec.ACRCloudResult;
import com.acrcloud.rec.IACRCloudListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.musicrec.R;
import com.xiaoma.musicrec.manager.MusicRecManager;


/*  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.musicrec
 *  @文件名:   MusicRecActivity
 *  @创建者:   Rookie
 *  @创建时间:  2018/12/4 11:52
 *  @描述：    音乐识别  */

public class MusicRecActivity extends BaseActivity implements IACRCloudListener {


    private TextView tvResult;

    private static final String TAG="musicrec--";
    //private MusicRecVM musicRecVM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_rec);
        initView();
        initData();
    }

    private void initData() {
       /* musicRecVM = ViewModelProviders.of(this).get(MusicRecVM.class);
        musicRecVM.getMusicData().observe(this, new Observer<ACRCloudResult>() {
            @Override
            public void onChanged(@Nullable ACRCloudResult acrCloudResult) {
                tvResult.setText(MusicRecManager.getInstance().handlerResult(acrCloudResult));
            }
        });*/
        MusicRecManager.getInstance().init(this,this);
    }


    private void initView() {
        tvResult =  findViewById(R.id.tv_result);
    }

    /**
     * 开始识别
     * @param view
     */
    public void startRec(View view){
        MusicRecManager.getInstance().handleRecognize();
    }

    /**
     * 停止识别
     * @param view
     */
    public void stopRec(View view){
        MusicRecManager.getInstance().stopRecognize();
    }

    @Override
    public void onResult(ACRCloudResult acrCloudResult) {
        Log.d(TAG,"onResult callback");
      //  musicRecVM.setMusicData(acrCloudResult);

    }

    @Override
    public void onVolumeChanged(double v) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicRecManager.getInstance().releaseMusicClient();

    }
}
