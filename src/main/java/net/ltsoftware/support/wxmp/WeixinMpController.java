package net.ltsoftware.support.wxmp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import net.ltsoftware.usercenter.constant.ErrorCode;
import net.ltsoftware.usercenter.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class WeixinMpController {

    @Autowired
    private WeixinMpService weixinMpService;

    @GetMapping("/wxmp/sign")
    public void getWxmpSign(String url, HttpServletResponse response){
        JSONObject json = weixinMpService.getWxmpSignData(url);
        json.put("appId",WeixinMpConstants.WEIXIN_MP_APPID);
        JsonUtil.toJsonMsg(response,ErrorCode.SUCCESS,json);

    }


}
