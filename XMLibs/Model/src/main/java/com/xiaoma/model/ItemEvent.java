package com.xiaoma.model;

import java.io.Serializable;

/**
 * Created by Thomas on 2018/12/6 0006
 * 收集业务场景下click事件信息
 */

public class ItemEvent implements Serializable {

    //按钮操作业务类型
    public String name;
    //对应业务类型后台id
    public String id;

    public ItemEvent() {

    }

    public ItemEvent(String name, String id) {
        this.name = name;
        this.id = id;
    }

}
