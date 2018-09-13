package com.fcs.common.algorithm;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by fengcs on 2018/9/11.
 */
public class NumObject {

    private BigDecimal totalFee = new BigDecimal(0);
    private List<Integer> feeList = Lists.newArrayList();
    private BigDecimal fixFee = new BigDecimal(100);

    public boolean compareAndAdd(int fee) {
        BigDecimal sumFee = totalFee.add(new BigDecimal(fee));
        if (sumFee.compareTo(fixFee) > 0) {
            return false;
        } else {
            totalFee = sumFee;
            return true;
        }
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public List<Integer> getFeeList() {
        return feeList;
    }

    public void setFeeList(List<Integer> feeList) {
        this.feeList = feeList;
    }
}
