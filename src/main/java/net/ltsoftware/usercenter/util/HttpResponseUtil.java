package net.ltsoftware.usercenter.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class HttpResponseUtil {

    public static JSONObject convertParametersToJSONObject(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (String paramName : parameterMap.keySet()) {
            String[] paramValues = parameterMap.get(paramName);
            if (paramValues.length == 1) {
                jsonObject.put(paramName, paramValues[0]);
            } else {
                jsonObject.put(paramName, paramValues);
            }
        }

        return jsonObject;
    }

    public static void write(HttpServletResponse httpResponse, String html) throws IOException {
        httpResponse.setContentType("text/html;charset=utf-8");
        httpResponse.getWriter().write(html);//直接将完整的表单html输出到页面
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }
}
