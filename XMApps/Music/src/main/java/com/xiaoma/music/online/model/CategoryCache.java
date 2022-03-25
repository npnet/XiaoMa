package com.xiaoma.music.online.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.xiaoma.music.common.model.BaseCacheInfo;

/**
 * Author: loren
 * Date: 2018/11/16 0016
 */
@Table("categoryCacheList")
public class CategoryCache extends BaseCacheInfo {

    private static final long serialVersionUID = 2L;

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private long id;

    @Override
    public BaseCacheInfo newInstance() {
        return new CategoryCache();
    }
}
