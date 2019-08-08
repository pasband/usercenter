package net.ltsoftware.usercenter.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.github.wxpay.sdk.WXPay;
import net.ltsoftware.usercenter.config.MyWxpayConfig;
import net.ltsoftware.usercenter.constant.AlipayConstants;
import net.ltsoftware.usercenter.constant.MwxpayConstants;
import net.ltsoftware.usercenter.constant.WxpayConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    private AlipayClient alipayClient;


    private static Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService() {
        init();
    }

    private void init() {
        //AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do","app_id","your private_key","json","GBK","alipay_public_key","RSA2");
        alipayClient = new DefaultAlipayClient(
                AlipayConstants.PAY_URL,
                AlipayConstants.APP_ID,
                AlipayConstants.APP_PRI_KEY,
                AlipayConstants.FORMAT,
                AlipayConstants.CHARSET,
                AlipayConstants.ALI_PUB_KEY,
                AlipayConstants.SIGN_TYPE

        );


    }

//    public String getChargePage(String channel, Integer amount, Long userId) throws Exception {
//        switch (channel) {
//            case "alipay":
//                return alipayCharge(amount, userId);
//            case "weixin":
//                return weixinCharge(amount, userId);
//
//
//        }
//
//        return null;
//    }

    public String getMwxpayPrepayId(String tradeNo, Long amount,
                              String clientIp, String openId) throws Exception {
        MyWxpayConfig config = new MyWxpayConfig();
        WXPay wxpay = new WXPay(config);

        Map<String, String> data = new HashMap<>();
        data.put("body", "旅易软件服务购买");
        data.put("out_trade_no", tradeNo);
        data.put("device_info", openId);
        data.put("fee_type", "CNY");
        data.put("total_fee", "1");
        data.put("spbill_create_ip", clientIp);
        data.put("notify_url", WxpayConstants.NOTIFY_URL);
        data.put("trade_type", MwxpayConstants.TRADE_TYPE);  // 此处指定为扫码支付
        data.put("product_id", "12");
        data.put("openid",openId);

        Map<String, String> resp = wxpay.unifiedOrder(data);
        logger.info(resp.toString());
        String returnCode = resp.get("return_code");
        if(!WxpayConstants.NOTIFY_RETURN_SUCCESS.equals(returnCode)){
            logger.error("mwxpay get pay url failed, return msg: "+resp.get("return_msg"));
            return null;
        }
        String prepayId = resp.get("prepay_id");
        logger.info(prepayId);
        return prepayId;

    }

    public String getWxpayUrl(String tradeNo, Long amount,
                              String clientIp) throws Exception {
        MyWxpayConfig config = new MyWxpayConfig();
        WXPay wxpay = new WXPay(config);

        Map<String, String> data = new HashMap<>();
        data.put("body", "旅易软件服务购买");
        data.put("out_trade_no", tradeNo);
        data.put("device_info", "WEB");
        data.put("fee_type", "CNY");
        data.put("total_fee", "1");
        data.put("spbill_create_ip", clientIp);
        data.put("notify_url", WxpayConstants.NOTIFY_URL);
        data.put("trade_type", WxpayConstants.TRADE_TYPE);  // 此处指定为扫码支付
        data.put("product_id", "12");

        Map<String, String> resp = wxpay.unifiedOrder(data);
        logger.info(resp.toString());
        String returnCode = resp.get("return_code");
        if(!WxpayConstants.NOTIFY_RETURN_SUCCESS.equals(returnCode)){
            logger.error("wxpay get pay url failed, return msg: "+resp.get("return_msg"));
            return null;
        }
        String payurl = resp.get("code_url");

        logger.info(payurl);
//            QrcodeUtil.createQrCode(new FileOutputStream(new File("/usr/local/usercenter/qrcode.jpg")), payurl, 900, "JPEG");
        return payurl;

    }


    //alipay
    public String getAlipayPage(String tradeNo, Long amount) throws AlipayApiException {

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConstants.RETURN_URL);
        alipayRequest.setNotifyUrl(AlipayConstants.NOTIFY_URL);

        String out_trade_no = tradeNo;
        String total_amount = String.valueOf(amount);
        String subject = AlipayConstants.CHANGE_TITLE;

        //for test
        total_amount = "0.01";

//        //商户订单号，商户网站订单系统中唯一订单号，必填
//        String out_trade_no = new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"), "UTF-8");
//        //付款金额，必填
//        String total_amount = new String(request.getParameter("WIDtotal_amount").getBytes("ISO-8859-1"), "UTF-8");
//        //订单名称，必填
//        String subject = new String(request.getParameter("WIDsubject").getBytes("ISO-8859-1"), "UTF-8");
//        //商品描述，可空
//        String body = new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"), "UTF-8");

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
//                + "\"body\":\"" + body + "\","
                //+ "\"integration_type\":\"ALIAPP\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);
        if (response.isSuccess()) {
            String payForm = response.getBody();
            logger.info(payForm);
            return payForm;
        }

//        String tradeNo3rd = response.getTradeNo();


//        <form name="punchout_form" method="post" action="https://openapi.alipay.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=Kw%2FKUsD8InN2SJJ5315l7kT7p393n0nCYOCvsL6JxmX9smHCtM58A3Z%2FHcdkjnzA9ZL%2FqSGCPXIJs1owc9jYP2n3Yg1%2FnbWngnIP4XVJ6KkVwnkt0I6o4uQhyRLCgXw7ys61uEbx24pNDWjkJw3%2BUt7QmYrNzA1hOhTnoKu9Bxbd%2BrMPvoSPR%2BRYcI3A7g%2FyuSG2hCRMShntqnV13jMqQw4ubWZ4ekhaz4nAQYL6fpYihoRRT%2BZOnrLoqJj1Z%2F2POsslqKt%2FY38NhW0plZtLjU6G%2FTt31dq%2B9U8fj1zjMmWxP7Ja%2Fffkf1PyZVFWyFadDtiz78k%2F7dB8%2FGNc4pGB%2BQ%3D%3D&version=1.0&app_id=2019061065521285&sign_type=RSA&timestamp=2019-06-11+18%3A25%3A34&alipay_sdk=alipay-sdk-java-3.7.89.ALL&format=JSON">
//<input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;20190611182534264&quot;,&quot;total_amount&quot;:&quot;0.01&quot;,&quot;subject&quot;:&quot;旅通服务平台账户充值&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}">
//<input type="submit" value="立即支付" style="display:none" >
//</form>
//<script>document.forms[0].submit();</script>

//        //调用成功，则处理业务逻辑
//        if (response.isSuccess()) {
//            //.....
//        }


        return null;
    }

    public String getAlipayTradeDetail(String alipayTradeNo) throws AlipayApiException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{\"trade_no\":\"" + alipayTradeNo + "\"}");

        AlipayTradeQueryResponse response = alipayClient.execute(request);
        if (response.isSuccess()) {
            String body = response.getBody();
            return body;
        }else{
            return response.getMsg();
        }


    }

    public Map<String, String> getWxpayTradeDetail(String wxpayTradeNo) throws Exception {
        MyWxpayConfig config = new MyWxpayConfig();
        WXPay wxpay = new WXPay(config);

        Map<String, String> data = new HashMap<>();
        data.put("transaction_id", wxpayTradeNo);

        try {
            Map<String, String> respMap = wxpay.orderQuery(data);
            return respMap;
        } catch (Exception e) {
            logger.error("get wxpay trade order failed: ",e);
            e.printStackTrace();
        }
        return null;

    }


}
