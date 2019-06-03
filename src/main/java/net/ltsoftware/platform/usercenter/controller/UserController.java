package net.ltsoftware.platform.usercenter.controller;

import net.ltsoftware.platform.usercenter.model.User;
import net.ltsoftware.platform.usercenter.service.UserService;
import net.ltsoftware.platform.usercenter.util.JsonUtil;
import net.ltsoftware.platform.usercenter.util.YXSmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private YXSmsSender smsSender;

    @RequestMapping("/user/reg")
    public void addUser(User user, HttpServletResponse response) {
        try {
            userService.insert(user);
            JsonUtil.writer(response, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/user/reg/sendCode")
    public void sendCode(String phone, HttpServletResponse response) {
        try {
            int code = smsSender.sendPhoneCode(phone);
            JsonUtil.toJsonMsg(response, code, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/login")
    public void login(String access_token, int expires_in) {


    }

    @RequestMapping("/user/oauth2/getCode")
    public void getCode() {
//
//
//        //拼接url
//        StringBuilder url = new StringBuilder();
//        url.append("https://graph.qq.com/oauth2.0/authorize?");
//        url.append("response_type=code");
//        url.append("&client_id=" + constants.getQqAppId());
//        //回调地址 ,回调地址要进行Encode转码
//        String redirect_uri = constants.getQqRedirectUrl();
//        //转码
//        url.append("&redirect_uri=" + URLEncodeUtil.getURLEncoderString(redirect_uri));
//        url.append("&state=ok");
//        String result = HttpClientUtils.get(url.toString(), "UTF-8");
//        System.out.println(url.toString());
//        return url.toString();
    }


}
