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
import net.ltsoftware.usercenter.constant.WxpayConstants;
import net.ltsoftware.usercenter.model.Trade;
import net.ltsoftware.usercenter.pay.PaymentService;
import net.ltsoftware.usercenter.pay.WxPartnerPayService;
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
import java.util.*;

@Controller
public class UnifiedOrderController {

    @Autowired
    private WxPartnerPayService wxPartnerPayService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private HttpUtil httpUtil;

    private static Logger logger = LoggerFactory.getLogger(UnifiedOrderController.class);

    //@RequestMapping("/pay/unifiedOrder")
    @PostMapping(value = "/pay/unifiedOrder", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CrossOrigin
    public void unifiedOrder(@RequestBody String requestBody, HttpServletResponse httpServletResponse) {
        System.out.println(requestBody);
//        {"amount":"1500",
//        "busNotify":"http://192.168.20.122:8080//action/payAndReceive/ajaxAddReceiveMoneyRemote/",
//        "clientDomain":"kaifa",
//        "divisionMode":0,
//        "mchOrderNo":"467102693",
//        "orderList":[{"routePactNo":"*PWXA*QCSRY-2023-09-09[1]","routePactName":"【品味西安】汽车四日游","financialMoney":"1500","fukuanType":"11441","totalPayMoney":"1500.00","routePactId":"1367","distributionId":"77","transactionTime":"2023-10-07","transactionPerson":"王威","transactionRemark":"","transactionCertificate":"<p><\/p><p><br><\/p>"}],
//        "orderTitle":"","payDataType":"codeImgUrl","clientSystem":"XJL","subMchId":"1652286075"}
        try {
            JSONObject jsonObject = JSONObject.parseObject(requestBody);
            String amount = jsonObject.getString("amount");
            String busNotify = jsonObject.getString("busNotify");
            String clientDomain = jsonObject.getString("clientDomain");
            String divisionMode = jsonObject.getString("divisionMode");
            String mchOrderNo = jsonObject.getString("mchOrderNo");
            String orderList = jsonObject.getString("orderList");
            String orderTitle = jsonObject.getString("orderTitle");
            String payDataType = jsonObject.getString("payDataType");
            String softType = jsonObject.getString("softType");
            String subMchId = jsonObject.getString("subMchId");

//        String key = softType+"_"+clientDomain+"_"+mchOrderNo;
            String tradeNo = CodeHelper.getRandomString(10);
            String listPage = jsonObject.getString("orderList");
            System.out.println("listPage: " + listPage);

//        JSONObject jsonObject = HttpResponseUtil.convertParametersToJSONObject(request);
            //持久化
            Trade trade = new Trade();
//        trade.setAmount(Long.parseLong(amount));
            //tradeService.insert(trade);
//        trade.setTradeNo3rd();
//        JSONArray pageJson = JSONArray.parseArray(listPage);
//        JSONArray simpleJsonArray = new JSONArray();
//        for (int i = 0; i < pageJson.size(); i++) {
//            JSONObject orderJson = pageJson.getJSONObject(i);
//            JSONObject simpleJson = new JSONObject();
//            simpleJson.put("routePactNo", orderJson.getString("routePactNo"));
//            simpleJson.put("routePactName", orderJson.getString("routePactName"));
//            simpleJson.put("financialMoney", orderJson.getString("financialMoney"));
//            simpleJsonArray.add(simpleJson);
//        }
//        jsonObject.put("addData", listPage);
//        jsonObject.put("orderList");
            redisClient.set(tradeNo, jsonObject.toString());
//            System.out.println("listPage: " + listPage);

//        String pageUrl = "/confirmPay.html";
//        pageUrl += "?key=" + tradeNo;
//        pageUrl += "&amount=" + amount;
//        pageUrl += "&listPage=" + UrlEncoder.urlEncode(simpleJson.toString());
//        System.out.println("pageUrl: " + pageUrl);
            String imageUrl = "https://uc.ltsoftware.net/pay/page";
            imageUrl += "?key=" + tradeNo;
            JSONObject resultJson = new JSONObject();
            resultJson.put("imgUrl", imageUrl);
//            QrcodeUtil.createQrCode(httpServletResponse.getOutputStream(), imageUrl, 10, "png");
            JsonUtil.toJsonMsg(httpServletResponse, ErrorCode.SUCCESS, resultJson);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @RequestMapping("/pay/page")
    @CrossOrigin
    public void confirmPage(HttpServletRequest request, HttpServletResponse httpServletResponse){
        try{
            String tradeNo = request.getParameter("key");
            String jsonStr = redisClient.get(tradeNo);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            String amount = jsonObject.getString("amount");
            String listPage = jsonObject.getString("orderList");
            JSONArray pageJson = JSONArray.parseArray(listPage);
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
            System.out.println("pageUrl: " + pageUrl);
            httpServletResponse.sendRedirect(pageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    @RequestMapping("/pay3/page")
//    @CrossOrigin
//    public void confirmPage(HttpServletRequest request, HttpServletResponse httpServletResponse){
//        try {
////            String payChannel = request.getParameter("payChannel");
////            String subMchid = request.getParameter("subMchid");
////            String busNotify = request.getParameter("busNotify");
//            String amount = request.getParameter("amount");
//            String outTradeNo = request.getParameter("outTradeNo");
//            String orderListUrl = request.getParameter("orderListUrl");
//            String clientDomain = request.getParameter("clientDomain");
//            String softType = request.getParameter("softType");
//            JSONObject jsonObject = convertParametersToJSONObject(request);
//            String key = softType+"_"+clientDomain+"_"+outTradeNo;
//            System.out.println("key: " + key);
//            System.out.println("jsonObject: " + jsonObject.toString());
//
//            String listPage = httpUtil.get(orderListUrl,null);
//            System.out.println("listPage: " + listPage);
////            listPage = UrlEncoder.urlEncode(listPage);
////            listPage = HtmlUtils.htmlEscape(listPage);
//            JSONArray pageJson = JSONArray.parseArray(listPage);
//            JSONArray simpleJson = new JSONArray();
//            for (int i = 0; i < pageJson.size(); i++) {
//                JSONObject jsonObject1 = pageJson.getJSONObject(i);
//                JSONObject simpleJson1 = new JSONObject();
//                simpleJson1.put("routePactNo",jsonObject1.getString("routePactNo"));
//                simpleJson1.put("routePactName",jsonObject1.getString("routePactName"));
//                simpleJson1.put("financialMoney",jsonObject1.getString("financialMoney"));
//                simpleJson.add(simpleJson1);
//            }
//            jsonObject.put("addData",listPage);
//            redisClient.set(key,jsonObject.toString());
////            System.out.println("listPage: " + listPage);
//            String pageUrl = "/confirmPay.html";
//            pageUrl+="?key="+key;
//            pageUrl+="&amount="+amount;
//            pageUrl+="&listPage="+UrlEncoder.urlEncode(simpleJson.toString());
//            System.out.println("pageUrl: " + pageUrl);
//            httpServletResponse.sendRedirect(pageUrl);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @RequestMapping("/pay3")
//    @CrossOrigin
//    public void pay3(HttpServletRequest request, HttpServletResponse httpServletResponse){
//        try {
//            String payChannel = request.getParameter("payChannel");
//            String key = request.getParameter("key");
//            System.out.println("key: " + key);
//            switch (payChannel) {
//                case WxpayConstants.CHANNEL_MP:
//                    String callback = "https://uc.ltsoftware.net/pay3/wxpay/jsapi?key="+UrlEncoder.urlEncode(key);
////                             + "?redirectUrl="+ UrlEncoder.urlEncode(redirectUrl)
//                    String wxPayUrl = "http://ads.sanshak.com/action/ads/getWxOpenid2/?callback="
//                            + UrlEncoder.urlEncode(callback);
//                    System.out.println("wxPayUrl: " + wxPayUrl);
//                    httpServletResponse.sendRedirect(wxPayUrl);
//                    break;
//            }
//
//        }catch (Exception e) {
//            logger.error("pay3", e);
//        }
//
//    }
//
//    @RequestMapping("/pay3/wxpay/jsapi")
//    @CrossOrigin
//    public void wxPartnerPayJsapi(HttpServletRequest request, HttpServletResponse httpServletResponse){
//        try {
//        String openid = request.getParameter("openid");
//        String key = request.getParameter("key");
//        System.out.println("key: " + key);
//        String jsonString = redisClient.get(key);
//        System.out.println("jsonString: " + jsonString);
//        JSONObject json = JSONObject.parseObject(jsonString);
////        String successUrl = request.getParameter("redirectUrl");
//        PrepayWithRequestPaymentResponse response = wxPartnerPayService.prepayWithRequestPayment(openid,key,json);
//        String respStr = response.toString();
//        System.out.println(respStr);
////        System.out.println("successUrl: " +successUrl);
//        System.out.println("openid: " +openid);
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("appId", response.getAppId());
//        jsonObject.put("timeStamp", response.getTimeStamp());
//        jsonObject.put("nonceStr", response.getNonceStr());
//        jsonObject.put("package", response.getPackageVal());
//        jsonObject.put("signType", response.getSignType());
//        jsonObject.put("paySign", response.getPaySign());
////        JsonUtil.toJsonMsg(httpServletResponse,ErrorCode.SUCCESS,jsonObject);
////        redirectUrl+="?data="+jsonObject.toString();
//
//            String redirectUrl = "/wxmpPay.html";
//            redirectUrl+="?isWeChatVisit=true";
//            redirectUrl+="&data="+UrlEncoder.urlEncode(jsonObject.toJSONString());
////            redirectUrl+="&successUrl="+UrlEncoder.urlEncode(successUrl);
////            request.setAttribute("url",jsonObject.toString());
////            request.setAttribute("isWeChatVisit",true);
//            System.out.println("redirectUrl: " +redirectUrl);
//            httpServletResponse.sendRedirect(redirectUrl);
//        } catch (Exception e) {
//            logger.error("wxPartnerPayJsapi", e);
//        }
//
//    }
//    @RequestMapping("/pay3/wxpay/jsapi/notify")
//    @CrossOrigin
//    public void wxPartnerPayJsapiNotify(HttpServletRequest request, HttpServletResponse httpServletResponse){
//        //{"summary":"支付成功",
//        // "event_type":"TRANSACTION.SUCCESS",
//        // "create_time":"2023-09-18T15:29:46+08:00",
//        // "resource":{"original_type":"transaction",
//        // "algorithm":"AEAD_AES_256_GCM",
//        // "ciphertext":"TS9yC6KnxX43tU272OrQeErp4KY6j734FZCXXxbu/tdh0+OHDMvTugkxIEVhOhNtnWCNb/RjadZDpcghO3Ian/lzFi0YyeGxb+1OosNk/utDSmKsi4ZDVE72Gdv1jMohRdFzJ4JaCRrYGR2UUuA3uBpp9DcaKKuq1iT5kt3fIkT7umHQ6H2m4PCm8bjoNZ1ZKCHsOVu3bINAV1QJZktW08WmAHuUcpydLu0iF9blkrK2QoDOSU1F8yrnUWWzjL1hkq5K7DZF6W9UZ/cLAoEF5KYqVdU2vX/MXln2cOl8HNetlmnI8ai19A9aMF8sZC2DKclJ1TcDuuZhwZeQjHp4p5wgi/ulH3VODm5+x/8736iXhFNTzhY6UW74ZtnGDJ+nBEBR5NtOMI0v+Fq54579Kuiw7AaRD982+GQtfCr89gc/Z8FiJAL5l7OT3htoWT3KQhJ53dE8N3NEY5LI8LlgXNtUtQsP2/5nIo+bYgNkkfGOArndfZ0ag/JzuUfKMe1wxbTaRPwNvVVf4S9aJsmG2ka2pzBGvPdH+kDvuo6RxBZh6z+dcEPRKxioqc1Wk9aPFOkP8v4GIw7hZAVsV2sBMeDxB0p5mk5yVD2HTItiekvMNplniRhcj0BpmCdg4zGwpyVYsabOCqP6U3tI+TO+O6anICB4G2tKGdFB9QBqbkconeiG0O2Z40BPfAawDtS2NoVY19P/9hMzCG3azhDb2XA=",
//        // "associated_data":"transaction","nonce":"8vBACRCOU66x"},
//        // "resource_type":"encrypt-resource","id":"f6d3e392-7a32-5e7a-97d7-12a343048b1c"}
//        try {
//            Transaction transaction = wxPartnerPayService.getTransaction(request);
//            String key = transaction.getOutTradeNo();
//            String result = redisClient.get(key);
//            JSONObject json = JSONObject.parseObject(result);
//            String busNotifyUrl = json.getString("busNotify");
//            System.out.println("busNotifyUrl: " +busNotifyUrl);
//            HttpUtil httpUtil = new HttpUtil();
////            String resp = httpUtil.get(busNotifyUrl,null);
//            System.out.println("addData: " +json.getString("addData"));
////            String resp = httpUtil.post(busNotifyUrl,json.getString("addData"));
//            List<NameValuePair> list = new ArrayList<NameValuePair>();
//            list.add(new BasicNameValuePair("datas",json.getString("addData")));
//            list.add(new BasicNameValuePair("wxpay","uc"));
//            String resp = httpUtil.post(busNotifyUrl,list, "UTF-8");
//            System.out.println("resp: " +resp);
////            httpServletResponse.sendRedirect("http://ly.ltsoftware.net/action/website/listOrders/?t=jlkk");
//        } catch (Exception e) {
//            logger.error("wxPartnerPayJsapiNotify", e);
//        }
//    }
//
//    public JSONObject convertParametersToJSONObject(HttpServletRequest request) {
//        JSONObject jsonObject = new JSONObject();
//        Map<String, String[]> parameterMap = request.getParameterMap();
//
//        for (String paramName : parameterMap.keySet()) {
//            String[] paramValues = parameterMap.get(paramName);
//            if (paramValues.length == 1) {
//                jsonObject.put(paramName, paramValues[0]);
//            } else {
//                jsonObject.put(paramName, paramValues);
//            }
//        }
//
//        return jsonObject;
//    }


}
