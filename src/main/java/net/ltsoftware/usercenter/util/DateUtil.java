package net.ltsoftware.usercenter.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static String getTimeStampPrefix(Date date) {
        return yyyyMMddHHmmssSSS.format(date);
    }
}
