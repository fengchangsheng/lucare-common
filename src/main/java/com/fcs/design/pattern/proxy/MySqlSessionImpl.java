package com.fcs.design.pattern.proxy;

/**
 * Created by fengcs on 2018/2/6.
 */
public class MySqlSessionImpl implements MySqlSession {
    @Override
    public void selects() {
        System.out.println("i select something.");
    }
}
