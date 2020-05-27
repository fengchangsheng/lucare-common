package com.fcs.design.algorithm;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeMultiset;

import java.util.List;
import java.util.Map;

/**
 * 定额发票分配策略
 * 可怜可怜我吧，还要写算法
 * Created by fengcs on 2020/5/11.
 */
public class DistributionPlusTest {

    /**
     * 思路：
     * 1、把资源数据排序，把使用者排序
     * 2、当最大的资源大于最大的使用者，需要分出来多余的资源给第二大的使用者，如果还是用不完，继续分下去
     * 3、当最大的资源小于最大的使用者，先分配给最大的使用者全部资源，把剩下的需求切割下来作新的分配
     */
    public static void main(String[] args) {
        List<FeeItem> feeItems = Lists.newArrayList();
        feeItems.add(new FeeItem(1, 50));
        feeItems.add(new FeeItem(2, 40));
        feeItems.add(new FeeItem(3, 10));
        List<UseItem> useItems = Lists.newArrayList();
        useItems.add(new UseItem(101, 30));
        useItems.add(new UseItem(102, 30));
        useItems.add(new UseItem(103, 50));
        TreeMultiset<FeeItem> feeSet = TreeMultiset.create(feeItems);
        TreeMultiset<UseItem> useSet = TreeMultiset.create(useItems);
        Map<Integer, List<UseItem>> resultCache = Maps.newHashMap();
        replay(feeSet, useSet, resultCache);
        for (Map.Entry<Integer, List<UseItem>> integerListEntry : resultCache.entrySet()) {
            System.out.println(integerListEntry.getKey());
            for (UseItem useItem : integerListEntry.getValue()) {
                System.out.println(useItem.getId() + " -- " + useItem.getFee());
            }
        }
    }

    private static void replay(TreeMultiset<FeeItem> feeSet, TreeMultiset<UseItem> useSet, Map<Integer, List<UseItem>> resultCache) {
        FeeItem[] a = new FeeItem[feeSet.size()];
        FeeItem[] newFeeArray = feeSet.toArray(a);
        int alen = newFeeArray.length;

        UseItem[] b = new UseItem[useSet.size()];
        UseItem[] newUseArray = useSet.toArray(b);
        int blen = newUseArray.length;
        for (int i = alen - 1; i >= 0; i--) {
            for (int j = blen - 1; j >= 0; j--) {
                if (resultCache.get(newFeeArray[i].getId()) == null) {
                    List<UseItem> resultList = Lists.newArrayList();
                    resultList.add(newUseArray[j]);
                    resultCache.put(newFeeArray[i].getId(), resultList);
                }else {
                    resultCache.get(newFeeArray[i].getId()).add(newUseArray[j]);
                }
                // 发票大于明细  明细全使用  发票多余退回参与下次分配
                if (newFeeArray[i].getFee() > newUseArray[j].getFee()) {
                    newFeeArray[i].setFee(newFeeArray[i].getFee() - newUseArray[j].getFee());
                    useSet.remove(newUseArray[j]);
                } else if (newFeeArray[i].getFee() < newUseArray[j].getFee()) {
                    // 发票小于明细  发票全使用  明细多余退回参与下次分配
                    feeSet.remove(newFeeArray[i]);
                    newUseArray[j].setFee(newUseArray[j].getFee() - newFeeArray[i].getFee());
                } else {
                    feeSet.remove(newFeeArray[i]);
                    useSet.remove(newUseArray[j]);
                }

                if (!feeSet.isEmpty()) {
                    replay(feeSet, useSet, resultCache);
                    return;
                }
            }
        }

    }

}
