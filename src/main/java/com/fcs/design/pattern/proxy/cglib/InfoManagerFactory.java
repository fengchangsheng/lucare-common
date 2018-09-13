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
        // 设置要代理的目标类
        enhancer.setSuperclass(InfoManager.class);
        // 设置要代理的拦截器
        enhancer.setCallback(authProxy);
        // 生成代理类的实例
        return (InfoManager) enhancer.create();
    }

}
