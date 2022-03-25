package com.xiaoma.hotfix.model;

import java.io.Serializable;


public class PatchResult implements Serializable {
    /**
     * id : 12
     * packageName : com.xiaoma.launcher
     * packageVersion : 1.0.8
     * patchVersion : 2
     * file : http://www.carbuyin.net/by2/app/com.xiaoma.launcher_1.0.8_patch_2.apk
     * md5 : 6ee4078998e63ef58ff9e9834bebc33d
     */

    private int id;
    private String packageName;
    private String packageVersion;
    private int patchVersion;
    private String file;
    private String md5;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public int getPatchVersion() {
        return patchVersion;
    }

    public void setPatchVersion(int patchVersion) {
        this.patchVersion = patchVersion;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
