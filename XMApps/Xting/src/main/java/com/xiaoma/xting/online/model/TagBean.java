package com.xiaoma.xting.online.model;

import com.xiaoma.xting.sdk.bean.XMTag;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
public class TagBean extends XMTag implements Serializable, INamed {


    public TagBean(Tag tag) {
        super(tag);
    }

    public static List<TagBean> convert(List<XMTag> xmTags) {
        List<TagBean> childCategoryBeans = new ArrayList<>(xmTags.size());
        for (XMTag xmTag : xmTags) {
            childCategoryBeans.add(new TagBean(xmTag.getSDKBean()));
        }
        return childCategoryBeans;
    }

    @Override
    public String getName() {
        return getTagName();
    }
}
