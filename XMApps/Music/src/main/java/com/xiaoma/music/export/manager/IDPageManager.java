package com.xiaoma.music.export.manager;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/2/26 0026
 */
public class IDPageManager {
    public static final int DEFAULT_PAGE_SIZE = 30;
    private List<Long> mIdList = new ArrayList<>();
    private SparseArray<List<Long>> mPageMap = new SparseArray<>();
    private int mPageSize = DEFAULT_PAGE_SIZE;
    //从第0页开始
    private int mTotalPage;

    public static IDPageManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final IDPageManager instance = new IDPageManager();
    }

    public SparseArray<List<Long>> getPageMap() {
        return mPageMap;
    }

    public List<Long> searchListByPage(int page) {
        if (page < 0 || page > mTotalPage - 1) {
            return new ArrayList<>();
        }
        return mPageMap.get(page);
    }

    public int getTotalPage() {
        return mTotalPage;
    }

    public void paging(int pageSize, List<Long> ids) {
        mIdList.clear();
        mPageMap.clear();
        mPageSize = pageSize;
        mIdList = ids;
        int size = mIdList.size();
        int total = (size / mPageSize) + ((size % mPageSize > 0) ? 1 : 0);
        mTotalPage = total;
        for (int i = 0; i < total; i++) {
            int fromIndex = i * mPageSize;
            int toIndex = ((i == total - 1) ? size : ((i + 1) * mPageSize));
            List<Long> list = new ArrayList<>(mIdList);
            List<Long> currentPageList = list.subList(fromIndex, toIndex);
            mPageMap.put(i, currentPageList);
        }
    }
}
