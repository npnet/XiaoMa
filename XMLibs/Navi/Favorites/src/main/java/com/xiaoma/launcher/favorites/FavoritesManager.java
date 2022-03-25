package com.xiaoma.launcher.favorites;

/**
 * Created by Thomas on 2019/4/10 0010
 * 收藏夹管理, 接口空方法体编译使用，不需要打包class到aar包中，宿主apk中存在FavoritesManager.class实现类
 */

public class FavoritesManager {

    /**
     * 收藏点总数
     * @return
     */
    public String getCollectionNumber() {
        //网络
        return null;
    }

    /**
     *添加收藏点
     * @param favorite
     * @return
     */
    public String addItem(String favorite) {
        return null;
    }

    /**
     * 获取收藏点
     * @param index
     */
    public String getPoiByIndex(int index) {
        return null;
    }

    /**
     * 更新收藏点
     * @param index
     * @param favorite
     */
    public String updateCollectionByIndex(int index, String favorite) {
        return null;
    }

    /**
     * 收藏点是否存在
     * @param favorite
     */
    public String isFavoriteExist(String favorite) {
        return null;
    }

    /**
     * 获得给出的收藏点信息是第几个收藏点
     * @param favorite
     */
    public String findItem(String favorite) {
        return null;
    }

    /**
     * 删除收藏点
     * @param index
     */
    public String deleteItemByIndex(int index) {
        return null;
    }

    /**
     *删除所有的收藏点
     * @return
     */
    public String deleteAll() {
        return null;
    }

    /**
     * 获取所有收藏点
     */
    public String findAll() {
        return null;
    }

    /**
     * 设置家/公司
     * @param shortcutsType
     * @param favorite
     */
    public String setShortCutInfo(String shortcutsType, String favorite) {
        return null;
    }

    /**
     * 获取家/公司
     * @param shortcutsType
     */
    public String getShortCutByIndex(String shortcutsType) {
        return null;
    }

    /**
     * 删除家/公司
     * @param shortcutsType
     * @return
     */
    public String deleteItem(String shortcutsType) {
        return null;
    }

    /**
     * 清除家和公司
     */
    public String clearAll() {
        return null;
    }
}
