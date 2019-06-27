package net.ltsoftware.usercenter.controller;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import net.ltsoftware.usercenter.constant.ErrorCode;
import net.ltsoftware.usercenter.constant.SmsConstants;
import net.ltsoftware.usercenter.model.User;
import net.ltsoftware.usercenter.oauth2.WxOauthService;
import net.ltsoftware.usercenter.service.UserService;
import net.ltsoftware.usercenter.util.CodeHelper;
import net.ltsoftware.usercenter.util.JsonUtil;
import net.ltsoftware.usercenter.util.RedisClient;
import net.ltsoftware.usercenter.util.YXSmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private YXSmsSender smsSender;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private WxOauthService wxOauthService;

    private static Logger logger = LoggerFactory.getLogger(LoginController.class);


    @RequestMapping("/user/info")
    @CrossOrigin(origins = "http://platform.ltsoftware.net", allowCredentials = "true")
    public void getUserInfo(User user, HttpServletResponse response) throws Exception {
        Long id = user.getId();
        user = userService.selectByPrimaryKey(id);
        JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, user);
    }

    @RequestMapping("/oauth/qqurl")
    public void getQqAuthUrl(HttpServletRequest request, HttpServletResponse response) throws QQConnectException, IOException {
        String authurl = new Oauth().getAuthorizeURL(request);
        logger.info(authurl);
        response.sendRedirect(authurl);
    }

    @RequestMapping("/oauth/qqcallback")
    @CrossOrigin(origins = "https://platform.ltsoftware.net", allowCredentials = "true")
    public void qqCallback(HttpServletRequest request, HttpServletResponse response) throws QQConnectException {

        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);
            String accessToken = null,
                    openID = null;
            long tokenExpireIn = 0L;

            if (accessTokenObj.getAccessToken().equals("")) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                logger.error("没有获取到响应参数");
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();

//                request.getSession().setAttribute("demo_access_token", accessToken);
//                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj = new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();


                User user = userService.selectByQqOpenId(openID);

                if (user == null) {
                    user = new User();
                    user.setBalance(0);
                    long id = userService.insert(user);
                    user.setId(id);
                }
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                String nickname = userInfoBean.getNickname();
                String avatar30 = userInfoBean.getAvatar().getAvatarURL30();

                user.setName(nickname);
                user.setQqOpenid(openID);
                user.setAvatar(avatar30);
                userService.updateByPrimaryKey(user);

                String token = CodeHelper.getUUID();
                Cookie cookie1 = new Cookie("login_user", token);
                cookie1.setDomain("ltsoftware.net");
                cookie1.setPath("/");
                Cookie cookie2 = new Cookie("login_user_id", String.valueOf(user.getId()));
                cookie2.setDomain("ltsoftware.net");
                cookie2.setPath("/");
                response.addCookie(cookie1);
                response.addCookie(cookie2);
                response.sendRedirect("https://platform.ltsoftware.net/home?id=" + user.getId());

            }
        } catch (QQConnectException e) {
            logger.error("qq connect error:", e);
        } catch (Exception e) {
            logger.error("qq connect unknown error:", e);
        }
    }


    @RequestMapping("/oauth/wxcallback")
    public void wxCallback(String code, String state, HttpServletResponse response) throws Exception {
        //redirect_uri?code=CODE&state=STATE
        //check state
        Map<String, Object> result = wxOauthService.getToken(code, state);
        String accessToken = result.get("access_token").toString();
        String tokenExpireIn = result.get("expires_in").toString();
        String refreshToken = result.get("refresh_token").toString();
        String openId = result.get("openid").toString();
        String unionId = result.get("unionid").toString();
//        logger.info("wxcallback, token:"+result);
        User user = userService.selectByWxOpenId(openId);
        if (user == null) {
            user = new User();
            user.setBalance(0);
            long id = userService.insert(user);
            user.setId(id);
        }
        Map<String, Object> wxUserinfo = wxOauthService.getWxUserinfo(accessToken, openId);
        String name = wxUserinfo.get("nickname").toString();
        String avatar = wxUserinfo.get("headimgurl").toString();
        user.setName(name);
        user.setAvatar(avatar);
        userService.updateByPrimaryKey(user);

        String token = CodeHelper.getUUID();
        Cookie cookie1 = new Cookie("login_user", token);
        cookie1.setDomain("ltsoftware.net");
        cookie1.setPath("/");
        Cookie cookie2 = new Cookie("login_user_id", String.valueOf(user.getId()));
        cookie2.setDomain("ltsoftware.net");
        cookie2.setPath("/");
        response.addCookie(cookie1);
        response.addCookie(cookie2);
        response.sendRedirect("http://platform.ltsoftware.net/home?id=" + user.getId());

    }

    @RequestMapping("/oauth/wxurl")
    public void getWxOauthUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String result = wxOauthService.getUrl();
//        JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, result);
        response.sendRedirect(result);
    }


    @RequestMapping("/phone/bind")
    public void bindPhone(String phone, String code, String userId, HttpServletResponse response) throws Exception {
        int errCode = -1;
        String codeInCache = redisClient.get(SmsConstants.PREFIX + SmsConstants.CODE + phone);
        if (codeInCache == null || codeInCache.equals(code)) {
            errCode = ErrorCode.PHONE_CODE_WRONG;
        } else {
            User user = userService.selectByPrimaryKey(Long.parseLong(userId));
            user.setPhone(phone);
            user.setStatus("3");
            userService.updateByPrimaryKey(user);
            errCode = ErrorCode.SUCCESS;
        }
        JsonUtil.toJsonMsg(response, errCode, null);
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

}
