package com.fcs.common;

import java.util.UUID;

/**
 * Created by Lucare.Feng on 2016/8/8.
 */
public class Strings {

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String nullToEmpty(String string) {
        return string == null ? "" : string;
    }

    public static String emptyToNull(String string) {
        return isNullOrEmpty(string) ? null : string;
    }

    public static boolean isNullOrEmpty(String string) {
        return string == null || string.length() == 0;
    }

    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static String trim(String input) {
        return input == null?null:input.trim();
    }

}
