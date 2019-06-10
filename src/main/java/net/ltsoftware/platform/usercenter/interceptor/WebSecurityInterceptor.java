package net.ltsoftware.platform.usercenter.interceptor;

import net.ltsoftware.platform.usercenter.constant.SessionConstants;
import net.ltsoftware.platform.usercenter.util.CookieHelper;
import net.ltsoftware.platform.usercenter.util.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "security")
public class WebSecurityInterceptor implements HandlerInterceptor {

    private List<String> excludeUrls;

    public List<String> getExcludeUrls() {
        return excludeUrls;
    }

    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    @Autowired
    private SessionUtil sessionUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String url = requestUri.substring(contextPath.length());

        String path = request.getContextPath();
        String temp = request.getServerPort() == 80 ? "" : ":"
                + request.getServerPort() + path;
        String basePath = request.getScheme() + "://" + request.getServerName()
                + temp + "/";

        if (excludeUrls.contains(url)) {
            return true;
        } else {
            //find token in request
            String token = CookieHelper.getCookieValue(request,
                    SessionConstants.USER_TOKEN);
            if (token == null) {
                response.sendRedirect(response.encodeURL(basePath
                        + "/"));
                return false;
            }
            // check token in redis
            sessionUtil.get(token);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

}
