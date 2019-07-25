package net.ltsoftware.usercenter.constant;

public interface WxpayConstants {

    //more constants see config.MyWxpayConfig
    String CHANNEL_NAME = "wxpay";

    String NOTIFY_URL = "https://uc.ltsoftware.net/pay/wxpay/notify";

    String KEY_NOTIFY_URL_TAIL ="_notify_url";

    String NOTIFY_RETURN_SUCCESS = "SUCCESS";

    String NOTIFY_RESULT_SUCCESS = "SUCCESS";

    int PAY_WAIT_TIMEOUT = 7200;

}
