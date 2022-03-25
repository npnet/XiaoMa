package com.xiaoma.shop.business.model;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/24
 *     desc   :
 * </pre>
 */
public class AbsChildThemeBean {
    private String name;

    public AbsChildThemeBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
