package net.ltsoftware.platform.usercenter.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import net.ltsoftware.platform.usercenter.constant.ErrorCode;
import net.ltsoftware.platform.usercenter.constant.SmsConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 云信使
 * @author
 * @version 1.0
 * @created
 */
@Component
public class YXSmsSender {
	private Logger log= Logger.getLogger(this.getClass());

	@Autowired
	private RedisClient redis;

	public int sendPhoneCode(String phone) throws Exception {

		Object val = redis.get(phone);
		if(val!=null){
			return ErrorCode.SMS_PHONE_FREQ_HIGH;
		}
		String code = CodeHelper.getRandomNum();
		String message = SmsConstants.REG_CONTENT.replaceAll("%s",code);

		return send(phone,message);
	}

	public int send(String phoneNum, String message) throws IOException {

//		message +=sign;

		String reqUrl = SmsConstants.SERV_URL +
				"?id=" + SmsConstants.USER +
				"&pwd=" + SmsConstants.PASS +
				"&to=" + phoneNum +
				"&content=" + URLEncoder.encode(message, "GBK");

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
		return 0;
	}

	public int getBalance(){
		return 1;
	}

	public void main(String[] args) throws IOException {
//		new YXSmsSender().send("18598050612","验证码111111");

	}
	
	
}