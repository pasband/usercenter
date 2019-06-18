package net.ltsoftware.usercenter.oauth2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ltsoftware.usercenter.constant.TencentConstants;
import net.ltsoftware.usercenter.util.HttpUtil;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

@Deprecated
@Service
public class QqOauth2Service {

    @Autowired
    private HttpUtil httpUtil;

    //request https://graph.qq.com/oauth2.0/me?access_token=A110C6E862F4B29B719B8E3F37B7A403
    //response callback( {"client_id":"101584460","openid":"88076875A787B01378E9D7172DE7ED86"} );
    public String getOpenID(String token) {

        String respStr = httpUtil.get(TencentConstants.QQ_OPENID_URL,
                asList(new BasicNameValuePair("access_token", token)));

        Matcher m = Pattern.compile("\"openid\"\\s*:\\s*\"(\\w+)\"").matcher(respStr);
        if (m.find()) {
            String openid = m.group(1);
            return openid;
        }
        return null;
    }

    //request https://graph.qq.com/user/get_user_info
    // ?access_token=A110C6E862F4B29B719B8E3F37B7A403
    // &oauth_consumer_key=101584460
    // &openid=88076875A787B01378E9D7172DE7ED86
    //response
    public JSONObject getUserinfo(String token, String openId) {
        String respStr = httpUtil.get(TencentConstants.QQ_USERINFO_URL, asList(
                new BasicNameValuePair("access_token", token),
                new BasicNameValuePair("oauth_consumer", TencentConstants.QQ_APP_ID),
                new BasicNameValuePair("openid", openId)));
        JSONObject json = JSON.parseObject(respStr);

        return json;
    }

}
