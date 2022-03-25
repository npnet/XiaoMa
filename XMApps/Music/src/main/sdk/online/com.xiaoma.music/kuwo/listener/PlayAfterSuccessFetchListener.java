package com.xiaoma.music.kuwo.listener;

//该listener是为了在数据fetchSuccess后进行播放才可使用该类，
public abstract class PlayAfterSuccessFetchListener<T> implements OnAudioFetchListener<T>{


    public boolean playAfterFetchSuccess(){
        return true;
    }
}
