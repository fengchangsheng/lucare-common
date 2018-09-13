package com.fcs.design.pattern.proxy.cglib;

/**
 * Created by fengcs on 2018/2/7.
 */
public class Client {

    public static void main(String[] args) {
        InfoManager infoManager = InfoManagerFactory.getInstance();
        infoManager.update();

        InfoManager anthInfoManager = InfoManagerFactory.getAuthInstance(new AuthProxy("s"));
        anthInfoManager.update();
    }

}
