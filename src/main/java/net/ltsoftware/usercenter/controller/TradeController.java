package net.ltsoftware.usercenter.controller;

import com.alibaba.fastjson.JSON;
import net.ltsoftware.usercenter.model.Trade;
import net.ltsoftware.usercenter.model.TradeExample;
import net.ltsoftware.usercenter.service.TradeService;
import net.ltsoftware.usercenter.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class TradeController {

    @Autowired
    private TradeService tradeService;

    @RequestMapping("/trade/add")
    public void addTrade(Trade Trade, HttpServletResponse response) {
        try {
            tradeService.insert(Trade);
            JsonUtil.writer(response, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/trade/list")
    public void listTrade(TradeExample example, HttpServletResponse response) {
        try {
            List<Trade> TradeList = tradeService.selectByExample(example);

            JsonUtil.writer(response, JSON.toJSONString(TradeList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


