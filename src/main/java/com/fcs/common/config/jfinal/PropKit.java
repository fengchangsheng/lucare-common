package com.fcs.common.config.jfinal;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Lucare.Feng on 2016/9/1.
 */
public class PropKit {

    private static Prop prop = null;
    private static final ConcurrentHashMap<String, Prop> map = new ConcurrentHashMap<>();


    public static Prop use(String fileName) {
        return use(fileName,"UTF-8");
    }

    public static Prop use(String fileName,String encoding){
        Prop result = map.get(fileName);
        if (result == null) {
            result = new Prop(fileName, encoding);
            map.put(fileName, result);
            if (prop == null) {
                prop = result;
            }

        }
        return result;
    }
}
