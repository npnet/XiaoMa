package com.xiaoma.audio.listener;

import java.util.List;

public interface IMusicSearchListener<MUSIC_INFO> {

    void musicSearchFinished(boolean searchStatus, List<MUSIC_INFO> musicList);

}
