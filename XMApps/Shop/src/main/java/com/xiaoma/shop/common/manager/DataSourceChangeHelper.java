package com.xiaoma.shop.common.manager;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/24
 * @Describe: 数据源变化时 的帮助类
 *             解决资源使用时，数据源变化，导致UI显示错误
 */

public class DataSourceChangeHelper {

    private String mJustUpdateFilePath;//刚刚更新文件路径
    private int mJustUpdateFilePosition = -1;//刚刚更新文件 position

    private int mTryPosition = -1;//试听文件 Position


    public String getJustUpdateFilePath() {
        return mJustUpdateFilePath;
    }

    public void setJustUpdateFilePath(String justUpdateFilePath) {
        mJustUpdateFilePath = justUpdateFilePath;
    }

    public int getJustUpdateFilePosition() {
        return mJustUpdateFilePosition;
    }

    public void setJustUpdateFilePosition(int justUpdateFilePosition) {
        mJustUpdateFilePosition = justUpdateFilePosition;
    }

    public int getTryPosition() {
        return mTryPosition;
    }

    public void setTryPosition(int tryPosition) {
        mTryPosition = tryPosition;
    }
}
