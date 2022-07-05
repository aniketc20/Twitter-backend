package com.spring.twitter.api.utils;

import java.util.*;

/**
 * @author Aniket
 * @version 1.0
 * @date 30/06/22
 */
public class Utils {
    private static final Random randomGenerator = new Random();
    public static long getCurrentTime() {
        return new Date().getTime();
    }
    public static int randomInteger(int min, int max) {
        return randomGenerator.nextInt(max - min) + min;
    }
    public static long generateId() {
        return System.currentTimeMillis() * 100 + randomInteger(1, 9);
    }
}
