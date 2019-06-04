package net.ltsoftware.platform.usercenter.controller;

import com.alibaba.fastjson.JSONObject;
import net.ltsoftware.platform.usercenter.model.User;
import net.ltsoftware.platform.usercenter.oauth2.QqOauth2Service;
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

    @Autowired
    private QqOauth2Service qqOauth2Service;

//    @RequestMapping("/user/reg")
//    public void addUser(User user, HttpServletResponse response) {
//        try {
//            userService.insert(user);
//            JsonUtil.writer(response, "");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @RequestMapping("/bindPhone")
    public void bindPhone(String phone, String code, String userId) {

    }

    @RequestMapping("/sendCode")
    public void sendCode(String phone, HttpServletResponse response) {
        try {
            int errCode = smsSender.sendPhoneCode(phone);
            JsonUtil.toJsonMsg(response, errCode, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/login")
    public void login(String type, String access_token, int expires_in, HttpServletResponse response) throws Exception {
        User user = null;
        if ("QQ".equals(type)) {
            String openId = qqOauth2Service.getOpenID(access_token);
            user = userService.selectByQqOpenId(openId);
            if (user == null) {
                user = registerByQq(openId, access_token);
            }
        } else if ("WEIXIN".equals(type)) {
            //
        }
        response.sendRedirect("/home");

    }

    private User registerByQq(String openId, String access_token) throws Exception {
        JSONObject json = qqOauth2Service.getUserinfo(access_token, openId);
        String nickname = json.getString("nickname");
        User user = new User();
        user.setName(nickname);
        user.setQqOpenid(openId);
        userService.insert(user);
        return user;
    }


//    @RequestMapping("/user/oauth2/getCode")
//    public void getCode() {
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
//    }


}
