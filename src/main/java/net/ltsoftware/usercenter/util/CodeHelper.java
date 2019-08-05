package net.ltsoftware.usercenter.util;

import org.springframework.util.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static String getSha1(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
//        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char[] buf = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);

    }

    public static String HMACSHA256(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        sha256_HMAC.init(secret_key);

        byte[] array = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();

        for (byte item : array) {

            sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);

        }

        return sb.toString().toUpperCase();

    }

    public static String getMD5(String str) {
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }


    public static void main(String[] args) throws Exception {
        System.out.println(HMACSHA256("appId=wxed61c321c91d5d92&nonceStr=dr3bwdpr7neakxsm794nm9k8r8tpstne&package=prepay_id=wx041539367887558a5b6c3b951675746800&signType=HMAC-SHA256&timeStamp=1564904376&key=7231d0955d2a4c85b2d038d39aa5588d","7231d0955d2a4c85b2d038d39aa5588d"));
    }
}
