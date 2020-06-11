package com.fcs.design.algorithm.training;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 动态规划--爬台  f(n)=f(n-1)+f(n-2)
 * Created by fengcs on 2020/6/9.
 */
public class DynamicProgramming {

    public static void main(String[] args) {
        System.out.println(goStep(10));
        Map<Integer, Integer> resultMap = Maps.newHashMap();
        System.out.println(goStepFromMap(10, resultMap));
        System.out.println(goStepFromOne(10));
    }

    /**
     * 递归实现
     */
    private static int goStep(int n) {
        if (n < 1) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        return goStep(n - 1) + goStep(n - 2);
    }

    /**
     * 备忘录法
     */
    private static int goStepFromMap(int n, Map<Integer, Integer> resultMap) {
        if (n < 1) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        if (resultMap.get(n) != null) {
            return resultMap.get(n);
        } else {
            int result = goStepFromMap(n - 1, resultMap) + goStepFromMap(n - 2, resultMap);
            resultMap.put(n, result);
            return result;
        }
    }

    /**
     * 自底向上法
     */
    private static int goStepFromOne(int n) {
        if (n < 1) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        if (n == 2) {
            return 2;
        }
        int sum = 0;
        int sumTemp = 2;
        int temp = 1;
        for (int i = 3; i <= n; i++) {
            sum = sumTemp + temp;
            temp = sumTemp;
            sumTemp = sum;
        }
        return sum;
    }
}
