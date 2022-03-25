package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMTagList extends XMBean<TagList> {
    public XMTagList(TagList tagList) {
        super(tagList);
    }

    public List<XMTag> getTagList() {
        List<Tag> tags = getSDKBean().getTagList();
        List<XMTag> xmCategories = new ArrayList<>();
        if (tags != null && !tags.isEmpty()) {
            for (Tag tag : tags) {
                if (tag == null) {
                    continue;
                }
                xmCategories.add(new XMTag(tag));
            }
        }
        return xmCategories;
    }

    public void setTagList(List<XMTag> tagList) {
        if (tagList == null) {
            getSDKBean().setTagList(null);
            return;
        }
        List<Tag> tags = new ArrayList<>();
        for (XMTag xmTag : tagList) {
            tags.add(xmTag.getSDKBean());
        }
        getSDKBean().setTagList(tags);
    }

}
