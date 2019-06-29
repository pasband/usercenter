package net.ltsoftware.usercenter.interceptor;

import net.ltsoftware.usercenter.annotation.PassToken;
import net.ltsoftware.usercenter.constant.ErrorCode;
import net.ltsoftware.usercenter.constant.SessionConstants;
import net.ltsoftware.usercenter.model.User;
import net.ltsoftware.usercenter.service.UserService;
import net.ltsoftware.usercenter.util.CookieUtils;
import net.ltsoftware.usercenter.util.JsonUtil;
import net.ltsoftware.usercenter.util.SessionUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AuthenticationInterceptor implements HandlerInterceptor {


    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //获取访问URL
//        String url = request.getRequestURL().toString();

        if(!(o instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod) o;
        Method method=handlerMethod.getMethod();
        //检查是否有passToken注释，有则跳过验证
        if(method.isAnnotationPresent(PassToken.class)){
            PassToken passToken=method.getAnnotation(PassToken.class);
            if(passToken.required()){
                return true;
            }
        }

        String token = request.getHeader(SessionConstants.LOGIN_TOKEN_NAME);
        if (StringUtils.isBlank(token)) {
            token = CookieUtils.getCookieValue(request, SessionConstants.LOGIN_TOKEN_NAME);
        }
        if (StringUtils.isBlank(token)) {
            JsonUtil.toJsonMsg(response, ErrorCode.NEED_LOGIN,null);
            return false;
        }
        User currentUser = userService.getUserByToken(token);
        if(currentUser==null){
            JsonUtil.toJsonMsg(response, ErrorCode.NEED_LOGIN,null);
            return false;
        }
        request.setAttribute("login_user",currentUser);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

}
