package com.xiaoma.launcher.mark.cluster.bean;

import java.util.Objects;

public class BitmapSave {
    int num;
    String path;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BitmapSave that = (BitmapSave) o;
        return num == that.num &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, path);
    }
}
