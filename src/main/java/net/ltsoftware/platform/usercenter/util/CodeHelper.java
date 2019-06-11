package net.ltsoftware.platform.usercenter.util;

import java.util.Date;
import java.util.UUID;

public class CodeHelper {

    public static String getRandomNum() {
        int num = (int) ((Math.random() * 9 + 1) * 10000);
        return String.valueOf(num);
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid;

    }

    public static String getOrderId() {
        String time = DateUtil.getTimeStampPrefix(new Date());
        return time;
    }

    public static void main(String[] args) {
        System.out.println(getUUID());
    }
}
