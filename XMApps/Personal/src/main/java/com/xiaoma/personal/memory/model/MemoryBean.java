package com.xiaoma.personal.memory.model;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/11/20
 *     desc   :
 * </pre>
 */
public class MemoryBean {
    private String name;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public MemoryBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
