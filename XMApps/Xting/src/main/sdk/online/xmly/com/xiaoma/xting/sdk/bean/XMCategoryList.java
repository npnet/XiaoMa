package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/10
 */
public class XMCategoryList extends XMBean<CategoryList> {

    public XMCategoryList(CategoryList categoryList) {
        super(categoryList);
    }

    public List<XMCategory> getCategories() {
        List<Category> categories = getSDKBean().getCategories();
        List<XMCategory> xmCategories = new ArrayList<>();
        if (categories != null && !categories.isEmpty()) {
            for (Category category : categories) {
                if (category == null) {
                    continue;
                }
                xmCategories.add(new XMCategory(category));
            }
        }
        return xmCategories;
    }

    public void setCategories(List<XMCategory> xmCategories) {
        if (xmCategories == null) {
            getSDKBean().setCategories(null);
            return;
        }
        List<Category> categories = new ArrayList<>();
        for (XMCategory xmCategory : xmCategories) {
            categories.add(xmCategory.getSDKBean());
        }
        getSDKBean().setCategories(categories);
    }

}
