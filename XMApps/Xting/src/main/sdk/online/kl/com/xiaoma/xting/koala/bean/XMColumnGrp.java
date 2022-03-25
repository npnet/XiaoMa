package com.xiaoma.xting.koala.bean;

import android.util.ArrayMap;

import com.kaolafm.opensdk.api.operation.model.ImageFile;
import com.kaolafm.opensdk.api.operation.model.column.ColumnGrp;
import com.xiaoma.adapter.base.XMBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/6
 */
public class XMColumnGrp<T extends ColumnGrp> extends XMBean<T> {

    public XMColumnGrp(T t) {
        super(t);
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

    public Map<String, XMImageFile> getImageFiles() {
        Map<String, ImageFile> imageFiles = getSDKBean().getImageFiles();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            ArrayMap<String, XMImageFile> imageFileArray = new ArrayMap<>(imageFiles.size());
            Set<Map.Entry<String, ImageFile>> entries = imageFiles.entrySet();
            for (Map.Entry<String, ImageFile> entry : entries) {
                imageFileArray.put(entry.getKey(), new XMImageFile(entry.getValue()));
            }
            return imageFileArray;
        }

        return null;
    }

    public Map<String, String> getExtInfo() {
        return getSDKBean().getExtInfo();
    }

    public String getType() {
        return getSDKBean().getType();
    }

    public List<? extends XMColumnGrp> getChildColumns() {
        List<? extends ColumnGrp> childColumns = getSDKBean().getChildColumns();
        if (childColumns == null || childColumns.isEmpty()) {
            return null;
        }
        List<XMColumnGrp> list = new ArrayList<>(childColumns.size());
        for (ColumnGrp childColumn : childColumns) {
            list.add(new XMColumnGrp(childColumn));
        }
        return list;
    }

    @Override
    public String toString() {
        return getSDKBean().toString();
    }
}
