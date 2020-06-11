package com.fcs.design.algorithm.business;

/**
 * Created by fengcs on 2020/5/27.
 */
public class UseItem implements Comparable<UseItem>{

    private Integer id;
    private Integer fee;

    public UseItem() {
    }

    public UseItem(Integer id, Integer fee) {
        this.id = id;
        this.fee = fee;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    @Override
    public int compareTo(UseItem o) {
        int feeStatus = this.fee.compareTo(o.getFee());
        if (feeStatus == 0) {
            if (this.id != null) {
                feeStatus = this.id.compareTo(o.getId());
            }
        }
        return feeStatus;
    }
}
