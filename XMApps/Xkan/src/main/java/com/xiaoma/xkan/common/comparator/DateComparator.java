package com.xiaoma.xkan.common.comparator;

import com.xiaoma.xkan.common.model.UsbMediaInfo;

import java.util.Comparator;

/*
 *  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.xkan.common.comparator
 *  @文件名:   DateComparator
 *  @创建者:   Rookie
 *  @创建时间:  2018/11/15 14:04
 *  @描述：    文件日期比较器
 */
public class DateComparator implements Comparator<UsbMediaInfo> {

    private boolean filterCon;

    public DateComparator(boolean filterCon) {
        this.filterCon = filterCon;
    }

    @Override
    public int compare(UsbMediaInfo o1, UsbMediaInfo o2) {
        if (o1.getFileType() != o2.getFileType()) {
            return o1.getFileType() - o2.getFileType() < 0 ? -1 : 1;
        } else {
            if (o1.getDate() == o2.getDate()) {

                return filterCon ? o2.getMediaName().compareTo(o1.getMediaName()) : o1.getMediaName().compareTo(o2.getMediaName());
            } else {
                return (filterCon ? 1 : -1) * (o1.getDate() - o2.getDate() > 0 ? 1 : -1);
            }
        }
    }

}
