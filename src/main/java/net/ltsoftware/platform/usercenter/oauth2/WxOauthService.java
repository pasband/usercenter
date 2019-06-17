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

    public String getCode() {

        List<NameValuePair> paras = new ArrayList<>();
        paras.add(new BasicNameValuePair("appid", WxLoginConstants.APPID));
        paras.add(new BasicNameValuePair("redirect_uri", WxLoginConstants.REDIRECT_URI));
        paras.add(new BasicNameValuePair("response_type", WxLoginConstants.RESPONSE_TYPE));
        paras.add(new BasicNameValuePair("scope", WxLoginConstants.SCOPE));
        paras.add(new BasicNameValuePair("state", CodeHelper.getRandomNum(12)));


        httpUtil = new HttpUtil();
        String result = httpUtil.get(WxLoginConstants.CODE_API, paras);
        logger.info(result);

        return result;
    }

    public static void main(String[] args) {
        new WxOauthService().getCode();
    }


}
