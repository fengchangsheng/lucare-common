package com.fcs.design.pattern.build;

/**
 * Created by fengcs on 2018/2/7.
 * 子接口里的Build继承父接口的Build
 */
public interface WebSubject extends Subject{

    public static class Builder extends Subject.Builder {


    }

}
