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

    public static String getyyyyMMddHHmmssSSS() {
        return getyyyyMMddHHmmssSSS(new Date());
    }
    public static String getyyyyMMddHHmmss() { return getyyyyMMddHHmmss(new Date());}

    public static String getTimestamp() {return String.valueOf(System.currentTimeMillis()/1000);}


}
