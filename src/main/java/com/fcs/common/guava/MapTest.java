package com.fcs.common.guava;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Created by fengcs on 2017/9/8.
 */
public class MapTest {

    public static void main(String[] args) {
        //Map<String,List<Integer>>
        Multimap<String,Integer> multiMap = ArrayListMultimap.create();
        multiMap.put("1",1);
        multiMap.put("1",11);
        multiMap.put("1",111);
        System.out.println(multiMap);
    }

}
