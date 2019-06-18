package net.ltsoftware.usercenter.controller;

import com.alibaba.fastjson.JSON;
import net.ltsoftware.usercenter.model.Order;
import net.ltsoftware.usercenter.model.OrderExample;
import net.ltsoftware.usercenter.service.OrderService;
import net.ltsoftware.usercenter.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/order/add")
    public void addOrder(Order order, HttpServletResponse response) {
        try {
            orderService.insert(order);
            JsonUtil.writer(response, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/order/list")
    public void listOrder(OrderExample example, HttpServletResponse response) {
        try {
            List<Order> orderList = orderService.selectByExample(example);

            JsonUtil.writer(response, JSON.toJSONString(orderList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


