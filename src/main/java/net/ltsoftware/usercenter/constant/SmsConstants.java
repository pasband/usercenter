package net.ltsoftware.usercenter.constant;

public interface SmsConstants {

    String SERV_URL = "http://service.winic.org/sys_port/gateway/";
    String MONEY_URL = "http://service.winic.org/webservice/public/remoney.asp";
    String USER = "andrew888";
    String PASS = "andrew888";
    String PREFIX_FREQ = "SMS_FREQ_";
    String PREFIX_CODE = "SMS_CODE_";

    int MIN_INTERVAL = 10;

    int CODE_VALID = 900;

//    String ENCODE = "utf8";


//    String SIGN = "【旅通软件】";

    String LOGIN_CONTENT = "%s（登录验证码）。工作人员不会向您索要，请勿向任何人泄露，以免造成账户或资金损失。";
//    String FORGET_CONTENT = "找回密码验证码：%s 短信验证码15分钟内有效，请勿泄露给其他人。如非本人操作请忽略。";
//    String LOGIN_CONTENT = "登录验证码：%s 短信验证码15分钟内有效，请勿泄露给其他人。如非本人操作请忽略。";


}
