package net.ltsoftware.platform.usercenter.controller;

import net.ltsoftware.platform.usercenter.constant.ErrorCode;
import net.ltsoftware.platform.usercenter.constant.SmsConstants;
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("/user/reg/sendCode")
    public void sendCode(String phone, HttpServletResponse response) {
        try {
            int code = smsSender.sendPhoneCode(phone);
            JsonUtil.toJsonMsg(response, code,null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("/user/login")
    public void login(User user) {



    }



}
