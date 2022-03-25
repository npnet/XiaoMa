package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.category.Category;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMCategory extends XMBean<Category> {

    public XMCategory(Category category) {
        super(category);
    }

    public long getId() {
        return getSDKBean().getId();
    }

    public void setId(long id) {
        getSDKBean().setId(id);
    }

    public String getKind() {
        return getSDKBean().getKind();
    }

    public void setKind(String kind) {
        getSDKBean().setKind(kind);
    }

    public String getCategoryName() {
        return getSDKBean().getCategoryName();
    }

    public void setCategoryName(String categoryName) {
        getSDKBean().setCategoryName(categoryName);
    }

    public String getCoverUrlSmall() {
        return getSDKBean().getCoverUrlSmall();
    }

    public void setCoverUrlSmall(String coverUrlSmall) {
        getSDKBean().setCoverUrlSmall(coverUrlSmall);
    }

    public String getCoverUrlMiddle() {
        return getSDKBean().getCoverUrlMiddle();
    }

    public void setCoverUrlMiddle(String coverUrlMiddle) {
        getSDKBean().setCoverUrlMiddle(coverUrlMiddle);
    }

    public String getCoverUrlLarge() {
        return getSDKBean().getCoverUrlLarge();
    }

    public void setCoverUrlLarge(String coverUrlLarge) {
        getSDKBean().setCoverUrlLarge(coverUrlLarge);
    }

    public String toString() {
        return getSDKBean().toString();
    }

    public boolean isNeedAddHighQualityTag() {
        return getSDKBean().isNeedAddHighQualityTag();
    }


}
