package net.ltsoftware.usercenter.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import net.ltsoftware.usercenter.constant.AlipayConstants;
import net.ltsoftware.usercenter.constant.WxpayConstants;
import net.ltsoftware.usercenter.model.Order;
import net.ltsoftware.usercenter.pay.PaymentService;
import net.ltsoftware.usercenter.service.OrderService;
import net.ltsoftware.usercenter.service.UserService;
import net.ltsoftware.usercenter.util.HttpResponseUtil;
import net.ltsoftware.usercenter.util.HttpUtil;
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
import java.io.IOException;
import java.util.*;

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
                    String returnUrl, String notifyUrl, HttpServletResponse response) throws Exception {

        switch (payChannel) {
            case AlipayConstants.CHANNEL_NAME:
                String payPage = paymentServcie.getAlipayPage(tradeNo,amount);
                redisClient.set(tradeNo+AlipayConstants.RETURN_URL,returnUrl);
                redisClient.set(tradeNo+AlipayConstants.NOTIFY_URL,notifyUrl);
                HttpResponseUtil.write(response,payPage);
                break;
            case WxpayConstants.CHANNEL_NAME:
                String chargeUrl = paymentServcie.getWxpayUrl(tradeNo,amount,clientIp);
                redisClient.set(tradeNo+WxpayConstants.NOTIFY_URL,notifyUrl);
                HttpResponseUtil.write(response,chargeUrl);
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

	/* 实际验证过程建议商户务必添加以下校验：
	1、需要验证该通知数据中的out_trade_no是否为商户系统中创建的订单号，
	2、判断total_amount是否确实为该订单的实际金额（即商户订单创建时的金额），
	3、校验通知中的seller_id（或者seller_email) 是否为out_trade_no这笔单据的对应的操作方（有的时候，一个商户可能有多个seller_id/seller_email）
	4、验证app_id是否为该商户本身。
	*/
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
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
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

            List<NameValuePair> paralist = new ArrayList<>();
            paralist.add(new BasicNameValuePair("tradeNo",out_trade_no));
            paralist.add(new BasicNameValuePair("tradeNo3rd",trade_no));
            paralist.add(new BasicNameValuePair("amount",total_amount));

            String result = httpUtil.get(returnUrl,paralist);
            logger.info("recall: "+returnUrl+", result: "+result);

        } else {

            logger.error("验签失败"+request.toString());
        }
//        JsonUtil.toJsonMsg(response, errCode, null);

    }

    @RequestMapping("/pay/wxpay/notify")
    public void wxpayNotify(HttpServletRequest request, HttpServletResponse response) {


    }

}
