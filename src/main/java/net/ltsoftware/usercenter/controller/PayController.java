package net.ltsoftware.usercenter.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import net.ltsoftware.usercenter.config.MyWxpayConfig;
import net.ltsoftware.usercenter.constant.AlipayConstants;
import net.ltsoftware.usercenter.constant.ErrorCode;
import net.ltsoftware.usercenter.constant.MwxpayConstants;
import net.ltsoftware.usercenter.constant.WxpayConstants;
import net.ltsoftware.usercenter.pay.PaymentService;
import net.ltsoftware.usercenter.service.OrderService;
import net.ltsoftware.usercenter.service.UserService;
import net.ltsoftware.usercenter.util.HttpUtil;
import net.ltsoftware.usercenter.util.JsonUtil;
import net.ltsoftware.usercenter.util.RedisClient;
import net.ltsoftware.usercenter.util.YXSmsSender;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PayController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private YXSmsSender smsSender;

    @Autowired
    private PaymentService paymentServcie;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private HttpUtil httpUtil;

    private static Logger logger = LoggerFactory.getLogger(PayController.class);

    @GetMapping("/pay")
    public void pay(String tradeNo, Long amount, String payChannel, String clientIp,
                    String returnUrl, String notifyUrl, String openId, HttpServletResponse response) throws Exception {

        switch (payChannel) {
            case AlipayConstants.CHANNEL_NAME:
                String payPage = paymentServcie.getAlipayPage(tradeNo,amount);
                redisClient.setex(tradeNo+AlipayConstants.KEY_RETURN_URL_TAIL,AlipayConstants.PAY_WAIT_TIMEOUT,returnUrl);
                redisClient.setex(tradeNo+AlipayConstants.KEY_NOTIFY_URL_TAIL,AlipayConstants.PAY_WAIT_TIMEOUT,notifyUrl);
//                HttpResponseUtil.write(response,payPage);
                if(payPage!=null){
                    JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, payPage);
                }else{
                    JsonUtil.toJsonMsg(response, ErrorCode.PAY_URL_FAIL, null);
                }
                break;
            case WxpayConstants.CHANNEL_NAME:
                String payUrl = paymentServcie.getWxpayUrl(tradeNo,amount,clientIp);
                redisClient.setex(tradeNo+WxpayConstants.KEY_NOTIFY_URL_TAIL,WxpayConstants.PAY_WAIT_TIMEOUT,notifyUrl);
//                HttpResponseUtil.write(response,payUrl);
                if(payUrl!=null){
                    JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, payUrl);
                }else{
                    JsonUtil.toJsonMsg(response, ErrorCode.PAY_URL_FAIL, null);
                }
                break;
            case MwxpayConstants.CHANNEL_NAME:
                String prepayId = paymentServcie.getMwxpayPrepayId(tradeNo,amount,clientIp,openId);
                if(prepayId!=null){
                    JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, prepayId);
                }else{
                    JsonUtil.toJsonMsg(response, ErrorCode.PAY_URL_FAIL, null);
                }
                break;
        }

    }


//    @RequestMapping("/pay/alipay/charge")
//    public void aliCharge(Long userId, Integer amount, HttpServletResponse response) throws AlipayApiException, IOException {
//        String payPage = paymentServcie.getAlipayPage(amount, userId);
////        JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, chargePage);
//        HttpResponseUtil.write(response,payPage);
//
//    }

//    @RequestMapping("/pay/wxpay/charge")
//    public void wxCharge(Long userId, Integer amount, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String ipAddresses = request.getHeader("X-Real-IP");
////        logger.info("ip : " + ipAddresses);
//        String chargeUrl = paymentServcie.getWxpayUrl(amount, userId, ipAddresses);
//        JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, chargeUrl);
//    }

    //支付宝异步通知
    @RequestMapping("/pay/alipay/notify")
    public void aliNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        /*
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                AlipayConstants.ALI_PUB_KEY,
                AlipayConstants.CHARSET,
                AlipayConstants.SIGN_TYPE); //调用SDK验证签名

        logger.info("alipay notify : " + signVerified);

        //——请在这里编写您的程序（以下代码仅作参考）——

        实际验证过程建议商户务必添加以下校验：
        1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
        2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
        3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
        4、验证app_id是否为该商户本身。

        if (signVerified) {//验证成功
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

            logger.info("trade status : " + trade_status);

            if (trade_status.equals("TRADE_FINISHED")) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
            } else if (trade_status.equals("TRADE_SUCCESS")) {
                //判断该笔订单是否在商户网站中已经做过处理
                //如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                //如果有做过处理，不执行商户的业务程序

                //注意：
                //付款完成后，支付宝系统发送该交易状态通知
                Order order = orderService.selectByTradeNo(out_trade_no);
                userService.addBalance(order.getUserId(), order.getOrigAmount().intValue());
            }

//            out.println("success");

        } else {//验证失败
//            out.println("fail");

            //调试用，写文本函数记录程序运行情况是否正常
            //String sWord = AlipaySignature.getSignCheckContentV1(params);
            //AlipayConfig.logResult(sWord);
        }
        //*/
    }

    //支付宝同步跳转
    @RequestMapping("/pay/alipay/return")
    public void aliReturn(HttpServletRequest request, HttpServletResponse response) throws AlipayApiException, IOException {
        //获取支付宝GET过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            logger.info("name:"+name+", valueStr:"+valueStr);
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            logger.info("name:"+name+", valueStr:"+valueStr);
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                AlipayConstants.ALI_PUB_KEY,
                AlipayConstants.CHARSET,
                AlipayConstants.SIGN_TYPE); //调用SDK验证签名

        logger.info("alipay return : " + signVerified);

        //——请在这里编写您的程序（以下代码仅作参考）——
        if (signVerified) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");

            String returnUrl = redisClient.get(out_trade_no+AlipayConstants.KEY_RETURN_URL_TAIL);
            if(returnUrl==null){
                logger.error("cannot find return url in cache.");
                return;
            }

//            List<NameValuePair> paralist = new ArrayList<>();
//            paralist.add(new BasicNameValuePair("tradeNo",out_trade_no));
//            paralist.add(new BasicNameValuePair("tradeNo3rd",trade_no));
//            paralist.add(new BasicNameValuePair("amount",total_amount));
            returnUrl = returnUrl+"?tradeNo="+out_trade_no+"&tradeNo3rd="+trade_no+"&amount="+total_amount;
            logger.info("sendRedirect:"+returnUrl);
            response.sendRedirect(returnUrl);

        } else {
            logger.error("验签失败"+request.toString());
        }
//        JsonUtil.toJsonMsg(response, errCode, null);

    }

    @RequestMapping("/pay/wxpay/notify")
    public void wxpayNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BufferedReader reader =  request.getReader();
        String line ;
        StringBuilder inputString = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            inputString.append(line);
        }
        String notifyData = inputString.toString(); // 支付结果通知的xml格式数据
        logger.info("wxpay notify: "+notifyData);
        MyWxpayConfig config = new MyWxpayConfig();
        WXPay wxpay = new WXPay(config);
        Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyData);  // 转换成map
        //微信支付sdk这个接口有个缺陷，如果返回消息没有指明sign_type，sdk默认用md5签名，实际上api是按照HMACSHA256签的！MMP
        //这里修改了sdk的代码，把默认签名算法改为HMAC_SHA256
        boolean signatureValid = wxpay.isPayResultNotifySignatureValid(notifyMap);
        //for test
        //signatureValid = true;
        if (signatureValid) {
            logger.info("wxpay notify signature valid.");
            // 签名正确
            // 进行处理。
            // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
            //返回状态码
            String returnCode = notifyMap.get("return_code");
            //业务结果
            String resultCode = notifyMap.get("result_code");

            if(!WxpayConstants.NOTIFY_RETURN_SUCCESS.equals(returnCode)){
                logger.error("wxpay notify failed, return code:"+returnCode);
                return;
            }

            if(!WxpayConstants.NOTIFY_RESULT_SUCCESS.equals(resultCode)){
                logger.error("wxpay notify failed, result code:"+resultCode);
                return;
            }

            //商户订单号
            String outTradeNo = notifyMap.get("out_trade_no");
            //微信支付订单号
            String transactionId= notifyMap.get("transaction_id");
            //订单金额，单位为分
            String totalFee = notifyMap.get("total_fee");

            String notifyUrl = redisClient.get(outTradeNo+WxpayConstants.KEY_NOTIFY_URL_TAIL);
            if(notifyUrl==null){
                logger.error("cannot find return url in cache.");
                return;
            }

            List<NameValuePair> paralist = new ArrayList<>();
            paralist.add(new BasicNameValuePair("tradeNo",outTradeNo));
            paralist.add(new BasicNameValuePair("tradeNo3rd",transactionId));
            paralist.add(new BasicNameValuePair("amount",totalFee));
            logger.info("send notify: "+notifyUrl);
            logger.info("tradeNo: "+outTradeNo);
            logger.info("tradeNo3rd: "+transactionId);
            logger.info("amount: "+totalFee);
            String getResponse = httpUtil.get(notifyUrl,paralist);
            logger.info("http get response: "+getResponse);
            JSONObject respJson = JSONObject.parseObject(getResponse);
            int errCode = respJson.getIntValue("code");
            if(ErrorCode.SUCCESS==errCode){
                logger.info("callback reply success");
                response.setContentType("application/xml");
                PrintWriter out = response.getWriter();
                out.print(WxpayConstants.NOTIFY_REPLY);
                out.flush();
                out.close();
            }else if(ErrorCode.PAY_AMOUNT_MISFIT==errCode){
                logger.error("pay amount misfit, trade no: "+outTradeNo);
            }
        }
        else {
            logger.error("wxpay notify signature failed.");
            // 签名错误，如果数据里没有sign字段，也认为是签名错误
        }

    }

    public static void main(String[] args) throws Exception {
        String notifyData="<xml><appid><![CDATA[wxed61c321c91d5d92]]></appid><bank_type><![CDATA[CFT]]></bank_type><cash_fee><![CDATA[1]]></cash_fee><device_info><![CDATA[WEB]]></device_info><fee_type><![CDATA[CNY]]></fee_type><is_subscribe><![CDATA[N]]></is_subscribe><mch_id><![CDATA[1309846801]]></mch_id><nonce_str><![CDATA[JAgqfOFKTULMGcKf5M0OpK83YwT5NCCw]]></nonce_str><openid><![CDATA[oWvcPuHrXdhVYsKhR9oq39iIKQig]]></openid><out_trade_no><![CDATA[sk5q6swd_20190725145309]]></out_trade_no><result_code><![CDATA[SUCCESS]]></result_code><return_code><![CDATA[SUCCESS]]></return_code><sign><![CDATA[92FB5FB9461C48792FB14EED76CEF89E7C004A6E374399B2B769DBDB6CBA6013]]></sign><time_end><![CDATA[20190725145332]]></time_end><total_fee>1</total_fee><trade_type><![CDATA[NATIVE]]></trade_type><transaction_id><![CDATA[4200000353201907252813793937]]></transaction_id></xml>";
        MyWxpayConfig config = new MyWxpayConfig();
        WXPay wxpay = new WXPay(config);
        Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyData);
        System.out.println(notifyMap);
        boolean isValid = wxpay.isPayResultNotifySignatureValid(notifyMap);
        System.out.print(isValid);

    }

}
