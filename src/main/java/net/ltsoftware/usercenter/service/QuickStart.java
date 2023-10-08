package net.ltsoftware.usercenter.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
/** Native 支付下单为例 */
public class QuickStart {
    /** 商户号 */
//    public static String merchantId = "1652286075";
    public static String merchantId = "1649454671";


    /** 商户API私钥路径 */
//    public static String privateKeyPath = "/Users/ernest/Documents/LT_works/usercenter/src/main/resources/apiclient_key.pem";

    public static String privateKey = "7231d0955d2a4c85b2d038d39aa5588d";
    /** 商户证书序列号 */
    public static String merchantSerialNumber = "1478D38E95B9CA37228D1B2868ACEC734CA1F0EE";
    /** 商户APIV3密钥 */
    public static String apiV3key = "7231d0955d2a4c85b2d038d39aa5588d";
    public static void main(String[] args) {
//        try {
//            // 使用自动更新平台证书的RSA配置
//            // 建议将 config 作为单例或全局静态对象，避免重复的下载浪费系统资源
//            Config config =
//                    new RSAAutoCertificateConfig.Builder()
//                            .merchantId(merchantId)
////                            .privateKeyFromPath(privateKeyPath)
//                            .privateKey(privateKey)
//                            .merchantSerialNumber(merchantSerialNumber)
//                            .apiV3Key(apiV3key)
//                            .build();
//            // 构建service
//            NativePayService service = new NativePayService.Builder().config(config).build();
//            // request.setXxx(val)设置所需参数，具体参数可见Request定义
//            PrepayRequest request = new PrepayRequest();
//            Amount amount = new Amount();
//            amount.setTotal(100);
//            request.setAmount(amount);
//            request.setAppid("wxa9d9651ae******");
//            request.setMchid("190000****");
//            request.setDescription("测试商品标题");
//            request.setNotifyUrl("https://notify_url");
//            request.setOutTradeNo("out_trade_no_001");
//            // 调用下单方法，得到应答
//            PrepayResponse response = service.prepay(request);
//            // 使用微信扫描 code_url 对应的二维码，即可体验Native支付
//            System.out.println(response.getCodeUrl());
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
        String listPage = "[{\"routePactNo\":\"LT-_202309-07[3]\",\"routePactName\":\"海南3日\",\"financialMoney\":\"17000\",\"fukuanType\":\"11694\",\"totalPayMoney\":0,\"routePactId\":\"714\",\"distributionId\":\"45\",\"transactionTime\":\"2023-09-19\",\"transactionPerson\":\"旅通\",\"transactionRemark\":\"\"}]";
        JSONArray pageJson = JSONArray.parseArray(listPage);
        JSONArray simpleJson = new JSONArray();
        for (int i = 0; i < pageJson.size(); i++) {
            JSONObject jsonObject1 = pageJson.getJSONObject(i);
            JSONObject simpleJson1 = new JSONObject();
            simpleJson1.put("routePactNo",jsonObject1.getString("routePactNo"));
            simpleJson1.put("routePactName",jsonObject1.getString("routePactName"));
            simpleJson1.put("financialMoney",jsonObject1.getString("financialMoney"));
            simpleJson.add(simpleJson1);
        }
        System.out.println(simpleJson);
    }
}