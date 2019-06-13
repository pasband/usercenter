package net.ltsoftware.platform.usercenter.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import net.ltsoftware.platform.usercenter.constant.AlipayConstants;
import net.ltsoftware.platform.usercenter.util.CodeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
                AlipayConstants.ALI_PUB_KEY

        );


    }

    public String getChargePage(String channel, Integer amount, Long userId) throws AlipayApiException {
        switch (channel) {
            case "alipay":
                return alipayCharge(amount, userId);
            case "weixin":
                return weixinCharge(amount, userId);


        }

        return null;
    }

    private String weixinCharge(Integer amount, Long userId){








        return null;
    }

    private String alipayCharge(Integer amount, Long userId) throws AlipayApiException {


//        AlipayOpenPublicTemplateMessageIndustryModifyRequest request = new AlipayOpenPublicTemplateMessageIndustryModifyRequest();
//        //SDK已经封装掉了公共参数，这里只需要传入业务参数
//        //此次只是参数展示，未进行字符串转义，实际情况下请转义
//        request.setBizContent("  {" +
//                "    \"primary_industry_name\":\"IT科技/IT软件与服务\"," +
//                "    \"primary_industry_code\":\"10001/20102\"," +
//                "    \"secondary_industry_code\":\"10001/20102\"," +
//                "    \"secondary_industry_name\":\"IT科技/IT软件与服务\"" +
//                " }");
//        AlipayOpenPublicTemplateMessageIndustryModifyResponse response = alipayClient.execute(request);


        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(AlipayConstants.RETURN_URL);
        alipayRequest.setNotifyUrl(AlipayConstants.NOTIFY_URL);

        String out_trade_no = CodeHelper.getOrderId();
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
        if(response.isSuccess()) {
            String payForm = alipayClient.pageExecute(alipayRequest).getBody();
            logger.info(payForm);
            return payForm;
        }

        String tradeNo = response.getTradeNo();



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


}
