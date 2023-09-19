package net.ltsoftware.usercenter.pay;

import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.partnerpayments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.*;
import net.ltsoftware.usercenter.constant.WxpayConstants;
import net.ltsoftware.usercenter.controller.LoginController;
import net.ltsoftware.usercenter.support.wxmp.WeixinMpConstants;
import net.ltsoftware.usercenter.util.CodeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

@Service
public class WxPartnerPayService {

    public static String merchantId = "1649454671";
    /** 商户API私钥路径 */
    public static String privateKeyPath = "D:/xuyn/wxpay/apiclient_key.pem";
//    public static String privateKeyPath = "/Users/ernest/Documents/LT_works/usercenter/src/main/resources/apiclient_key.pem";
    /** 商户证书序列号 */
    public static String merchantSerialNumber = "2DA543568AD62C31089D5ED947B932BA762CBA8E";
    /** 商户APIV3密钥 */
    public static String apiV3Key = "7231d0955d2a4c85b2d038d39aa5588d";

    public JsapiServiceExtension service;

    private static Logger logger = LoggerFactory.getLogger(WxPartnerPayService.class);

    public WxPartnerPayService(){init();}

    private RSAAutoCertificateConfig config;
    private void init(){

            // 初始化商户配置
            config =
                    new RSAAutoCertificateConfig.Builder()
                            .merchantId(merchantId)
                            // 使用 com.wechat.pay.java.core.util 中的函数从本地文件中加载商户私钥，商户私钥会用来生成请求的签名
                            .privateKeyFromPath(privateKeyPath)
                            .merchantSerialNumber(merchantSerialNumber)
                            .apiV3Key(apiV3Key)
                            .build();
            // 初始化服务
            service =
                    new JsapiServiceExtension.Builder()
                            .config(config)
                            .signType("RSA") // 不填则默认为RSA
                            .build();

    }

    /** JSAPI支付下单，并返回JSAPI调起支付数据 */
    public PrepayWithRequestPaymentResponse prepayWithRequestPayment(String openid, String key, JSONObject jsonObject) {
        // 商户申请的公众号对应的appid，由微信支付生成，可在公众号后台查看
//        String requestPaymentAppid = "test-request-payment-appid";
//        PrepayRequest request = new PrepayRequest();
//        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
//        request.setSpAppid("test-sp-appid");
        String subMchid = jsonObject.getString("subMchid");
        int amount = jsonObject.getIntValue("amount");
//        String outTradeNo = jsonObject.getString("outTradeNo");
        PrepayRequest request = new PrepayRequest();
        request.setSpAppid(WeixinMpConstants.WEIXIN_MP_APPID);
        request.setSpMchid("1649454671");
//        request.setSubMchid("1652286075");
        request.setSubMchid(subMchid);
        request.setDescription("特约商户支付测试");
//        request.setOutTradeNo("test_"+ CodeHelper.getRandomString(10));
        request.setOutTradeNo(key);
        request.setNotifyUrl("https://uc.ltsoftware.net/pay3/wxpay/jsapi/notify");
        Amount fen = new Amount();
        fen.setTotal(amount);
        request.setAmount(fen);
        Payer payer = new Payer();
        payer.setSpOpenid(openid);
        request.setPayer(payer);
        // 调用接口
        return service.prepayWithRequestPayment(request, WeixinMpConstants.WEIXIN_MP_APPID);
    }

    public String getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        return requestBody.toString();
    }

    public Transaction getTransaction(HttpServletRequest request) throws IOException {
        logger.info("收到支付回调-------------");

        // 请求头Wechatpay-nonce
        String nonce = request.getHeader("Wechatpay-nonce");
        // 请求头Wechatpay-Timestamp
        String timestamp = request.getHeader("Wechatpay-Timestamp");
        // 请求头Wechatpay-Signature
        String signature = request.getHeader("Wechatpay-Signature");
        // 微信支付证书序列号 TODO 这里千万不能搞错，不能是商家证书序列号！！！
        String serial = request.getHeader("Wechatpay-Serial");
        // 签名方式
        String signType = request.getHeader("Wechatpay-Signature-Type");

        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(serial) // TODO 不能搞错！！！
                .nonce(nonce)
                .signature(signature)
                .timestamp(timestamp)
                .signType(signType)
                .body(getRequestBody(request))
                .build();

        // 初始化 NotificationParser
        NotificationParser parser = new NotificationParser(config);

        // 以支付通知回调为例，验签、解密并转换成 Transaction
        logger.info("验签参数：" + requestParam);
        Transaction transaction = parser.parse(requestParam, Transaction.class);
        logger.info("验签成功！-支付回调结果：" + transaction.toString());
        Transaction.TradeStateEnum state = transaction.getTradeState();
        if (state == Transaction.TradeStateEnum.SUCCESS) {
            logger.info("支付成功！");
            return transaction;
        } else {
            logger.info("支付失败！");
            return null;
        }

//        2023-09-18 16:11:58.874  INFO 29440 --- [io-12001-exec-5] n.l.usercenter.pay.WxPartnerPayService   : 验签成功！-支付回调结果：class Transaction {
//            amount: class TransactionAmount {
//                currency: CNY
//                payerCurrency: CNY
//                payerTotal: 1
//                total: 1
//            }
//            spAppid: wxed61c321c91d5d92
//            subAppid: wxed61c321c91d5d92
//            spMchid: 1649454671
//            subMchid: 1652286075
//            attach:
//            bankType: CMB_CREDIT
//            outTradeNo: test_jy45w99yxm
//            payer: class TransactionPayer {
//                spOpenid: oWvcPuHrXdhVYsKhR9oq39iIKQig
//                subOpenid: oWvcPuHrXdhVYsKhR9oq39iIKQig
//            }
//            promotionDetail: null
//            successTime: 2023-09-18T16:07:55+08:00
//            tradeState: SUCCESS
//            tradeStateDesc: 支付成功
//            tradeType: JSAPI
//            transactionId: 4200001894202309189721938699
//        }

    }

    public Transaction queryOrderById() {

        QueryOrderByIdRequest request = new QueryOrderByIdRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        // 调用接口
        return service.queryOrderById(request);
    }
    /** 商户订单号查询订单 */
    public Transaction queryOrderByOutTradeNo() {

        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        // 调用接口
        return service.queryOrderByOutTradeNo(request);
    }


}
