package com.xiaoma.service.order.model;

import java.util.List;

/**
 * Created by ZSH on 2018/12/6 0006.
 */
public class ProvinceBean {

    /**
     * value : 110000
     * text : 北京市
     * children : [{"value":"110100","text":"北京市","vprofixer":"B"}]
     */

    private String value;
    private String text;
    private List<ChildrenBean> children;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ChildrenBean> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBean> children) {
        this.children = children;
    }

    public static class ChildrenBean {
        /**
         * value : 110100
         * text : 北京市
         * vprofixer : B
         */

        private String value;
        private String text;
        private String vprofixer;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getVprofixer() {
            return vprofixer;
        }

        public void setVprofixer(String vprofixer) {
            this.vprofixer = vprofixer;
        }
    }
}
