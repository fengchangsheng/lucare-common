package com.fcs.design.algorithm;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by fengcs on 2020/5/11.
 */
public class FixObject {

    private List<Integer> feeList = Lists.newArrayList();
    private Integer totalFee = 0;

    public List<Integer> getFeeList() {
        return feeList;
    }

    public void setFeeList(List<Integer> feeList) {
        this.feeList = feeList;
    }
}
