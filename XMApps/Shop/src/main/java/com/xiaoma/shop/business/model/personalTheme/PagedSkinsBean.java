package com.xiaoma.shop.business.model.personalTheme;

import com.xiaoma.shop.business.model.SkinVersionsBean;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/4/17
 */
public class PagedSkinsBean {

    /**
     * pageInfo : {"pageNum":0,"pageSize":5,"totalRecord":2,"totalPage":1}
     * skinVersions : [{"id":244,"createDate":1536844680000,"modifyDate":1537349629000,"versionName":"1.0","versionCode":"1","channelId":"AA1000","appName":"test111","versionDesc":"额外企鹅我去吃","packageName":"com.xiaoma.launcher","size":216255,"sizeFomat":"211 KB","md5String":"8d7eb0c4bb21c03b0f4e055749c90441","url":"http://www.carbuyin.net/by2/skin/launcher_3647e0df598843cab760f2d6acbaa193_1.0.skin","apkFilename":"launcher_3647e0df598843cab760f2d6acbaa193_1.0.skin","apkFilePath":"http://www.carbuyin.net/by2/skin/launcher_3647e0df598843cab760f2d6acbaa193_1.0.skin","isForceUpdate":false,"isAddUpdate":false,"isNeedUpdate":false,"bigImg1":"http://www.carbuyin.net/by2/skinImg/d668a138-d5a0-4402-ac45-79c06aabafce.png","bigImg2":"","bigImgUrl1":"http://www.carbuyin.net/by2/skinImg/d668a138-d5a0-4402-ac45-79c06aabafce.png","bigImgUrl2":"http://www.maicheyo.net:8181/","bigImgUrl3":"","isRecommend":"0","iconPath":"http://www.carbuyin.net/by2/skinImg/1dbc89a6-93c5-49f1-8f8d-443391eb40e3.png","iconPathUrl":"http://www.carbuyin.net/by2/skinImg/1dbc89a6-93c5-49f1-8f8d-443391eb40e3.png","price":11,"scorePrice":0,"isBuy":false,"tagPath":"","logoUrl":"http://www.carbuyin.net/by2/skinImg/1dbc89a6-93c5-49f1-8f8d-443391eb40e3.png","status":0,"discountPrice":1,"isDefault":false,"usedNum":0,"defaultShowNum":0,"showNum":0,"canTry":-1,"trialTime":0,"orderNumber":30,"likeNum":0,"isThumbs":0,"hotSkin":0,"used":false,"hot":false},{"id":75,"createDate":1520837392000,"modifyDate":1523443910000,"versionName":"1.0","versionCode":"1","channelId":"AA1000","appName":"默认皮肤","versionDesc":"规范","packageName":"com.xiaoma.launcher","size":216255,"sizeFomat":"211 KB","md5String":"8d7eb0c4bb21c03b0f4e055749c90441","url":"http://www.carbuyin.net/by2/skin/auncher_1.0.skin","apkFilename":"auncher_1.0.skin","apkFilePath":"http://www.carbuyin.net/by2/skin/auncher_1.0.skin","isForceUpdate":false,"isAddUpdate":false,"isNeedUpdate":false,"bigImg1":"http://www.carbuyin.net/by2/skinImg/bd658b4a-fc28-4820-8c6d-51be48d12bef.png","bigImg2":"http://www.carbuyin.net/by2/skinImg/818c42a2-099e-496c-9392-a8bcb04deb30.png","bigImgUrl1":"http://www.carbuyin.net/by2/skinImg/bd658b4a-fc28-4820-8c6d-51be48d12bef.png","bigImgUrl2":"http://www.carbuyin.net/by2/skinImg/818c42a2-099e-496c-9392-a8bcb04deb30.png","bigImgUrl3":"","updateContent":"","isRecommend":"0","iconPath":"http://www.carbuyin.net/by2/skinImg/0d4f560f-adae-4fb9-b059-83555992e5c0.png","iconPathUrl":"http://www.carbuyin.net/by2/skinImg/0d4f560f-adae-4fb9-b059-83555992e5c0.png","price":0,"scorePrice":0,"isBuy":false,"tagPath":"","logoUrl":"http://www.carbuyin.net/by2/skinImg/0d4f560f-adae-4fb9-b059-83555992e5c0.png","status":0,"isDefault":false,"usedNum":0,"defaultShowNum":200,"showNum":0,"canTry":0,"trialTime":5,"orderNumber":0,"likeNum":0,"isThumbs":0,"hotSkin":0,"used":false,"hot":false}]
     */

    private PageInfoBean pageInfo;
    private List<SkinVersionsBean> skinVersions;

    public PageInfoBean getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfoBean pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<SkinVersionsBean> getSkinVersions() {
        return skinVersions;
    }

    public void setSkinVersions(List<SkinVersionsBean> skinVersions) {
        this.skinVersions = skinVersions;
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


}
