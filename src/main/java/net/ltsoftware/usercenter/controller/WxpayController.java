package net.ltsoftware.usercenter.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.java.core.http.UrlEncoder;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.partnerpayments.jsapi.model.Transaction;
import net.ltsoftware.usercenter.constant.ErrorCode;
import net.ltsoftware.usercenter.constant.PayOrderConstants;
import net.ltsoftware.usercenter.constant.WxpayConstants;
import net.ltsoftware.usercenter.model.PayOrder;
import net.ltsoftware.usercenter.pay.WxPartnerPayService;
import net.ltsoftware.usercenter.service.PayOrderService;
import net.ltsoftware.usercenter.util.CodeHelper;
import net.ltsoftware.usercenter.util.HttpUtil;
import net.ltsoftware.usercenter.util.JsonUtil;
import net.ltsoftware.usercenter.util.RedisClient;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WxpayController {

    @Autowired
    private WxPartnerPayService wxPartnerPayService;

    @Autowired
    private PayOrderService payOrderService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private HttpUtil httpUtil;

    private static Logger logger = LoggerFactory.getLogger(WxpayController.class);

    @RequestMapping("/pay/wxpay/jsapi")
    @CrossOrigin
    public void wxPartnerPayJsapi(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        try {
            String openid = request.getParameter("openid");
            String key = request.getParameter("key");
            logger.info("key: " + key);
//            String jsonString = redisClient.get(key);
            PayOrder payOrder = payOrderService.selectByMchOrderNo(key);
            Byte state = payOrder.getState();
            if(state==null || state!= PayOrderConstants.STATE_INIT && state!= PayOrderConstants.STATE_ING){
                logger.error("PayOrder["+key+"] state:"+state);
                return;
            }
//            JSONObject json = JSONObject.parseObject(jsonString);
//        String successUrl = request.getParameter("redirectUrl");
            PrepayWithRequestPaymentResponse response = wxPartnerPayService.prepayWithRequestPayment(openid, key, payOrder);
            String respStr = response.toString();
            logger.info(respStr);
//        System.out.println("successUrl: " +successUrl);
            logger.info("openid: " + openid);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", response.getAppId());
            jsonObject.put("timeStamp", response.getTimeStamp());
            jsonObject.put("nonceStr", response.getNonceStr());
            jsonObject.put("package", response.getPackageVal());
            jsonObject.put("signType", response.getSignType());
            jsonObject.put("paySign", response.getPaySign());
//        JsonUtil.toJsonMsg(httpServletResponse,ErrorCode.SUCCESS,jsonObject);
//        redirectUrl+="?data="+jsonObject.toString();
            payOrder.setChannelUser(openid);
            payOrder.setWayCode(WxpayConstants.CHANNEL_JSAPI);
            payOrder.setState(PayOrderConstants.STATE_ING);
            payOrder.setNotifyState(PayOrderConstants.NOTIFY_STATE_NONE);
            payOrderService.updateByPrimaryKey(payOrder);
            String redirectUrl = "/wxmpPay.html";
            redirectUrl += "?isWeChatVisit=true";
            redirectUrl += "&data=" + UrlEncoder.urlEncode(jsonObject.toJSONString());
            redirectUrl += "&key=" + key;
//            redirectUrl+="&successUrl="+UrlEncoder.urlEncode(successUrl);
//            request.setAttribute("url",jsonObject.toString());
//            request.setAttribute("isWeChatVisit",true);
            logger.info("redirectUrl: " + redirectUrl);
            httpServletResponse.sendRedirect(redirectUrl);
        } catch (Exception e) {
            logger.error("wxPartnerPayJsapi", e);
        }

    }

    @RequestMapping("/pay/wxpay/jsapi/notify")
    @CrossOrigin
    public void wxPartnerPayJsapiNotify(HttpServletRequest request, HttpServletResponse httpServletResponse) {
        //{"summary":"支付成功",
        // "event_type":"TRANSACTION.SUCCESS",
        // "create_time":"2023-09-18T15:29:46+08:00",
        // "resource":{"original_type":"transaction",
        // "algorithm":"AEAD_AES_256_GCM",
        // "ciphertext":"TS9yC6KnxX43tU272OrQeErp4KY6j734FZCXXxbu/tdh0+OHDMvTugkxIEVhOhNtnWCNb/RjadZDpcghO3Ian/lzFi0YyeGxb+1OosNk/utDSmKsi4ZDVE72Gdv1jMohRdFzJ4JaCRrYGR2UUuA3uBpp9DcaKKuq1iT5kt3fIkT7umHQ6H2m4PCm8bjoNZ1ZKCHsOVu3bINAV1QJZktW08WmAHuUcpydLu0iF9blkrK2QoDOSU1F8yrnUWWzjL1hkq5K7DZF6W9UZ/cLAoEF5KYqVdU2vX/MXln2cOl8HNetlmnI8ai19A9aMF8sZC2DKclJ1TcDuuZhwZeQjHp4p5wgi/ulH3VODm5+x/8736iXhFNTzhY6UW74ZtnGDJ+nBEBR5NtOMI0v+Fq54579Kuiw7AaRD982+GQtfCr89gc/Z8FiJAL5l7OT3htoWT3KQhJ53dE8N3NEY5LI8LlgXNtUtQsP2/5nIo+bYgNkkfGOArndfZ0ag/JzuUfKMe1wxbTaRPwNvVVf4S9aJsmG2ka2pzBGvPdH+kDvuo6RxBZh6z+dcEPRKxioqc1Wk9aPFOkP8v4GIw7hZAVsV2sBMeDxB0p5mk5yVD2HTItiekvMNplniRhcj0BpmCdg4zGwpyVYsabOCqP6U3tI+TO+O6anICB4G2tKGdFB9QBqbkconeiG0O2Z40BPfAawDtS2NoVY19P/9hMzCG3azhDb2XA=",
        // "associated_data":"transaction","nonce":"8vBACRCOU66x"},
        // "resource_type":"encrypt-resource","id":"f6d3e392-7a32-5e7a-97d7-12a343048b1c"}
        try {
            Transaction transaction = wxPartnerPayService.getTransaction(request);
            if(transaction==null){
                logger.error("transaction is null.");
                return;
            }
            String key = transaction.getOutTradeNo();
//            String result = redisClient.get(key);
//            JSONObject json = JSONObject.parseObject(result);
            PayOrder payOrder = payOrderService.selectByMchOrderNo(key);
            if(payOrder==null){
                logger.error("payOrder not found.");
                return;
            }
            Byte state = payOrder.getState();
            Byte notifyState = payOrder.getNotifyState();
            if(state==null){
                logger.error("state is null.");
                return;
            }
            if(state==PayOrderConstants.STATE_SUCCESS && notifyState==PayOrderConstants.NOTIFY_STATE_SUCCESS){
                logger.error("payOrder state already is success");
                return;
            }
            //获取
            String transactionId = transaction.getTransactionId();
            String spOpenid = transaction.getPayer().getSpOpenid();
            String subOpenid = transaction.getPayer().getSubOpenid();
            logger.info("spOpenid: " + spOpenid + ", subOpenid: " + subOpenid);
            payOrder.setState(PayOrderConstants.STATE_SUCCESS);
            payOrder.setChannelOrderNo(transactionId);
            payOrderService.updateByPrimaryKey(payOrder);
            String busNotifyUrl = payOrder.getNotifyUrl();
//            String busNotifyUrl = json.getString("busNotify");
            logger.info("busNotifyUrl: " + busNotifyUrl);
            HttpUtil httpUtil = new HttpUtil();
//            String resp = httpUtil.get(busNotifyUrl,null);
            String addData = payOrder.getNotifyData();
            logger.info("addData: " + addData);
//            String resp = httpUtil.post(busNotifyUrl,json.getString("addData"));
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            String openId = payOrder.getChannelUser();
            String mchOrderNo = payOrder.getMchOrderNo();
            String wxPayOrderNo = transactionId;
            logger.info("openId: " +openId + " mchOrderNo: " + mchOrderNo + " wxPayOrderNo: " + wxPayOrderNo);
            String remark = "【支付方式：微信扫码支付, 支付流水号："+mchOrderNo+", 微信支付单号："+wxPayOrderNo+"】, 【支付人信息：openId="+openId+"】";

            list.add(new BasicNameValuePair("datas", addData));
            list.add(new BasicNameValuePair("payChannel", payOrder.getWayCode()));
            list.add(new BasicNameValuePair("extra",remark));
            String resp = httpUtil.post(busNotifyUrl, list, "UTF-8");
            logger.info("resp: " + resp);
            JSONObject json = JSONObject.parseObject(resp);
            Integer isSuccess = json.getInteger("isSuccess");
            if(isSuccess==1) {
                payOrder.setNotifyState(PayOrderConstants.NOTIFY_STATE_SUCCESS);
                payOrderService.updateByPrimaryKey(payOrder);
            }
        } catch (Exception e) {
            logger.error("wxPartnerPayJsapiNotify", e);
        }
    }

}
