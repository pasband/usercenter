package net.ltsoftware.platform.usercenter.controller;

import com.alibaba.fastjson.JSONObject;
import net.ltsoftware.platform.usercenter.model.User;
import net.ltsoftware.platform.usercenter.oauth2.QqOauth2Service;
import net.ltsoftware.platform.usercenter.service.UserService;
import net.ltsoftware.platform.usercenter.util.CodeHelper;
import net.ltsoftware.platform.usercenter.util.JsonUtil;
import net.ltsoftware.platform.usercenter.util.YXSmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private YXSmsSender smsSender;

    @Autowired
    private QqOauth2Service qqOauth2Service;

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

//    @RequestMapping("/user/reg")
//    public void addUser(User user, HttpServletResponse response) {
//        try {
//            userService.insert(user);
//            JsonUtil.writer(response, "");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @RequestMapping("/phone/bind")
    public void bindPhone(String phone, String code, String userId) {

    }

    @RequestMapping("/phone/code")
    public void sendCode(String phone, HttpServletResponse response) {
        try {
            int errCode = smsSender.sendPhoneCode(phone);
            JsonUtil.toJsonMsg(response, errCode, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/login")
    public void login(String type, String access_token, Integer expires_in, HttpServletRequest request, HttpServletResponse response) throws Exception {
        User user = null;
        if ("QQ".equals(type)) {
            String openId = qqOauth2Service.getOpenID(access_token);
            user = userService.selectByQqOpenId(openId);
            if (user == null) {
                user = registerByQq(openId, access_token);
            }
        } else if ("WX".equals(type)) {
            //
        }
        String token = CodeHelper.getUUID();
        Cookie cookie1 = new Cookie("login_user", token);
        cookie1.setPath("/");
        Cookie cookie2 = new Cookie("login_user_id", user.getId().toString());
        cookie2.setPath("/");
        response.addCookie(cookie1);
        response.addCookie(cookie2);
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
