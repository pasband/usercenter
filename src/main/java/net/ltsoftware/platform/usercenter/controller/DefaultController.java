package net.ltsoftware.platform.usercenter.controller;

import net.ltsoftware.platform.usercenter.util.JsonUtil;
import net.ltsoftware.platform.util.JsonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class DefaultController {

    @RequestMapping("/test")
    public void helloWorld(@RequestParam String id, HttpServletResponse response) {

      JsonUtil.writer(response,"test, id="+id);
    }

}
