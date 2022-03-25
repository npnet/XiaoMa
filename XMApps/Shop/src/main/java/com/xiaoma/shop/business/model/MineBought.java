package com.xiaoma.shop.business.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/4/18 0018 18:37
 *   desc:  已购列表bean
 * </pre>
 */
public class MineBought implements Parcelable {

    private List<SkinVersionsBean> skinVersions;
    private List<SkusBean> skus;
    private List<HoloListModel> holos;
    private List<VehicleSoundEntity.SoundEffectListBean> soundEffectList;
    private List<VehicleSoundEntity.SoundEffectListBean> instrumentList;



    private PageInfo pageInfo;

    public MineBought() {
    }

    protected MineBought(Parcel in) {
        skinVersions = in.createTypedArrayList(SkinVersionsBean.CREATOR);
        skus = in.createTypedArrayList(SkusBean.CREATOR);
    }

    public static final Creator<MineBought> CREATOR = new Creator<MineBought>() {
        @Override
        public MineBought createFromParcel(Parcel in) {
            return new MineBought(in);
        }

        @Override
        public MineBought[] newArray(int size) {
            return new MineBought[size];
        }
    };

    public List<SkinVersionsBean> getSkinVersions() {
        return skinVersions;
    }

    public void setSkinVersions(List<SkinVersionsBean> skinVersions) {
        this.skinVersions = skinVersions;
    }

    public List<VehicleSoundEntity.SoundEffectListBean> getInstrumentList() {
        return instrumentList;
    }

    public void setInstrumentList(List<VehicleSoundEntity.SoundEffectListBean> instrumentList) {
        this.instrumentList = instrumentList;
    }

    public List<SkusBean> getSkus() {
        return skus;
    }

    public void setSkus(List<SkusBean> skus) {
        this.skus = skus;
    }

    public List<HoloListModel> getHolos() {
        return holos;
    }

    public void setHolos(List<HoloListModel> holos) {
        this.holos = holos;
    }
    public List<VehicleSoundEntity.SoundEffectListBean> getVehicleSounds() {
        return soundEffectList;
    }

    public void setVehicleSounds(List<VehicleSoundEntity.SoundEffectListBean> vehicleSounds) {
        this.soundEffectList = vehicleSounds;
    }
    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(skinVersions);
        dest.writeTypedList(skus);
    }

    public static class PageInfo {
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
