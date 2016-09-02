package com.fcs.common.generate;

import com.fcs.common.db.DBConstant;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import static com.fcs.common.generate.SqlHelper.initcap;
import static com.fcs.common.generate.SqlHelper.sqlType2JavaType;

/**
 * Created by Lucare.Feng on 2016/8/29.
 */
public abstract class AbstractGenerateEntity {

    private static Connection con = null;
    private String[] colnames; // 列名数组
    private String[] colTypes; //列名类型数组
    private int[] colSizes; //列名大小数组
    private boolean f_util = false; // 是否需要导入包java.util.*
    private boolean f_sql = false; // 是否需要导入包java.sql.*
    private String content = null;

    public void generate(String[] tablenames) {
        for (String tableName : tablenames) {
            generate(tableName);
        }
    }

    final void generate(String tablename) {

        initCols(tablename);

        parse(tablename);

        writeData(tablename);

    }

    /**
     * 初始化列
     * @param tablename
     */
    private void initCols(String tablename) {
        String sql = "select * from " + tablename;
        PreparedStatement pStemt = null;
        try {
            Class.forName(DBConstant.DRIVER);
            con = DriverManager.getConnection(DBConstant.URL, DBConstant.NAME, DBConstant.PASS);
            pStemt = con.prepareStatement(sql);
            ResultSetMetaData rsmd = pStemt.getMetaData();
            int size = rsmd.getColumnCount();   //统计列
            colnames = new String[size];
            colTypes = new String[size];
            colSizes = new int[size];
            for (int i = 0; i < size; i++) {
                colnames[i] = rsmd.getColumnName(i + 1);
                colTypes[i] = rsmd.getColumnTypeName(i + 1);

                if (colTypes[i].equalsIgnoreCase("datetime")) {
                    f_util = true;
                }
                if (colTypes[i].equalsIgnoreCase("image") || colTypes[i].equalsIgnoreCase("text")) {
                    f_sql = true;
                }
                colSizes[i] = rsmd.getColumnDisplaySize(i + 1);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析表并构建数据
     * @param tablename
     */
    private void parse(String tablename) {
        StringBuffer sb = new StringBuffer();
        sb.append("package " + DBConstant.packageOutPath + ";\r\n");
        sb.append("\r\n");
        //判断是否导入工具包
        if (f_util) {
            sb.append("import java.util.Date;\r\n");
        }
        if (f_sql) {
            sb.append("import java.sql.*;\r\n");
        }
        //注释部分
        sb.append("\r\n/**\r\n");
        sb.append(" * " + tablename + " 实体类\r\n");
        sb.append(" */ \r\n");
        //实体部分
        sb.append("public class " + initcap(tablename) + "{\r\n");
        processAllAttrs(sb);//属性
        processAllMethod(sb);//get set方法
        sb.append("}\r\n");

        content = sb.toString();
    }

    /**
     * 属性字段
     * @param sb
     */
    private void processAllAttrs(StringBuffer sb) {
        for (int i = 0; i < colnames.length; i++) {
            addAnnotation(colnames[i]);
            sb.append("\tprivate " + sqlType2JavaType(colTypes[i]) + " " + colnames[i] + ";\r\n");
        }

    }

    /**
     * get/set方法
     * @param sb
     */
    private void processAllMethod(StringBuffer sb) {
        for (int i = 0; i < colnames.length; i++) {
            sb.append("\n\tpublic void set" + initcap(colnames[i]) + "(" + sqlType2JavaType(colTypes[i]) + " " +
                    colnames[i] + "){\r\n");
            sb.append("\t\tthis." + colnames[i] + "=" + colnames[i] + ";\r\n");
            sb.append("\t}\r\n");
            sb.append("\n\tpublic " + sqlType2JavaType(colTypes[i]) + " get" + initcap(colnames[i]) + "(){\r\n");
            sb.append("\t\treturn " + colnames[i] + ";\r\n");
            sb.append("\t}\r\n");
        }
    }

    /**
     * 写成java实体类
     * @param tableName
     */
    private void writeData(String tableName) {
        try {

            File directory = new File("D:/devData/common/train-share");
            StringBuilder outputPath = new StringBuilder();
            outputPath.append(directory.getAbsolutePath());
            if (DBConstant.isMavenProject) {
                outputPath.append("/src/main/java/");
            }
            outputPath.append(DBConstant.packageOutPath.replace(".", "/") + "/");
            outputPath.append(initcap(tableName) + ".java");
            FileWriter fw = new FileWriter(outputPath.toString());
            PrintWriter pw = new PrintWriter(fw);
            pw.println(content);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract void addAnnotation(String colName);

}
