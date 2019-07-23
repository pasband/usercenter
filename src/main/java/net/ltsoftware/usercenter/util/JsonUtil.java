package net.ltsoftware.usercenter.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * APP调用UTIL类
 *
 * @author
 * @date
 */
public class JsonUtil {

    private final static Logger log = Logger.getLogger(JsonUtil.class);

    /**
     * 写入到页面
     *
     * @param response
     * @param mes      写入的内容
     */
    public static void writer(HttpServletResponse response, String mes) {
        try {
            // 设置页面不缓存
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            log.info("response header:Access-Control-Allow-Origin="+response.getHeader("Access-Control-Allow-Origin"));
            if(StringUtils.isBlank(response.getHeader("Access-Control-Allow-Origin")))
                response.setHeader("Access-Control-Allow-Origin", "*");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            PrintWriter out = null;
            out = response.getWriter();
            out.print(mes);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("JsonUtil error:", e);
            e.printStackTrace();
        }
    }

    /**
     * 转化成既定的格式
     *
     * @param response
     * @param code     写入的内容
     */
    public static void toJsonMsg(HttpServletResponse response, int code,
                                 Object data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", code);
        map.put("data", data);
        writer(response,
                JSON.toJSONStringWithDateFormat(map, "yyyy-MM-dd HH:mm:ss.SSS"));
    }

    public static void toJsonMsg(HttpServletResponse response,
                                 Object data) {
        writer(response,
                JSON.toJSONStringWithDateFormat(data, "yyyy-MM-dd HH:mm:ss"));
    }

}
