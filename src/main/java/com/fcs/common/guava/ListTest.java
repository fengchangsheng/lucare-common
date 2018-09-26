package com.fcs.common.guava;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fengcs on 2017/9/8.
 */
public class ListTest {

    public static void main(String[] args) {
        arrayToList();
        operatorString();
    }

    public static void arrayToList(){
        //数组转集合  优于Arrays.asList();
        List<Integer> integers = Lists.newArrayList(1, 2, 3, 4, 5);
        integers.add(6);
        // asList方法创建定长集合  不可再变更
        List<Integer> simpleList = Arrays.asList(new Integer[]{1,2,3,4,5});
        simpleList.add(6);
    }

    public static void operatorString() {
        //集合中字符串拼接
        List<String> list = new ArrayList<String>();
        list.add("aa");
        list.add("bb");
        list.add("cc");
        String result = Joiner.on("-").join(list);
        System.out.println(result);
        //result为  aa-bb-cc
    }

}
