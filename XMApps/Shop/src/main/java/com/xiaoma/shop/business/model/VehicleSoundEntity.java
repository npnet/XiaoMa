package com.xiaoma.shop.business.model;

import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.track.model.GoodsTrackInfo;
import com.xiaoma.utils.GsonHelper;

import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/04
 * @Describe:
 */

public class VehicleSoundEntity {


    /**
     * pageInfo : {"pageNum":0,"pageSize":5,"totalRecord":2,"totalPage":1}
     * soundEffectList : [{"id":1,"createDate":"123156454123","icon":"hhtp:/aaa.jpg","tagPath":"hhtp:/aaa.jpg","modifyDate":"123156454123","order_list":12,"price":12,"themeName":"音效1","category":"音效","channelId":"AA1090","status":1,"discountPrice":25,"scorePrice":12,"defaultStatus":1,"source":1,"isBuy":false,"isUsed":false,"size":2341231,"sizeFomat":"200 KB","downloadNum":12,"defaultShowNum":200,"filePath":"http://sjadjakowj.sdmkajs.sdasdkjoqwd.zip","discountScorePrice":2},{"id":2,"createDate":"123156454123","icon":"hhtp:/aaa.jpg","tagPath":"hhtp:/aaa.jpg","modifyDate":"123156454123","order_list":12,"price":12,"themeName":"音效2","category":"音效","channelId":"AA1090","status":1,"discountPrice":25,"scorePrice":12,"defaultStatus":1,"source":1,"isBuy":false,"isUsed":false,"size":2341231,"sizeFomat":"200 KB","downloadNum":12,"defaultShowNum":200,"filePath":"http://sjadjakowj.sdmkajs.sdasdkjoqwd.zip","discountScorePrice":2},{"id":3,"createDate":"123156454123","icon":"hhtp:/aaa.jpg","tagPath":"hhtp:/aaa.jpg","modifyDate":"123156454123","order_list":12,"price":12,"themeName":"音效3","category":"音效","channelId":"AA1090","status":1,"discountPrice":25,"scorePrice":12,"defaultStatus":1,"source":1,"isBuy":false,"isUsed":false,"size":2341231,"sizeFomat":"200 KB","downloadNum":12,"defaultShowNum":200,"filePath":"http://sjadjakowj.sdmkajs.sdasdkjoqwd.zip","discountScorePrice":2}]
     */

    private PageInfoBean pageInfo;
    private List<SoundEffectListBean> soundEffectList;
    private List<SoundEffectListBean> instrumentList;


    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<SoundEffectListBean> getSoundEffectList() {
        return soundEffectList;
    }

    public void setSoundEffectList(List<SoundEffectListBean> soundEffectList) {
        this.soundEffectList = soundEffectList;
    }

    public List<SoundEffectListBean> getInstrumentList() {
        return instrumentList;
    }

    public void setInstrumentList(List<SoundEffectListBean> instrumentList) {
        this.instrumentList = instrumentList;
    }

    public static class PageInfoBean {
        /**
         * pageNum : 0
         * pageSize : 5
         * totalRecord : 2
         * totalPage : 1
         */

        private int pageNum;
        private int pageSize;
        private int totalRecord;
        private int totalPage;

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalRecord() {
            return totalRecord;
        }

        public void setTotalRecord(int totalRecord) {
            this.totalRecord = totalRecord;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }
    }

    public static class SoundEffectListBean {
        /**
         * id : 1
         * createDate : 123156454123
         * icon : hhtp:/aaa.jpg
         * tagPath : hhtp:/aaa.jpg
         * modifyDate : 123156454123
         * order_list : 12
         * price : 12
         * themeName : 音效1
         * category : 音效
         * channelId : AA1090
         * status : 1
         * discountPrice : 25
         * scorePrice : 12
         * defaultStatus : 1
         * source : 1
         * isBuy : false
         * isUsed : false
         * size : 2341231
         * sizeFomat : 200 KB
         * downloadNum : 12
         * defaultShowNum : 200
         * filePath : http://sjadjakowj.sdmkajs.sdasdkjoqwd.zip
         * discountScorePrice : 2
         */
        public String toTrackString() {
            GoodsTrackInfo goodsTrackInfo = new GoodsTrackInfo();
            goodsTrackInfo.setId(id);
            goodsTrackInfo.setName(themeName);
            goodsTrackInfo.setRmbPrice(String.valueOf(price));
            goodsTrackInfo.setRmbPriceDiscount(String.valueOf(discountPrice));
            goodsTrackInfo.setCoinPrice(String.valueOf(scorePrice));
            goodsTrackInfo.setCoinPriceDiscount(String.valueOf(discountScorePrice));
            goodsTrackInfo.setCount(String.valueOf(defaultShowNum + downloadNum));
            goodsTrackInfo.setTag("");
            return GsonHelper.toJson(goodsTrackInfo);
        }

        private int id;
        private String createDate;
        private String icon;
        private String tagPath;
        private String modifyDate;
        private int order_list;
        private float price;
        private String themeName;
        private String category;
        private String channelId;
        private int status;
        private float discountPrice;
        private int scorePrice;
        private int defaultStatus;
        private int source;
        private boolean isBuy;
        private boolean isUsed;
        private int size;
        private String sizeFomat;
        private int downloadNum;
        private int defaultShowNum;
        private String filePath;
        private int discountScorePrice;
        private String auditionPath;
        private int auditionSize;
        private String auditionSizeFomat;
        /**********************************/
        @ShopContract.Pay
        private transient int pay;

        private boolean isPlay;


        public String getAuditionPath() {
            return auditionPath;
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getTagPath() {
            return tagPath;
        }

        public void setTagPath(String tagPath) {
            this.tagPath = tagPath;
        }

        public String getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(String modifyDate) {
            this.modifyDate = modifyDate;
        }

        public int getOrder_list() {
            return order_list;
        }

        public void setOrder_list(int order_list) {
            this.order_list = order_list;
        }

        public float getPrice() {
            return price;
        }

        public void setPrice(float price) {
            this.price = price;
        }

        public String getThemeName() {
            return themeName;
        }

        public void setThemeName(String themeName) {
            this.themeName = themeName;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public float getDiscountPrice() {
            return discountPrice;
        }

        public void setDiscountPrice(float discountPrice) {
            this.discountPrice = discountPrice;
        }

        public int getScorePrice() {
            return scorePrice;
        }

        public void setScorePrice(int scorePrice) {
            this.scorePrice = scorePrice;
        }

        public int getDefaultStatus() {
            return defaultStatus;
        }

        public void setDefaultStatus(int defaultStatus) {
            this.defaultStatus = defaultStatus;
        }

        public int getSource() {
            return source;
        }

        public void setSource(int source) {
            this.source = source;
        }

        public boolean isBuy() {
            return isBuy;
        }

        public void setBuy(boolean isBuy) {
            this.isBuy = isBuy;
        }

        public boolean isUsed() {
            return isUsed;
        }

        public void setUsed(boolean isUsed) {
            this.isUsed = isUsed;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public String getSizeFomat() {
            return sizeFomat;
        }

        public void setSizeFomat(String sizeFomat) {
            this.sizeFomat = sizeFomat;
        }

        public int getDownloadNum() {
            return downloadNum;
        }

        public void setDownloadNum(int downloadNum) {
            this.downloadNum = downloadNum;
        }

        public int getDefaultShowNum() {
            return defaultShowNum;
        }

        public void setDefaultShowNum(int defaultShowNum) {
            this.defaultShowNum = defaultShowNum;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public int getDiscountScorePrice() {
            return discountScorePrice;
        }

        public void setDiscountScorePrice(int discountScorePrice) {
            this.discountScorePrice = discountScorePrice;
        }

        public int getPay() {
            return pay;
        }

        public void setPay(int pay) {
            this.pay = pay;
        }

        public boolean isPlay() {
            return isPlay;
        }

        public void setPlay(boolean play) {
            isPlay = play;
        }


        public void setAuditionPath(String auditionPath) {
            this.auditionPath = auditionPath;
        }

        public int getAuditionSize() {
            return auditionSize;
        }

        public void setAuditionSize(int auditionSize) {
            this.auditionSize = auditionSize;
        }

        public String getAuditionSizeFomat() {
            return auditionSizeFomat;
        }

        public void setAuditionSizeFomat(String auditionSizeFomat) {
            this.auditionSizeFomat = auditionSizeFomat;
        }
    }
}
