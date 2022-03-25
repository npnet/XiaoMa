package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.operation.model.ImageFile;
import com.kaolafm.opensdk.api.operation.model.category.Category;
import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/6
 */
public class XMCategory extends XMBean<Category> {

    public XMCategory(Category category) {
        super(category);
    }

    public String getCode() {
        return getSDKBean().getCode();
    }

    public String getType() {
        return getSDKBean().getType();
    }

    public String getName() {
        return getSDKBean().getName();
    }

    public String getDescription() {
        return getSDKBean().getDescription();
    }

    public int getContentType() {
        return getSDKBean().getContentType();
    }

    public List<XMCategory> getChildCategories() {
        List<Category> childCategories = getSDKBean().getChildCategories();
        if (childCategories == null || childCategories.isEmpty()) {
            return null;
        }
        List<XMCategory> xmCategoryList = new ArrayList<>(childCategories.size());
        for (Category childCategory : childCategories) {
            xmCategoryList.add(new XMCategory(childCategory));
        }
        return xmCategoryList;
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
