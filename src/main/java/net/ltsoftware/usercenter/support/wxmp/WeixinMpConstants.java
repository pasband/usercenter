package net.ltsoftware.usercenter.support.wxmp;

public interface WeixinMpConstants {

    String WEIXIN_MP_APPID = "wxed61c321c91d5d92";
    String WEIXIN_MP_SECRET = "9fad261d66fbbd0df8bf0f3a0aff2db1";
    String GRANT_TYPE_OF_ACCESS_TOKEN = "client_credential";

    String TYPE_OF_JSAPI = "jsapi";

    String REDIS_KEY_TOKEN = "wxmp_access_token";
    String REDIS_KEY_TICKET = "wxmp_access_ticket";


    String RESPONSE_TYPE = "code";
    String SNS_SCOPE_BASE = "snsapi_base";

    String SNS_SCOPE_USERINFO = "snsapi_base";
    String TOKEN_API = "https://api.weixin.qq.com/sns/oauth2/access_token";

    String REFRESH_TOKEN_API = "https://api.weixin.qq.com/sns/oauth2/refresh_token";

    String SNS_USERINFO_API = "https://api.weixin.qq.com/sns/userinfo";
    String GRANT_TYPE = "authorization_code";
    String SNS_REDIRECT_URL = "https://uc.ltsoftware.net/wxmp/sns/redirect";

    String USERINFO_API = "https://open.weixin.qq.com/connect/oauth2/authorize";


    int EXPIRES_TIME_OF_ACCESS_TOKEN = 7200-15; //防止取到的token/ticket失效的情况，提前15秒释放

    int EXPIRES_TIME_OF_REFRESH_TOKEN = 3600*24*30-5*60;

}

