package com.xiaoma.songname;

import android.app.Application;

import com.xiaoma.network.XmHttp;
import com.xiaoma.songname.common.manager.AudioFocusManager;
import com.xiaoma.utils.log.KLog;

import skin.support.SkinCompatManager;

public class SongName extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KLog.init(this);
        XmHttp.getDefault().init(getApplicationContext());
        SkinCompatManager.init(this);
        AudioFocusManager.getInstance().init(this);
    }
}
