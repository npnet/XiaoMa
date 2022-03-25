package com.xiaoma.trip.category.response;

import java.util.List;

public class CategoryBean {


    /**
     * name : 美食
     * subcate : [{"id":2147483647,"name":"米其林星级餐厅","nameEn":"Michelin"},{"name":"食品保健","id":21413.0,"nameEn":"Food health"},{"name":"特色菜","id":21404.0,"nameEn":"Featured dishes"},{"name":"福建菜","id":401.0,"nameEn":"Fujian cuisine"},{"name":"日本菜","id":20059.0,"nameEn":"Japanese food"},{"name":"饮品店","id":21329.0,"nameEn":"Beverage"},{"name":"面包甜点","id":11.0,"nameEn":"bread&dessert"},{"name":"生日蛋糕","id":20097.0,"nameEn":"Birthday Cake"},{"name":"火锅","id":17.0,"nameEn":"Hot pot"},{"name":"自助餐","id":40.0,"nameEn":"Buffet"},{"name":"小吃快餐","id":36.0,"nameEn":"Snack"},{"name":"日韩料理","id":28.0,"nameEn":"Japan/korea cuisine"},{"name":"西餐","id":35.0,"nameEn":"Western food"},{"name":"生鲜蔬果","id":20638.0,"nameEn":"Fresh"},{"name":"聚餐宴请","id":395.0,"nameEn":"Dinner party"},{"name":"烧烤烤肉","id":54.0,"nameEn":"Roast"},{"name":"川湘菜","id":55.0,"nameEn":"Sichuan/Hunan cuisine"},{"name":"江浙菜","id":56.0,"nameEn":"Jiangsu/Zhejiang cuisine"},{"name":"小龙虾","id":528.0,"nameEn":"Cray"},{"name":"香锅烤鱼","id":20004.0,"nameEn":"Grilled fish"},{"name":"粤菜","id":57.0,"nameEn":"Guangdong cuisine"},{"name":"中式烧烤/烤串","id":400.0,"nameEn":"Barbecue"},{"name":"西北菜","id":58.0,"nameEn":"Northwest cuisine"},{"name":"京菜鲁菜","id":59.0,"nameEn":"Beijing/Shandong cuisine"},{"name":"东北菜","id":20003.0,"nameEn":"Northeast cuisine"},{"name":"云贵菜","id":60.0,"nameEn":"Yunnan/guizhou cuisine"},{"name":"东南亚菜","id":62.0,"nameEn":"Southeast Asian cuisine"},{"name":"江河湖海鲜","id":63.0,"nameEn":"Seafood"},{"name":"台湾/客家菜","id":227.0,"nameEn":"Taiwan/Hakka cuisine"},{"name":"汤/粥/炖菜","id":229.0,"nameEn":"soup/porridge"},{"name":"蒙餐","id":232.0,"nameEn":"Mongolian meal"},{"name":"新疆菜","id":233.0,"nameEn":"Xinjiang cuisine"},{"name":"其他美食","id":24.0,"nameEn":"Other delicacies"}]
     * id : 1
     */

    private String name;
    private int id;
    private List<SubcateBean> subcate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<SubcateBean> getSubcate() {
        return subcate;
    }

    public void setSubcate(List<SubcateBean> subcate) {
        this.subcate = subcate;
    }

    public static class SubcateBean {
        /**
         * name : 福建菜
         * id : 401
         */

        private String name;
        private String nameEn;
        private int id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
