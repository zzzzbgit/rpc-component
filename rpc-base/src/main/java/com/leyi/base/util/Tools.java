package com.leyi.base.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ljw
 * @Date 2021/5/12 10:48
 */
public class Tools {
    private Tools(){
        throw new IllegalStateException("Tools class");
    }

    /**
     * 字节数组转换为十六进制字符串
     *
     * @param b
     *            byte[] 需要转换的字节数组
     * @return String 十六进制字符串
     */
    public static String byteToHex(byte[] b) {
        if (b == null) {
            throw new IllegalArgumentException("Argument b ( byte array ) is null! ");
        }
        String stmp = "";
        StringBuilder hs=new StringBuilder();

        for (byte n:b){
            stmp = Integer.toHexString(n & 0xff);
            if (stmp.length() == 1) {
                hs.append('0');
                hs.append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }

    /**
     * 十六进制串转化为byte数组
     *
     * @return the array of byte
     */
    public static byte[] hexToByte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = String.valueOf(arr[i++]) + arr[i];
            Integer byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] =  byteint.byteValue();
        }
        return b;
    }

    public static void getLine() {
        int lineLength = 100;
        String line = "";
        for (int i = 0; i < lineLength; i++) {
            line += "-";
        }
        System.out.println(line);
    }

    /*long limit*/
    private static long MAX = 9000000000000000000L;

    /*消息唯一标识*/
    private static AtomicLong RequestIdGenerate = new AtomicLong(0);

    /**
     * 获取唯一序列号
     * @return
     */
    public static long GenSerialNumber() {
        long sn = RequestIdGenerate.incrementAndGet();
        if (sn > MAX) {
            RequestIdGenerate.set(0);
        }
        return sn;
    }

    public static boolean NotNull(String obj) {
        if (null != obj && !"".equals(obj)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof Optional) {
            return !((Optional<?>) obj).isPresent();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }

        return false;
    }

    /**
     * 任何一个为空
     * @param member
     * @return
     */
    public static boolean anyNull(String... member) {
        return Arrays.stream(member).anyMatch(str -> !NotNull(str));
    }

    public static String bytesToString(byte[] strBytes) {
        int endIdx = 0;
        for (int i = 0; i < strBytes.length; i++) {
            if (0 == strBytes[i]) {
                endIdx = i;
                break;
            }
        }
        return new String(strBytes, 0, endIdx, StandardCharsets.UTF_8);
    }

}
