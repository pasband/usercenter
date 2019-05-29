package net.ltsoftware.platform.usercenter.util;

public class CodeHelper {

    public static String getRandomNum(){
        int num = (int)((Math.random()*9+1)*10000);
        return String.valueOf(num);
    }
}
