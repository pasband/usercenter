package net.ltsoftware.platform.usercenter.constant;

public interface WxLoginConstants {

    String CODE_API = "https://open.weixin.qq.com/connect/qrconnect";
    String APPID = "wxe6624d74e53bd06c";
    String REDIRECT_URI = "http://uc.ltsoftware.net/oauth/wxcallback";
    String RESPONSE_TYPE = "code";
    String SCOPE = "snsapi_login";

    String TOKEN_API = "https://api.weixin.qq.com/sns/oauth2/access_token";
    String SECRET = "381714848e653f474543c2bf327147d7";
    String GRANT_TYPE = "authorization_code";

    String USERINFO_API = "https://api.weixin.qq.com/sns/userinfo";


}
