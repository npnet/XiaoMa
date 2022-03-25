package com.xiaoma.player;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/1/24
 */
public interface ISearchBackListener<RESULT> {

    void onSearchBack(@AudioConstants.AudioResponseCode int code, RESULT result);
}
