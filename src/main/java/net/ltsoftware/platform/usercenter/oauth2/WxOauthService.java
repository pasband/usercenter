package net.ltsoftware.platform.usercenter.oauth2;

import net.ltsoftware.platform.usercenter.constant.WxLoginConstants;
import net.ltsoftware.platform.usercenter.controller.LoginController;
import net.ltsoftware.platform.usercenter.util.CodeHelper;
import net.ltsoftware.platform.usercenter.util.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WxOauthService {


    @Autowired
    private HttpUtil httpUtil;

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);


    public String getUrl() {
        List<NameValuePair> paras = new ArrayList<>();
        paras.add(new BasicNameValuePair("appid", WxLoginConstants.APPID));
        paras.add(new BasicNameValuePair("redirect_uri", WxLoginConstants.REDIRECT_URI));
        paras.add(new BasicNameValuePair("response_type", WxLoginConstants.RESPONSE_TYPE));
        paras.add(new BasicNameValuePair("scope", WxLoginConstants.SCOPE));
        paras.add(new BasicNameValuePair("state", CodeHelper.getRandomNum(12)));

        //只获取url，不发送请求
        return httpUtil.getUrl(WxLoginConstants.CODE_API, paras);

    }

//    https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code

    public String getToken(String code) {
        List<NameValuePair> paras = new ArrayList<>();
        paras.add(new BasicNameValuePair("appid", WxLoginConstants.APPID));
        paras.add(new BasicNameValuePair("secret", WxLoginConstants.SECRET));
        paras.add(new BasicNameValuePair("code", code));
        paras.add(new BasicNameValuePair("grant_type", WxLoginConstants.GRANT_TYPE));

        String result = httpUtil.get(WxLoginConstants.TOKEN_API, paras);

        return null;
    }


}
