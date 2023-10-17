package net.ltsoftware.usercenter.support.wxmp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ltsoftware.usercenter.util.CodeHelper;
import net.ltsoftware.usercenter.util.DateUtil;
import net.ltsoftware.usercenter.util.HttpUtil;
import net.ltsoftware.usercenter.util.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeixinMpService {

    @Resource
    private WeixinMpClient weixinMpClient;

    @Resource
    private WeixinAuthClient weixinAuthClient;

    @Resource
    private RedisClient<String> redisClient;

    private static Logger logger = LoggerFactory.getLogger(WeixinMpService.class);

    private String getTicket(){
        String ticket = redisClient.get(WeixinMpConstants.REDIS_KEY_TICKET);
        if(ticket==null){
            String token = getToken();
            if(token==null){
                logger.error("wxmp: token is null, abandon to get ticket.");
                return null;
            }
            String respTicket = weixinMpClient.getJsapiTicket(token,WeixinMpConstants.TYPE_OF_JSAPI);
            JSONObject result = JSON.parseObject(respTicket);
            if(result.getIntValue("errcode")==0){
                ticket = result.getString("ticket");
                if(ticket!=null){
                    redisClient.setex(WeixinMpConstants.REDIS_KEY_TICKET,WeixinMpConstants.EXPIRES_TIME_OF_ACCESS_TOKEN,ticket);
                }else{
                    logger.error("wxmp: get ticket failed:"+result);
                }
            }else{
                logger.error("wxmp: get ticket failed:"+result);
            }
        }
        logger.info("get ticket:"+ticket);
        return ticket;
    }

    private String getToken(){
        String token = redisClient.get(WeixinMpConstants.REDIS_KEY_TOKEN);
        if(token==null){
            String resp = weixinMpClient.getAccessToken(
                    WeixinMpConstants.GRANT_TYPE_OF_ACCESS_TOKEN,
                    WeixinMpConstants.WEIXIN_MP_APPID,
                    WeixinMpConstants.WEIXIN_MP_SECRET);
            JSONObject json = JSON.parseObject(resp);
            logger.info("get token resp json:"+json);
            token = json.getString("access_token");
            if(token==null){
                logger.error("wxmp: get token failed:"+json);
            }else{
                redisClient.setex(WeixinMpConstants.REDIS_KEY_TOKEN,WeixinMpConstants.EXPIRES_TIME_OF_ACCESS_TOKEN,token);
            }

        }
        logger.info("get token:"+token);
        return token;

    }

//    https://open.weixin.qq.com/connect/oauth2/authorize?
//    appid=wxed61c321c91d5d92
//    &redirect_uri=http://www.ltsoftware.net/art/saas/buy.html
//    &response_type=code
//    &scope=snsapi_base
//    &state=123

    // https://open.weixin.qq.com/connect/oauth2/authorize?
    // appid=APPID
    // &redirect_uri=REDIRECT_URI
    // &response_type=code
    // &scope=SCOPE
    // &state=STATE#wechat_redirect

    public String getOpenid(String code){

//        String state = CodeHelper.getRandomString(32);
//        weixinAuthClient.getCode(WeixinMpConstants.WEIXIN_MP_APPID,
//                WeixinMpConstants.REDIRECT_URL,
//                WeixinMpConstants.RESPONSE_TYPE,
//                WeixinMpConstants.SCOPE,
//                state
//        );
//        return state;
        String respStr = weixinMpClient.getOpenid(WeixinMpConstants.WEIXIN_MP_APPID,
                WeixinMpConstants.WEIXIN_MP_SECRET,
                code,
                WeixinMpConstants.GRANT_TYPE);
        logger.info(code);
        logger.info(respStr);
        JSONObject respJson = JSON.parseObject(respStr);
        return respJson.getString("openid");
    }

    //生成微信权限验证的参数
    public JSONObject getWxmpSignData(String url) {
        String ticket = getTicket();
        if(ticket==null){
            logger.error("wxmp: ticket is null, abandon generate signature.");
            return null;
        }
        String nonceStr = CodeHelper.getRandomString(32);
        String timestamp = DateUtil.getTimestamp();
        String signature = null;
        //注意这里参数名必须全部小写，且必须有序
        String string1 = "jsapi_ticket=" + ticket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;
        try{
            signature = CodeHelper.getSha1(string1);
            JSONObject json = new JSONObject();
            json.put("url", url);
            json.put("nonceStr", nonceStr);
            json.put("timestamp", timestamp);
            json.put("signature", signature);
            return json;

        }
        catch (Exception e)
        {
            logger.error("sign failed:"+e);
        }
        return null;
    }

    // https://open.weixin.qq.com/connect/oauth2/authorize
    // ?appid=APPID&redirect_uri=REDIRECT_URI
    // &response_type=code&scope=SCOPE&state=STATE#wechat_redirect

    public String getSnsBaseUrl(){
        String state = CodeHelper.getRandomString(32);
        String snsBaseUrl = WeixinMpConstants.USERINFO_API+
                "?appid="+WeixinMpConstants.WEIXIN_MP_APPID+
                "&redirect_uri="+WeixinMpConstants.SNS_REDIRECT_URL+
                "&response_type="+WeixinMpConstants.RESPONSE_TYPE+
                "&scope="+WeixinMpConstants.SNS_SCOPE_BASE+
                "&state="+state;
        return snsBaseUrl;
    }

    public String getSnsUserinfoUrl(){
        String state = CodeHelper.getRandomString(32);
        String snsUserinfoUrl = WeixinMpConstants.USERINFO_API+
                "?appid="+WeixinMpConstants.WEIXIN_MP_APPID+
                "&redirect_uri="+WeixinMpConstants.SNS_REDIRECT_URL+
                "&response_type="+WeixinMpConstants.RESPONSE_TYPE+
                "&scope="+WeixinMpConstants.SNS_SCOPE_USERINFO+
                "&state="+state;
        return snsUserinfoUrl;
    }

    public String getSnsOpenid(String code){
        //https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        String getTokenUrl = WeixinMpConstants.TOKEN_API+
                "?appid="+WeixinMpConstants.WEIXIN_MP_APPID+
                "&secret="+WeixinMpConstants.WEIXIN_MP_SECRET+
                "&code="+code+
                "&grant_type="+WeixinMpConstants.GRANT_TYPE;
        String respStr = new HttpUtil().get(getTokenUrl,null);
        logger.info("respStr: "+respStr);
        JSONObject respJson = JSON.parseObject(respStr);
        String openid = respJson.getString("openid");

        String access_token = respJson.getString("access_token");
        String refresh_token = respJson.getString("refresh_token");
        if(access_token!=null){
            String access_token_key = openid+"_access_token";
            redisClient.setex(access_token_key,WeixinMpConstants.EXPIRES_TIME_OF_ACCESS_TOKEN,access_token);
        }
        if(refresh_token!=null){
            String refresh_token_key = openid+"_refresh_token";
            redisClient.setex(refresh_token_key,WeixinMpConstants.EXPIRES_TIME_OF_REFRESH_TOKEN,refresh_token);
        }
//        responseStr: {"access_token":"72_HBa7rUwLdzKA6F4gkDxYqI8i4K8T2bn_ObhLs26u1Y4MBWw9T_YZyqIWX3017Ooem6W07UaJwqFvzFUMMarm6loAqJamX9E9ZW_NlY-2TlY",
//                "expires_in":7200,
//                "refresh_token":"72_5JYnA9lv6T1l9z8wak9GcnyPOAxM2aW3PVEkjBPTMA9fgeuD-IzDLvIgRUhja-CDRkY2TO7KVjAkGWzsT9zDPe1EBvdB-ImTl4kLAGChcr0",
//                "openid":"oWvcPuHrXdhVYsKhR9oq39iIKQig","scope":"snsapi_base"}
        return openid;

    }

    public String getSnsUserinfo(String openid){
        String access_token_key = openid+"_access_token";
        String access_token = redisClient.get(access_token_key);
        if(access_token==null){
            String refresh_token_key = openid+"_refresh_token";
            String refresh_token = redisClient.get(refresh_token_key);
            if(refresh_token!=null){
                access_token = refreshToken(refresh_token);
            }
        }
        if(access_token!=null) {
            //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
            String snsUserInfoUrl = WeixinMpConstants.SNS_USERINFO_API +
                    "?access_token=" + access_token+
                    "&openid=" + openid+
                    "&lang=zh_CN";
            String respStr = new HttpUtil().get(snsUserInfoUrl,null);
            return respStr;
        }
        return null;
    }

    public String refreshToken(String refreshToken){
        //https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN
        String getRefreshTokenUrl = WeixinMpConstants.REFRESH_TOKEN_API+
                "?appid="+WeixinMpConstants.WEIXIN_MP_APPID+
                "grant_type=refresh_token"+
                "refresh_token="+refreshToken;

        String respStr = new HttpUtil().get(getRefreshTokenUrl,null);

        JSONObject respJson = JSON.parseObject(respStr);
        String access_token = respJson.getString("access_token");
        String refresh_token = respJson.getString("refresh_token");
        String openid = respJson.getString("openid");
        String access_token_key = openid+"_access_token";
        String refresh_token_key = openid+"_refresh_token";
        redisClient.setex(access_token_key,WeixinMpConstants.EXPIRES_TIME_OF_ACCESS_TOKEN,access_token);
        redisClient.setex(refresh_token_key,WeixinMpConstants.EXPIRES_TIME_OF_REFRESH_TOKEN,refresh_token);
        return access_token;

    }


}
