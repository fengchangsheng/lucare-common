package com.fcs.design.pattern.proxy.jdks;

import com.fcs.design.pattern.proxy.MySqlSession;
import com.fcs.design.pattern.proxy.MySqlSessionImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static java.lang.reflect.Proxy.newProxyInstance;

/**
 * Created by fengcs on 2018/2/6.
 */
public class DynamicProxy implements MySqlSession {

    private final MySqlSession sqlSessionProxy;

    public DynamicProxy() {
        this.sqlSessionProxy = (MySqlSession) newProxyInstance(
                MySqlSession.class.getClassLoader(),
                new Class[]{MySqlSession.class},
                new SqlSessionInterceptor());
    }

    public void selects(){
        sqlSessionProxy.selects();
    }

    public class SqlSessionInterceptor implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("hehheheh");
            Object result = method.invoke(new MySqlSessionImpl(), args);
            System.out.println("hahahahh");
            return result;
        }
    }

    public static void main(String[] args) {
        DynamicProxy dynamicProxy = new DynamicProxy();
        dynamicProxy.selects();
    }

}
