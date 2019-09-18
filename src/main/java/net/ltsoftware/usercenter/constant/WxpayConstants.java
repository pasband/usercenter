package net.ltsoftware.usercenter.constant;

//pc端网页支付
public interface WxpayConstants {

    //more constants see config.MyWxpayConfig
    String CHANNEL_PC = "wxpay";
    String CHANNEL_H5 = "h5wxpay";

    String TRADE_TYPE_NATIVE = "NATIVE";


    String NOTIFY_URL = "https://uc.ltsoftware.net/pay/wxpay/notify";

    String KEY_NOTIFY_URL_TAIL ="_notify_url";

    String NOTIFY_RETURN_SUCCESS = "SUCCESS";

    String NOTIFY_RESULT_SUCCESS = "SUCCESS";

    int PAY_WAIT_TIMEOUT = 7200;

    String NOTIFY_REPLY = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";


    String CHANNEL_MP = "mwxpay";

    String TRADE_TYPE_JSAPI = "JSAPI";

    String SIGN_TYPE_HMACSHA256 = "HMAC-SHA256";

    String SIGN_TYPE_MD5 = "MD5";

}
