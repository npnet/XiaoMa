package com.xiaoma.music;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaoma.component.base.BaseActivity;

/**
 * @author taojin
 * @date 2019/3/11
 */
public class MusicActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        OnlineMusicFactory.getKWPlayer().init(getApplicationContext());
////        AudioShareManager.getInstance().init(getApplication());
//        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
//            @Override
//            public void run() {
//                AudioShareManager.getInstance().shareInitMusicInfo();
//            }
//        });
        finish();
    }
}
