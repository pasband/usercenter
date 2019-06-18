package net.ltsoftware.usercenter.oauth2;

import com.alibaba.fastjson.JSON;
import net.ltsoftware.usercenter.constant.WxLoginConstants;
import net.ltsoftware.usercenter.controller.LoginController;
import net.ltsoftware.usercenter.util.CodeHelper;
import net.ltsoftware.usercenter.util.HttpUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Map<String, Object> getToken(String code, String state) {
        List<NameValuePair> paras = new ArrayList<>();
        paras.add(new BasicNameValuePair("appid", WxLoginConstants.APPID));
        paras.add(new BasicNameValuePair("secret", WxLoginConstants.SECRET));
        paras.add(new BasicNameValuePair("code", code));
        paras.add(new BasicNameValuePair("grant_type", WxLoginConstants.GRANT_TYPE));

        String result = httpUtil.get(WxLoginConstants.TOKEN_API, paras);
        return JSON.parseObject(result).getInnerMap();

    }

    //    http请求方式: GET
//    https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
    public Map<String, Object> getWxUserinfo(String accessToken, String openId) {
        List<NameValuePair> paras = new ArrayList<>();
        paras.add(new BasicNameValuePair("access_token", accessToken));
        paras.add(new BasicNameValuePair("openid", openId));
        String result = httpUtil.get(WxLoginConstants.USERINFO_API, paras);
        return JSON.parseObject(result).getInnerMap();

    }


}
