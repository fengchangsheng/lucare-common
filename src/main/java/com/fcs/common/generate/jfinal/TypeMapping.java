package com.fcs.common.generate.jfinal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucare.Feng on 2016/9/2.
 */
public class TypeMapping {

    protected Map<String, String> map = new HashMap<String, String>(){{
        // java.util.Data can not be returned
        // java.sql.Date, java.sql.Time, java.sql.Timestamp all extends java.util.Data so getDate can return the three types data
        // put("java.util.Date", "java.util.Date");

        // date, year
        put("java.sql.Date", "Date");

        // time
        put("java.sql.Time", "Date");

        // timestamp, datetime
        put("java.sql.Timestamp", "Date");

        // binary, varbinary, tinyblob, blob, mediumblob, longblob
        // qjd project: print_info.content varbinary(61800);
        put("[B", "byte[]");

        // ---------

        // varchar, char, enum, set, text, tinytext, mediumtext, longtext
        put("java.lang.String", "String");

        // int, integer, tinyint, smallint, mediumint
        put("java.lang.Integer", "int");

        // bigint
        put("java.lang.Long", "long");

        // real, double
        put("java.lang.Double", "double");

        // float
        put("java.lang.Float", "float");

        // bit
        put("java.lang.Boolean", "boolean");

        // decimal, numeric
        put("java.math.BigDecimal", "BigDecimal");

        // unsigned bigint
        put("java.math.BigInteger", "BigInteger");

    }};

    public String getType(String typeString) {
        return map.get(typeString);
    }

    public static void main(String[] args) {
        new TypeMapping().getType("java.math.BigInteger");
    }
}
