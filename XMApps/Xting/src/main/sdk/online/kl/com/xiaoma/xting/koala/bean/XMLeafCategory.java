package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.operation.model.category.CategoryMember;
import com.kaolafm.opensdk.api.operation.model.category.LeafCategory;
import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
public class XMLeafCategory extends XMBean<LeafCategory> {
    public XMLeafCategory(LeafCategory leafCategory) {
        super(leafCategory);
    }

    public List<XMCategoryMember> getCategoryMembers() {
        List<CategoryMember> categoryMembers = getSDKBean().getCategoryMembers();
        if (categoryMembers != null && !categoryMembers.isEmpty()) {
            List<XMCategoryMember> categoryMemberList = new ArrayList<>(categoryMembers.size());
            for (CategoryMember categoryMember : categoryMembers) {
                categoryMemberList.add(new XMCategoryMember(categoryMember));
            }
            return categoryMemberList;
        }
        return null;
    }

    @Override
    public String toString() {
        return getSDKBean().toString();
    }
}
