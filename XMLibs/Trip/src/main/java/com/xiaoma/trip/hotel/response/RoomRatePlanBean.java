package com.xiaoma.trip.hotel.response;

import java.util.List;

/**
 * Created by zhushi.
 * Date: 2018/12/7
 */
public class RoomRatePlanBean {

    /**
     * room : {"images":[{"imageId":"","imageName":"豪华大床房","imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201804/c53a9847-d8f4-423c-929a-013f49b644be.jpg"}],"allowAddBedNum":"0","roomId":"227179","intro":"","guide":"","roomName":"豪华大床房","roomTypeId":"009020","roomType":"大床房","bedTypeId":"001025","bedType":"200*200cm大床","acreage":"45","floor":"3-20","roomAcount":"10","maxAdult":"2","maxChild":"0","hasWindow":"2","allowAddBed":"0","allowSmoke":"0","hasNet":"2","isNetFee":"0","netFee":"0"}
     * ratePlan : {"description":"","ratePlanId":"1195","ratePlanName":"当天18:00前预订 不含早 ","cutOffDay":"0","cutOffTime":"18:00","breakfast":"011001","breakfastType":"","minRoomCount":"1","personCount":"0","minDay":"1","bedLimit":"045001","nationality":"013001","breakfastName":"不含早餐","bedLimitName":"不限","nationalityName":"不限"}
     * priceAndStatuList : [{"cancel":false,"date":"2019/01/09","count":"5","currency":"CNY","price":"925","rateId":"2071108","statu":"026001","statuName":"开启","lastCancelTime":"2019/1/9 13:03:43"},{"cancel":false,"date":"2019/01/10","count":"0","currency":"CNY","price":"875","rateId":"2071108","statu":"026002","statuName":"冻结","lastCancelTime":"2019/1/9 13:03:43"}]
     */

    private RoomBean room;
    private RatePlanBean ratePlan;
    private List<PriceAndStatuListBean> priceAndStatuList;

    private String totalPrice;

    private boolean hasRoom;

    private String roomMsg;

    public String getRoomMsg() {
        return roomMsg;
    }

    public void setRoomMsg(String roomMsg) {
        this.roomMsg = roomMsg;
    }

    //房间剩余总数量 默认很大值 方便比较最小值
    private int roomCount = 100000;

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public RoomBean getRoom() {
        return room;
    }

    public void setRoom(RoomBean room) {
        this.room = room;
    }

    public RatePlanBean getRatePlan() {
        return ratePlan;
    }

    public void setRatePlan(RatePlanBean ratePlan) {
        this.ratePlan = ratePlan;
    }

    public List<PriceAndStatuListBean> getPriceAndStatuList() {
        return priceAndStatuList;
    }

    public void setPriceAndStatuList(List<PriceAndStatuListBean> priceAndStatuList) {
        this.priceAndStatuList = priceAndStatuList;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isHasRoom() {
        return hasRoom;
    }

    public void setHasRoom(boolean hasRoom) {
        this.hasRoom = hasRoom;
    }

    public static class RoomBean {
        /**
         * images : [{"imageId":"","imageName":"豪华大床房","imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201804/c53a9847-d8f4-423c-929a-013f49b644be.jpg"}]
         * allowAddBedNum : 0
         * roomId : 227179
         * intro :
         * guide :
         * roomName : 豪华大床房
         * roomTypeId : 009020
         * roomType : 大床房
         * bedTypeId : 001025
         * bedType : 200*200cm大床
         * acreage : 45
         * floor : 3-20
         * roomAcount : 10
         * maxAdult : 2
         * maxChild : 0
         * hasWindow : 2
         * allowAddBed : 0
         * allowSmoke : 0
         * hasNet : 2
         * isNetFee : 0
         * netFee : 0
         */

        private String allowAddBedNum;
        private String roomId;
        private String intro;
        private String guide;
        private String roomName;
        private String roomTypeId;
        private String roomType;
        private String bedTypeId;
        private String bedType;
        private String acreage;
        private String floor;
        private String roomAcount;
        private String maxAdult;
        private String maxChild;
        private String hasWindow;
        private String allowAddBed;
        private String allowSmoke;
        private String hasNet;
        private String isNetFee;
        private String netFee;
        private List<ImagesBean> images;

        public String getAllowAddBedNum() {
            return allowAddBedNum;
        }

        public void setAllowAddBedNum(String allowAddBedNum) {
            this.allowAddBedNum = allowAddBedNum;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getGuide() {
            return guide;
        }

        public void setGuide(String guide) {
            this.guide = guide;
        }

        public String getRoomName() {
            return roomName;
        }

        public void setRoomName(String roomName) {
            this.roomName = roomName;
        }

        public String getRoomTypeId() {
            return roomTypeId;
        }

        public void setRoomTypeId(String roomTypeId) {
            this.roomTypeId = roomTypeId;
        }

        public String getRoomType() {
            return roomType;
        }

        public void setRoomType(String roomType) {
            this.roomType = roomType;
        }

        public String getBedTypeId() {
            return bedTypeId;
        }

        public void setBedTypeId(String bedTypeId) {
            this.bedTypeId = bedTypeId;
        }

        public String getBedType() {
            return bedType;
        }

        public void setBedType(String bedType) {
            this.bedType = bedType;
        }

        public String getAcreage() {
            return acreage;
        }

        public void setAcreage(String acreage) {
            this.acreage = acreage;
        }

        public String getFloor() {
            return floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }

        public String getRoomAcount() {
            return roomAcount;
        }

        public void setRoomAcount(String roomAcount) {
            this.roomAcount = roomAcount;
        }

        public String getMaxAdult() {
            return maxAdult;
        }

        public void setMaxAdult(String maxAdult) {
            this.maxAdult = maxAdult;
        }

        public String getMaxChild() {
            return maxChild;
        }

        public void setMaxChild(String maxChild) {
            this.maxChild = maxChild;
        }

        public String getHasWindow() {
            return hasWindow;
        }

        public void setHasWindow(String hasWindow) {
            this.hasWindow = hasWindow;
        }

        public String getAllowAddBed() {
            return allowAddBed;
        }

        public void setAllowAddBed(String allowAddBed) {
            this.allowAddBed = allowAddBed;
        }

        public String getAllowSmoke() {
            return allowSmoke;
        }

        public void setAllowSmoke(String allowSmoke) {
            this.allowSmoke = allowSmoke;
        }

        public String getHasNet() {
            return hasNet;
        }

        public void setHasNet(String hasNet) {
            this.hasNet = hasNet;
        }

        public String getIsNetFee() {
            return isNetFee;
        }

        public void setIsNetFee(String isNetFee) {
            this.isNetFee = isNetFee;
        }

        public String getNetFee() {
            return netFee;
        }

        public void setNetFee(String netFee) {
            this.netFee = netFee;
        }

        public List<ImagesBean> getImages() {
            return images;
        }

        public void setImages(List<ImagesBean> images) {
            this.images = images;
        }

        public static class ImagesBean {
            /**
             * imageId :
             * imageName : 豪华大床房
             * imageUrl : http://pic.cnbooking.net:10541/uploadorder/201804/c53a9847-d8f4-423c-929a-013f49b644be.jpg
             */

            private String imageId;
            private String imageName;
            private String imageUrl;

            public String getImageId() {
                return imageId;
            }

            public void setImageId(String imageId) {
                this.imageId = imageId;
            }

            public String getImageName() {
                return imageName;
            }

            public void setImageName(String imageName) {
                this.imageName = imageName;
            }

            public String getImageUrl() {
                return imageUrl;
            }

            public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
            }
        }
    }

    public static class RatePlanBean {
        /**
         * "status": "026001",
         * "confirm": true,
         * "confirmInfo": "",
         * "minPrice": "130",
         * "cancel": true,
         * "lastCancelTime": "2019/2/15 22:00:00",
         * description :
         * ratePlanId : 1195
         * ratePlanName : 当天18:00前预订 不含早
         * cutOffDay : 0
         * cutOffTime : 18:00
         * breakfast : 011001
         * breakfastType :
         * minRoomCount : 1
         * personCount : 0
         * minDay : 1
         * bedLimit : 045001
         * nationality : 013001
         * breakfastName : 不含早餐
         * bedLimitName : 不限
         * nationalityName : 不限
         */
        private String status;
        private boolean confirm;
        private String confirmInfo;
        private String minPrice;
        private boolean cancel;
        private String lastCancelTime;
        private String description;
        private String ratePlanId;
        private String ratePlanName;
        private String cutOffDay;
        private String cutOffTime;
        private String breakfast;
        private String breakfastType;
        private String minRoomCount;
        private String personCount;
        private String minDay;
        private String bedLimit;
        private String nationality;
        private String breakfastName;
        private String bedLimitName;
        private String nationalityName;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public boolean isConfirm() {
            return confirm;
        }

        public void setConfirm(boolean confirm) {
            this.confirm = confirm;
        }

        public String getConfirmInfo() {
            return confirmInfo;
        }

        public void setConfirmInfo(String confirmInfo) {
            this.confirmInfo = confirmInfo;
        }

        public String getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(String minPrice) {
            this.minPrice = minPrice;
        }

        public boolean isCancel() {
            return cancel;
        }

        public void setCancel(boolean cancel) {
            this.cancel = cancel;
        }

        public String getLastCancelTime() {
            return lastCancelTime;
        }

        public void setLastCancelTime(String lastCancelTime) {
            this.lastCancelTime = lastCancelTime;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getRatePlanId() {
            return ratePlanId;
        }

        public void setRatePlanId(String ratePlanId) {
            this.ratePlanId = ratePlanId;
        }

        public String getRatePlanName() {
            return ratePlanName;
        }

        public void setRatePlanName(String ratePlanName) {
            this.ratePlanName = ratePlanName;
        }

        public String getCutOffDay() {
            return cutOffDay;
        }

        public void setCutOffDay(String cutOffDay) {
            this.cutOffDay = cutOffDay;
        }

        public String getCutOffTime() {
            return cutOffTime;
        }

        public void setCutOffTime(String cutOffTime) {
            this.cutOffTime = cutOffTime;
        }

        public String getBreakfast() {
            return breakfast;
        }

        public void setBreakfast(String breakfast) {
            this.breakfast = breakfast;
        }

        public String getBreakfastType() {
            return breakfastType;
        }

        public void setBreakfastType(String breakfastType) {
            this.breakfastType = breakfastType;
        }

        public String getMinRoomCount() {
            return minRoomCount;
        }

        public void setMinRoomCount(String minRoomCount) {
            this.minRoomCount = minRoomCount;
        }

        public String getPersonCount() {
            return personCount;
        }

        public void setPersonCount(String personCount) {
            this.personCount = personCount;
        }

        public String getMinDay() {
            return minDay;
        }

        public void setMinDay(String minDay) {
            this.minDay = minDay;
        }

        public String getBedLimit() {
            return bedLimit;
        }

        public void setBedLimit(String bedLimit) {
            this.bedLimit = bedLimit;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getBreakfastName() {
            return breakfastName;
        }

        public void setBreakfastName(String breakfastName) {
            this.breakfastName = breakfastName;
        }

        public String getBedLimitName() {
            return bedLimitName;
        }

        public void setBedLimitName(String bedLimitName) {
            this.bedLimitName = bedLimitName;
        }

        public String getNationalityName() {
            return nationalityName;
        }

        public void setNationalityName(String nationalityName) {
            this.nationalityName = nationalityName;
        }
    }

    public static class PriceAndStatuListBean {
        /**
         * cancel : false
         * date : 2019/01/09
         * count : 5
         * currency : CNY
         * price : 925
         * rateId : 2071108
         * statu : 026001
         * statuName : 开启
         * lastCancelTime : 2019/1/9 13:03:43
         */

        private boolean cancel;
        private String date;
        private String count;
        private String currency;
        private String price;
        private String rateId;
        private String statu;
        private String statuName;
        private String lastCancelTime;

        public boolean isCancel() {
            return cancel;
        }

        public void setCancel(boolean cancel) {
            this.cancel = cancel;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getRateId() {
            return rateId;
        }

        public void setRateId(String rateId) {
            this.rateId = rateId;
        }

        public String getStatu() {
            return statu;
        }

        public void setStatu(String statu) {
            this.statu = statu;
        }

        public String getStatuName() {
            return statuName;
        }

        public void setStatuName(String statuName) {
            this.statuName = statuName;
        }

        public String getLastCancelTime() {
            return lastCancelTime;
        }

        public void setLastCancelTime(String lastCancelTime) {
            this.lastCancelTime = lastCancelTime;
        }
    }

}
