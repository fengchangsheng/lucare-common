package com.fcs.design.algorithm;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fengcs on 2020/5/27.
 */

public class FeeItem implements Comparable<FeeItem>{

    private Integer id;
    private Integer fee;

    private List<UseItem> useItemList = Lists.newArrayList();

    public FeeItem(Integer id, Integer fee) {
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

    public List<UseItem> getUseItemList() {
        return useItemList;
    }

    public void setUseItemList(List<UseItem> useItemList) {
        this.useItemList = useItemList;
    }

    @Override
    public int compareTo(FeeItem o) {
        int feeStatus = this.fee.compareTo(o.getFee());
        if (feeStatus == 0) {
            if (this.id != null) {
                feeStatus = this.id.compareTo(o.getId());
            }
        }
        return feeStatus;
    }
}
