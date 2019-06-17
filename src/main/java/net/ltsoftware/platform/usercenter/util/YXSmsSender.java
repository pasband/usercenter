package net.ltsoftware.platform.usercenter.util;

import net.ltsoftware.platform.usercenter.constant.ErrorCode;
import net.ltsoftware.platform.usercenter.constant.SmsConstants;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static java.util.Arrays.asList;


/**
 * 云信使
 *
 * @author
 * @version 1.0
 * @created
 */
@Component
public class YXSmsSender {
    private Logger log = Logger.getLogger(this.getClass());

    @Autowired
    private RedisClient redis;

    @Autowired
    private HttpUtil httpUtil;

    public int sendPhoneCode(String phone) throws Exception {

        Object val = redis.get(SmsConstants.PREFIX + SmsConstants.FREQ + phone);
        if (val != null) {
            return ErrorCode.SMS_PHONE_FREQ_HIGH;
        }
        String code = CodeHelper.getRandomNum(5);
        String message = SmsConstants.LOGIN_CONTENT.replaceAll("%s", code);
        int errcode = send(phone, message);
        if (errcode == 0) {
            //defense high freq call
            redis.setex(SmsConstants.PREFIX + SmsConstants.FREQ + phone, SmsConstants.MIN_INTERVAL, phone);
            //save code to redis
            redis.setex(SmsConstants.PREFIX + SmsConstants.CODE + phone, SmsConstants.CODE_VALID, code);
        }
        return ErrorCode.SUCCESS;
    }

    private int send(String phone, String message) throws Exception {
        if (redis.get(SmsConstants.PREFIX + phone) != null) {
            return ErrorCode.SMS_PHONE_FREQ_HIGH;
        }

        List<NameValuePair> nvs = asList(
                new BasicNameValuePair("id", SmsConstants.USER),
                new BasicNameValuePair("pwd", SmsConstants.PASS),
                new BasicNameValuePair("to", phone),
                new BasicNameValuePair("content", message));

        String result = httpUtil.post(SmsConstants.SERV_URL, nvs, "GBK");
        //save msg&result to db

        if (result != null) {
            String[] words = result.split("/");
            if (words.length > 1) {
                int code = Integer.parseInt(words[0]);
                return code;

            }
        }

        return -99;
    }

    public int getBalance() throws IOException {
        String reqUrl = SmsConstants.MONEY_URL +
                "?uid=" + SmsConstants.USER +
                "&pwd=" + SmsConstants.PASS;
        //发送请求
        URL url = new URL(reqUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(30000);
        connection.setRequestMethod("POST");
        BufferedReader r = new BufferedReader(new InputStreamReader(
                url.openStream()));
        //返回结果
        String line = r.readLine();
        System.out.println(line);
        return 1;
    }

    public static void main(String[] args) throws IOException {
//		new YXSmsSender().send("18598050612","验证码111111");
        int code = Integer.parseInt("-02");
        System.out.println(code);
    }

}