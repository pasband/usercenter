package net.ltsoftware.usercenter.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.wechat.pay.java.core.http.UrlEncoder;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import net.ltsoftware.usercenter.config.MyWxpayConfig;
import net.ltsoftware.usercenter.constant.AlipayConstants;
import net.ltsoftware.usercenter.constant.ErrorCode;
import net.ltsoftware.usercenter.constant.PayOrderConstants;
import net.ltsoftware.usercenter.constant.WxpayConstants;
import net.ltsoftware.usercenter.model.PayOrder;
import net.ltsoftware.usercenter.model.Trade;
import net.ltsoftware.usercenter.pay.PaymentService;
import net.ltsoftware.usercenter.pay.WxPartnerPayService;
import net.ltsoftware.usercenter.service.PayOrderService;
import net.ltsoftware.usercenter.service.TradeService;
import net.ltsoftware.usercenter.service.UserService;
import net.ltsoftware.usercenter.util.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;

@Controller
public class UnifiedOrderController {

    @Autowired
    private WxPartnerPayService wxPartnerPayService;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private HttpUtil httpUtil;

    private static Logger logger = LoggerFactory.getLogger(UnifiedOrderController.class);

    //@RequestMapping("/pay/unifiedOrder")
    @PostMapping(value = "/pay/unifiedOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    public void unifiedOrder(@RequestBody String requestBody, HttpServletResponse httpServletResponse) {
        logger.info(requestBody);
        try {
            JSONObject jsonObject = JSONObject.parseObject(requestBody);
            String mchOrderNo = savePayOrder(jsonObject);
//            redisClient.set(mchOrderNo, jsonObject.toString());
            String imageUrl = "https://uc.ltsoftware.net/pay/page";
            imageUrl += "?key=" + mchOrderNo;
            JSONObject resultJson = new JSONObject();
            resultJson.put("imgUrl", imageUrl);
//            QrcodeUtil.createQrCode(httpServletResponse.getOutputStream(), imageUrl, 10, "png");
            JsonUtil.toJsonMsg(httpServletResponse, ErrorCode.SUCCESS, resultJson);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/pay/checkOrder")
    @CrossOrigin
    public void checkOrder(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        String mchOrderNo = request.getParameter("mchOrderNo");
        PayOrder payOrder = payOrderService.selectByMchOrderNo(mchOrderNo);
//        System.out.println(payOrder);
        if(payOrder!=null) {
            Byte state = payOrder.getState();
            Byte notifyState = payOrder.getNotifyState();
            JSONObject resultJson = new JSONObject();
            resultJson.put("mchOrderNo", mchOrderNo);
            resultJson.put("state", state);
            resultJson.put("notifyState", notifyState);
            JsonUtil.toJsonMsg(httpServletResponse, ErrorCode.SUCCESS, resultJson);
        }
        JsonUtil.toJsonMsg(httpServletResponse, -1, null);
    }

    private String savePayOrder(JSONObject jsonObject){
        Long amount = jsonObject.getLong("amount");
        String busNotify = jsonObject.getString("busNotify");
        String clientDomain = jsonObject.getString("clientDomain");
        String divisionMode = jsonObject.getString("divisionMode");
//            String mchOrderNo = jsonObject.getString("mchOrderNo");
        String orderList = jsonObject.getString("orderList");
        String orderTitle = jsonObject.getString("orderTitle");
        String payDataType = jsonObject.getString("payDataType");
        String softType = jsonObject.getString("clientSystem");
        String subMchId = jsonObject.getString("subMchId");
        String mchOrderNo = "P"+System.currentTimeMillis()+CodeHelper.getRandomNum(3);
//        String listPage = jsonObject.getString("orderList");
        logger.info("orderList: " + orderList);
        //持久化
        PayOrder payOrder = new PayOrder();
        payOrder.setAmount(amount);
        payOrder.setClientIp(clientDomain);
        payOrder.setDivisionMode(PayOrderConstants.DIVISION_MODE_FORBID);
        payOrder.setNotifyUrl(busNotify);
        payOrder.setMchOrderNo(mchOrderNo);
        payOrder.setSubject(orderTitle);
        payOrder.setSubMchId(subMchId);
        payOrder.setMchOrderList(orderList);
        payOrder.setSofttype(softType);
        payOrder.setState(PayOrderConstants.STATE_INIT);
        payOrderService.insert(payOrder);
        return mchOrderNo;
    }

    @RequestMapping("/pay/page")
    @CrossOrigin
    public void confirmPage(HttpServletRequest request, HttpServletResponse httpServletResponse){
        try{
            String tradeNo = request.getParameter("key");
//            String jsonStr = redisClient.get(tradeNo);
//            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            logger.info("tradeNo: " + tradeNo);
            PayOrder payOrder = payOrderService.selectByMchOrderNo(tradeNo);
            if(payOrder == null){
                logger.error("payOrder not exist.");
                return;
            }
            long amount = payOrder.getAmount();
            String listPage = payOrder.getMchOrderList();
            logger.info("listPage: " + listPage);
            JSONArray pageJson = JSONArray.parseArray(listPage);
            logger.info("pageJson: " + pageJson);
            JSONArray simpleJsonArray = new JSONArray();
            for (int i = 0; i < pageJson.size(); i++) {
                JSONObject orderJson = pageJson.getJSONObject(i);
                JSONObject simpleJson = new JSONObject();
                simpleJson.put("routePactNo", orderJson.getString("routePactNo"));
                simpleJson.put("routePactName", orderJson.getString("routePactName"));
                simpleJson.put("financialMoney", orderJson.getString("financialMoney"));
                simpleJsonArray.add(simpleJson);
            }
            String pageUrl = "/confirmPay.html";
            pageUrl+="?key="+tradeNo;
            pageUrl+="&amount="+amount;
            pageUrl+="&listPage="+UrlEncoder.urlEncode(simpleJsonArray.toString());
            logger.info("pageUrl: " + pageUrl);
            httpServletResponse.sendRedirect(pageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequestMapping("/pay")
    @CrossOrigin
    public void pay3(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        try {
            String payChannel = request.getParameter("payChannel");
            String key = request.getParameter("key");
            logger.info("key: " + key);
            PayOrder payOrder = payOrderService.selectByMchOrderNo(key);
            Byte state = payOrder.getState();
            if(state==null||state!=PayOrderConstants.STATE_INIT){
                logger.error("PayOrder["+key+"] state:"+state);
                httpServletResponse.sendRedirect("/error.html");
                return;
            }
            switch (payChannel) {
                case WxpayConstants.CHANNEL_MP:
                    String callback = "https://uc.ltsoftware.net/pay/wxpay/jsapi?key=" + UrlEncoder.urlEncode(key);
//                             + "?redirectUrl="+ UrlEncoder.urlEncode(redirectUrl)
                    String wxPayUrl = "http://ads.sanshak.com/action/ads/getWxOpenid2/?callback="
                            + UrlEncoder.urlEncode(callback);
                    logger.info("wxPayUrl: " + wxPayUrl);
                    httpServletResponse.sendRedirect(wxPayUrl);
                    break;
            }

        } catch (Exception e) {
            logger.error("pay", e);
        }

    }


}
