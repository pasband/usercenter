package net.ltsoftware.usercenter.support.wxmp;

public interface WeixinMpConstants {

    String WEIXIN_MP_APPID = "wxed61c321c91d5d92";
    String WEIXIN_MP_SECRET = "9fad261d66fbbd0df8bf0f3a0aff2db1";
    String GRANT_TYPE_OF_ACCESS_TOKEN = "client_credential";

    String TYPE_OF_JSAPI = "jsapi";

    String REDIS_KEY_TOKEN = "wxmp_access_token";
    String REDIS_KEY_TICKET = "wxmp_access_ticket";

    int EXPIRES_TIME = 7200-15; //防止取到的token/ticket失效的情况，提前15秒释放

}

