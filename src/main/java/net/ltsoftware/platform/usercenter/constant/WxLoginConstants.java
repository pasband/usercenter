package net.ltsoftware.platform.usercenter.constant;

public interface WxLoginConstants {

    String CODE_API = "https://open.weixin.qq.com/connect/qrconnect";
    String APPID = "wxe6624d74e53bd06c";
    String REDIRECT_URI = "http://uc.ltsoftware.net/oauth/wxcallback";
    String RESPONSE_TYPE = "code";
    String SCOPE = "snsapi_login";

    String TOKEN_API = "https://api.weixin.qq.com/sns/oauth2/access_token";
    String SECRET = "a1e821f5632a0265485f60e65dde643d";
    String GRANT_TYPE = "authorization_code";

}
