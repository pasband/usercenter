package net.ltsoftware.usercenter.support.wxmp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.ltsoftware.usercenter.constant.ErrorCode;
import net.ltsoftware.usercenter.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "WeixinMpController")
@Controller
public class WeixinMpController {

    private static Logger logger = LoggerFactory.getLogger(WeixinMpController.class);

    @Autowired
    private WeixinMpService weixinMpService;

    @ApiOperation(value = "获取签名信息")
    @ApiImplicitParams({@ApiImplicitParam(name = "url", value = "网页链接", required = true, dataType = "String")})
    @GetMapping("/wxmp/sign")
    public void getWxmpSign(String url, HttpServletResponse response){
        JSONObject json = weixinMpService.getWxmpSignData(url);
        if(json==null){
            JsonUtil.toJsonMsg(response,ErrorCode.UNCLASSIFIED,null);
            return;
        }
        json.put("appId",WeixinMpConstants.WEIXIN_MP_APPID);
        JsonUtil.toJsonMsg(response,ErrorCode.SUCCESS,json);

    }

    @GetMapping("/wxmp/openid")
    public void getOpenid(String code, HttpServletResponse response) {
        String resp = weixinMpService.getOpenid(code);
        JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, resp);
    }

    @GetMapping("/wxmp/auth/redirect")
    public void authRedirect(HttpServletRequest request, HttpServletResponse response){

//        {"code":["001JFmL908j6uz1u0lL90ximL90JFmLe"],"state":["123"]}
        String paraStr = JSON.toJSONString(request.getParameterMap());
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        logger.info("/wxmp/auth/redirect:"+paraStr);

    }

    @GetMapping("/wxmp/sns/base")
    public void getSnsBase(String redirectUrl, HttpServletResponse response){
        String snsBaseUrl = weixinMpService.getSnsBase(redirectUrl);
        JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, snsBaseUrl);
    }

//    @GetMapping("/MP_verify_HgwQzcbwgDoAAs0t.txt")
//    public void temp(HttpServletResponse response){
//        JsonUtil.writer(response,"HgwQzcbwgDoAAs0t");
//    }

}
