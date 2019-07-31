package net.ltsoftware.usercenter.constant;

//pc端网页支付
public interface WxpayConstants {

    //more constants see config.MyWxpayConfig
    String CHANNEL_NAME = "wxpay";

    String TRADE_TYPE = "NATIVE";

    String NOTIFY_URL = "https://uc.ltsoftware.net/pay/wxpay/notify";

    String KEY_NOTIFY_URL_TAIL ="_notify_url";

    String NOTIFY_RETURN_SUCCESS = "SUCCESS";

    String NOTIFY_RESULT_SUCCESS = "SUCCESS";

    int PAY_WAIT_TIMEOUT = 7200;

    String NOTIFY_REPLY = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";

}
