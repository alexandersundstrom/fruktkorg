package com.evry.fruktkorgrest.utils;

public class NumberUtils {
    public static boolean isLong(String number) {
        try {
            Long.valueOf(number);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInteger(String number) {
        try {
            Integer.valueOf(number);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
