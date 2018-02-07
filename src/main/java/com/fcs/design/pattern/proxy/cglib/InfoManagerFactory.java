package com.fcs.design.pattern.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;

/**
 * Created by fengcs on 2018/2/7.
 */
public class InfoManagerFactory {

    private static InfoManager infoManager = new InfoManager();

    public static InfoManager getInstance(){
        return infoManager;
    }

    public static InfoManager getAuthInstance(AuthProxy authProxy){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(InfoManager.class);
        enhancer.setCallback(authProxy);
        return (InfoManager) enhancer.create();
    }

}
