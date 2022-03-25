package com.xiaoma.audio.processor;

import android.content.Context;
import com.xiaoma.audio.listener.IMusicHandle;

//音源具体操作控制中心
public class XMMusicCenter {

    private IMusicHandle qqMusicControl;
    private IMusicHandle kwMusicControl;
    private IMusicHandle xtingMusicControl;
    private IMusicHandle systemLocalMusicHandle;

    public void init(Context context) {
        kwMusicControl = new KWMusicCarHandle(context);
        kwMusicControl.init();
        qqMusicControl = new QQMusicCarHandle(context);
        qqMusicControl.init();
        xtingMusicControl = new XTingMusicHandle(context);
        xtingMusicControl.init();
        systemLocalMusicHandle = new SystemLocalMusicHandle(context);
        systemLocalMusicHandle.init();
    }

    public IMusicHandle getSystemLocalMusicHandle() {
        return systemLocalMusicHandle;
    }

    public IMusicHandle getKWMusicHandle() {
        return kwMusicControl;
    }

    public IMusicHandle getQQMusicHandle() {
        return qqMusicControl;
    }

    public IMusicHandle getXTingMusicHandle() {
        return xtingMusicControl;
    }

}
