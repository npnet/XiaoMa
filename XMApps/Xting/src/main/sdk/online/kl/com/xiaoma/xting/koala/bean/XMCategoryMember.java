package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.operation.model.ImageFile;
import com.kaolafm.opensdk.api.operation.model.category.CategoryMember;
import com.xiaoma.adapter.base.XMBean;

import java.util.Map;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/6
 */
public class XMCategoryMember extends XMBean<CategoryMember> {


    public XMCategoryMember(CategoryMember categoryMember) {
        super(categoryMember);
    }

    public String getCode() {
        return getSDKBean().getCode();
    }

    public String getTitle() {
        return getSDKBean().getTitle();
    }

    public String getSubtitle() {
        return getSDKBean().getSubtitle();
    }

    public String getDescription() {
        return getSDKBean().getDescription();
    }

    public String getType() {
        return getSDKBean().getType();
    }

    public Map<String, ImageFile> getImageFiles() {
        return getSDKBean().getImageFiles();
    }

    public Map<String, String> getExtInfo() {
        return getSDKBean().getExtInfo();
    }

    @Override
    public String toString() {
        return getSDKBean().toString();
    }
}
