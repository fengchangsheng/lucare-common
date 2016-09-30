package com.fcs.common.generate.jfinal;

/**
 * Created by Lucare.Feng on 2016/9/2.
 */
public class ColumnMeta {

    public String name;
    public String javaType;
    public String attrName;

    public String type;
    public String isNullable;
    public String isPrimaryKey;
    public String defaultValue;
    public String remarks = "";  //字段备注

    public static String initcap(String str) {

        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }

        return new String(ch);
    }
}
