package net.ltsoftware.usercenter.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String getyyyyMMddHHmmssSSS(Date date) {
        return yyyyMMddHHmmssSSS.format(date);
    }
    public static String getyyyyMMddHHmmss(Date date) {
        return yyyyMMddHHmmss.format(date);
    }
}
