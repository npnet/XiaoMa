package com.xiaoma.launcher.favorites;


import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2019/4/10 0010
 * 收藏夹管理  四维地图间接性调用 切勿删除此类文件  不然运行时出现class文件找不到异常
 */

public class FavoritesManager {

    /**1
     * 没有标记为家或者公司的收藏点总数
     * @return
     *
     */
    public String getCollectionNumber() {
        KLog.e("getCollectionNumber() ");
        long count=FavoritesDBManager.getInstance().getNoMarkCollectionNumber();

        ToMapMessage message=new ToMapMessage(String.valueOf(count),null);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**2
     *添加收藏点
     * @param favorite
     * @return
     * {
     *             "type": 1,
     *             "posX": "100posX",
     *             "posY": "100posY",
     *             "displayPosX": "100displayPosX",
     *             "displayPosY": "100displayPosY",
     *             "poiId": 100,
     *             "childPoiNum": 100,
     *             "name": "100name",
     *             "address": "100address",
     *             "phoneNumber": "100phoneNumber",
     *             "regionName": "100regionName",
     *             "typeName": "100typeName",
     *             "tagIconId": 100,
     *             "tagName": "100tagName"
     *         }
     *
     */
    public String addItem(String favorite) {
        KLog.e("addItem() favorite="+favorite);
        PoiFavorite poiFavorite=GsonHelper.fromJson(favorite,PoiFavorite.class);
        long ret=FavoritesDBManager.getInstance().addItem(poiFavorite);

        if(ret>-1){
            ret=1;
        }

        ToMapMessage message=new ToMapMessage(String.valueOf(ret),null);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**3
     * 获取收藏点
     * @param index   0、1、2、3、4......
     */
    public String getPoiByIndex(int index) {
        KLog.e("getPoiByIndex() index="+index);
        PoiFavorite poiFavorite=FavoritesDBManager.getInstance().getPoiByIndex(index);

        List list=new ArrayList<PoiFavorite>();
        if(poiFavorite!=null){
            list.add(poiFavorite);
        }
        ToMapMessage message=new ToMapMessage("data",list);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**4
     * 更新收藏点
     * @param index     0、1、2、3、4......
     * @param favorite
     * {
     *             "type": 4,
     *             "posX": "400posX",
     *             "posY": "400posY",
     *             "displayPosX": "400displayPosX",
     *             "displayPosY": "400displayPosY",
     *             "poiId": 400,
     *             "childPoiNum": 400,
     *             "name": "400name",
     *             "address": "400address",
     *             "phoneNumber": "400phoneNumber",
     *             "regionName": "400regionName",
     *             "typeName": "400typeName",
     *             "tagIconId": 400,
     *             "tagName": "100tagName"
     *         }
     */
    public String updateCollectionByIndex(int index, String favorite) {
        KLog.e("updateCollectionByIndex() index="+index+" favorite="+favorite);
        PoiFavorite poiFavorite=GsonHelper.fromJson(favorite,PoiFavorite.class);

        long ret=FavoritesDBManager.getInstance().updateCollectionByIndex(index,poiFavorite);

        ToMapMessage message=new ToMapMessage(String.valueOf(ret),null);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**5
     * 收藏点是否存在
     * @param favorite
     * {
     *             "type": 4,
     *             "posX": "400posX",
     *             "posY": "400posY",
     *             "displayPosX": "400displayPosX",
     *             "displayPosY": "400displayPosY",
     *             "poiId": 400,
     *             "childPoiNum": 400,
     *             "name": "400name",
     *             "address": "400address",
     *             "phoneNumber": "400phoneNumber",
     *             "regionName": "400regionName",
     *             "typeName": "400typeName",
     *             "tagIconId": 400,
     *             "tagName": "100tagName"
     *         }
     */
    public String isFavoriteExist(String favorite) {
        KLog.e("isFavoriteExist() favorite="+favorite);
        PoiFavorite poiFavorite=GsonHelper.fromJson(favorite,PoiFavorite.class);

        boolean ret=FavoritesDBManager.getInstance().isFavoriteExist(poiFavorite);

        ToMapMessage message=new ToMapMessage(ret,null);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**6
     * 获得给出的收藏点信息是第几个收藏点
     * @param favorite
     * {
     *             "type": 4,
     *             "posX": "400posX",
     *             "posY": "400posY",
     *             "displayPosX": "400displayPosX",
     *             "displayPosY": "400displayPosY",
     *             "poiId": 400,
     *             "childPoiNum": 400,
     *             "name": "400name",
     *             "address": "400address",
     *             "phoneNumber": "400phoneNumber",
     *             "regionName": "400regionName",
     *             "typeName": "400typeName",
     *             "tagIconId": 400,
     *             "tagName": "100tagName"
     *         }
     */
    public String findItem(String favorite) {
        KLog.e("findItem() favorite="+favorite);
        PoiFavorite poiFavorite=GsonHelper.fromJson(favorite,PoiFavorite.class);

        long ret=FavoritesDBManager.getInstance().findItem(poiFavorite);

        ToMapMessage message=new ToMapMessage(String.valueOf(ret),null);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**7
     * 删除收藏点
     * @param index 0、1、2、3、4......
     */
    public String deleteItemByIndex(int index) {
        KLog.e("deleteItemByIndex() index="+index);
        long ret=FavoritesDBManager.getInstance().deleteItemByIndex(index);

        ToMapMessage message=new ToMapMessage(String.valueOf(ret),null);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**8
     *删除所有的收藏点
     * @return
     */
    public String deleteAll() {
        KLog.e("deleteAll() ");
        long ret=FavoritesDBManager.getInstance().deleteAll();

        ToMapMessage message=new ToMapMessage(String.valueOf(ret),null);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**9
     * 获取所有收藏点
     */
    public String findAll() {
        KLog.e("findAll() ");
        List<PoiFavorite> list=FavoritesDBManager.getInstance().findAll();

        ToMapMessage message=new ToMapMessage("data",list);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**10
     * 设置家/公司
     * @param shortcutsType     home、company、custom1、custom2、custom3......
     * @param favorite
     * {
     *             "type": 4,
     *             "posX": "400posX",
     *             "posY": "400posY",
     *             "displayPosX": "400displayPosX",
     *             "displayPosY": "400displayPosY",
     *             "poiId": 400,
     *             "childPoiNum": 400,
     *             "name": "400name",
     *             "address": "400address",
     *             "phoneNumber": "400phoneNumber",
     *             "regionName": "400regionName",
     *             "typeName": "400typeName",
     *             "tagIconId": 400,
     *             "tagName": "100tagName"
     *         }
     */
    public String setShortCutInfo(String shortcutsType, String favorite) {
        KLog.e("setShortCutInfo() shortcutsType="+shortcutsType+" favorite="+favorite);
        PoiFavorite poiFavorite=GsonHelper.fromJson(favorite,PoiFavorite.class);
        if(poiFavorite!=null){
            poiFavorite.setShortcutsType(shortcutsType);
        }

        long ret=FavoritesDBManager.getInstance().setShortCutInfo(poiFavorite);

        ToMapMessage message=new ToMapMessage(String.valueOf(ret),null);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**11
     * 获取家/公司
     * @param shortcutsType    home、company、custom1、custom2、custom3......
     */
    public String getShortCutByIndex(String shortcutsType) {
        KLog.e("getShortCutByIndex() shortcutsType="+shortcutsType);
        List<PoiFavorite> list=FavoritesDBManager.getInstance().getShortCutByIndex(shortcutsType);

        ToMapMessage message=new ToMapMessage("data",list);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**12
     * 删除家/公司
     * @param shortcutsType     home、company、custom1、custom2、custom3......
     * @return
     */
    public String deleteItem(String shortcutsType) {
        KLog.e("deleteItem() shortcutsType="+shortcutsType);
        long ret=FavoritesDBManager.getInstance().deleteItem(shortcutsType);

        ToMapMessage message=new ToMapMessage(String.valueOf(ret),null);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }

    /**13
     * 清除家和公司
     */
    public String clearAll() {
        KLog.e("clearAll() ");
        long ret=FavoritesDBManager.getInstance().clearAll();

        ToMapMessage message=new ToMapMessage(String.valueOf(ret),null);
        String result=GsonHelper.toJson(message);
        KLog.e("result="+result);
        return result;
    }
}
