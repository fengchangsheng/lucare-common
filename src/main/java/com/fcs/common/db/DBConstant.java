package com.fcs.common.db;

/**
 * Created by Lucare.Feng on 2016/8/8.
 */
public class DBConstant {
    //数据库连接
    public static final String URL = "jdbc:mysql://112.74.217.22:3306/train2";
    public static final String NAME = "root";
    public static final String PASS = "root";
    public static final String DRIVER = "com.mysql.jdbc.Driver";

    //指定实体生成所在包的路径
    public static String packageOutPath = "cn.pxzs.train.share.test";

    public static boolean isMavenProject = true;
}
