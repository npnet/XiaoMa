package com.xiaoma.launcher.favorites;

import android.text.TextUtils;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.mapbar.android.mapbarnavi.PoiBean;
import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;
import com.xiaoma.mapadapter.utils.MapUtil;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2019/4/11 0011
 * 地图收藏夹DB数据管理
 */

public class FavoritesDBManager {
    private static final FavoritesDBManager favoritesDBManager = new FavoritesDBManager();
    private String uid;
    private IDatabase userDBManager;

    public static FavoritesDBManager getInstance() {
        return favoritesDBManager;
    }

    public void init(String uid) {
        if (TextUtils.isEmpty(uid)) {
            KLog.e("FavoritesDBManager init uid is null...");
            return;
        }
        if (!TextUtils.isEmpty(this.uid)) {
            KLog.e("FavoritesDBManager init repeat...");
            return;
        }
        this.uid = uid;
        userDBManager = DBManager.getInstance().getUserDBManager(uid);
    }

    /**
     * 收藏点总数
     */
    public long getCollectionNumber(){
        long count=0;
        if(userDBManager==null){
            return count;
        }
        count=userDBManager.queryCount(PoiFavorite.class);
        return count;
    }

    /**
     * 没有标记为家或者公司的收藏点总数
     * @return
     */
    public long getNoMarkCollectionNumber(){
        long count=0;
        if(userDBManager==null){
            return count;
        }
        List<PoiFavorite> list=findAll();
        for(PoiFavorite p:list){
            if("home".equals(p.getShortcutsType())|| "company".equals(p.getShortcutsType())){
                count++;
            }
        }
        return list.size()-count;
    }

    /**
     *添加收藏点
     * @return 返回的是id（第几条数据）
     */
    public synchronized long addItem(PoiFavorite poiFavorite){
        if(userDBManager==null){
            return -1;
        }
        if(isFavoriteExist(poiFavorite)){
            return -1;
        }

        return userDBManager.save(poiFavorite);
    }

    /**
     * 获取收藏点
     */
    public PoiFavorite getPoiByIndex(int index){
        PoiFavorite ret=null;
        if(userDBManager==null){
            return ret;
        }
        if(index<0){
            return ret;
        }

        List<PoiFavorite> list=findAll();
        if(list.size()>index){
            ret=list.get(index);
        }
        return ret;
    }

    /**
     * 更新收藏点
     * @return 成功是1，失败是-1
     */
    public synchronized long updateCollectionByIndex(int index,PoiFavorite poiFavorite){
        long ret=-1;
        if(userDBManager==null){
            return ret;
        }
        if(index<0){
            return ret;
        }

        PoiFavorite data=getPoiByIndex(index);

        if(data==null){
            return ret;
        }

        data.setType(poiFavorite.getType());
        data.setPosX(poiFavorite.getPosX());
        data.setPosY(poiFavorite.getPosY());
        data.setDisplayPosX(poiFavorite.getDisplayPosX());
        data.setDisplayPosY(poiFavorite.getDisplayPosY());
        data.setPoiId(poiFavorite.getPoiId());
        data.setChildPoiNum(poiFavorite.childPoiNum);
        data.setName(poiFavorite.getName());
        data.setAddress(poiFavorite.getAddress());
        data.setPhoneNumber(poiFavorite.getPhoneNumber());
        data.setRegionName(poiFavorite.getRegionName());
        data.setTypeName(poiFavorite.getTypeName());
        data.setTagIconId(poiFavorite.getTagIconId());
        data.setTypeName(poiFavorite.getTypeName());
        data.setShortcutsType(poiFavorite.getShortcutsType());

        ret=userDBManager.update(data);
        return ret;
    }

    /**
     * 收藏点是否存在
     * @return 存在是true；不存在是false。
     */
    public boolean isFavoriteExist(PoiFavorite poiFavorite){
        boolean ret=false;
        if(userDBManager==null){
            return ret;
        }
        String keys[] = {"displayPosX","displayPosY","name","address"};
        String values[][] = {{poiFavorite.getDisplayPosX()},{poiFavorite.getDisplayPosY()},{poiFavorite.getName()},{poiFavorite.getAddress()}};
        List<PoiFavorite> list=userDBManager.queryByWhere(PoiFavorite.class,keys,values);
        return !list.isEmpty();
    }

    /**
     * 获得给出的收藏点信息是第几个收藏点
     * @return 数据库中的id值
     */
    public long findItem(PoiFavorite poiFavorite){
        long count=-1;
        if(userDBManager==null){
            return count;
        }
        String keys[] = {"displayPosX","displayPosY","name","address"};
        String values[][] = {{poiFavorite.getDisplayPosX()},{poiFavorite.getDisplayPosY()},{poiFavorite.getName()},{poiFavorite.getAddress()}};
        List<PoiFavorite> list=userDBManager.queryByWhere(PoiFavorite.class,keys,values);
        if(list.isEmpty()){
            return count;
        }

        List<PoiFavorite> allList=findAll();
        if(ListUtils.isEmpty(allList)){
            return count;
        }

        for (int i=0;i<allList.size();i++){
            if(allList.get(i).getId()==list.get(0).getId()){
                count=i;
                break;
            }
        }
        return count;
    }

    /**
     * 删除收藏点
     * @return 失败是-1；成功是1。
     */
    public long deleteItemByIndex(int index){
        long ret=-1;
        if(userDBManager==null){
            return ret;
        }
        if(index<0){
            return ret;
        }
        PoiFavorite poiFavorite=getPoiByIndex(index);
        if(poiFavorite==null){
            return ret;
        }
        ret=userDBManager.delete(poiFavorite);
        return ret;
    }

    /**
     * 删除所有的收藏点
     * @return 返回的是删除的数目
     */
    public long deleteAll(){
        long ret=-1;
        if(userDBManager==null){
            return ret;
        }
//        ret=userDBManager.delete(PoiFavorite.class);

        QueryBuilder<PoiFavorite> queryBuilder=new QueryBuilder(PoiFavorite.class);
        queryBuilder.where("shortcutsType" + "!=?", new String[]{"home"}).whereAnd("shortcutsType" + "!=?", new String[]{"company"});
        List<PoiFavorite> list=userDBManager.queryByWhere(PoiFavorite.class,queryBuilder);
        ret=userDBManager.delete(list);
        return ret;
    }

    /**
     * 获取所有收藏点
     */
    public List findAll(){
        if(userDBManager==null){
            return new ArrayList();
        }
        return userDBManager.queryAll(PoiFavorite.class);
    }

    /**
     * 设置家/公司
     * @return 失败是-1；成功是1。
     */
    public long setShortCutInfo( PoiFavorite poiFavorite){
        long ret=-1;
        if(userDBManager==null){
            return ret;
        }
        int count=(int) findItem(poiFavorite);
        if(count>-1){
            ret=updateCollectionByIndex(count,poiFavorite);
        }else{
            ret=addItem(poiFavorite);
        }
        if(ret>-1){
            ret=1;
        }
        return ret;
    }

    /**
     * 获取家/公司
     */
    public List getShortCutByIndex(String shortcutsType){
        if(userDBManager==null){
            return new ArrayList();
        }
        List<PoiFavorite> list=userDBManager.queryByWhere(PoiFavorite.class,"shortcutsType",shortcutsType);
        return list;
    }

    /**
     * 删除家/公司
     * @return 失败是-1；成功是1.
     */
    public long deleteItem(String shortcutsType){
        long ret=-1;
        if(userDBManager==null){
            return ret;
        }
        ret=userDBManager.delete(getShortCutByIndex(shortcutsType));
        return ret;
    }

    /**
     * 清除家和公司
     * @return 失败是-1；成功是1.
     */
    public long clearAll(){
        long ret1=-1;
        long ret2=-1;
        if(userDBManager==null){
            return -1;
        }
        ret1=userDBManager.delete(getShortCutByIndex("home"));
        ret2=userDBManager.delete(getShortCutByIndex("company"));
        return ((ret1+ret2)==-2?-1:1);
    }

    /**
     * 判断该位置是不是家或公司
     * @param poiBean
     * @return  家：home；公司：company；其他自定义：custom；未设置："null".
     */
    public String getShortCutsType(PoiBean poiBean){
//        String ret=poiBean.getTypeName();
        String ret="null";
        if(userDBManager==null){
            return ret;
        }
        QueryBuilder<PoiFavorite> queryBuilder=new QueryBuilder(PoiFavorite.class);
        queryBuilder.where("shortcutsType" + "=?", new String[]{"home"}).whereOr("shortcutsType" + "=?", new String[]{"company"});
        List<PoiFavorite> list=userDBManager.queryByWhere(PoiFavorite.class,queryBuilder);
        KLog.e("FavoritesDBManager","list="+list);
        for(PoiFavorite p:list){
            double longitude=Double.parseDouble(p.getDisplayPosX())/100000;
            double latitude=Double.parseDouble(p.getDisplayPosY())/100000;
            KLog.e("FavoritesDBManager","getShortCutsType longitude="+longitude+" , latitude="+latitude);
            double distance=MapUtil.getPointsDistance(poiBean.getLongitude(),poiBean.getLatitude(),longitude,latitude);
            KLog.e("FavoritesDBManager","distance="+distance);
            if(distance<=100){
                ret=p.getShortcutsType();
                break;
            }
        }
        return ret;
    }
}
