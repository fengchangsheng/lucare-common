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
        put("java.sql.Date", "java.util.Date");

        // time
        put("java.sql.Time", "java.util.Date");

        // timestamp, datetime
        put("java.sql.Timestamp", "java.util.Date");

        // binary, varbinary, tinyblob, blob, mediumblob, longblob
        // qjd project: print_info.content varbinary(61800);
        put("[B", "byte[]");

        // ---------

        // varchar, char, enum, set, text, tinytext, mediumtext, longtext
        put("java.lang.String", "java.lang.String");

        // int, integer, tinyint, smallint, mediumint
        put("java.lang.Integer", "java.lang.Integer");

        // bigint
        put("java.lang.Long", "java.lang.Long");

        // real, double
        put("java.lang.Double", "java.lang.Double");

        // float
        put("java.lang.Float", "java.lang.Float");

        // bit
        put("java.lang.Boolean", "java.lang.Boolean");

        // decimal, numeric
        put("java.math.BigDecimal", "java.math.BigDecimal");

        // unsigned bigint
        put("java.math.BigInteger", "java.math.BigInteger");

    }};

    public String getType(String typeString) {
        return map.get(typeString);
    }

    public static void main(String[] args) {
        new TypeMapping().getType("java.math.BigInteger");
    }
}
