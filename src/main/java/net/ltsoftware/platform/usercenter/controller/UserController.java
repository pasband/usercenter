package net.ltsoftware.platform.usercenter.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import net.ltsoftware.platform.usercenter.constant.AlipayConstants;
import net.ltsoftware.platform.usercenter.constant.ErrorCode;
import net.ltsoftware.platform.usercenter.constant.SmsConstants;
import net.ltsoftware.platform.usercenter.model.Order;
import net.ltsoftware.platform.usercenter.model.User;
import net.ltsoftware.platform.usercenter.oauth2.QqOauth2Service;
import net.ltsoftware.platform.usercenter.pay.PaymentService;
import net.ltsoftware.platform.usercenter.service.OrderService;
import net.ltsoftware.platform.usercenter.service.UserService;
import net.ltsoftware.platform.usercenter.util.CodeHelper;
import net.ltsoftware.platform.usercenter.util.JsonUtil;
import net.ltsoftware.platform.usercenter.util.RedisClient;
import net.ltsoftware.platform.usercenter.util.YXSmsSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private YXSmsSender smsSender;

    @Autowired
    private QqOauth2Service qqOauth2Service;

    @Autowired
    private PaymentService paymentServcie;

    @Autowired
    private RedisClient redisClient;

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

//    @RequestMapping("/user/reg")
//    public void addUser(User user, HttpServletResponse response) {
//        try {
//            userService.insert(user);
//            JsonUtil.writer(response, "");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    @RequestMapping("/user/info")
    @CrossOrigin(origins = "http://platform.ltsoftware.net", allowCredentials = "true")
    public void getUserInfo(User user, HttpServletResponse response) throws Exception {
        Long id = user.getId();
        user = userService.selectByPrimaryKey(id);
        JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, user);
    }

    @RequestMapping("/oauth/qqurl")
    public void getQqAuthUrl(HttpServletRequest request, HttpServletResponse response) throws QQConnectException, IOException {
        String authurl = new Oauth().getAuthorizeURL(request);
        logger.info(authurl);
//        Map<String, String> data = new HashMap<>();
//        data.put("qqurl", authurl);
//        JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, data);
        response.sendRedirect(authurl);
    }

    @RequestMapping("/oauth/qqcallback")
    @CrossOrigin(origins = "http://platform.ltsoftware.net", allowCredentials = "true")
    public void qqCallback(HttpServletRequest request, HttpServletResponse response) throws QQConnectException {

        try {
            AccessToken accessTokenObj = (new Oauth()).getAccessTokenByRequest(request);

            String accessToken = null,
                    openID = null;
            long tokenExpireIn = 0L;

            if (accessTokenObj.getAccessToken().equals("")) {
//                我们的网站被CSRF攻击了或者用户取消了授权
//                做一些数据统计工作
                System.out.print("没有获取到响应参数");
            } else {
                accessToken = accessTokenObj.getAccessToken();
                tokenExpireIn = accessTokenObj.getExpireIn();

                request.getSession().setAttribute("demo_access_token", accessToken);
                request.getSession().setAttribute("demo_token_expirein", String.valueOf(tokenExpireIn));

                // 利用获取到的accessToken 去获取当前用的openid -------- start
                OpenID openIDObj = new OpenID(accessToken);
                openID = openIDObj.getUserOpenID();


                User user = userService.selectByQqOpenId(openID);

                if (user == null) {
                    user = new User();
                    long id = userService.insert(user);
                    user.setId(id);
                }
                UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                String nickname = userInfoBean.getNickname();
                String avatar30 = userInfoBean.getAvatar().getAvatarURL30();

                user.setName(nickname);
                user.setQqOpenid(openID);
                user.setAvatar(avatar30);
                userService.updateByPrimaryKey(user);

                String token = CodeHelper.getUUID();
                Cookie cookie1 = new Cookie("login_user", token);
                cookie1.setDomain("ltsoftware.net");
                cookie1.setPath("/");
                Cookie cookie2 = new Cookie("login_user_id", String.valueOf(user.getId()));
                cookie2.setDomain("ltsoftware.net");
                cookie2.setPath("/");
                response.addCookie(cookie1);
                response.addCookie(cookie2);
                response.sendRedirect("http://platform.ltsoftware.net/home?id=" + user.getId());

            }
        } catch (QQConnectException e) {
            logger.error("qq connect error:", e);
        } catch (Exception e) {
            logger.error("qq connect unknown error:", e);
        }
    }


    @RequestMapping("/phone/bind")
    public void bindPhone(String phone, String code, String userId, HttpServletResponse response) throws Exception {
        int errCode = -1;
        String codeInCache = redisClient.get(SmsConstants.PREFIX + SmsConstants.CODE + phone);
        if (codeInCache == null || codeInCache.equals(code)) {
            errCode = ErrorCode.PHONE_CODE_WRONG;
        } else {
            User user = userService.selectByPrimaryKey(Long.parseLong(userId));
            user.setPhone(phone);
            user.setStatus("3");
            userService.updateByPrimaryKey(user);
            errCode = ErrorCode.SUCCESS;
        }
        JsonUtil.toJsonMsg(response, errCode, null);
    }

    @RequestMapping("/phone/code")
    public void sendCode(String phone, HttpServletResponse response) {
        try {
            int errCode = smsSender.sendPhoneCode(phone);
            JsonUtil.toJsonMsg(response, errCode, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/pay/charge")
    public void charge(Long userId, Integer amount, String channel, HttpServletResponse response) throws AlipayApiException {
        String chargePage = paymentServcie.getChargePage(channel, amount, userId);
        JsonUtil.toJsonMsg(response, ErrorCode.SUCCESS, chargePage);

    }

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
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params,
                AlipayConstants.ALI_PUB_KEY,
                AlipayConstants.CHARSET,
                AlipayConstants.SIGN_TYPE); //调用SDK验证签名

        logger.info("alipay return : " + signVerified);

        int errCode = -1;
        //——请在这里编写您的程序（以下代码仅作参考）——
        if (signVerified) {
            errCode = ErrorCode.SUCCESS;
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8");

            orderService.selectByTradeNo(out_trade_no);
//          out.println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);
        } else {
//            out.println("验签失败");

        }
//        JsonUtil.toJsonMsg(response, errCode, null);
        response.sendRedirect("http://platform.ltsoftware.net/home/baseinformation?code=" + errCode);
    }

    @RequestMapping("/oauth/wxcallback")
    public void wxCallback() {


    }


}
