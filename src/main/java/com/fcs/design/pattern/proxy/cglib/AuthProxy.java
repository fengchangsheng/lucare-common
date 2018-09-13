package com.fcs.design.pattern.proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by fengcs on 2018/2/7.
 */
public class AuthProxy implements MethodInterceptor {

    private String name;

    public AuthProxy(String name) {
        this.name = name;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        if (!"lucare".equals(name)) {
            System.out.println("has no authority..to invoke the method: "+method.getName());
            return null;
        }
        return methodProxy.invokeSuper(o, objects);
    }
}
