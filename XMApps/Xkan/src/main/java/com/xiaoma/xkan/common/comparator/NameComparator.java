package com.xiaoma.xkan.common.comparator;

import com.xiaoma.utils.StringUtil;
import com.xiaoma.xkan.common.model.UsbMediaInfo;

import java.util.Comparator;

/*
 *  @项目名：  XMAgateOS
 *  @包名：    com.xiaoma.xkan.common.comparator
 *  @文件名:   NameComparator
 *  @创建者:   Rookie
 *  @创建时间:  2018/11/15 14:04
 *  @描述：    文件名称比较器
 */
public class NameComparator implements Comparator<UsbMediaInfo> {

    private boolean filterCon;


    public NameComparator(boolean filterCon) {
        this.filterCon = filterCon;
    }

    @Override
    public int compare(UsbMediaInfo o1, UsbMediaInfo o2) {
        if (o1.getFileType() != o2.getFileType()) {
            return o1.getFileType() - o2.getFileType() < 0 ? -1 : 1;
        } else {
//            String sortLetter1 = StringUtil.getSortLetter2(o1.getMediaName());
//            String sortLetter2 = StringUtil.getSortLetter2(o2.getMediaName());
           /* if ("@".equals(sortLetter1) || "#".equals(sortLetter2)) {
                return filterCon ? 1 : -1;
            } else if ("#".equals(sortLetter1) || "@".equals(sortLetter2)) {
                return filterCon ? -1 : 1;
            } else {*/
            return filterCon ? o2.getFirstLetter().compareToIgnoreCase(o1.getFirstLetter()) : o1.getFirstLetter().compareToIgnoreCase(o2.getFirstLetter());
//            }
        }
    }

}

