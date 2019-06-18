package net.ltsoftware.usercenter.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Component
public class HttpUtil {

    private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public String post(String url, List<NameValuePair> paras, String charset) throws IOException {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(paras, charset));
            logger.info("Executing request: " + post.getRequestLine());
            return httpclient.execute(post, new SimpleHandler());

        } finally {
            httpclient.close();
        }


    }

    public String getUrl(String url, List<NameValuePair> paras) {
        return getUrl(url, paras, "UTF-8");

    }

    public String getUrl(String url, List<NameValuePair> paras, String charset) {

        if (null != paras && paras.size() > 0) {
            String encodedParams = encodeParameters(paras, charset);
            if (-1 == url.indexOf("?")) {
                url = url + "?" + encodedParams;
            } else {
                url = url + "&" + encodedParams;
            }
        }
        return url;

    }

    public String get(String url, List<NameValuePair> paras) {
        return get(url, paras, "UTF-8");
    }

    public String get(String url, List<NameValuePair> paras, String charset) {
        String respStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        url = getUrl(url, paras, charset);
        try {
            HttpGet get = new HttpGet(url);
            logger.info("Executing request: " + get.getRequestLine());
            respStr = httpclient.execute(get, new SimpleHandler());

        } catch (ClientProtocolException e) {
            logger.error("Executing request error: ", e);
        } catch (IOException e) {
            logger.error("Executing request error: ", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error("Close httpclient error: ", e);
            }
        }
        return respStr;
    }

    private class SimpleHandler implements ResponseHandler<String> {

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                return entity != null ? EntityUtils.toString(entity) : null;
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        }
    }

    private static String encodeParameters(List<NameValuePair> postParams, String charset) {
        StringBuffer buf = new StringBuffer();

        for (int j = 0; j < postParams.size(); j++) {
            if (j != 0) {
                buf.append("&");
            }

            NameValuePair nvp = postParams.get(j);
            System.out.println("   ---> " + j + ":" + nvp);
            try {
                buf
                        .append(URLEncoder.encode(nvp.getName(), charset))
                        .append("=")
                        .append(URLEncoder.encode(nvp.getValue(), charset));
            } catch (UnsupportedEncodingException var4) {

            }
        }

        return buf.toString();
    }
}
