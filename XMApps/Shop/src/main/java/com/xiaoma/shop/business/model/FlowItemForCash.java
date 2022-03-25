package com.xiaoma.shop.business.model;

import com.xiaoma.shop.common.constant.ShopContract;

import java.util.List;

/**
 * Author: Ljb
 * Time  : 2019/7/19
 * Description:
 */
public class FlowItemForCash {

    /**
     * commoId : ff80808160da2a540160ddf649190199
     * commoNo : 198
     * catNo : 010001001
     * commoName : 1G/30天
     * commoNameEn : 1G/30D
     * commoTitle :
     * commoTitleEn :
     * expiryDate : 2030-01-31 23:59:59
     * unit : G
     * traffic : 1
     * serviceLife : 30
     * overageUnit : K
     * marketPrice : 0.01
     * commoPcPic : http://123.56.84.179/online-store/images/commodity/D0183ED85F5E708A4145B4A37C3AA861.jpg
     * commoWapPic : http://123.56.84.179/online-store/images/commodity/896190FB0D0EB290BFDC89E32406716C.jpg
     * description :
     * descriptionEn :
     * deleteFlag : 0
     * commoState : 2
     * createTime : 2018-01-10 10:46:33
     * operatorNo : 000001
     * jasperCommoName :
     * commoType : 0
     * chargesType :
     * serviceLifeUnit : 0
     * stock : 0
     * num : 1
     * exemptionClause :
     * isEnable : 0
     * distributionChannel :
     * carmNo :
     * carmName :
     * brandNo :
     * brandName :
     * lineNo :
     * lineName :
     * productNo : CUYQWIFI_1G_30D_50Y
     * productType : 1
     * implementationSystem :
     * commoCarlines : [{"commoLineId":"ff80808161ff92bf01620486950a03da","commoNo":"198","lineNo":"022"}]
     * carmUseSet : [{"commoLineId":"ff80808161ff92bf01620486950b03db","commoNo":"198","distributionChannel":""}]
     */

    private String commoId;
    private String commoNo;
    private String catNo;
    private String commoName;
    private String commoNameEn;
    private String commoTitle;
    private String commoTitleEn;
    private String expiryDate;
    private String unit;
    private int traffic;
    private int serviceLife;
    private String overageUnit;
    private float marketPrice;
    private String commoPcPic;
    private String commoWapPic;
    private String description;
    private String descriptionEn;
    private String deleteFlag;
    private String commoState;
    private String createTime;
    private String operatorNo;
    private String jasperCommoName;
    private String commoType;
    private String chargesType;
    private String serviceLifeUnit;
    private String stock;
    private String num;
    private String exemptionClause;
    private String isEnable;
    private String distributionChannel;
    private String carmNo;
    private String carmName;
    private String brandNo;
    private String brandName;
    private String lineNo;
    private String lineName;
    private String productNo;
    private String productType;
    private String implementationSystem;
    private List<CommoCarlinesBean> commoCarlines;
    private List<CarmUseSetBean> carmUseSet;


    private boolean isSelected;
    private @ShopContract.Pay int payType;

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getCommoId() {
        return commoId;
    }

    public void setCommoId(String commoId) {
        this.commoId = commoId;
    }

    public String getCommoNo() {
        return commoNo;
    }

    public void setCommoNo(String commoNo) {
        this.commoNo = commoNo;
    }

    public String getCatNo() {
        return catNo;
    }

    public void setCatNo(String catNo) {
        this.catNo = catNo;
    }

    public String getCommoName() {
        return commoName;
    }

    public void setCommoName(String commoName) {
        this.commoName = commoName;
    }

    public String getCommoNameEn() {
        return commoNameEn;
    }

    public void setCommoNameEn(String commoNameEn) {
        this.commoNameEn = commoNameEn;
    }

    public String getCommoTitle() {
        return commoTitle;
    }

    public void setCommoTitle(String commoTitle) {
        this.commoTitle = commoTitle;
    }

    public String getCommoTitleEn() {
        return commoTitleEn;
    }

    public void setCommoTitleEn(String commoTitleEn) {
        this.commoTitleEn = commoTitleEn;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getTraffic() {
        return traffic;
    }

    public void setTraffic(int traffic) {
        this.traffic = traffic;
    }

    public int getServiceLife() {
        return serviceLife;
    }

    public void setServiceLife(int serviceLife) {
        this.serviceLife = serviceLife;
    }

    public String getOverageUnit() {
        return overageUnit;
    }

    public void setOverageUnit(String overageUnit) {
        this.overageUnit = overageUnit;
    }

    public float getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(float marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getCommoPcPic() {
        return commoPcPic;
    }

    public void setCommoPcPic(String commoPcPic) {
        this.commoPcPic = commoPcPic;
    }

    public String getCommoWapPic() {
        return commoWapPic;
    }

    public void setCommoWapPic(String commoWapPic) {
        this.commoWapPic = commoWapPic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getCommoState() {
        return commoState;
    }

    public void setCommoState(String commoState) {
        this.commoState = commoState;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOperatorNo() {
        return operatorNo;
    }

    public void setOperatorNo(String operatorNo) {
        this.operatorNo = operatorNo;
    }

    public String getJasperCommoName() {
        return jasperCommoName;
    }

    public void setJasperCommoName(String jasperCommoName) {
        this.jasperCommoName = jasperCommoName;
    }

    public String getCommoType() {
        return commoType;
    }

    public void setCommoType(String commoType) {
        this.commoType = commoType;
    }

    public String getChargesType() {
        return chargesType;
    }

    public void setChargesType(String chargesType) {
        this.chargesType = chargesType;
    }

    public String getServiceLifeUnit() {
        return serviceLifeUnit;
    }

    public void setServiceLifeUnit(String serviceLifeUnit) {
        this.serviceLifeUnit = serviceLifeUnit;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getExemptionClause() {
        return exemptionClause;
    }

    public void setExemptionClause(String exemptionClause) {
        this.exemptionClause = exemptionClause;
    }

    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    public String getDistributionChannel() {
        return distributionChannel;
    }

    public void setDistributionChannel(String distributionChannel) {
        this.distributionChannel = distributionChannel;
    }

    public String getCarmNo() {
        return carmNo;
    }

    public void setCarmNo(String carmNo) {
        this.carmNo = carmNo;
    }

    public String getCarmName() {
        return carmName;
    }

    public void setCarmName(String carmName) {
        this.carmName = carmName;
    }

    public String getBrandNo() {
        return brandNo;
    }

    public void setBrandNo(String brandNo) {
        this.brandNo = brandNo;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getLineNo() {
        return lineNo;
    }

    public void setLineNo(String lineNo) {
        this.lineNo = lineNo;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getImplementationSystem() {
        return implementationSystem;
    }

    public void setImplementationSystem(String implementationSystem) {
        this.implementationSystem = implementationSystem;
    }

    public List<CommoCarlinesBean> getCommoCarlines() {
        return commoCarlines;
    }

    public void setCommoCarlines(List<CommoCarlinesBean> commoCarlines) {
        this.commoCarlines = commoCarlines;
    }

    public List<CarmUseSetBean> getCarmUseSet() {
        return carmUseSet;
    }

    public void setCarmUseSet(List<CarmUseSetBean> carmUseSet) {
        this.carmUseSet = carmUseSet;
    }

    public static class CommoCarlinesBean {
        /**
         * commoLineId : ff80808161ff92bf01620486950a03da
         * commoNo : 198
         * lineNo : 022
         */

        private String commoLineId;
        private String commoNo;
        private String lineNo;

        public String getCommoLineId() {
            return commoLineId;
        }

        public void setCommoLineId(String commoLineId) {
            this.commoLineId = commoLineId;
        }

        public String getCommoNo() {
            return commoNo;
        }

        public void setCommoNo(String commoNo) {
            this.commoNo = commoNo;
        }

        public String getLineNo() {
            return lineNo;
        }

        public void setLineNo(String lineNo) {
            this.lineNo = lineNo;
        }
    }

    public static class CarmUseSetBean {
        /**
         * commoLineId : ff80808161ff92bf01620486950b03db
         * commoNo : 198
         * distributionChannel :
         */

        private String commoLineId;
        private String commoNo;
        private String distributionChannel;

        public String getCommoLineId() {
            return commoLineId;
        }

        public void setCommoLineId(String commoLineId) {
            this.commoLineId = commoLineId;
        }

        public String getCommoNo() {
            return commoNo;
        }

        public void setCommoNo(String commoNo) {
            this.commoNo = commoNo;
        }

        public String getDistributionChannel() {
            return distributionChannel;
        }

        public void setDistributionChannel(String distributionChannel) {
            this.distributionChannel = distributionChannel;
        }
    }
}
