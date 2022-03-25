package com.xiaoma.xkan.common.comparator;

import com.xiaoma.xkan.common.model.UsbMediaInfo;

import java.util.Comparator;

/*
 *  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.xkan.common.comparator
 *  @文件名:   SizeComparator
 *  @创建者:   Rookie
 *  @创建时间:  2018/11/15 14:04
 *  @描述：    文件大小比较器
 */
public class SizeComparator implements Comparator<UsbMediaInfo> {
    //决定 是否需要反转集合
    private boolean filterCon;

    public SizeComparator(boolean filterCon) {
        this.filterCon = filterCon;
    }

    @Override
    public int compare(UsbMediaInfo o1, UsbMediaInfo o2) {
        if (o1.getFileType() != o2.getFileType()) {
            return o1.getFileType() - o2.getFileType() < 0 ? -1 : 1;
        } else {
            if (o1.getSize() == o2.getSize()) {
                return filterCon ? o2.getMediaName().compareTo(o1.getMediaName()) : o1.getMediaName().compareTo(o2.getMediaName());
            } else {
                return (filterCon ? -1 : 1) * (o1.getSize() - o2.getSize() > 0 ? 1 : -1);
            }
        }
    }

}
