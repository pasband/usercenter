package net.ltsoftware.usercenter.support.wxmp;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.ltsoftware.usercenter.constant.ErrorCode;
import net.ltsoftware.usercenter.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

@Api(value = "WeixinMpController")
@Controller
public class WeixinMpController {

    @Autowired
    private WeixinMpService weixinMpService;

    @ApiOperation(value = "获取签名信息")
    @ApiImplicitParams({@ApiImplicitParam(name = "url", value = "网页链接", required = true, dataType = "String")})
    @GetMapping("/wxmp/sign")
    public void getWxmpSign(String url, HttpServletResponse response){
        JSONObject json = weixinMpService.getWxmpSignData(url);
        if(json==null){
            JsonUtil.toJsonMsg(response,ErrorCode.UNCLASSIFIED,null);
        }
        json.put("appId",WeixinMpConstants.WEIXIN_MP_APPID);
        JsonUtil.toJsonMsg(response,ErrorCode.SUCCESS,json);

    }


}
