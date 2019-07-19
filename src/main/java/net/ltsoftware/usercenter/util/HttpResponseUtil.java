package net.ltsoftware.usercenter.util;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpResponseUtil {

    public static void write(HttpServletResponse httpResponse, String html) throws IOException {
        httpResponse.setContentType("text/html;charset=utf-8");
        httpResponse.getWriter().write(html);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }
}
