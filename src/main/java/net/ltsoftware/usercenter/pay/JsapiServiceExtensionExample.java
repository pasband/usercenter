package net.ltsoftware.usercenter.pay;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.exception.HttpException;
import com.wechat.pay.java.core.exception.MalformedMessageException;
import com.wechat.pay.java.core.exception.ServiceException;
import com.wechat.pay.java.service.partnerpayments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.*;
import net.ltsoftware.usercenter.support.wxmp.WeixinMpConstants;
import net.ltsoftware.usercenter.util.CodeHelper;

public class JsapiServiceExtensionExample {

    /** 商户号 */
//    public static String merchantId = "190000****";
//    /** 商户API私钥路径 */
//    public static String privateKeyPath = "/Users/yourname/your/path/apiclient_key.pem";
//    /** 商户证书序列号 */
//    public static String merchantSerialNumber = "5157F09EFDC096DE15EBE81A47057A72********";
//    /** 商户APIV3密钥 */
//    public static String apiV3Key = "...";

    public static String merchantId = "1649454671";
    /** 商户API私钥路径 */
    public static String privateKeyPath = "/Users/ernest/Documents/LT_works/usercenter/src/main/resources/apiclient_key.pem";
    /** 商户证书序列号 */
    public static String merchantSerialNumber = "2DA543568AD62C31089D5ED947B932BA762CBA8E";
    /** 商户APIV3密钥 */
    public static String apiV3Key = "7231d0955d2a4c85b2d038d39aa5588d";

    public static JsapiServiceExtension service;

    public static void main(String[] args) {
        // 初始化商户配置
        Config config =
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
        try {
            PrepayWithRequestPaymentResponse response = prepayWithRequestPayment();
            System.out.println(response);
        } catch (HttpException e) { // 发送HTTP请求失败
            // 调用e.getHttpRequest()获取请求打印日志或上报监控，更多方法见HttpException定义
        } catch (ServiceException e) { // 服务返回状态小于200或大于等于300，例如500
            // 调用e.getResponseBody()获取返回体打印日志或上报监控，更多方法见ServiceException定义
        } catch (MalformedMessageException e) { // 服务返回成功，返回体类型不合法，或者解析返回体失败
            // 调用e.getMessage()获取信息打印日志或上报监控，更多方法见MalformedMessageException定义
        }
    }
    /** 关闭订单 */
    public static void closeOrder() {

        CloseOrderRequest request = new CloseOrderRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        // 调用接口
        service.closeOrder(request);
    }
    /** JSAPI支付下单，并返回JSAPI调起支付数据 */
    public static PrepayWithRequestPaymentResponse prepayWithRequestPayment() {
        // 商户申请的公众号对应的appid，由微信支付生成，可在公众号后台查看
//        String requestPaymentAppid = "test-request-payment-appid";
//        PrepayRequest request = new PrepayRequest();
//        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
//        request.setSpAppid("test-sp-appid");
        PrepayRequest request = new PrepayRequest();
        request.setSpAppid(WeixinMpConstants.WEIXIN_MP_APPID);
        request.setSpMchid("1649454671");
        request.setSubMchid("1652286075");
        request.setDescription("特约商户支付测试");
        request.setOutTradeNo("test_"+ CodeHelper.getRandomString(10));
        request.setNotifyUrl("https://uc.ltsoftware.net/");
        Amount fen = new Amount();
        fen.setTotal(1);
        request.setAmount(fen);
        Payer payer = new Payer();
        payer.setSpOpenid("oWvcPuHrXdhVYsKhR9oq39iIKQig");
        request.setPayer(payer);

        // 调用接口
        return service.prepayWithRequestPayment(request, WeixinMpConstants.WEIXIN_MP_APPID);
    }
    /** 微信支付订单号查询订单 */
    public static Transaction queryOrderById() {

        QueryOrderByIdRequest request = new QueryOrderByIdRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        // 调用接口
        return service.queryOrderById(request);
    }
    /** 商户订单号查询订单 */
    public static Transaction queryOrderByOutTradeNo() {

        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        // 调用request.setXxx(val)设置所需参数，具体参数可见Request定义
        // 调用接口
        return service.queryOrderByOutTradeNo(request);
    }
}