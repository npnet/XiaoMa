package com.xiaoma.xting.online.model;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;

import com.chad.library.adapter.base.entity.SectionEntity;
import com.xiaoma.xting.sdk.bean.XMCategory;
import com.xiaoma.xting.sdk.bean.XMCategoryList;
import com.xiaoma.xting.sdk.bean.XMRadioCategory;
import com.xiaoma.xting.sdk.bean.XMRadioCategoryList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KY
 * @date 2018/10/10
 */
public class CategoryBean extends SectionEntity implements Serializable, INamed {
    public static final int TYPE_ALBUM = 0;
    public static final int TYPE_RADIO = 1;
    private String name;
    private Long id;

    @IntDef({TYPE_ALBUM, TYPE_RADIO})
    @interface Type {
    }
    /**
     * 专辑(0)还是直播(1)
     */
    @Type
    private int type;

    public static List<CategoryBean> convertFromAlbum(XMCategoryList categoryList) {
        List<XMCategory> categories = categoryList.getCategories();
        ArrayList<CategoryBean> list = new ArrayList<>(categories.size());
        for (XMCategory category : categories) {
            list.add(new CategoryBean(category.getCategoryName(), category.getId(), TYPE_ALBUM));
        }
        return list;
    }

    public static List<CategoryBean> convertFromRadio(XMRadioCategoryList radioCategories) {
        List<XMRadioCategory> categories = radioCategories.getRadioCategories();
        ArrayList<CategoryBean> list = new ArrayList<>(categories.size());
        for (XMRadioCategory category : categories) {
            list.add(new CategoryBean(category.getRadioCategoryName(), category.getId(), TYPE_RADIO));
        }
        return list;
    }

    public CategoryBean(String name, Long id, @IntRange(from = 0, to = 1) int type) {
        super(false, null);
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public CategoryBean(boolean isHeader, String header) {
        super(isHeader, header);
    }

    @Override
    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    @Type
    public int getType() {
        return type;
    }
}
