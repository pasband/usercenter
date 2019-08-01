package net.ltsoftware.usercenter.support.wxmp;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="wxmp-service",url="https://api.weixin.qq.com/")
public interface WeixinMpClient {

//    https请求方式: GET
//    https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET

//    {"access_token":"ACCESS_TOKEN","expires_in":7200}
//    {"errcode":40013,"errmsg":"invalid appid"}

    @RequestMapping(method = RequestMethod.GET, value = "/cgi-bin/token")
    String getAccessToken(@RequestParam(value = "grant_type") String grant_type,
                   @RequestParam(value = "appid") String appid,
                   @RequestParam(value = "secret") String secret);

//    https请求方式: GET
//    https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi

//    {
//        "errcode":0,
//        "errmsg":"ok",
//        "ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA",
//        "expires_in":7200
//    }

    @RequestMapping(method = RequestMethod.GET, value = "/cgi-bin/ticket/getticket")
    String getJsapiTicket(@RequestParam(value = "access_token") String access_token,
                          @RequestParam(value = "type") String type);

    @RequestMapping(method = RequestMethod.GET, value = "https://open.weixin.qq.com/connect/oauth2/authorize")
    String getCode(@RequestParam(value = "appid") String appid,
                   @RequestParam(value = "redirect_uri") String redirect_uri,
                   @RequestParam(value = "response_type") String response_type,
                   @RequestParam(value = "scope") String scope,
                   @RequestParam(value = "state", required = false) String state);

    @RequestMapping(method = RequestMethod.GET, value = "https://api.weixin.qq.com/sns/oauth2/access_token")
    String getOpenid(@RequestParam(value = "appid") String appid,
                   @RequestParam(value = "secret") String secret,
                   @RequestParam(value = "code") String code,
                   @RequestParam(value = "grant_type") String grant_type);

}
