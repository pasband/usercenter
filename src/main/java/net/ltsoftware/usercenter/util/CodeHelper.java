package net.ltsoftware.usercenter.util;

import java.util.Date;
import java.util.UUID;

public class CodeHelper {

    public static String getRandomNum(int n) {
        int num = (int) ((Math.random() * 9 + 1) * Math.pow(10, n - 1));
        return String.valueOf(num);
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid;

    }

    public static String getOrderId() {
        String time = DateUtil.getyyyyMMddHHmmssSSS(new Date());
        return time;
    }

    public static void main(String[] args) {
        System.out.println(getUUID());
    }
}
