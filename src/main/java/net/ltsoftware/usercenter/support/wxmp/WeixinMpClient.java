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
    String getAccessToken(@RequestParam(value = "grant_type", required = true) String grant_type,
                   @RequestParam(value = "appid", required = true) String appid,
                   @RequestParam(value = "secret", required = true) String secret);

//    https请求方式: GET
//    https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi

//    {
//        "errcode":0,
//        "errmsg":"ok",
//        "ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA",
//        "expires_in":7200
//    }

    @RequestMapping(method = RequestMethod.GET, value = "/cgi-bin/ticket/getticket")
    String getJsapiTicket(@RequestParam(value = "access_token", required = true) String access_token,
                          @RequestParam(value = "type", required = true) String type);
}
