package net.ltsoftware.usercenter.interceptor;

import net.ltsoftware.usercenter.constant.ErrorCode;
import net.ltsoftware.usercenter.constant.SessionConstants;
import net.ltsoftware.usercenter.model.User;
import net.ltsoftware.usercenter.service.UserService;
import net.ltsoftware.usercenter.util.CookieUtils;
import net.ltsoftware.usercenter.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class AuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    private List<String> passPhoneBindList;

    private static Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    public void setPassPhoneBind(List<String> passPhoneBindList){
        this.passPhoneBindList = passPhoneBindList;

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //获取访问URL
        String uri = request.getRequestURI();
        logger.info("uri:"+uri);

        String token = request.getHeader(SessionConstants.LOGIN_TOKEN_NAME);
        if (StringUtils.isBlank(token)) {
            logger.info("token blank");
            token = CookieUtils.getCookieValue(request, SessionConstants.LOGIN_TOKEN_NAME);
        }
        if (StringUtils.isBlank(token)) {
            logger.info("token blank");
            JsonUtil.toJsonMsg(response, ErrorCode.TOKEN_NULL,null);
            return false;
        }
        User currentUser = userService.getUserByToken(token);
        if(currentUser==null){
            logger.info("currentUser blank");
            //cannot found token key in redis
            JsonUtil.toJsonMsg(response, ErrorCode.INVALID_TOKEN,null);
            return false;
        }
//        if(currentUser.getStatus().equals("1") && !passPhoneBindList.contains(uri)){
//            logger.info("need phone bind");
//            JsonUtil.toJsonMsg(response, ErrorCode.NEED_PHONE_BIND,null);
//            return false;
//        }
        request.setAttribute("login_user",currentUser);
        userService.refreshToken(token);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

}
