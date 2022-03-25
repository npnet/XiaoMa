package com.xiaoma.music.kuwo.listener;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/8/30
 *  description :
 * </pre>
 */
public interface OnAudioFetchListener<T> {

    void onFetchSuccess(T t);

    void onFetchFailed(String msg);
}
