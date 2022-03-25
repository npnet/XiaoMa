package com.xiaoma.music.kuwo.handler;

import com.xiaoma.music.kuwo.listener.OnAudioFetchListener;
import com.xiaoma.music.kuwo.model.XMBaseOnlineSection;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/17 0017
 */
public interface IHandler {
    void handle(OnAudioFetchListener listener, List<XMBaseOnlineSection> list);
}
