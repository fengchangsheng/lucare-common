package com.fcs.design.algorithm.business;

import com.google.common.collect.Lists;
import com.google.common.collect.TreeMultiset;

import java.util.List;

/**
 * 定额发票分配策略
 * 可怜可怜我吧，还要写算法
 * Created by fengcs on 2020/5/11.
 */
public class DistributionTest {

    /**
     * 思路：
     * 1、把资源数据排序，把使用者排序
     * 2、当最大的资源大于最大的使用者，需要分出来多余的资源给第二大的使用者，如果还是用不完，继续分下去
     * 3、当最大的资源小于最大的使用者，先分配给最大的使用者全部资源，把剩下的需求切割下来作新的分配
     */
    public static void main(String[] args) {
        // 现有固定资源一批
        Integer[] feeArray = new Integer[]{50, 40, 10};
//        Integer[] feeArray = new Integer[]{50, 50};
        // 现有等资源的使用者一批
        Integer[] useArray = new Integer[]{30, 30, 50};
//        Integer[] useArray = new Integer[]{30, 30, 50};
        TreeMultiset<Integer> feeSet = TreeMultiset.create(Lists.newArrayList(feeArray));
        TreeMultiset<Integer> useSet = TreeMultiset.create(Lists.newArrayList(useArray));
        replay(feeSet, useSet);
    }

    private static void replay(TreeMultiset<Integer> feeSet, TreeMultiset<Integer> useSet) {
        Integer[] a = new Integer[feeSet.size()];
        Integer[] newFeeArray = feeSet.toArray(a);
        int alen = newFeeArray.length;

        Integer[] b = new Integer[useSet.size()];
        Integer[] newUseArray = useSet.toArray(b);
        int blen = newUseArray.length;
//        FixObject fixObject;
        for (int i = alen - 1; i >= 0; i--) {
//            fixObject = new FixObject();
//            fixObjectList.add(fixObject);
            for (int j = blen - 1; j >= 0; j--) {
                System.out.println("=============== i = " + i + "=========== j= " +j+"=================");

                // 发票大于明细  明细全使用  发票多余退回参与下次分配
                if (newFeeArray[i] > newUseArray[j]) {
                    feeSet.remove(newFeeArray[i]);
                    feeSet.add(newFeeArray[i] - newUseArray[j]);
                    useSet.remove(newUseArray[j]);
                    System.out.println(newUseArray[j] + " -- " + newUseArray[j]);
                } else if (newFeeArray[i] < newUseArray[j]) {
                    // 发票小于明细  发票全使用  明细多余退回参与下次分配
                    feeSet.remove(newFeeArray[i]);
                    useSet.remove(newUseArray[j]);
                    useSet.add(newUseArray[j] - newFeeArray[i]);
                    System.out.println(newFeeArray[i] + " -- " + newFeeArray[i]);
                } else {
                    feeSet.remove(newFeeArray[i]);
                    useSet.remove(newUseArray[j]);
                    System.out.println(newFeeArray[i] + " -- " + newUseArray[j]);
                }
                if (!feeSet.isEmpty()) {
                    replay(feeSet, useSet);
                    return;
                }
            }
        }

    }

}
