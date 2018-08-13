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
}
