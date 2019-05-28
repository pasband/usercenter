package net.ltsoftware.platform.usercenter.controller;

import net.ltsoftware.platform.model.User;
import net.ltsoftware.platform.service.UserService;
import net.ltsoftware.platform.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

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
    public void sendCode(User user, HttpServletResponse response) {
        try {
            userService.insert(user);
            JsonUtil.writer(response, "");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("/user/login")
    public void login(User user) {



    }



}
