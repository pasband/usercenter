package net.ltsoftware.usercenter.util;

import java.security.MessageDigest;
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

    public static String getRandomString(int length) {
        String useChars = "abcdefghjkmnpqrstuvwxy3456789";
        int count = useChars.length();
        char[] code = new char[length];
        while(length-->0){
            int pos = (int)(Math.random()*count);
            code[length] = useChars.charAt(pos);
        }
        return String.copyValueOf(code);
    }

    public static String getSha1(String str) {

        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(getUUID());
    }
}
