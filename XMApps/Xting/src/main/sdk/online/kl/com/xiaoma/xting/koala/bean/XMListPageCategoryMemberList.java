package com.xiaoma.xting.koala.bean;

import com.kaolafm.opensdk.api.BasePageResult;
import com.kaolafm.opensdk.api.operation.model.category.CategoryMember;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/2
 */
public class XMListPageCategoryMemberList extends XMPageResult<XMCategoryMember, CategoryMember> {

    public XMListPageCategoryMemberList(BasePageResult<List<CategoryMember>> listBasePageResult) {
        super(listBasePageResult);
    }

    @Override
    protected XMCategoryMember handleConverter(CategoryMember categoryMember) {
        return new XMCategoryMember(categoryMember);
    }
}
