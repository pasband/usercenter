package net.ltsoftware.platform.usercenter.controller;

import net.ltsoftware.platform.model.Order;
import net.ltsoftware.platform.service.OrderService;
import net.ltsoftware.platform.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/order/add")
    public void addOrder(Order order, HttpServletResponse response) {
        try {
            orderService.insert(order);
            JsonUtil.writer(response, "");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("/order/list")
    public void listOrder(Order order, HttpServletResponse response) {
        try {

            JsonUtil.writer(response, "");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}


