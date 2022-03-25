package com.xiaoma.launcher.service.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.List;

public class NewServiceBean {

    /**
     * id : 1
     * categoryName : CAR
     * name : 车辆
     * imgUrl : http://www.carbuyin.net/by2/filePath/3a21fd68-0212-4205-a3a0-47633492fcca.png
     * parentId : 0
     * createDate : 2019-07-09 17:16:12.0
     * modifyDate : 2019-07-09 17:16:14.0
     * channelId : AA1090
     * enableStatus : 1
     * items : [{"id":6,"name":"附近加油站","imgUrl":"http://www.carbuyin.net/by2/filePath/c7c9bb22-cb97-47a0-9879-ff83292f7fef.png","type":"NearbyGasStation","parentId":1,"createDate":"2019-07-09 17:26:36.0","modifyDate":"2019-07-09 17:26:38.0","channelId":"AA1090","enableStatus":1},{"id":7,"name":"附近停车场","imgUrl":"http://www.carbuyin.net/by2/filePath/e45da206-f899-4a0e-898a-208bcb50f532.png","type":"NearbyParkingLot","parentId":1,"createDate":"2019-07-09 17:26:40.0","modifyDate":"2019-07-09 17:26:43.0","channelId":"AA1090","enableStatus":1},{"id":8,"name":"保养期","imgUrl":"http://www.carbuyin.net/by2/filePath/f8318c69-c0a4-4f9a-a96f-11d252f5600f.png","type":"MaintenancePeriod","parentId":1,"createDate":"2019-07-09 17:26:47.0","modifyDate":"2019-07-09 17:26:45.0","channelId":"AA1090","enableStatus":1}]
     */

    private int id;
    private String categoryName;
    private String name;
    private String imgUrl;
    private int parentId;
    private String createDate;
    private String modifyDate;
    private String channelId;
    private int enableStatus;
    private List<ItemsBean> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(int enableStatus) {
        this.enableStatus = enableStatus;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "NewServiceBean{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", name='" + name + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", parentId=" + parentId +
                ", createDate='" + createDate + '\'' +
                ", modifyDate='" + modifyDate + '\'' +
                ", channelId='" + channelId + '\'' +
                ", enableStatus=" + enableStatus +
                ", items=" + items +
                '}';
    }

    public static class ItemsBean implements MultiItemEntity {
        /**
         * id : 6
         * categoryName : NearbyGasStation
         * name : 附近加油站
         * imgUrl : http://www.carbuyin.net/by2/filePath/c7c9bb22-cb97-47a0-9879-ff83292f7fef.png
         * type : NearbyGasStation
         * pageType : NearbyGasStationPage
         * parentId : 1
         * createDate : 2019-07-09 17:26:36.0
         * modifyDate : 2019-07-09 17:26:38.0
         * channelId : AA1090
         * enableStatus : 1
         */

        private int id;
        private String categoryName;
        private String name;
        private String imgUrl;
        private String type;
        private String pageType;
        private int parentId;
        private String createDate;
        private String modifyDate;
        private String channelId;
        private int enableStatus;

        private int itemType;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPageType() {
            return pageType;
        }

        public void setPageType(String pageType) {
            this.pageType = pageType;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(String modifyDate) {
            this.modifyDate = modifyDate;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public int getEnableStatus() {
            return enableStatus;
        }

        public void setEnableStatus(int enableStatus) {
            this.enableStatus = enableStatus;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        @Override
        public int getItemType() {
            return itemType;
        }

        @Override
        public String toString() {
            return "ItemsBean{" +
                    "id=" + id +
                    ", categoryName='" + categoryName + '\'' +
                    ", name='" + name + '\'' +
                    ", imgUrl='" + imgUrl + '\'' +
                    ", type='" + type + '\'' +
                    ", pageType='" + pageType + '\'' +
                    ", parentId=" + parentId +
                    ", createDate='" + createDate + '\'' +
                    ", modifyDate='" + modifyDate + '\'' +
                    ", channelId='" + channelId + '\'' +
                    ", enableStatus=" + enableStatus +
                    ", itemType=" + itemType +
                    '}';
        }
    }
}
