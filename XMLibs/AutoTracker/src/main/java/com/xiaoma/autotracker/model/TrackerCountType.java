package com.xiaoma.autotracker.model;

/**
 * 简介: 上报类型
 *
 * @author lingyan
 */
public enum TrackerCountType {
    MUSICTIME(50000000),
    MUSICCOLLECT(50100000),
    FMTIME(50200000),
    FMCOLLECT(50300000),
    JOINGROUP(50400000),
    ADDFRIEND(50500000),
    SENDMESSAGE(50600000),
    REDPACKETMESSAGE(50700000),
    BOOKMOVIE(50800000),
    BOOKHOTEL(50900000),
    JOINMOTORCADE(51000000),
    RECOMMANDMUSIC(51400000),
    BOOKRECOMMANDMOVIE(51500000),
    MUSICRECOGNIZE(51600000),
    CREATESCHEDULE(51700000),
    LOGINSMARTACCOUNT(51900000),
    FEEDPET(52000000),
    BUYROLE(52100000),
    BUYACCESSORIES(52200000),
    CHANGE_INFO_TASK(52400000),
    GUIDETASK(52500000),
    BUYFLOW(52700000),
    BUYHOLOGRAM(52600000),
    BUYVOICE(52800000),
    BUYTHEME(52900000),
    FACERECOGINIZE(53100000),
    PASSGUESSNAME(53200000),
    KEYBIND(53000000);



    private int type;

    TrackerCountType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
