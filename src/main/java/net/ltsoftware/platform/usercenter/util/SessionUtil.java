package net.ltsoftware.platform.usercenter.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionUtil {

    @Autowired
    private RedisClient redisClient;

    public String get(String key) {
        return redisClient.get(key);

    }

//    public String setAttr(HttpServletRequest request,
//                          HttpServletResponse response, String key, Object value, int seconds) {
//        String redisKey = generateKeyPrefix(request, response, key, key);
//        if (redisKey == null) {
//            return null;
//        }
//
//        redisClient.setex(redisKey, seconds, (String) value);
//        return redisKey;
//
//    }
//
//    private String generateKeyPrefix(HttpServletRequest request,
//                                     HttpServletResponse response, String... key) {
//
//        String cookieName = "uky";
//        if (key.length > 1)
//            cookieName = key[1];
//
//        String sessionId = CookieHelper.getCookieValue(request, cookieName);
//        if (sessionId == null || "".equals(sessionId)) {
//            sessionId = request.getSession().getId().toUpperCase() + getUUID();
//            response.addCookie(new Cookie(cookieName, sessionId));
//        }
//        StringBuffer sb = new StringBuffer(sessionId).append("-")
//                .append(key[0]);
//        return sb.toString();
//    }

}
