package net.ltsoftware.usercenter.support.wxmp;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="wxmp-service",url="https://open.weixin.qq.com/")
public interface WeixinAuthClient {

    @RequestMapping(method = RequestMethod.GET, value = "/connect/oauth2/authorize")
    String getCode(@RequestParam(value = "appid") String appid,
                   @RequestParam(value = "redirect_uri") String redirect_uri,
                   @RequestParam(value = "response_type") String response_type,
                   @RequestParam(value = "scope") String scope,
                   @RequestParam(value = "state", required = false) String state);


}
