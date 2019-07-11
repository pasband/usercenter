package net.ltsoftware.usercenter.support.wxmp;

public interface WeixinMpConstants {

    String WEIXIN_MP_APPID = "wxed61c321c91d5d92";
    String WEIXIN_MP_SECRET = "7231d0955d2a4c85b2d038d39aa5588d";
    String GRANT_TYPE_OF_ACCESS_TOKEN = "client_credential";

    String TYPE_OF_JSAPI = "jsapi";

    String REDIS_KEY_TOKEN = "wxmp_access_token";
    String REDIS_KEY_TICKET = "wxmp_access_ticket";

    int EXPIRES_TIME = 7200-15; //防止取到的token/ticket失效的情况，提前15秒释放

}

