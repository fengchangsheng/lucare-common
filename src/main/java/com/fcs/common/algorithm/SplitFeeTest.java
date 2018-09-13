package com.fcs.common.algorithm;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * 动态规划  金额分组
 * 业务场景 -- 根据最大票面金额，账单自动划分到不同发票
 * Created by fengcs on 2018/9/11.
 */
public class SplitFeeTest {

    public static void main(String[] args) {
//        Integer[] feeArray = new Integer[]{10, 80, 50, 40, 30, 60, 20, 70};
        Integer[] feeArray = new Integer[]{10, 80, 15, 15, 30, 25, 25, 56};
        TreeSet<Integer> feeSet = Sets.newTreeSet(Arrays.asList(feeArray));
        // 一个而大容器   用来放桶
        List<NumObject> numObjectList = Lists.newArrayList();
        replayMethod(feeSet, numObjectList);
        for (NumObject object : numObjectList) {
            System.out.println("=======================================");
            System.out.println("total fee : " + object.getTotalFee());
            for (Integer fee : object.getFeeList()) {
                System.out.println("element: " + fee);
            }
        }
    }

    public static void replayMethod(TreeSet<Integer> feeSet, List<NumObject> numObjectList) {
        Integer[] a = new Integer[feeSet.size()];
        Integer[] newArray = feeSet.toArray(a);
        int len = newArray.length;
        NumObject numObject = new NumObject();
        numObjectList.add(numObject);
        for (int i = len - 1; i >= 0; i--) {
            int tempFee = newArray[i];
            if (numObject.compareAndAdd(tempFee)) {
                feeSet.remove(tempFee);
                numObject.getFeeList().add(tempFee);
            }
            if (i == 0 && !feeSet.isEmpty()) {
                replayMethod(feeSet, numObjectList);
            }
        }
    }
}
