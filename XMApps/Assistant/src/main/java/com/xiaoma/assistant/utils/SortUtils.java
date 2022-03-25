package com.xiaoma.assistant.utils;

import android.content.Context;
import android.text.TextUtils;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.parser.HotelBean;
import com.xiaoma.assistant.model.parser.RestaurantInfo;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qiuboxiang on 2019/4/17 14:34
 * Desc:
 */
public class SortUtils {

    public static void sortHotelByScore(List<HotelBean> list, final boolean ascending) {
        Collections.sort(list, new Comparator<HotelBean>() {
            @Override
            public int compare(HotelBean lBean, HotelBean rBean) {
                double lScore = TextUtils.isEmpty(lBean.getScore()) ? 0 : Double.parseDouble(lBean.getScore());
                double rScore = TextUtils.isEmpty(rBean.getScore()) ? 0 : Double.parseDouble(rBean.getScore());
                lScore *= 1000;
                rScore *= 1000;
                return (int) (ascending ? lScore - rScore : rScore - lScore);
            }
        });
    }

    public static void sortRestaurantByScore(List<RestaurantInfo> list, final boolean ascending) {
        Collections.sort(list, new Comparator<RestaurantInfo>() {
            @Override
            public int compare(RestaurantInfo lBean, RestaurantInfo rBean) {
                double lScore = lBean.getAvgscore();
                double rScore = rBean.getAvgscore();
                lScore *= 1000;
                rScore *= 1000;
                return (int) (ascending ? lScore - rScore : rScore - lScore);
            }
        });
    }

    public static void sortHotelByDistance(List<HotelBean> list, final boolean ascending) {
        Collections.sort(list, new Comparator<HotelBean>() {
            @Override
            public int compare(HotelBean lBean, HotelBean rBean) {
                int lDistance = lBean.getDistance();
                int rDistance = rBean.getDistance();
                return ascending ? lDistance - rDistance : rDistance - lDistance;
            }
        });
    }

    public static void sortPoiResultByDistance(List<PoiBean> list, final boolean ascending) {
        Collections.sort(list, new Comparator<PoiBean>() {
            @Override
            public int compare(PoiBean lBean, PoiBean rBean) {
                int lDistance = (int) lBean.getDistance();
                int rDistance = (int) rBean.getDistance();
                return ascending ? lDistance - rDistance : rDistance - lDistance;
            }
        });
    }

    public static void sortRestaurantByDistance(List<RestaurantInfo> list, final boolean ascending) {
        Collections.sort(list, new Comparator<RestaurantInfo>() {
            @Override
            public int compare(RestaurantInfo lBean, RestaurantInfo rBean) {
                int lDistance = (int) lBean.getDistance();
                int rDistance = (int) rBean.getDistance();
                return ascending ? lDistance - rDistance : rDistance - lDistance;
            }
        });
    }


    public static void sortHotelByPrice(List<HotelBean> list, final boolean ascending) {
        Collections.sort(list, new Comparator<HotelBean>() {
            @Override
            public int compare(HotelBean lBean, HotelBean rBean) {
                double lPrice = TextUtils.isEmpty(lBean.getStartPrice()) ? 0 : Double.parseDouble(lBean.getStartPrice());
                double rPrice = TextUtils.isEmpty(rBean.getStartPrice()) ? 0 : Double.parseDouble(rBean.getStartPrice());
                lPrice *= 1000;
                rPrice *= 1000;
                return (int) (ascending ? lPrice - rPrice : rPrice - lPrice);
            }
        });
    }

    public static void sortRestaurantByPrice(List<RestaurantInfo> list, final boolean ascending) {
        Collections.sort(list, new Comparator<RestaurantInfo>() {
            @Override
            public int compare(RestaurantInfo lBean, RestaurantInfo rBean) {
                double lPrice = lBean.getAvgprice();
                double rPrice = rBean.getAvgprice();
                lPrice *= 1000;
                rPrice *= 1000;
                return (int) (ascending ? lPrice - rPrice : rPrice - lPrice);
            }
        });
    }

    public static void sortHotelByStarLevel(List<HotelBean> list, final boolean ascending, final Context context) {
        Collections.sort(list, new Comparator<HotelBean>() {
            @Override
            public int compare(HotelBean lBean, HotelBean rBean) {
                if (TextUtils.isEmpty(lBean.getStarName()) && TextUtils.isEmpty(rBean.getStarName())) {
                    return 0;
                }
                Map<String, Integer> starLevelMap = new HashMap<>();
                String[] array = context.getResources().getStringArray(R.array.star_level);
                for (int i = 0; i < array.length; i++) {
                    starLevelMap.put(array[i], i + 1);
                }
                int lStarLevel = getStarLevel(starLevelMap, lBean.getStarName());
                int rStarLevel = getStarLevel(starLevelMap, rBean.getStarName());
                return ascending ? lStarLevel - rStarLevel : rStarLevel - lStarLevel;
            }
        });
    }

    private static int getStarLevel(Map<String, Integer> starLevelMap, String starName) {
        if (!TextUtils.isEmpty(starName)) {
            return starLevelMap.containsKey(starName) ? starLevelMap.get(starName) : 0;
        } else {
            return 0;
        }
    }

}
