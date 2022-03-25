package com.xiaoma.model.pratice;

import java.util.List;

/**
 * Created by ZSH on 2018/12/6 0006.
 */
public class ProvinceBean {

    private int id;
    private int code;
    private int parentCode;
    private int type;
    private String name;
    private String fullName;
    private String initial;
    private List<ChildrenRegion> childrenRegions;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getParentCode() {
        return parentCode;
    }

    public void setParentCode(int parentCode) {
        this.parentCode = parentCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInitial() {
        return initial;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public List<ChildrenRegion> getChildrenRegions() {
        return childrenRegions;
    }

    public void setChildrenRegions(List<ChildrenRegion> childrenRegions) {
        this.childrenRegions = childrenRegions;
    }

    @Override
    public String toString() {
        return "ProvinceBean{" +
                "id=" + id +
                ", code=" + code +
                ", parentCode=" + parentCode +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", fullName='" + fullName + '\'' +
                ", initial='" + initial + '\'' +
                ", childrenRegions=" + childrenRegions +
                '}';
    }

    public static class ChildrenRegion {
        private int id;
        private int code;
        private int parentCode;
        private int type;
        private String name;
        private String fullName;
        private String initial;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getParentCode() {
            return parentCode;
        }

        public void setParentCode(int parentCode) {
            this.parentCode = parentCode;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getInitial() {
            return initial;
        }

        public void setInitial(String initial) {
            this.initial = initial;
        }

        @Override
        public String toString() {
            return "ChildrenRegion{" +
                    "id=" + id +
                    ", code=" + code +
                    ", parentCode=" + parentCode +
                    ", type=" + type +
                    ", name='" + name + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", initial='" + initial + '\'' +
                    '}';
        }
    }
}
