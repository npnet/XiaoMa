package com.xiaoma.shop.business.model;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/04/09
 * @Describe: .
 */

public class FlowBean {

    private String total;
    private String balance;
    private String usage;

    public FlowBean(String total, String balance, String usage) {
        this.total = total;
        this.balance = balance;
        this.usage = usage;
    }

    public FlowBean() {
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }
}
