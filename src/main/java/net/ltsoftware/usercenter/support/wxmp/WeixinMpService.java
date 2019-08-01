package net.ltsoftware.usercenter.support.wxmp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ltsoftware.usercenter.util.CodeHelper;
import net.ltsoftware.usercenter.util.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
                    redisClient.setex(WeixinMpConstants.REDIS_KEY_TICKET,WeixinMpConstants.EXPIRES_TIME,ticket);
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
                redisClient.setex(WeixinMpConstants.REDIS_KEY_TOKEN,WeixinMpConstants.EXPIRES_TIME,token);
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
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
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


}
