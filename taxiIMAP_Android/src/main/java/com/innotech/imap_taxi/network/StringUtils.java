package com.innotech.imap_taxi.network;

/**
 * Created with IntelliJ IDEA.
 * User: Kvest
 * Date: 15.09.13
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {
    public static byte[] strToBytes(String data) {
        try {
            return data.getBytes("utf-8");
        } catch (Exception ioex) {
            return data.getBytes();
        }
    }

    public static String bytesToStr(byte[] data) {
        try {
            return new String(data, "utf-8");
        } catch (Exception ioex) {
            return new String(data);
        }
    }

    public static String replace(final String src, final String from, final String to) {
        String result = src;
        while (result.indexOf(from) != -1) {
            result = result.substring(0, result.indexOf(from)) + to + result.substring(result.indexOf(from) + from.length());
        }

        return result;
    }
}
