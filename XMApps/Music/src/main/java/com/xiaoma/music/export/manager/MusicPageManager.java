package com.xiaoma.music.export.manager;

import android.util.SparseArray;

import com.xiaoma.player.AudioInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/3/1 0001
 */
public class MusicPageManager {
    public static final int DEFAULT_PAGE_SIZE = 30;
    private List<AudioInfo> mAudioInfoList = new ArrayList<>();
    private SparseArray<List<AudioInfo>> mPageMap = new SparseArray<>();
    private int mPageSize = DEFAULT_PAGE_SIZE;
    //从第0页开始
    private int mTotalPage;

    public static MusicPageManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final MusicPageManager instance = new MusicPageManager();
    }

    public SparseArray<List<AudioInfo>> getPageMap() {
        return mPageMap;
    }

    public List<AudioInfo> searchListByPage(int page) {
        if (page < 0 || page > mTotalPage - 1) {
            return new ArrayList<>();
        }
        return mPageMap.get(page);
    }

    public int getTotalPage() {
        return mTotalPage;
    }

    public int getCurrentPageByIndex(int index) {
        int i = index / mPageSize;
        if (i >= 0 && i <= mTotalPage - 1) {
            return i;
        }
        return -1;
    }

    public void paging(int pageSize, List<AudioInfo> ids) {
        mAudioInfoList.clear();
        mPageMap.clear();
        mPageSize = pageSize;
        mAudioInfoList = ids;
        int size = mAudioInfoList.size();
        int total = (size / mPageSize) + ((size % mPageSize > 0) ? 1 : 0);
        mTotalPage = total;
        for (int i = 0; i < total; i++) {
            int fromIndex = i * mPageSize;
            int toIndex = ((i == total - 1) ? size : ((i + 1) * mPageSize));
            List<AudioInfo> list = new ArrayList<>(mAudioInfoList);
            List<AudioInfo> currentPageList = list.subList(fromIndex, toIndex);
            mPageMap.put(i, new ArrayList<>(currentPageList));
        }
    }
}
